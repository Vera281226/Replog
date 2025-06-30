import React, { useState } from 'react';
import api from '../../api/axios';
import StarSelector from './StarSelector';

function ReviewModal({ onClose, onReviewCreated, contentId, memberId }) {
  const [rating, setRating] = useState(0);
  const [cont, setCont] = useState('');
  const [isSpoiler, setIsSpoiler] = useState(false);

  const handleSubmit = async () => {
    if (rating < 1 || rating > 5) return alert('별점은 1~5 사이로 선택해주세요.');
    if (cont.trim() === '') return alert('내용을 입력해주세요.');

    try {
      await api.post('/reviews', {
        contentId,
        memberId,
        rating,
        cont,
        isSpoiler,
      });
      onReviewCreated();
      onClose();
    } catch (err) {
      alert('등록 실패');
    }
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-30 z-50">
      <div className="bg-white rounded shadow-lg w-full max-w-xl">
        <div className="bg-cyan-100 px-6 py-4 rounded-t">
          <h2 className="text-xl font-bold text-center">리뷰 쓰기</h2>
        </div>

        <div className="px-6 py-4">
          <table className="w-full text-sm">
            <tbody>
              <tr className="border-b">
                <td className="py-2 pr-4 font-semibold text-right w-1/4">작성자</td>
                <td className="py-2">{memberId}</td>
              </tr>
              <tr className="border-b align-top">
                <td className="py-2 pr-4 font-semibold text-right">내용</td>
                <td className="py-2">
                  <textarea
                    className="w-full border p-2 rounded resize-none"
                    rows={4}
                    value={cont}
                    onChange={(e) => setCont(e.target.value)}
                    placeholder="리뷰를 입력하세요"
                  />
                </td>
              </tr>
              <tr>
                <td className="py-2 pr-4 font-semibold text-right">별점</td>
                <td className="py-2">
                  <div className="flex items-center gap-2">
                    <StarSelector rating={rating} onChange={setRating} />
                  </div>
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
              작성
            </button>
            <button
              onClick={onClose}
              className="bg-gray-300 text-black px-5 py-2 rounded hover:bg-gray-400"
            >
              작성 취소
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ReviewModal;
