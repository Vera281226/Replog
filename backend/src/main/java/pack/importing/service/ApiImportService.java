package pack.importing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import pack.config.TmdbProperties;
import pack.importing.dto.*;
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

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ApiImportService {

    private final RestTemplate restTemplate;
    private final TmdbProperties tmdbProperties;

    private final GenresRepository genresRepository;
    private final ProvidersRepository providersRepository;
    private final ContentsRepository contentsRepository;
    private final ContentGenresRepository contentGenresRepository;
    private final ContentProvidersRepository contentProvidersRepository;
    private final PeopleRepository peopleRepository;
    private final ContentPeopleRepository contentPeopleRepository;

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbProperties.getToken());
        return headers;
    }

    public void importGenresFromTmdb() {
        String url = tmdbProperties.getBaseUrl() + "/genre/movie/list?language=ko";
        ResponseEntity<TmdbGenreResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(getHeaders()), TmdbGenreResponse.class);
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

    public void importProvidersFromTmdb() {
        String url = tmdbProperties.getBaseUrl() + "/watch/providers/movie?language=ko&watch_region=KR";
        ResponseEntity<TmdbProviderResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(getHeaders()), TmdbProviderResponse.class);
        List<TmdbProviderDto> providers = response.getBody().getResults();

        for (TmdbProviderDto p : providers) {
            if (!providersRepository.existsById(p.getProviderId())) {
                if (p.getProviderName() == null) continue;
                Providers provider = new Providers();
                provider.setProviderId(p.getProviderId());
                provider.setName(p.getProviderName());
                provider.setLogoPath(p.getLogoPath());
                providersRepository.save(provider);
            }
        }
    }

    public void importContentsFromTmdb(LocalDate startDate) {
        Set<Integer> processedTmdbIds = new HashSet<>();
        HttpHeaders headers = getHeaders();

        for (int page = 1; page <= 10; page++) {
            String url = tmdbProperties.getBaseUrl() + "/discover/movie?language=ko"
                    + "&sort_by=popularity.desc&include_adult=false"
                    + "&with_watch_monetization_types=flatrate"
                    + "&primary_release_date.gte=" + startDate
                    + "&primary_release_date.lte=" + LocalDate.now()
                    + "&region=KR&page=" + page;

            ResponseEntity<TmdbContentResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), TmdbContentResponse.class);

            List<TmdbContentDto> results = response.getBody().getResults();
            if (results == null || results.isEmpty()) break;

            for (TmdbContentDto dto : results) {
                if (processedTmdbIds.contains(dto.getId()) ||
                        contentsRepository.findByTmdbId(dto.getId()).isPresent()) continue;

                Contents content = new Contents();
                content.setTmdbId(dto.getId());
                content.setTitle(dto.getTitle());
                content.setOverview(dto.getOverview());
                content.setPosterPath(dto.getPosterPath());
                content.setBackdropPath(dto.getBackdropPath());
                content.setRating((float) dto.getVoteAverage());
                content.setMediaType("movie");

                if (dto.getReleaseDate() != null && !dto.getReleaseDate().isEmpty()) {
                    content.setReleaseDate(LocalDate.parse(dto.getReleaseDate()));
                }

                Contents saved = contentsRepository.save(content);
                int contentId = saved.getContentId();
                processedTmdbIds.add(dto.getId());

                for (Integer genreId : dto.getGenreIds()) {
                    if (genresRepository.existsById(genreId)) {
                        ContentGenres cg = new ContentGenres();
                        cg.setContentId(contentId);
                        cg.setGenreId(genreId);
                        contentGenresRepository.save(cg);
                    }
                }

                String providerUrl = tmdbProperties.getBaseUrl() + "/movie/" + dto.getId() + "/watch/providers";
                ResponseEntity<TmdbWatchProviderResponse> providerResponse = restTemplate.exchange(
                        providerUrl, HttpMethod.GET, new HttpEntity<>(headers), TmdbWatchProviderResponse.class);

                if (providerResponse.getBody().getResults().get("KR") != null) {
                    List<TmdbProviderDto> providers = providerResponse.getBody().getResults().get("KR").getFlatrate();
                    if (providers != null) {
                        for (TmdbProviderDto p : providers) {
                            if (p.getProviderName() == null) {
                                System.err.println("⚠️ provider_name is null for providerId: " + p.getProviderId());
                                continue;
                            }
                            if (!providersRepository.existsById(p.getProviderId())) {
                                Providers provider = new Providers();
                                provider.setProviderId(p.getProviderId());
                                provider.setName(p.getProviderName());
                                provider.setLogoPath(p.getLogoPath());
                                providersRepository.save(provider);
                            }
                            ContentProviders cp = new ContentProviders();
                            cp.setContentId(contentId);
                            cp.setProviderId(p.getProviderId());
                            contentProvidersRepository.save(cp);
                        }
                    }
                }
            }
        }
    }

    public void importPeopleAndCredits() {
        List<Contents> contentsList = contentsRepository.findAll();
        HttpHeaders headers = getHeaders();
        Set<String> savedMappings = new HashSet<>();

        for (Contents content : contentsList) {
            String url = tmdbProperties.getBaseUrl() + "/movie/" + content.getTmdbId() + "/credits";
            ResponseEntity<TmdbCreditsDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), TmdbCreditsDto.class);

            TmdbCreditsDto dto = response.getBody();
            if (dto == null) continue;

            for (TmdbCreditsDto.Cast cast : dto.getCast()) {
                if (cast.getId() == 0 || cast.getName() == null) continue;
                People person = peopleRepository.findByTmdbId(cast.getId()).orElseGet(() -> {
                    People p = new People();
                    p.setTmdbId(cast.getId());
                    p.setName(cast.getName());
                    p.setProfilePath(cast.getProfilePath());
                    p.setKnownForDepartment("Acting");
                    return peopleRepository.save(p);
                });
                String key = content.getContentId() + "-" + person.getPersonId() + "-actor";
                if (!savedMappings.contains(key)) {
                    ContentPeople cp = new ContentPeople();
                    cp.setContentId(content.getContentId());
                    cp.setPersonId(person.getPersonId());
                    cp.setRole("actor");
                    cp.setCharacterName(cast.getCharacter());
                    contentPeopleRepository.save(cp);
                    savedMappings.add(key);
                }
            }

            for (TmdbCreditsDto.Crew crew : dto.getCrew()) {
                if (!"Director".equalsIgnoreCase(crew.getJob())) continue;
                People director = peopleRepository.findByTmdbId(crew.getId()).orElseGet(() -> {
                    People p = new People();
                    p.setTmdbId(crew.getId());
                    p.setName(crew.getName());
                    p.setProfilePath(crew.getProfilePath());
                    p.setKnownForDepartment("Directing");
                    return peopleRepository.save(p);
                });
                String key = content.getContentId() + "-" + director.getPersonId() + "-director";
                if (!savedMappings.contains(key)) {
                    ContentPeople cp = new ContentPeople();
                    cp.setContentId(content.getContentId());
                    cp.setPersonId(director.getPersonId());
                    cp.setRole("director");
                    contentPeopleRepository.save(cp);
                    savedMappings.add(key);
                }
            }
        }
    }

}
