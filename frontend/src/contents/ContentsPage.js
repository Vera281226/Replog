// src/contents/ContentsPage.js

import React, { useState, useEffect } from 'react';
import axios from '../error/api/interceptor';  // ✅ axios 인스턴스

// ✅ 콘텐츠 전용 컴포넌트
import ContentsFilterBox from './components/ContentsFilterBox';
import ContentsGrid from './components/ContentsGrid';

// ✅ CSS
import './css/ContentsPage.css';

/* ================================================================================
 * 📄 ContentsPage.js
 * -------------------------------------------------------------------------------
 * ✅ OTT 콘텐츠 목록 필터 + 조회 + 무한스크롤
 * ✅ 매 새로고침마다 콘텐츠 순서 프론트에서 랜덤 셔플
 * ✅ 안내 문구는 상단 배너 이미지 위에 오버레이로 표시
 * ================================================================================
 */
function ContentsPage() {
    // ---------------------------------------------------------------------------
    // ✅ 상태값 정의
    // ---------------------------------------------------------------------------
    const [filters, setFilters] = useState({});     // 🔍 필터 조건들
    const [contents, setContents] = useState([]);   // 🎬 전체 콘텐츠 목록
    const [visibleCount, setVisibleCount] = useState(24); // 🔢 출력 개수
    const [isLoading, setIsLoading] = useState(false);     // ⏳ 로딩 상태

    // ---------------------------------------------------------------------------
    // ✅ 유틸: 배열 셔플 함수 (Fisher–Yates)
    // ---------------------------------------------------------------------------
    const shuffleArray = (arr) => {
        const shuffled = [...arr];
        for (let i = shuffled.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
        }
        return shuffled;
    };

    // ---------------------------------------------------------------------------
    // ✅ 필터 변경 시 API 호출 + 프론트에서 랜덤 셔플
    // ---------------------------------------------------------------------------
    useEffect(() => {
        const fetchContents = async () => {
            try {
                setIsLoading(true);
                const query = createQueryString(filters);
                const res = await axios.get(`/contents?${query}`);
                const shuffled = shuffleArray(res.data); // ✅ 여기서 셔플
                setContents(shuffled);
                setVisibleCount(24);
            } catch (err) {
                console.error('콘텐츠 불러오기 실패:', err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchContents();
    }, [filters]);

    // ---------------------------------------------------------------------------
    // ✅ 더보기 버튼 클릭 → 12개씩 추가 출력
    // ---------------------------------------------------------------------------
    const handleLoadMore = () => {
        setVisibleCount((prev) => prev + 12);
    };

    // ---------------------------------------------------------------------------
    // ✅ 필터 객체 → 쿼리 문자열 변환
    // ---------------------------------------------------------------------------
    const createQueryString = (filters) => {
        const params = new URLSearchParams();
        Object.entries(filters).forEach(([key, value]) => {
            if (Array.isArray(value)) {
                value.forEach((v) => params.append(key, v));
            } else if (value !== '') {
                params.append(key, value);
            }
        });
        return params.toString();
    };

    // ---------------------------------------------------------------------------
    // ✅ 렌더링
    // ---------------------------------------------------------------------------
    return (
        <main className="contents-page">
            {/* ✅ 상단 안내 배너 이미지 + 텍스트 오버레이 */}
            <div className="contents-warning-hero">
                <img
                    src="/images/contents.jpg"
                    alt="콘텐츠 안내 배너"
                    className="contents-warning-background"
                />
                <div className="contents-warning-overlay">
                    <div className="contents-warning-text">
                        죄송합니다! 디즈니, 넷플릭스를 제외한<br />
                        쿠팡플레이 / 웨이브 / 티빙은 추후 업데이트 예정입니다.
                    </div>
                </div>
            </div>

            {/* ✅ 콘텐츠 전체 레이아웃 (필터 + 카드) */}
            <div className="contents-container">
                {/* 🎛 필터 박스 */}
                <aside>
                    <ContentsFilterBox onFilterChange={setFilters} />
                </aside>

                {/* 🎬 콘텐츠 카드 출력 */}
                <section className="contents-grid-section">
                    {isLoading && (
                        <div className="loading-message">콘텐츠를 불러오는 중입니다...</div>
                    )}
                    {!isLoading && contents.length === 0 && (
                        <div className="no-results">검색 결과가 없습니다.</div>
                    )}
                    {!isLoading && contents.length > 0 && (
                        <>
                            <ContentsGrid contents={contents.slice(0, visibleCount)} />
                            <div className="load-more-wrapper">
                                <button
                                    className="load-more-button"
                                    onClick={handleLoadMore}
                                    disabled={visibleCount >= contents.length}
                                >
                                    더 보기
                                </button>
                            </div>
                        </>
                    )}
                </section>
            </div>
        </main>
    );
}

export default ContentsPage;
