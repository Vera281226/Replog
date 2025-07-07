// ========================================
// ApiImportService.java
// - TMDB API 데이터를 1회만 호출하여 MariaDB에 저장
// - 이후 프론트는 오직 Axios로 DB 조회만 수행함
// - TMDB API는 이 클래스 외 어디서도 호출하지 않음
// ========================================

package pack.importing.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import pack.config.TmdbProperties;
import pack.importing.dto.TmdbContentDto;
import pack.importing.dto.TmdbContentResponse;
import pack.importing.dto.TmdbCreditsDto;
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

import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class ApiImportService {

    // ✅ 외부 API 호출 및 저장에 필요한 모든 Repository 주입
    private final RestTemplate restTemplate;
    private final GenresRepository genresRepository;
    private final ProvidersRepository providersRepository;
    private final ContentsRepository contentsRepository;
    private final ContentGenresRepository contentGenresRepository;
    private final ContentProvidersRepository contentProvidersRepository;
    private final PeopleRepository peopleRepository;
    private final ContentPeopleRepository contentPeopleRepository;
    private final TmdbProperties tmdbProperties;

    // ✅ 1. TMDB 장르 API → genres 테이블 저장
    public void importGenresFromTmdb() {
        try {
            String url = tmdbProperties.getBaseUrl() + "/genre/movie/list?language=ko";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + tmdbProperties.getToken());
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<TmdbGenreResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, TmdbGenreResponse.class);
            List<TmdbGenreDto> genreList = response.getBody().getGenres();
            for (TmdbGenreDto dto : genreList) {
                if (!genresRepository.existsById(dto.getId())) {
                    Genres genre = new Genres();
                    genre.setGenreId(dto.getId());
                    genre.setName(dto.getName());
                    genresRepository.save(genre);
                }
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "TMDB 장르 데이터를 가져오는 중 오류 발생");
        }
    }

    // ✅ 2. TMDB 플랫폼 API → providers 테이블 저장
    public void importProvidersFromTmdb() {
        try {
            String sampleMovieId = "872585";
            String url = tmdbProperties.getBaseUrl() + "/movie/" + sampleMovieId + "/watch/providers";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + tmdbProperties.getToken());
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<TmdbWatchProviderResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, TmdbWatchProviderResponse.class);
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
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "TMDB 플랫폼 데이터를 가져오는 중 오류 발생");
        }
    }

    // ✅ 3. TMDB 예능(tv) 콘텐츠 수집
    public void importKoreanVarietyShowsFromTmdb() {
        try {
            String base = tmdbProperties.getBaseUrl() + "/discover/tv?language=ko&sort_by=popularity.desc&with_origin_country=KR&first_air_date.gte=2006-01-01&page=";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + tmdbProperties.getToken());
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            Set<Integer> processedTmdbIds = new HashSet<>();

            for (int page = 1; page <= 10; page++) {
                ResponseEntity<TmdbContentResponse> response = restTemplate.exchange(base + page, HttpMethod.GET, entity, TmdbContentResponse.class);
                List<TmdbContentDto> contents = response.getBody().getResults();
                for (TmdbContentDto dto : contents) {
                    if (processedTmdbIds.contains(dto.getId()) || contentsRepository.findByTmdbId(dto.getId()).isPresent()) continue;

                    Contents content = new Contents();
                    content.setTmdbId(dto.getId());
                    content.setTitle(dto.getName());
                    content.setOverview(dto.getOverview());
                    content.setPosterPath(dto.getPosterPath());
                    content.setBackdropPath(dto.getBackdropPath());
                    content.setRating((float) dto.getVoteAverage());
                    content.setMediaType("tv");
                    if (dto.getFirstAirDate() != null && !dto.getFirstAirDate().isEmpty()) {
                        content.setReleaseDate(LocalDate.parse(dto.getFirstAirDate()));
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

                    String providerUrl = tmdbProperties.getBaseUrl() + "/tv/" + dto.getId() + "/watch/providers";
                    ResponseEntity<TmdbWatchProviderResponse> providerResponse = restTemplate.exchange(providerUrl, HttpMethod.GET, entity, TmdbWatchProviderResponse.class);
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
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "TMDB 예능 데이터를 가져오는 중 오류 발생");
        }
    }

    // ✅ TMDB 인물 정보 수집
    public void importPeopleAndCredits() {
        try {
            List<Contents> contentsList = contentsRepository.findAll();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + tmdbProperties.getToken());
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            Set<String> savedMappings = new HashSet<>();

            for (Contents content : contentsList) {
                int tmdbId = content.getTmdbId();
                String mediaType = content.getMediaType();
                String url = tmdbProperties.getBaseUrl() + "/" + mediaType + "/" + tmdbId + "/credits";
                ResponseEntity<TmdbCreditsDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, TmdbCreditsDto.class);
                TmdbCreditsDto dto = response.getBody();
                if (dto == null) continue;

                for (TmdbCreditsDto.Cast cast : dto.getCast()) {
                    if (cast.getId() == 0 || cast.getName() == null) continue;
                    People person = peopleRepository.findByTmdbId(cast.getId()).orElseGet(() -> {
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

                for (TmdbCreditsDto.Crew crew : dto.getCrew()) {
                    if (!"Director".equalsIgnoreCase(crew.getJob())) continue;
                    if (crew.getId() == 0 || crew.getName() == null) continue;
                    People director = peopleRepository.findByTmdbId(crew.getId()).orElseGet(() -> {
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
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "TMDB 인물 및 크레딧 정보를 가져오는 중 오류 발생");
        }
    }

    // 🔒 향후 영화(movie) 콘텐츠 수집용 (현재 미사용)
    /*
    public void importContentsFromTmdb() {
        // 미사용: 영화 수집 로직은 추후 필요 시 복원
    }
    */

    // ✅ 전체 수집 통합 메서드
    public void importAllFromTmdbSince(LocalDate startDate) {
        importGenresFromTmdb();
        importProvidersFromTmdb();
        // importContentsFromTmdb(); // 🔒 영화용 주석 대기
        importPeopleAndCredits();
        importKoreanVarietyShowsFromTmdb();
    }
}
