import ReactModal from "react-modal"; 
import "./BannedWordModal.css";
ReactModal.setAppElement("#root");

const bannedWords = [
  "씨발", "ㅅㅂ", "시발", "병신", "ㅄ", "좆", "ㅈ같", "존나",
  "개새끼", "개소리", "ㅂㅅ", "꺼져", "죽어", "닥쳐", "엿먹어",
  "좆같", "지랄", "미친놈", "미친년", "놈", "년", "씹", "썅",
  "fuck", "shit", "bitch", "asshole", "bastard", "damn", "wtf"
];

/**
 * 금지어 포함 여부 검사
 * @param {string} text
 * @returns {{ hasBanned: boolean, matchedWords: string[] }}
 */
export function checkBannedWords(text) {
  const matchedWords = bannedWords.filter((word) => text.includes(word));
  return {
    hasBanned: matchedWords.length > 0,
    matchedWords,
  };
}

/**
 * 금지어 감지 시 모달 표시
 * @param {{
 *   isOpen: boolean;
 *   matchedWords: string[];
 *   onClose: () => void;
 * }} props
 */
export default function BannedWordFilterModal({ isOpen, matchedWords, onClose }) {
  return (
    <ReactModal
      isOpen={isOpen}
      onRequestClose={onClose}
      className="banned-modal"
      overlayClassName="banned-modal__overlay"
    >
      <div className="banned-modal__body">
        <h2>금지어가 포함되어 있습니다</h2>
        <p>다음 단어를 제거해주세요:</p>
        <ul>
          {matchedWords.map((word, idx) => (
            <li key={idx} style={{ color: "#dc2626", fontWeight: "bold" }}>
              {word}
            </li>
          ))}
        </ul>
        <div className="banned-modal__footer">
          <button onClick={onClose} className="banned-modal__btn">
            확인
          </button>
        </div>
      </div>
    </ReactModal>
  );
}