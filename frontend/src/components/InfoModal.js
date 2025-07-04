import ReactModal from 'react-modal';
import PropTypes from 'prop-types';
import './InfoModal.css';

ReactModal.setAppElement('#root');

export default function InfoModal({
  isOpen,
  type = 'info', // 'info', 'error', 'warning', 'success' 등
  title,
  message,
  confirmLabel = '확인',
  cancelLabel = '취소',
  onConfirm,
  onCancel,
}) {
  // 타입별 스타일 클래스
  const typeClass = `info-modal--${type}`;

  return (
    <ReactModal
      isOpen={isOpen}
      className={`info-modal ${typeClass}`}
      overlayClassName="info-modal__overlay"
      shouldCloseOnOverlayClick={false}
      shouldCloseOnEsc={false}
    >
      <div className="info-modal__header">
        <h2>{title}</h2>
      </div>
      <div className="info-modal__body">
        {message && <p>{message}</p>}
      </div>
      <div className="info-modal__footer">
        <button
          className="info-modal__btn info-modal__btn--primary"
          onClick={onConfirm}
        >
          {confirmLabel}
        </button>
        <button
          className="info-modal__btn info-modal__btn--sub"
          onClick={onCancel}
        >
          {cancelLabel}
        </button>
      </div>
    </ReactModal>
  );
}

InfoModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  type: PropTypes.oneOf(['info', 'error', 'warning', 'success']),
  title: PropTypes.string.isRequired,
  message: PropTypes.string,
  confirmLabel: PropTypes.string,
  cancelLabel: PropTypes.string,
  onConfirm: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
};
