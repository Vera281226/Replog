package pack.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import pack.search.dto.SearchResponse;
import pack.search.repository.SearchRepository;

import java.util.List;

/**
 * SearchService 클래스
 * - 콘텐츠 제목 기반 검색 비즈니스 로직 처리
 * - Repository로부터 검색 결과를 받아 클라이언트에 전달
 */
@Service
@RequiredArgsConstructor
public class SearchService {

    /** SearchRepository 의존성 주입 */
    private final SearchRepository searchRepository;

    /**
     * 키워드를 포함한 콘텐츠 제목 검색
     * @param keyword 검색 키워드
     * @return 검색 결과 리스트
     */
    public List<SearchResponse> searchContentsByKeyword(String keyword) {
        return searchRepository.searchByTitleIgnoreSpaces(keyword);
    }
}
