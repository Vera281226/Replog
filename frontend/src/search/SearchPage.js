// src/search/SearchPage.js

import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

import Header from '../components/Header';
import Footer from '../components/Footer';
import FabButton from '../components/FabButton';

import SearchBar from './components/SearchBar';
import SearchResultPoster from './components/SearchResultPoster';
import SearchResultList from './components/SearchResultList';

import './css/SearchPage.css';

/**
 * 🔍 SearchPage 컴포넌트
 * - 검색 결과 페이지 전체 구성
 * - URL: /search?keyword=...
 */
function SearchPage() {
    const location = useLocation(); // 현재 URL 정보
    const queryParams = new URLSearchParams(location.search);
    const keyword = queryParams.get('keyword'); // 쿼리 파라미터에서 키워드 추출

    const [results, setResults] = useState([]);    // 검색 결과
    const [loading, setLoading] = useState(true);  // 로딩 상태

    // 🔍 키워드 변경 시마다 API 요청
    useEffect(() => {
        if (!keyword) return;

        setLoading(true);
        axios
            .get(`/api/search?keyword=${encodeURIComponent(keyword)}`)
            .then((res) => setResults(res.data))
            .catch(() => setResults([]))
            .finally(() => setLoading(false));
    }, [keyword]);

    return (
        <div className="search-page-wrapper">
            {/* ✅ 공통 헤더 */}
            <Header />

            {/* ✅ 메인 콘텐츠 (검색창 + 결과) */}
            <main className="search-page-container">
                {/* 🔍 검색창 */}
                <SearchBar />

                {/* 🔄 검색 상태별 결과 출력 */}
                {loading ? (
                    <p className="search-loading">검색 중입니다...</p>
                ) : results.length === 0 ? (
                    <p className="search-loading">검색 결과가 없습니다.</p>
                ) : (
                    <>
                        {/* 🎬 연관 포스터 출력 */}
                        <SearchResultPoster results={results} />

                        {/* 📋 리스트 (영화/시리즈 분류) */}
                        <SearchResultList results={results} />
                    </>
                )}
            </main>

            {/* ✅ 공통 푸터 + FAB 버튼 */}
            <Footer />
            <FabButton />
        </div>
    );
}

export default SearchPage;
