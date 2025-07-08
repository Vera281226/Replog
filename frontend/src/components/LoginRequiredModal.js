import { useNavigate } from "react-router-dom";
import ReactModal from "react-modal";
import "./LoginRequiredModal.css";

ReactModal.setAppElement("#root");

export default function LoginRequiredModal({ isOpen }) {
  const navigate = useNavigate();

  const handleConfirm = () => {
    navigate("/login");
  };

  return (
    <ReactModal
      isOpen={isOpen}
      onRequestClose={handleConfirm} // 모달 외부 클릭 시에도 로그인 페이지로
      className="login-modal"
      overlayClassName="login-modal__overlay"
    >
      <div className="login-modal__body">
        <h2>로그인이 필요합니다</h2>
        <p>해당 기능은 로그인 후 이용 가능합니다.</p>
        <div className="login-modal__footer">
          <button onClick={handleConfirm} className="login-modal__btn">
            확인
          </button>
        </div>
      </div>
    </ReactModal>
  );
}