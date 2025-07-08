// src/components/review/ReviewEditPopup.jsx
import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import api from '../../error/api/interceptor';
import StarSelector from './StarSelector';

function ReviewEditPopup() {
  const [params] = useSearchParams();
  const reviewId = params.get('reviewId');
  const memberId = params.get('memberId');

  const [cont, setCont] = useState('');
  const [rating, setRating] = useState(1);
  const [isSpoiler, setIsSpoiler] = useState(false);
  console.log('🔥 reviewId:', reviewId, 'rating:', rating);

  useEffect(() => {
  const r = Number(params.get('rating'));
  const decodedCont = decodeURIComponent(params.get('cont') || '');
  const spoiler = params.get('isSpoiler') === 'true';

  setCont(decodedCont);
  setRating(!isNaN(r) && r >= 1 && r <= 5 ? r : 1); // ✅ 기본값 1로 보정
  setIsSpoiler(spoiler);
}, [params]);


  if (!reviewId) {
  return <div className="text-center mt-10 text-gray-500">잘못된 접근입니다.</div>;
}


  const handleSubmit = async () => {
    if (rating < 1 || rating > 5) return alert('별점은 1~5 사이로 선택해주세요.');
    if (cont.trim() === '') return alert('내용을 입력해주세요.');

    try {
      await api.patch(`/reviews/${reviewId}`, {
        memberId,
        cont,
        rating,
        isSpoiler,
      });
      window.opener?.postMessage('reviewUpdated', '*');
      window.close();
    } catch {
      alert('리뷰 수정 실패');
    }
  };

  return (
    <div className="p-4">
      <div className="bg-white rounded shadow-lg w-full max-w-xl">
        <div className="bg-cyan-100 px-6 py-4 rounded-t">
          <h2 className="text-xl font-bold text-center">리뷰 수정</h2>
        </div>

        <div className="px-6 py-4">
          <table className="w-full text-sm">
            <tbody>
              <tr className="border-b">
                <td className="py-2 pr-4 font-semibold text-right w-1/4">작성자</td>
                <td className="py-2">{memberId}</td>
              </tr>
              <tr className="border-b">
                <td className="py-2 pr-4 font-semibold text-right align-top">내용</td>
                <td className="py-2">
                  <textarea
                    className="w-full border p-2 rounded resize-none"
                    rows={4}
                    value={cont}
                    onChange={(e) => setCont(e.target.value)}
                  />
                </td>
              </tr>
              <tr>
                <td className="py-2 pr-4 font-semibold text-right">별점</td>
                <td className="py-2">
                  <StarSelector rating={rating} onChange={setRating} />
                </td>
              </tr>
            </tbody>
          </table>

          <div className="mt-4 flex items-center gap-2">
            <input
              type="checkbox"
              checked={isSpoiler}
              onChange={(e) => setIsSpoiler(e.target.checked)}
            />
            <span className="text-sm">스포일러 포함</span>
          </div>

          <div className="mt-6 flex justify-center gap-4">
            <button
              onClick={handleSubmit}
              className="bg-black text-white px-5 py-2 rounded hover:bg-gray-800"
            >
              수정
            </button>
            <button
              onClick={() => window.close()}
              className="bg-gray-300 text-black px-5 py-2 rounded hover:bg-gray-400"
            >
              닫기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ReviewEditPopup;
