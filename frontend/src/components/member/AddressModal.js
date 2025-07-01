import React, { useState, useEffect, useCallback, useRef } from 'react';
import ReactDOM from 'react-dom';
// import './css/AddressModal.css';

const AddressModal = ({ isOpen, onClose, onSelectAddress }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [addressData, setAddressData] = useState([]);
  const [error, setError] = useState('');
  const isDataLoaded = useRef(false);
  const searchInputRef = useRef(null); // modalRef 제거

  // 데이터 로드
  useEffect(() => {
    if (!isOpen || isDataLoaded.current) return;

    const parseTxtData = async () => {
      setIsLoading(true);
      setError('');
      
      try {
        const response = await fetch('/rnaddrkor_seoul.txt');
        if (!response.ok) throw new Error('파일 로드 실패');
        
        const txtData = await response.text();
        if (!txtData) throw new Error('빈 파일입니다');

        const lines = txtData.split('\n').filter(line => line.trim());
        const parsedData = lines.map(line => {
          const columns = line.split('|');
          if (columns.length < 22) return null;
          
          return {
            시도: columns[2]?.trim() || '',
            시군구: columns[3]?.trim() || '',
            읍면동: columns[4]?.trim() || '',
            도로명: columns[10]?.trim() || '',
            건물본번: columns[12]?.trim() || '',
            건물부번: columns[13]?.trim() || '',
            건물명: columns[21]?.trim() || ''
          };
        }).filter(Boolean);

        setAddressData(parsedData);
        isDataLoaded.current = true;
      } catch (error) {
        setError(error.message);
      } finally {
        setIsLoading(false);
      }
    };

    parseTxtData();
  }, [isOpen]);

  // 검색 핸들러
  const handleSearch = useCallback(() => {
    if (!searchTerm.trim()) {
      setSearchResults([]);
      return;
    }

    const searchLower = searchTerm.toLowerCase();
    const results = addressData
      .filter(item => {
        const fullAddress = `${item.시도} ${item.시군구} ${item.읍면동} ${item.도로명} ${item.건물본번}${item.건물부번 ? '-' + item.건물부번 : ''}`;
        return fullAddress.toLowerCase().includes(searchLower);
      })
      .slice(0, 50);

    setSearchResults(results);
  }, [searchTerm, addressData]);

  // 디바운싱
  useEffect(() => {
    if (!isOpen) return;
    const timer = setTimeout(handleSearch, 300);
    return () => clearTimeout(timer);
  }, [searchTerm, isOpen, handleSearch]);

  // Escape 키로 모달 닫기
  useEffect(() => {
    const handleEscape = (e) => {
      if (e.key === 'Escape') onClose();
    };

    if (isOpen) document.addEventListener('keydown', handleEscape);
    return () => document.removeEventListener('keydown', handleEscape);
  }, [isOpen, onClose]);

  // 모달이 열릴 때 검색 필드에 포커스
  useEffect(() => {
    if (isOpen && searchInputRef.current) {
      searchInputRef.current.focus();
    }
  }, [isOpen]);

  if (!isOpen) return null;

  return ReactDOM.createPortal(
    <div className="address-modal__overlay">
      <div className="address-modal">
        {/* 헤더 */}
        <div className="address-modal__header">
          <h2>주소 검색</h2>
        </div>

        {/* 바디 */}
        <div className="address-modal__body">
          {/* 검색 입력 필드 */}
          <div className="address-modal__search-container">
            <input
              ref={searchInputRef}
              type="text"
              className="address-modal__search-input"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="도로명, 건물명 또는 지번을 입력하세요"
            />
          </div>

          {/* 결과 영역 */}
          <div className="address-modal__results">
            {isLoading ? (
              <div className="address-modal__status-message">
                <p>주소 데이터를 불러오는 중...</p>
              </div>
            ) : error ? (
              <div className="address-modal__status-message address-modal__error-message">
                <p>데이터 로드 실패: {error}</p>
              </div>
            ) : searchResults.length > 0 ? (
              <ul>
                {searchResults.map((item, index) => {
                  const fullAddress = `${item.시도} ${item.시군구} ${item.읍면동} ${item.도로명} ${item.건물본번}${item.건물부번 ? '-' + item.건물부번 : ''}`;
                  
                  return (
                    <li 
                      key={index}
                      className="address-modal__result-item"
                      onClick={() => onSelectAddress(fullAddress)}
                      tabIndex={0}
                    >
                      {fullAddress}
                      {item.건물명 && (
                        <span className="address-modal__building-name">({item.건물명})</span>
                      )}
                    </li>
                  );
                })}
              </ul>
            ) : (
              <div className="address-modal__empty-message">
                {searchTerm ? '검색 결과가 없습니다' : '주소를 검색해주세요'}
              </div>
            )}
          </div>
        </div>

        {/* 푸터 */}
        <div className="address-modal__footer">
          <button 
            className="address-modal__btn address-modal__btn--close"
            onClick={onClose}
          >
            닫기
          </button>
        </div>
      </div>
    </div>,
    document.body
  );
};

export default AddressModal;