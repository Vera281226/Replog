// src/components/chat/ChatToggle.js
import React, { useState, useCallback } from 'react';
import ChatApp from './ChatApp';
import './css/ChatToggle.css'

const ChatToggle = ({ currentUser }) => {
  const [isOpen, setIsOpen] = useState(false);

  const handleToggle = useCallback(() => {
    setIsOpen(prev => !prev);
  }, []);

  const handleClose = useCallback(() => {
    setIsOpen(false);
  }, []);

  if (!currentUser?.memberId) {
    return null;
  }

  return (
    <>
      <button 
        onClick={handleToggle}
        style={{
          position: 'fixed',
          bottom: '20px',
          right: '20px',
          width: '60px',
          height: '60px',
          borderRadius: '50%',
          background: isOpen 
            ? '#ff5722' 
            : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          color: 'white',
          border: 'none',
          cursor: 'pointer',
          fontSize: '24px',
          boxShadow: '0 4px 20px rgba(0, 0, 0, 0.2)',
          zIndex: 999,
          transform: isOpen ? 'rotate(45deg)' : 'none',
          transition: 'all 0.3s ease',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}
        aria-label={isOpen ? '채팅 닫기' : '채팅 열기'}
      >
        💬
        {!isOpen && (
          <span style={{
            position: 'absolute',
            top: '-5px',
            right: '-5px',
            background: '#4caf50',
            color: 'white',
            fontSize: '10px',
            fontWeight: 'bold',
            padding: '2px 5px',
            borderRadius: '8px',
            transform: 'rotate(-45deg)'
          }}>
            AI
          </span>
        )}
      </button>

      {isOpen && (
        <ChatApp 
          currentUser={currentUser} 
          onClose={handleClose}
        />
      )}
    </>
  );
};

export default ChatToggle;
