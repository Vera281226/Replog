import React, { useState, useEffect } from 'react';

// ✅ 공통 레이아웃 컴포넌트
import Header from '../components/Header';
import Footer from '../components/Footer';
import FabButton from '../components/FabButton';

// ✅ 콘텐츠 전용 컴포넌트
import ContentsFilterBox from './components/ContentsFilterBox';
import ContentsGrid from './components/ContentsGrid';

// ✅ CSS: 콘텐츠 전용 스타일만 적용 (index.css는 사용 안 함)
import './css/ContentsPage.css';

/* ContentsPage
 *
 * ○ OTT 콘텐츠 전용 페이지입니다.
 * ○ 필터를 기반으로 콘텐츠를 조회하며, 그리드 및 더보기 기능을 포함합니다.
 */
function ContentsPage() {
    // ✅ 필터 상태 (장르, 플랫폼 등)
    const [filters, setFilters] = useState({});

    // ✅ 전체 콘텐츠 목록
    const [contents, setContents] = useState([]);

    // ✅ 현재 보여주는 콘텐츠 개수 (기본: 25개)
    const [visibleCount, setVisibleCount] = useState(25);

    // ✅ 로딩 상태 표시
    const [isLoading, setIsLoading] = useState(false);

    /* ✅ 필터 변경 시 API 호출 */
    useEffect(() => {
        const fetchContents = async () => {
            try {
                setIsLoading(true);
                const query = createQueryString(filters);  // ✅ 커스텀 쿼리 생성
                const res = await fetch(`/api/contents?${query}`);
                const data = await res.json();
                setContents(data);
                setVisibleCount(25); // 검색 시 초기화
            } catch (err) {
                console.error('콘텐츠 로딩 실패:', err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchContents();
    }, [filters]);

    /* ✅ '더 보기' 버튼 클릭 시 개수 증가 */
    const handleLoadMore = () => {
        setVisibleCount((prev) => prev + 10);
    };

    /* ✅ 쿼리 파라미터 문자열 생성 (배열도 대응) */
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

    return (
        <>
            <Header />

            <main className="contents-page">
                {/* ✅ 안내 메시지 영역 (검색창 자리 대체) */}
                <div className="searchbar-replacement">
                    <div className="platform-notice">
                        쿠팡플레이 / 웨이브 / 티빙은 추후 업데이트 예정입니다.
                    </div>
                </div>

                {/* ✅ 본문 레이아웃 */}
                <div className="contents-container">
                    {/* ✅ 필터 사이드바 */}
                    <aside>
                        <ContentsFilterBox onFilterChange={setFilters} />
                    </aside>

                    {/* ✅ 콘텐츠 카드 그리드 영역 */}
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

            <Footer />
            <FabButton />
        </>
    );
}

export default ContentsPage;
