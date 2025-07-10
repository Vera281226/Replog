// src/components/chat/ChatToggle.js
import React, { useState, useCallback } from 'react';
import ChatApp from './ChatApp';
import './css/ChatToggle.css';

const ChatToggle = ({ currentUser }) => {
  const [isOpen, setIsOpen] = useState(false);

  const handleToggle = useCallback(() => {
    setIsOpen(prev => !prev);
  }, []);

  const handleClose = useCallback(() => {
    setIsOpen(false);
  }, []);

  if (!currentUser?.memberId) return null;

  return (
    <>
      {/* 버튼은 항상 표시 */}
      <button
        className={`chat-toggle-button${isOpen ? ' active' : ''}`}
        onClick={handleToggle}
        aria-label={isOpen ? "채팅 닫기" : "채팅 열기"}
      >
        💬
      </button>
      
      {/* 대화창이 열렸을 때만 표시 */}
      {isOpen && (
        <div>
          <ChatApp currentUser={currentUser} onClose={handleClose} />
        </div>
      )}
    </>
  );
};

export default ChatToggle;
