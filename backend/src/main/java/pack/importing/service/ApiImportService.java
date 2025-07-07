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
        headers.set("Accept", "application/json");
        return headers;
    }

	
	@Transactional
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

            try {
                ResponseEntity<TmdbContentResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), TmdbContentResponse.class);

                List<TmdbContentDto> results = Optional.ofNullable(response.getBody())
                    .map(TmdbContentResponse::getResults)
                    .orElse(Collections.emptyList());

                if (results.isEmpty()) {
                    break;
                }

                for (TmdbContentDto dto : results) {
                    if (processedTmdbIds.contains(dto.getId()) ||
                        contentsRepository.findByTmdbId(dto.getId()).isPresent()) {
                        continue;
                    }

                    try {
                        // 1단계: 상세 정보 수집 (Runtime 포함)
                        TmdbContentDto detailedDto = getMovieDetails(dto.getId(), headers);
                        
                        // 2단계: Age Rating 수집
                        String certification = getMovieCertification(dto.getId(), headers);
                        detailedDto.setAgeRating(certification);
                        
                        // 3단계: 컨텐츠 저장 (Runtime 및 Age Rating 포함)
                        Contents saved = saveContentWithRuntimeAndCertification(detailedDto);
                        processedTmdbIds.add(dto.getId());

                        // 4단계: 기존 매핑 로직 유지
                        saveContentGenres(saved.getContentId(), detailedDto);
                        saveContentProviders(saved.getContentId(), detailedDto, headers);
                        
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
    }
	
	private String getMovieCertification(int movieId, HttpHeaders headers) {
        String releaseDateUrl = tmdbProperties.getBaseUrl() + "/movie/" + movieId + "/release_dates";
        
        try {
            ResponseEntity<TmdbReleaseDateDto> response = restTemplate.exchange(
                releaseDateUrl, HttpMethod.GET, new HttpEntity<>(headers), TmdbReleaseDateDto.class);
            
            TmdbReleaseDateDto releaseDates = response.getBody();
            if (releaseDates != null && releaseDates.getResults() != null) {
                
                // 1순위: 한국(KR) 인증 정보
                String krCertification = extractCertificationByCountry(releaseDates, "KR");
                if (krCertification != null && !krCertification.isEmpty()) {
                    return krCertification;
                }
                
                // 2순위: 미국(US) 인증 정보 (MPAA 등급)
                String usCertification = extractCertificationByCountry(releaseDates, "US");
                if (usCertification != null && !usCertification.isEmpty()) {
                    return usCertification;
                }
                
                // 3순위: 첫 번째로 발견되는 인증 정보
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
        
        return "미정"; // 기본값
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


	private TmdbContentDto getMovieDetails(int movieId, HttpHeaders headers) {
        String detailUrl = tmdbProperties.getBaseUrl() + "/movie/" + movieId + "?language=ko";
        
        try {
            ResponseEntity<TmdbContentDto> response = restTemplate.exchange(
                detailUrl, HttpMethod.GET, new HttpEntity<>(headers), TmdbContentDto.class);
            
            TmdbContentDto details = response.getBody();
            if (details != null) {
                return details;
            }
        } catch (Exception e) {
        }
        
        // 기본값 반환
        TmdbContentDto emptyDto = new TmdbContentDto();
        emptyDto.setId(movieId);
        return emptyDto;
    }


	private Contents saveContentWithRuntimeAndCertification(TmdbContentDto dto) {
        Contents content = new Contents();
        content.setTmdbId(dto.getId());
        content.setTitle(dto.getTitle());
        content.setOverview(dto.getOverview());
        content.setPosterPath(dto.getPosterPath());
        content.setBackdropPath(dto.getBackdropPath());
        content.setRating((float) dto.getVoteAverage());
        content.setMediaType("movie");
        
        //  Runtime 설정
        if (dto.getRuntime() != null && dto.getRuntime() > 0) {
            content.setRuntime(dto.getRuntime());
        }
        
        //  Age Rating 설정
        if (dto.getAgeRating() != null && !dto.getAgeRating().isEmpty()) {
            content.setAgeRating(dto.getAgeRating());
        }
        
        // 기존 날짜 파싱 로직 유지
        if (dto.getReleaseDate() != null && !dto.getReleaseDate().isEmpty()) {
            try {
                content.setReleaseDate(LocalDate.parse(dto.getReleaseDate()));
            } catch (Exception e) {
            }
        }
        
        return contentsRepository.save(content);
    }
	
	@Transactional
    private void saveContentProviders(Integer contentId, TmdbContentDto dto, HttpHeaders headers) {
        try {
            String providerUrl = tmdbProperties.getBaseUrl() + "/movie/" + dto.getId() + "/watch/providers";
            
            ResponseEntity<TmdbWatchProviderResponse> providerResponse = restTemplate.exchange(
                providerUrl, HttpMethod.GET, new HttpEntity<>(headers), TmdbWatchProviderResponse.class);

            if (providerResponse.getBody() != null && 
                providerResponse.getBody().getResults().containsKey("KR")) {
                
                TmdbWatchProviderRegion krRegion = providerResponse.getBody().getResults().get("KR");
                List<TmdbProviderDto> providers = Optional.ofNullable(krRegion.getFlatrate())
                    .orElse(Collections.emptyList());

                for (TmdbProviderDto provider : providers) {
                    try {
                        // Provider 이름 검증
                        if (provider.getProviderName() == null || provider.getProviderName().isEmpty()) {
                            continue;
                        }

                        // Provider 존재 여부 확인 및 저장
                        if (!providersRepository.existsById(provider.getProviderId())) {
                            Providers newProvider = new Providers();
                            newProvider.setProviderId(provider.getProviderId());
                            newProvider.setName(provider.getProviderName());
                            newProvider.setLogoPath(provider.getLogoPath());
                            providersRepository.save(newProvider);
                        }

                        // 중복 매핑 방지
                        if (!contentProvidersRepository.existsByContentIdAndProviderId(
                            contentId, provider.getProviderId())) {
                            
                            ContentProviders mapping = new ContentProviders();
                            mapping.setContentId(contentId);
                            mapping.setProviderId(provider.getProviderId());
                            
                            contentProvidersRepository.save(mapping);
                        }
                        
                    } catch (Exception e) {
                    }
                }
            } else {
            }
            
        } catch (Exception e) {
        }
    }
	
	 @Transactional
	    private void saveContentGenres(Integer contentId, TmdbContentDto dto) {
	        // Null-safe 장르 ID 처리
	        List<Integer> genreIds = Optional.ofNullable(dto.getGenreIds())
	            .orElse(Collections.emptyList());


	        for (Integer genreId : genreIds) {
	            try {
	                // 장르 존재 여부 확인
	                if (genresRepository.existsById(genreId)) {
	                    // 중복 매핑 방지
	                    if (!contentGenresRepository.existsByContentIdAndGenreId(contentId, genreId)) {
	                        ContentGenres mapping = new ContentGenres();
	                        mapping.setContentId(contentId);
	                        mapping.setGenreId(genreId);
	                        
	                        contentGenresRepository.save(mapping);
	                    } else {
	                    }
	                } else {
	                }
	            } catch (Exception e) {
	            }
	        }
	    }

	public void importGenresFromTmdb() {
		String url = tmdbProperties.getBaseUrl() + "/genre/movie/list?language=ko";
		ResponseEntity<TmdbGenreResponse> response = restTemplate.exchange(url, HttpMethod.GET,
				new HttpEntity<>(getHeaders()), TmdbGenreResponse.class);
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
		ResponseEntity<TmdbProviderResponse> response = restTemplate.exchange(url, HttpMethod.GET,
				new HttpEntity<>(getHeaders()), TmdbProviderResponse.class);
		List<TmdbProviderDto> providers = response.getBody().getResults();

		for (TmdbProviderDto p : providers) {
			if (!providersRepository.existsById(p.getProviderId())) {
				if (p.getProviderName() == null)
					continue;
				Providers provider = new Providers();
				provider.setProviderId(p.getProviderId());
				provider.setName(p.getProviderName());
				provider.setLogoPath(p.getLogoPath());
				providersRepository.save(provider);
			}
		}
	}


	 @Transactional
	    public void importPeopleAndCredits() {
	        List<Contents> contentsList = contentsRepository.findAll();
	        HttpHeaders headers = getHeaders();
	        Set<String> savedMappings = new HashSet<>();

	        for (Contents content : contentsList) {
	            try {
	                String url = tmdbProperties.getBaseUrl() + "/movie/" + content.getTmdbId() + "/credits";
	                
	                ResponseEntity<TmdbCreditsDto> response = restTemplate.exchange(
	                    url, HttpMethod.GET, new HttpEntity<>(headers), TmdbCreditsDto.class);

	                TmdbCreditsDto creditsDto = response.getBody();
	                if (creditsDto == null) {
	                    continue;
	                }

	                // Cast 처리
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
	                    }
	                }

	                // Crew 처리 (감독만)
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
	                    }
	                }
	                
	            } catch (Exception e) {
	            }
	        }
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
