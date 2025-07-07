package pack.importing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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
                        // 1단계: 컨텐츠 저장
                        Contents saved = saveContent(dto);
                        processedTmdbIds.add(dto.getId());

                        // 2단계: 안전한 장르 매핑 저장
                        saveContentGenres(saved.getContentId(), dto);
                        
                        // 3단계: 안전한 플랫폼 매핑 저장
                        saveContentProviders(saved.getContentId(), dto, headers);
                        
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
    }
	
	private Contents saveContent(TmdbContentDto dto) {
        Contents content = new Contents();
        content.setTmdbId(dto.getId());
        content.setTitle(dto.getTitle());
        content.setOverview(dto.getOverview());
        content.setPosterPath(dto.getPosterPath());
        content.setBackdropPath(dto.getBackdropPath());
        content.setRating((float) dto.getVoteAverage());
        content.setMediaType("movie");
        
        // 안전한 날짜 파싱
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
