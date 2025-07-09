// src/components/chat/ChatMessageArea.js
import React, { useState, useRef, useEffect, useCallback } from 'react';

const ChatMessageArea = ({ selectedRoom, messages, currentUser, onSendMessage, onLeaveRoom, loading }) => {
  const [inputValue, setInputValue] = useState('');
  const [sending, setSending] = useState(false);
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);

  // 메시지 목록 끝으로 스크롤
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
      setInputValue(messageText);
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

  // 채팅방 나가기 처리
  const handleLeaveRoom = useCallback(() => {
    if (selectedRoom && onLeaveRoom) {
      onLeaveRoom(selectedRoom.chatRoomId);
    }
  }, [selectedRoom, onLeaveRoom]);

  // 메시지 변경 시 스크롤
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
      <div style={styles.noRoomContainer}>
        <div style={styles.noRoomContent}>
          <h4>채팅방을 선택해주세요</h4>
          <p>왼쪽 목록에서 채팅방을 선택하면 대화를 시작할 수 있습니다.</p>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      {/* 채팅방 헤더 */}
      <div style={styles.header}>
        <div style={styles.headerInfo}>
          <h4 style={styles.roomTitle}>
            {selectedRoom.roomType === 'AI' ? '🤖' : '🎬'} {selectedRoom.roomName}
          </h4>
          <span style={styles.participantCount}>
            👥 {selectedRoom.participantCount || 0}명
          </span>
        </div>
        
        {/* 나가기 버튼 (AI 채팅방이 아닌 경우만) */}
        {selectedRoom.roomType !== 'AI' && (
          <button
            style={styles.leaveButton}
            onClick={handleLeaveRoom}
            title="채팅방 나가기"
          >
            🚪 나가기
          </button>
        )}
      </div>

      {/* 메시지 영역 */}
      <div style={styles.messagesContainer}>
        {loading ? (
          <div style={styles.loadingContainer}>
            <p>메시지를 불러오는 중...</p>
          </div>
        ) : messages.length === 0 ? (
          <div style={styles.emptyContainer}>
            <p>
              {selectedRoom.roomType === 'AI' 
                ? 'AI 어시스턴트와 대화를 시작해보세요!' 
                : '파티 메이트들과 대화를 시작해보세요!'}
            </p>
          </div>
        ) : (
          <div style={styles.messagesList}>
            {messages.map((message, index) => (
              <div
                key={index}
                style={{
                  ...styles.messageItem,
                  ...(message.senderId === currentUser.memberId ? styles.myMessage : styles.otherMessage)
                }}
              >
                <div style={styles.messageInfo}>
                  <span style={styles.senderName}>
                    {getSenderDisplayName(message)}
                  </span>
                  <span style={styles.messageTime}>
                    {new Date(message.sentAt).toLocaleTimeString()}
                  </span>
                </div>
                <div style={styles.messageContent}>
                  {message.messageText}
                </div>
              </div>
            ))}
            <div ref={messagesEndRef} />
          </div>
        )}
      </div>

      {/* 입력 영역 */}
      <div style={styles.inputContainer}>
        <textarea
          ref={inputRef}
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="메시지를 입력하세요..."
          style={styles.textInput}
          disabled={sending}
        />
        <button
          onClick={handleSendMessage}
          disabled={sending || !inputValue.trim()}
          style={{
            ...styles.sendButton,
            ...(sending || !inputValue.trim() ? styles.sendButtonDisabled : {})
          }}
        >
          {sending ? '전송중...' : '전송'}
        </button>
      </div>
    </div>
  );
};

// 스타일 정의
const styles = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    height: '100%',
    backgroundColor: '#fff'
  },

  header: {
    padding: '15px 20px',
    borderBottom: '1px solid #eee',
    backgroundColor: '#f8f9fa',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  },

  headerInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px'
  },

  roomTitle: {
    margin: 0,
    fontSize: '16px',
    fontWeight: '600',
    color: '#333'
  },

  participantCount: {
    fontSize: '12px',
    color: '#666'
  },

  leaveButton: {
    background: 'linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%)',
    border: 'none',
    color: 'white',
    padding: '8px 12px',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '12px',
    fontWeight: '500',
    transition: 'all 0.2s ease',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
  },

  messagesContainer: {
    flex: 1,
    overflowY: 'auto',
    padding: '10px',
    backgroundColor: '#f5f5f5'
  },

  messagesList: {
    display: 'flex',
    flexDirection: 'column',
    gap: '10px'
  },

  messageItem: {
    display: 'flex',
    flexDirection: 'column',
    maxWidth: '70%',
    marginBottom: '5px'
  },

  myMessage: {
    alignSelf: 'flex-end',
    alignItems: 'flex-end'
  },

  otherMessage: {
    alignSelf: 'flex-start',
    alignItems: 'flex-start'
  },

  messageInfo: {
    display: 'flex',
    gap: '8px',
    marginBottom: '4px',
    fontSize: '11px',
    color: '#666'
  },

  senderName: {
    fontWeight: '500'
  },

  messageTime: {
    color: '#999'
  },

  messageContent: {
    background: '#fff',
    padding: '10px 12px',
    borderRadius: '12px',
    fontSize: '14px',
    lineHeight: '1.4',
    boxShadow: '0 1px 2px rgba(0,0,0,0.1)',
    wordBreak: 'break-word'
  },

  inputContainer: {
    display: 'flex',
    gap: '10px',
    padding: '15px 20px',
    borderTop: '1px solid #eee',
    backgroundColor: '#fff'
  },

  textInput: {
    flex: 1,
    padding: '10px 12px',
    border: '1px solid #ddd',
    borderRadius: '6px',
    fontSize: '14px',
    resize: 'none',
    minHeight: '40px',
    maxHeight: '80px',
    outline: 'none',
    fontFamily: 'inherit'
  },

  sendButton: {
    background: 'linear-gradient(135deg, #4caf50 0%, #45a049 100%)',
    border: 'none',
    color: 'white',
    padding: '10px 20px',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500',
    transition: 'all 0.2s ease'
  },

  sendButtonDisabled: {
    background: '#ccc',
    cursor: 'not-allowed'
  },

  noRoomContainer: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
    backgroundColor: '#f8f9fa'
  },

  noRoomContent: {
    textAlign: 'center',
    color: '#666'
  },

  loadingContainer: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
    color: '#666'
  },

  emptyContainer: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
    color: '#666',
    textAlign: 'center'
  }
};

export default ChatMessageArea;
