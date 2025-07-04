// src/components/chat/ChatMessageArea.js
import React, { useState, useRef, useEffect, useCallback } from 'react';

const ChatMessageArea = ({ selectedRoom, messages, currentUser, onSendMessage, loading }) => {
  const [inputValue, setInputValue] = useState('');
  const [sending, setSending] = useState(false);
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);

  // 메시지 목록 끝으로 스크롤 (아래로)
  const scrollToBottom = useCallback(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, []);

  // 메시지 전송
  const handleSendMessage = useCallback(async () => {
    if (!inputValue.trim() || sending || !selectedRoom) return;

    const messageText = inputValue.trim();
    setInputValue('');
    setSending(true);

    try {
      await onSendMessage(messageText);
    } catch (error) {
      alert('메시지 전송에 실패했습니다.');
      setInputValue(messageText); // 입력값 복원
    } finally {
      setSending(false);
    }
  }, [inputValue, sending, selectedRoom, onSendMessage]);

  // Enter 키 처리
  const handleKeyPress = useCallback((e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  }, [handleSendMessage]);

  // 메시지 변경 시 스크롤 (새 메시지가 아래로 추가됨)
  useEffect(() => {
    scrollToBottom();
  }, [messages, scrollToBottom]);

  // 발신자 표시명
  const getSenderDisplayName = (message) => {
    if (message.senderId === 'AI_ASSISTANT') return '🤖 AI 어시스턴트';
    if (message.senderId === 'SYSTEM') return '🔧 시스템';
    return message.senderNickname || message.senderId;
  };

  if (!selectedRoom) {
    return (
      <div style={styles.noRoomSelected}>
        <div style={styles.noRoomContent}>
          <span style={{ fontSize: '48px', marginBottom: '10px' }}>💬</span>
          <p>채팅방을 선택해주세요</p>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      {/* 채팅방 헤더 */}
      <div style={styles.roomHeader}>
        <span style={styles.roomIcon}>
          {selectedRoom.roomType === 'AI' ? '🤖' : '🎬'}
        </span>
        <span style={styles.roomTitle}>{selectedRoom.roomName}</span>
        {selectedRoom.roomType === 'AI' && (
          <span style={styles.aiBadge}>AI</span>
        )}
      </div>

      {/* 메시지 목록 */}
      <div style={styles.messageList}>
        {loading && messages.length === 0 ? (
          <div style={styles.loading}>
            <div>⏳</div>
            <p>메시지를 불러오는 중...</p>
          </div>
        ) : messages.length === 0 ? (
          <div style={styles.noMessages}>
            <div style={{ fontSize: '48px', marginBottom: '10px' }}>
              {selectedRoom.roomType === 'AI' ? '🤖' : '🎬'}
            </div>
            <p>
              {selectedRoom.roomType === 'AI' 
                ? 'AI 어시스턴트와 대화를 시작해보세요!' 
                : '파티 메이트들과 대화를 시작해보세요!'}
            </p>
          </div>
        ) : (
          <>
            {messages.map((message, index) => (
              <div 
                key={message.chatMessagesId || index}
                style={{
                  ...styles.message,
                  alignSelf: message.senderId === currentUser.memberId ? 'flex-end' : 'flex-start'
                }}
              >
                {message.senderId !== currentUser.memberId && (
                  <div style={styles.senderName}>
                    {getSenderDisplayName(message)}
                  </div>
                )}
                <div style={{
                  ...styles.messageText,
                  backgroundColor: message.senderId === currentUser.memberId ? '#007bff' : '#f1f3f4',
                  color: message.senderId === currentUser.memberId ? 'white' : '#333'
                }}>
                  {message.messageText}
                </div>
                <div style={styles.messageTime}>
                  {new Date(message.sentAt).toLocaleTimeString('ko-KR', {
                    hour: '2-digit',
                    minute: '2-digit'
                  })}
                </div>
              </div>
            ))}
            {/* ✅ 스크롤 앵커 (맨 아래) */}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>

      {/* 입력 영역 */}
      <div style={styles.inputArea}>
        <div style={styles.inputWrapper}>
          <textarea
            ref={inputRef}
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={
              selectedRoom.roomType === 'AI' 
                ? "AI에게 질문하세요..." 
                : "메시지를 입력하세요..."
            }
            disabled={sending}
            rows={1}
            style={{
              ...styles.input,
              opacity: sending ? 0.6 : 1
            }}
          />
          <button
            onClick={handleSendMessage}
            disabled={sending || !inputValue.trim()}
            style={{
              ...styles.sendButton,
              opacity: sending || !inputValue.trim() ? 0.5 : 1
            }}
          >
            {sending ? '⏳' : '📤'}
          </button>
        </div>
        <div style={styles.inputHint}>
          Enter: 전송 | Shift + Enter: 줄바꿈
        </div>
      </div>
    </div>
  );
};

const styles = {
  container: {
    height: '100%',
    display: 'flex',
    flexDirection: 'column'
  },

  roomHeader: {
    padding: '12px 15px',
    borderBottom: '1px solid #eee',
    display: 'flex',
    alignItems: 'center',
    backgroundColor: '#f8f9fa'
  },

  roomIcon: {
    fontSize: '16px',
    marginRight: '8px'
  },

  roomTitle: {
    fontSize: '14px',
    fontWeight: '500',
    flex: 1
  },

  aiBadge: {
    background: '#4caf50',
    color: 'white',
    fontSize: '10px',
    padding: '2px 6px',
    borderRadius: '8px',
    fontWeight: 'bold'
  },

  messageList: {
    flex: 1,
    overflowY: 'auto',
    padding: '15px',
    display: 'flex',
    flexDirection: 'column',
    gap: '10px'
  },

  noRoomSelected: {
    height: '100%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  },

  noRoomContent: {
    textAlign: 'center',
    color: '#999'
  },

  loading: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
    color: '#666'
  },

  noMessages: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
    color: '#999',
    textAlign: 'center'
  },

  message: {
    display: 'flex',
    flexDirection: 'column',
    maxWidth: '80%'
  },

  senderName: {
    fontSize: '11px',
    color: '#666',
    marginBottom: '3px',
    fontWeight: '500'
  },

  messageText: {
    padding: '10px 12px',
    borderRadius: '12px',
    wordWrap: 'break-word',
    lineHeight: '1.4',
    fontSize: '14px'
  },

  messageTime: {
    fontSize: '10px',
    color: '#999',
    marginTop: '3px',
    alignSelf: 'flex-end'
  },

  inputArea: {
    padding: '15px',
    borderTop: '1px solid #eee',
    backgroundColor: '#fafafa'
  },

  inputWrapper: {
    display: 'flex',
    gap: '8px',
    alignItems: 'flex-end'
  },

  input: {
    flex: 1,
    padding: '10px 12px',
    border: '1px solid #ddd',
    borderRadius: '20px',
    resize: 'none',
    outline: 'none',
    fontSize: '14px',
    fontFamily: 'inherit',
    maxHeight: '80px',
    minHeight: '40px'
  },

  sendButton: {
    width: '40px',
    height: '40px',
    borderRadius: '50%',
    border: 'none',
    backgroundColor: '#007bff',
    color: 'white',
    cursor: 'pointer',
    fontSize: '16px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    transition: 'all 0.2s'
  },

  inputHint: {
    fontSize: '11px',
    color: '#999',
    marginTop: '5px',
    textAlign: 'center'
  }
};

export default ChatMessageArea;
