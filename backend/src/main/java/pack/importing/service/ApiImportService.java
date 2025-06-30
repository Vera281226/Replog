package pack.importing.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pack.importing.dto.TmdbContentDto;
import pack.importing.dto.TmdbContentResponse;
import pack.importing.dto.TmdbCreditsDto;
import pack.importing.dto.TmdbCreditsDto.Cast;
import pack.importing.dto.TmdbCreditsDto.Crew;
import pack.importing.dto.TmdbGenreDto;
import pack.importing.dto.TmdbGenreResponse;
import pack.importing.dto.TmdbProviderDto;
import pack.importing.dto.TmdbWatchProviderResponse;
import pack.modules.contentgenre.model.ContentGenres;
import pack.modules.contentgenre.repository.ContentGenresRepository;
import pack.modules.contentpeople.model.ContentPeople;
import pack.modules.contentpeople.repository.ContentPeopleRepository;
import pack.modules.contentprovider.model.ContentProviders;
import pack.modules.contentprovider.repository.ContentProvidersRepository;
import pack.modules.contents.model.Contents;
import pack.modules.contents.repository.ContentsRepository;
import pack.modules.genres.model.Genres;
import pack.modules.genres.repository.GenresRepository;
import pack.modules.people.model.People;
import pack.modules.people.repository.PeopleRepository;
import pack.modules.provides.model.Providers;
import pack.modules.provides.repository.ProvidersRepository;

/**
 * ApiImportService
 * <p>
 * TMDB API로부터 콘텐츠, 장르, 플랫폼, 인물(출연진/감독) 정보를 가져와 DB에 저장하는 서비스 클래스입니다.
 * - contents, genres, providers, people, content_people 등 복수 테이블을 동시에 다룹니다.
 * - API 호출 결과를 각각의 테이블 구조에 맞게 저장하고, 연관관계 매핑도 처리합니다.
 * - 이 클래스는 백엔드 전체 데이터 초기화 및 대량 수집 시 주로 사용됩니다.
 */
@Service
@RequiredArgsConstructor
public class ApiImportService {

    private final RestTemplate restTemplate;
    private final GenresRepository genresRepository;
    private final ProvidersRepository providersRepository;
    private final ContentsRepository contentsRepository;
    private final ContentGenresRepository contentGenresRepository;
    private final ContentProvidersRepository contentProvidersRepository;
    private final PeopleRepository peopleRepository;
    private final ContentPeopleRepository contentPeopleRepository;

    @Value("${tmdb.token}")
    private String tmdbToken;

    /**
     * TMDB 장르 데이터를 가져와 DB에 저장합니다.
     */
    public void importGenresFromTmdb() {
        String url = "https://api.themoviedb.org/3/genre/movie/list?language=ko";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<TmdbGenreResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                TmdbGenreResponse.class
        );

        List<TmdbGenreDto> genreList = response.getBody().getGenres();

        for (TmdbGenreDto dto : genreList) {
            if (!genresRepository.existsById(dto.getId())) {
                Genres genre = new Genres();
                genre.setGenreId(dto.getId());
                genre.setName(dto.getName());
                genresRepository.save(genre);
            }
        }
    }

    /**
     * TMDB 플랫폼 데이터를 가져와 DB에 저장합니다.
     */
    public void importProvidersFromTmdb() {
        String sampleMovieId = "872585";
        String providerUrl = "https://api.themoviedb.org/3/movie/" + sampleMovieId + "/watch/providers";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<TmdbWatchProviderResponse> response = restTemplate.exchange(
                providerUrl,
                HttpMethod.GET,
                entity,
                TmdbWatchProviderResponse.class
        );

        List<TmdbProviderDto> list = response.getBody().getResults().get("KR").getFlatrate();
        if (list == null) return;

        for (TmdbProviderDto dto : list) {
            if (!providersRepository.existsById(dto.getProviderId())) {
                Providers provider = new Providers();
                provider.setProviderId(dto.getProviderId());
                provider.setName(dto.getProviderName());
                provider.setLogoPath(dto.getLogoPath());
                providersRepository.save(provider);
            }
        }
    }

    /**
     * TMDB 콘텐츠 데이터를 가져와 DB에 저장하고 장르/플랫폼을 매핑합니다.
     */
    public void importContentsFromTmdb() {
        String url = "https://api.themoviedb.org/3/discover/movie?language=ko&sort_by=popularity.desc"
                + "&primary_release_date.gte=2023-01-01&page=";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Set<Integer> processedTmdbIds = new HashSet<>();

        for (int page = 1; page <= 10; page++) {
            ResponseEntity<TmdbContentResponse> response = restTemplate.exchange(
                    url + page,
                    HttpMethod.GET,
                    entity,
                    TmdbContentResponse.class
            );

            List<TmdbContentDto> contents = response.getBody().getResults();

            for (TmdbContentDto dto : contents) {
                if (processedTmdbIds.contains(dto.getId()) ||
                        contentsRepository.findByTmdbId(dto.getId()).isPresent()) {
                    continue;
                }

                Contents content = new Contents();
                content.setTmdbId(dto.getId());
                content.setTitle(dto.getTitle());
                content.setOverview(dto.getOverview());
                content.setPosterPath(dto.getPosterPath());
                content.setRating((float) dto.getVoteAverage());
                content.setMediaType("movie");

                if (dto.getReleaseDate() != null && !dto.getReleaseDate().isEmpty()) {
                    content.setReleaseDate(LocalDate.parse(dto.getReleaseDate()));
                }

                contentsRepository.save(content);
                processedTmdbIds.add(dto.getId());

                for (int genreId : dto.getGenreIds()) {
                    if (genresRepository.existsById(genreId)) {
                        ContentGenres cg = new ContentGenres();
                        cg.setContentId(content.getContentId());
                        cg.setGenreId(genreId);
                        contentGenresRepository.save(cg);
                    }
                }

                String providerUrl = "https://api.themoviedb.org/3/movie/" + dto.getId() + "/watch/providers";
                ResponseEntity<TmdbWatchProviderResponse> providerResponse = restTemplate.exchange(
                        providerUrl,
                        HttpMethod.GET,
                        entity,
                        TmdbWatchProviderResponse.class
                );

                if (providerResponse.getBody().getResults().get("KR") != null) {
                    List<TmdbProviderDto> providers = providerResponse.getBody().getResults().get("KR").getFlatrate();
                    if (providers != null) {
                        for (TmdbProviderDto p : providers) {
                            if (!providersRepository.existsById(p.getProviderId())) {
                                Providers provider = new Providers();
                                provider.setProviderId(p.getProviderId());
                                provider.setName(p.getProviderName());
                                provider.setLogoPath(p.getLogoPath());
                                providersRepository.save(provider);
                            }

                            ContentProviders cp = new ContentProviders();
                            cp.setContentId(content.getContentId());
                            cp.setProviderId(p.getProviderId());
                            contentProvidersRepository.save(cp);
                        }
                    }
                }
            }
        }
    }

    /**
     * TMDB에서 전체 데이터를 통합 가져옵니다.
     * - 장르, 플랫폼, 콘텐츠, 인물 순으로 호출됩니다.
     */
    public void importAllFromTmdbSince(LocalDate startDate) {
        importGenresFromTmdb();
        importProvidersFromTmdb();
        importContentsFromTmdb();
        importPeopleAndCredits();
    }

    /**
     * TMDB에서 출연진/감독 정보를 가져와 people, content_people 테이블에 저장합니다.
     */
    public void importPeopleAndCredits() {
        List<Contents> contentsList = contentsRepository.findAll();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Set<String> savedMappings = new HashSet<>();

        for (Contents content : contentsList) {
            int tmdbId = content.getTmdbId();
            String mediaType = content.getMediaType();

            String url = "https://api.themoviedb.org/3/" + mediaType + "/" + tmdbId + "/credits";

            try {
                ResponseEntity<TmdbCreditsDto> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        TmdbCreditsDto.class
                );

                TmdbCreditsDto dto = response.getBody();
                if (dto == null) continue;

                for (Cast cast : dto.getCast()) {
                    if (cast.getId() == 0 || cast.getName() == null) continue;

                    People person = peopleRepository.findByTmdbId(cast.getId())
                            .orElseGet(() -> {
                                People newPerson = new People();
                                newPerson.setTmdbId(cast.getId());
                                newPerson.setName(cast.getName());
                                newPerson.setProfilePath(cast.getProfilePath());
                                newPerson.setKnownForDepartment("Acting");
                                return peopleRepository.save(newPerson);
                            });

                    String key = content.getContentId() + "-" + person.getPersonId() + "-actor";
                    if (!savedMappings.contains(key)) {
                        ContentPeople mapping = new ContentPeople();
                        mapping.setContentId(content.getContentId());
                        mapping.setPersonId(person.getPersonId());
                        mapping.setRole("actor");
                        mapping.setCharacterName(cast.getCharacter());
                        contentPeopleRepository.save(mapping);
                        savedMappings.add(key);
                    }
                }

                for (Crew crew : dto.getCrew()) {
                    if (crew.getJob() == null || !crew.getJob().equalsIgnoreCase("Director")) continue;
                    if (crew.getId() == 0 || crew.getName() == null) continue;

                    People director = peopleRepository.findByTmdbId(crew.getId())
                            .orElseGet(() -> {
                                People newPerson = new People();
                                newPerson.setTmdbId(crew.getId());
                                newPerson.setName(crew.getName());
                                newPerson.setProfilePath(crew.getProfilePath());
                                newPerson.setKnownForDepartment("Directing");
                                return peopleRepository.save(newPerson);
                            });

                    String key = content.getContentId() + "-" + director.getPersonId() + "-director";
                    if (!savedMappings.contains(key)) {
                        ContentPeople mapping = new ContentPeople();
                        mapping.setContentId(content.getContentId());
                        mapping.setPersonId(director.getPersonId());
                        mapping.setRole("director");
                        mapping.setCharacterName(null);
                        contentPeopleRepository.save(mapping);
                        savedMappings.add(key);
                    }
                }

            } catch (Exception e) {
                System.err.println("[ERROR] TMDB credits 가져오기 실패: contentId=" + content.getContentId() + ", tmdbId=" + tmdbId);
                e.printStackTrace();
            }
        }
    }
}