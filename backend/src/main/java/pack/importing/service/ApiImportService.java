package pack.importing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.transaction.Transactional;
import pack.config.TmdbProperties;
import pack.importing.dto.TmdbContentDto;
import pack.importing.dto.TmdbContentResponse;
import pack.importing.dto.TmdbCreditsDto;
import pack.importing.dto.TmdbGenreDto;
import pack.importing.dto.TmdbGenreResponse;
import pack.importing.dto.TmdbProviderDto;
import pack.importing.dto.TmdbProviderResponse;
import pack.importing.dto.TmdbReleaseDateDto;
import pack.importing.dto.TmdbTvContentRatingDto;
import pack.importing.dto.TmdbWatchProviderRegion;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        headers.set("Accept", "application/json");
        return headers;
    }

    // =====================================================================
    // /api/import/all 에서 사용되는 핵심 메서드들
    // =====================================================================

    /**
     * 장르 데이터 수집 (Movie + TV 통합)
     */
    public void importGenresFromTmdb() {
        // Movie 장르
        String movieUrl = tmdbProperties.getBaseUrl() + "/genre/movie/list?language=ko";
        ResponseEntity<TmdbGenreResponse> movieResponse = restTemplate.exchange(
            movieUrl, HttpMethod.GET, new HttpEntity<>(getHeaders()), TmdbGenreResponse.class);
        
        if (movieResponse.getBody() != null) {
            List<TmdbGenreDto> movieGenres = movieResponse.getBody().getGenres();
            for (TmdbGenreDto dto : movieGenres) {
                saveGenreIfNotExists(dto);
            }
        }
        
        // TV 장르
        String tvUrl = tmdbProperties.getBaseUrl() + "/genre/tv/list?language=ko";
        ResponseEntity<TmdbGenreResponse> tvResponse = restTemplate.exchange(
            tvUrl, HttpMethod.GET, new HttpEntity<>(getHeaders()), TmdbGenreResponse.class);
        
        if (tvResponse.getBody() != null) {
            List<TmdbGenreDto> tvGenres = tvResponse.getBody().getGenres();
            for (TmdbGenreDto dto : tvGenres) {
                saveGenreIfNotExists(dto);
            }
        }
    }

    /**
     * 플랫폼 데이터 수집
     */
    public void importProvidersFromTmdb() {
        String url = tmdbProperties.getBaseUrl() + "/watch/providers/movie?language=ko&watch_region=KR";
        ResponseEntity<TmdbProviderResponse> response = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(getHeaders()), TmdbProviderResponse.class);
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

    /**
     * 통합 콘텐츠 수집 (Movie + TV)
     */
    @Transactional
    public void importAllContentsFromTmdb(LocalDate startDate) {
        // Movie 콘텐츠 수집
        importContentsByMediaType("movie", startDate);
        // TV 콘텐츠 수집  
        importContentsByMediaType("tv", startDate);
    }

    /**
     * People 및 Credits 정보 수집 (Movie + TV 통합)
     */
    @Transactional
    public void importPeopleAndCredits() {
        List<Contents> contentsList = contentsRepository.findAll();
        HttpHeaders headers = getHeaders();
        Set<String> savedMappings = new HashSet<>();

        for (Contents content : contentsList) {
            try {
                // 미디어 타입에 따른 API 엔드포인트 분기
                String creditsUrl;
                if ("tv".equals(content.getMediaType())) {
                    creditsUrl = tmdbProperties.getBaseUrl() + "/tv/" + content.getTmdbId() + "/aggregate_credits";
                } else {
                    creditsUrl = tmdbProperties.getBaseUrl() + "/movie/" + content.getTmdbId() + "/credits";
                }
                
                ResponseEntity<TmdbCreditsDto> response = restTemplate.exchange(
                    creditsUrl, HttpMethod.GET, new HttpEntity<>(headers), TmdbCreditsDto.class);

                TmdbCreditsDto creditsDto = response.getBody();
                if (creditsDto == null) {
                    System.out.println("Credits 정보 없음: contentId=" + content.getContentId() + 
                        ", mediaType=" + content.getMediaType());
                    continue;
                }

                // TV/Movie에 따른 Credits 처리
                if ("tv".equals(content.getMediaType())) {
                    processAggregateCredits(content, creditsDto, savedMappings);
                } else {
                    processMovieCredits(content, creditsDto, savedMappings);
                }
                
            } catch (Exception e) {
                System.err.println("Credits 처리 실패: contentId=" + content.getContentId() + 
                    ", mediaType=" + content.getMediaType() + ", 오류: " + e.getMessage());
            }
        }
    }

    // =====================================================================
    // 내부 헬퍼 메서드들
    // =====================================================================

    private void saveGenreIfNotExists(TmdbGenreDto dto) {
        if (!genresRepository.existsById(dto.getId())) {
            Genres genre = new Genres();
            genre.setGenreId(dto.getId());
            genre.setName(dto.getName());
            genresRepository.save(genre);
            System.out.println("장르 저장: " + dto.getName() + " (ID: " + dto.getId() + ")");
        }
    }

    private void importContentsByMediaType(String mediaType, LocalDate startDate) {
        Set<Integer> processedTmdbIds = new HashSet<>();
        HttpHeaders headers = getHeaders();
        
        String dateField = "movie".equals(mediaType) ? "primary_release_date" : "first_air_date";
        
        for (int page = 1; page <= 10; page++) {
            String url = tmdbProperties.getBaseUrl() + "/discover/" + mediaType + "?language=ko"
                + "&sort_by=popularity.desc&include_adult=false"
                + "&with_watch_monetization_types=flatrate"
                + "&" + dateField + ".gte=" + startDate  
                + "&" + dateField + ".lte=" + LocalDate.now()
                + "&region=KR&page=" + page;

            try {
                ResponseEntity<TmdbContentResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), TmdbContentResponse.class);

                List<TmdbContentDto> results = Optional.ofNullable(response.getBody())
                    .map(TmdbContentResponse::getResults)
                    .orElse(Collections.emptyList());

                if (results.isEmpty()) break;

                for (TmdbContentDto dto : results) {
                    if (processedTmdbIds.contains(dto.getId()) ||
                        contentsRepository.findByTmdbId(dto.getId()).isPresent()) continue;

                    try {
                        TmdbContentDto detailedDto = getContentDetails(dto.getId(), mediaType, headers);
                        
                        if (!dto.getGenreIds().isEmpty()) {
                            detailedDto.setGenreIds(dto.getGenreIds());
                        }
                        
                        String certification = getContentCertification(dto.getId(), mediaType, headers);
                        detailedDto.setAgeRating(certification);
                        
                        Contents saved = saveUnifiedContent(detailedDto, mediaType);
                        processedTmdbIds.add(dto.getId());

                        saveContentGenres(saved.getContentId(), detailedDto);
                        saveContentProviders(saved.getContentId(), detailedDto, headers, mediaType);
                        
                    } catch (Exception e) {
                        System.err.println("콘텐츠 저장 실패: TMDB ID = " + dto.getId() + ", mediaType = " + mediaType);
                    }
                }
            } catch (Exception e) {
                System.err.println("페이지 " + page + " 처리 실패 (mediaType: " + mediaType + ")");
            }
        }
    }

    private TmdbContentDto getContentDetails(int contentId, String mediaType, HttpHeaders headers) {
        String detailUrl = tmdbProperties.getBaseUrl() + "/" + mediaType + "/" + contentId + "?language=ko";
        
        try {
            ResponseEntity<TmdbContentDto> response = restTemplate.exchange(
                detailUrl, HttpMethod.GET, new HttpEntity<>(headers), TmdbContentDto.class);
            
            return response.getBody() != null ? response.getBody() : new TmdbContentDto();
        } catch (Exception e) {
            TmdbContentDto emptyDto = new TmdbContentDto();
            emptyDto.setId(contentId);
            return emptyDto;
        }
    }

    private String getContentCertification(int contentId, String mediaType, HttpHeaders headers) {
        try {
            if ("movie".equals(mediaType)) {
                return getMovieCertification(contentId, headers);
            } else {
                return getTvCertification(contentId, headers);
            }
        } catch (Exception e) {
            return "미정";
        }
    }

    private Contents saveUnifiedContent(TmdbContentDto dto, String mediaType) {
        Contents content = new Contents();
        content.setTmdbId(dto.getId());
        content.setTitle(dto.getUnifiedTitle());
        content.setOverview(dto.getOverview());
        content.setPosterPath(dto.getPosterPath());
        content.setBackdropPath(dto.getBackdropPath());
        content.setRating((float) dto.getVoteAverage());
        content.setMediaType(mediaType);
        
        Integer unifiedRuntime = dto.getUnifiedRuntime();
        if (unifiedRuntime != null && unifiedRuntime > 0) {
            content.setRuntime(unifiedRuntime);
        }
        
        if (dto.getAgeRating() != null && !dto.getAgeRating().isEmpty()) {
            content.setAgeRating(dto.getAgeRating());
        }
        
        String unifiedReleaseDate = dto.getUnifiedReleaseDate();
        if (unifiedReleaseDate != null && !unifiedReleaseDate.isEmpty()) {
            try {
                content.setReleaseDate(LocalDate.parse(unifiedReleaseDate));
            } catch (Exception e) {
                System.err.println("날짜 파싱 실패: " + unifiedReleaseDate);
            }
        }
        
        return contentsRepository.save(content);
    }

    private void saveContentProviders(Integer contentId, TmdbContentDto dto, HttpHeaders headers, String mediaType) {
        try {
            String providerUrl = tmdbProperties.getBaseUrl() + "/" + mediaType + "/" + dto.getId() + "/watch/providers";
            
            ResponseEntity<TmdbWatchProviderResponse> providerResponse = restTemplate.exchange(
                providerUrl, HttpMethod.GET, new HttpEntity<>(headers), TmdbWatchProviderResponse.class);
            
            if (providerResponse.getBody() != null && 
                providerResponse.getBody().getResults().containsKey("KR")) {
                
                TmdbWatchProviderRegion krRegion = providerResponse.getBody().getResults().get("KR");
                List<TmdbProviderDto> providers = Optional.ofNullable(krRegion.getFlatrate())
                    .orElse(Collections.emptyList());

                for (TmdbProviderDto provider : providers) {
                    if (provider.getProviderName() == null || provider.getProviderName().isEmpty()) continue;
                    
                    if (!providersRepository.existsById(provider.getProviderId())) {
                        Providers newProvider = new Providers();
                        newProvider.setProviderId(provider.getProviderId());
                        newProvider.setName(provider.getProviderName());
                        newProvider.setLogoPath(provider.getLogoPath());
                        providersRepository.save(newProvider);
                    }

                    if (!contentProvidersRepository.existsByContentIdAndProviderId(
                        contentId, provider.getProviderId())) {
                        ContentProviders mapping = new ContentProviders();
                        mapping.setContentId(contentId);
                        mapping.setProviderId(provider.getProviderId());
                        contentProvidersRepository.save(mapping);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("플랫폼 정보 수집 실패: contentId=" + contentId + ", mediaType=" + mediaType);
        }
    }

    @Transactional
    private void saveContentGenres(Integer contentId, TmdbContentDto dto) {
        List<Integer> allGenreIds = dto.getAllGenreIds();
        
        System.out.println("=== 장르 매핑 시작 ===");
        System.out.println("컨텐츠 ID: " + contentId);
        System.out.println("TMDB ID: " + dto.getId());
        System.out.println("장르 ID 목록: " + allGenreIds);
        System.out.println("장르 ID 개수: " + allGenreIds.size());
        
        if (allGenreIds.isEmpty()) {
            System.out.println("경고: 장르 ID가 없습니다. TMDB 응답을 확인하세요.");
            return;
        }
        
        for (Integer genreId : allGenreIds) {
            try {
                boolean genreExists = genresRepository.existsById(genreId);
                System.out.println("장르 ID " + genreId + " 존재 여부: " + genreExists);
                
                if (genreExists) {
                    boolean mappingExists = contentGenresRepository.existsByContentIdAndGenreId(contentId, genreId);
                    System.out.println("매핑 존재 여부 (contentId=" + contentId + ", genreId=" + genreId + "): " + mappingExists);
                    
                    if (!mappingExists) {
                        ContentGenres mapping = new ContentGenres();
                        mapping.setContentId(contentId);
                        mapping.setGenreId(genreId);
                        
                        ContentGenres saved = contentGenresRepository.save(mapping);
                        System.out.println("✅ 장르 매핑 저장 성공: " + saved.getContentId() + " - " + saved.getGenreId());
                    } else {
                        System.out.println("⚠️ 이미 존재하는 매핑: contentId=" + contentId + ", genreId=" + genreId);
                    }
                } else {
                    System.out.println("❌ 존재하지 않는 장르 ID: " + genreId);
                }
            } catch (Exception e) {
                System.err.println("❌ 장르 매핑 저장 실패: contentId=" + contentId + ", genreId=" + genreId);
                System.err.println("오류 메시지: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("=== 장르 매핑 완료 ===\n");
    }

    // Movie Certification 처리
    private String getMovieCertification(int movieId, HttpHeaders headers) {
        String releaseDateUrl = tmdbProperties.getBaseUrl() + "/movie/" + movieId + "/release_dates";
        
        try {
            ResponseEntity<TmdbReleaseDateDto> response = restTemplate.exchange(
                releaseDateUrl, HttpMethod.GET, new HttpEntity<>(headers), TmdbReleaseDateDto.class);
            
            TmdbReleaseDateDto releaseDates = response.getBody();
            if (releaseDates != null && releaseDates.getResults() != null) {
                String krCertification = extractCertificationByCountry(releaseDates, "KR");
                if (krCertification != null && !krCertification.isEmpty()) {
                    return krCertification;
                }
                
                String usCertification = extractCertificationByCountry(releaseDates, "US");
                if (usCertification != null && !usCertification.isEmpty()) {
                    return usCertification;
                }
                
                for (TmdbReleaseDateDto.TmdbReleaseDateResult result : releaseDates.getResults()) {
                    if (result.getReleaseDates() != null && !result.getReleaseDates().isEmpty()) {
                        String firstCertification = result.getReleaseDates().get(0).getCertification();
                        if (firstCertification != null && !firstCertification.isEmpty()) {
                            return firstCertification;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        
        return "미정";
    }

    private String extractCertificationByCountry(TmdbReleaseDateDto releaseDates, String countryCode) {
        return releaseDates.getResults().stream()
            .filter(result -> countryCode.equals(result.getCountryCode()))
            .flatMap(result -> result.getReleaseDates().stream())
            .filter(release -> release.getCertification() != null && !release.getCertification().isEmpty())
            .map(TmdbReleaseDateDto.TmdbReleaseInfo::getCertification)
            .findFirst()
            .orElse(null);
    }

    // TV Certification 처리
    private String getTvCertification(int tvId, HttpHeaders headers) {
        String url = tmdbProperties.getBaseUrl() + "/tv/" + tvId + "/content_ratings";
        try {
            ResponseEntity<TmdbTvContentRatingDto> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), TmdbTvContentRatingDto.class);
            TmdbTvContentRatingDto ratings = response.getBody();
            if (ratings != null && ratings.getResults() != null) {
                String kr = ratings.getCertificationByCountry("KR");
                if (kr != null) return kr;
                String us = ratings.getCertificationByCountry("US");
                if (us != null) return us;
                return ratings.getFirstCertification();
            }
        } catch (Exception e) {}
        return "미정";
    }

    // People Credits 처리
    private void processAggregateCredits(Contents content, TmdbCreditsDto creditsDto, Set<String> savedMappings) {
        List<TmdbCreditsDto.Cast> castList = Optional.ofNullable(creditsDto.getCast())
            .orElse(Collections.emptyList());
            
        for (TmdbCreditsDto.Cast cast : castList) {
            if (cast.getId() == 0 || cast.getName() == null || cast.getName().isEmpty()) {
                continue;
            }

            try {
                People person = saveOrGetPerson(cast.getId(), cast.getName(), 
                    cast.getProfilePath(), "Acting");
                
                String characterName = extractTvCharacterName(cast);
                
                String mappingKey = content.getContentId() + "-" + person.getPersonId() + "-actor";
                if (!savedMappings.contains(mappingKey)) {
                    ContentPeople cp = new ContentPeople();
                    cp.setContentId(content.getContentId());
                    cp.setPersonId(person.getPersonId());
                    cp.setRole("actor");
                    cp.setCharacterName(characterName);
                    
                    contentPeopleRepository.save(cp);
                    savedMappings.add(mappingKey);
                    System.out.println("✅ TV 출연진 매핑 저장: " + person.getName() + " - " + content.getTitle());
                }
            } catch (Exception e) {
                System.err.println("TV Cast 저장 실패: " + e.getMessage());
            }
        }

        List<TmdbCreditsDto.Crew> crewList = Optional.ofNullable(creditsDto.getCrew())
            .orElse(Collections.emptyList());
            
        for (TmdbCreditsDto.Crew crew : crewList) {
            if (!"Executive Producer".equalsIgnoreCase(crew.getJob()) && 
                !"Creator".equalsIgnoreCase(crew.getJob()) ||
                crew.getId() == 0 || crew.getName() == null) {
                continue;
            }

            try {
                People producer = saveOrGetPerson(crew.getId(), crew.getName(), 
                    crew.getProfilePath(), crew.getDepartment());
                
                String mappingKey = content.getContentId() + "-" + producer.getPersonId() + "-producer";
                if (!savedMappings.contains(mappingKey)) {
                    ContentPeople cp = new ContentPeople();
                    cp.setContentId(content.getContentId());
                    cp.setPersonId(producer.getPersonId());
                    cp.setRole("producer");
                    
                    contentPeopleRepository.save(cp);
                    savedMappings.add(mappingKey);
                }
            } catch (Exception e) {
                System.err.println("TV Crew 저장 실패: " + e.getMessage());
            }
        }
    }

    private void processMovieCredits(Contents content, TmdbCreditsDto creditsDto, Set<String> savedMappings) {
        List<TmdbCreditsDto.Cast> castList = Optional.ofNullable(creditsDto.getCast())
            .orElse(Collections.emptyList());
            
        for (TmdbCreditsDto.Cast cast : castList) {
            if (cast.getId() == 0 || cast.getName() == null || cast.getName().isEmpty()) {
                continue;
            }

            try {
                People person = saveOrGetPerson(cast.getId(), cast.getName(), 
                    cast.getProfilePath(), "Acting");
                
                String mappingKey = content.getContentId() + "-" + person.getPersonId() + "-actor";
                if (!savedMappings.contains(mappingKey)) {
                    ContentPeople cp = new ContentPeople();
                    cp.setContentId(content.getContentId());
                    cp.setPersonId(person.getPersonId());
                    cp.setRole("actor");
                    cp.setCharacterName(cast.getCharacter());
                    
                    contentPeopleRepository.save(cp);
                    savedMappings.add(mappingKey);
                }
            } catch (Exception e) {
                System.err.println("Movie Cast 저장 실패: " + e.getMessage());
            }
        }

        List<TmdbCreditsDto.Crew> crewList = Optional.ofNullable(creditsDto.getCrew())
            .orElse(Collections.emptyList());
            
        for (TmdbCreditsDto.Crew crew : crewList) {
            if (!"Director".equalsIgnoreCase(crew.getJob()) || 
                crew.getId() == 0 || crew.getName() == null) {
                continue;
            }

            try {
                People director = saveOrGetPerson(crew.getId(), crew.getName(), 
                    crew.getProfilePath(), "Directing");
                
                String mappingKey = content.getContentId() + "-" + director.getPersonId() + "-director";
                if (!savedMappings.contains(mappingKey)) {
                    ContentPeople cp = new ContentPeople();
                    cp.setContentId(content.getContentId());
                    cp.setPersonId(director.getPersonId());
                    cp.setRole("director");
                    
                    contentPeopleRepository.save(cp);
                    savedMappings.add(mappingKey);
                }
            } catch (Exception e) {
                System.err.println("Movie Director 저장 실패: " + e.getMessage());
            }
        }
    }

    private String extractTvCharacterName(TmdbCreditsDto.Cast cast) {
        if (cast.getCharacter() != null && !cast.getCharacter().isEmpty()) {
            return cast.getCharacter();
        }
        return "Unknown Character";
    }

    private People saveOrGetPerson(int tmdbId, String name, String profilePath, String department) {
        return peopleRepository.findByTmdbId(tmdbId).orElseGet(() -> {
            People person = new People();
            person.setTmdbId(tmdbId);
            person.setName(name);
            person.setProfilePath(profilePath);
            person.setKnownForDepartment(department);
            return peopleRepository.save(person);
        });
    }
}
