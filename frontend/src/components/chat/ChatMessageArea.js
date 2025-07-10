// src/components/chat/ChatMessageArea.js
import React, { useState, useRef, useEffect, useCallback } from 'react';
import './css/ChatMessageArea.css';

const ChatMessageArea = ({
  selectedRoom,
  messages,
  currentUser,
  onSendMessage,
  onLeaveRoom,
  loading
}) => {
  const [inputValue, setInputValue] = useState('');
  const [sending, setSending] = useState(false);
  const messagesEndRef = useRef(null);

  // 스크롤 하단 이동
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
    } catch {
      setInputValue(messageText);
    } finally {
      setSending(false);
    }
  }, [inputValue, sending, selectedRoom, onSendMessage]);

  // 엔터키 전송
  const handleKeyPress = useCallback((e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  }, [handleSendMessage]);

  // 채팅방 나가기
  const handleLeaveRoom = useCallback(() => {
    if (selectedRoom && onLeaveRoom) {
      onLeaveRoom(selectedRoom.chatRoomId);
    }
  }, [selectedRoom, onLeaveRoom]);

  useEffect(() => {
    scrollToBottom();
  }, [messages, scrollToBottom]);

  const getSenderDisplayName = (message) => {
    if (message.senderId === 'AI_ASSISTANT') return '🤖 AI 어시스턴트';
    if (message.senderId === 'SYSTEM') return '🔧 시스템';
    return message.senderNickname || message.senderId;
  };

  if (!selectedRoom) {
    return (
      <div className="chat-message-area empty">
        <div>왼쪽 목록에서 채팅방을 선택하면 대화를 시작할 수 있습니다.</div>
      </div>
    );
  }

  return (
    <div className="chat-message-area">
      <div className="chat-message-header">
        <span className="chat-message-room-title">{selectedRoom.roomName}</span>
        {selectedRoom.roomType !== 'AI' && (
          <button className="chat-message-leave-btn" onClick={handleLeaveRoom}>
            나가기
          </button>
        )}
      </div>
      <div className="chat-message-list">
        {loading ? (
          <div className="chat-message-loading">메시지를 불러오는 중...</div>
        ) : (
          messages.map((msg, idx) => (
            <div
              key={msg.messageId || idx}
              className={`chat-message-item${msg.senderId === currentUser?.memberId ? ' mine' : ''}${msg.senderId === 'AI_ASSISTANT' ? ' ai' : ''}${msg.senderId === 'SYSTEM' ? ' system' : ''}`}
            >
              <div className="chat-message-sender">{getSenderDisplayName(msg)}</div>
              <div className="chat-message-content">{msg.content || msg.messageText}</div>

              <div className="chat-message-time">{msg.createdAt?.slice(11, 16)}</div>
            </div>
          ))
        )}
        <div ref={messagesEndRef} />
      </div>
      <div className="chat-message-input-area">
        <textarea
          className="chat-message-input"
          value={inputValue}
          onChange={e => setInputValue(e.target.value)}
          onKeyDown={handleKeyPress}
          placeholder="메시지를 입력하세요..."
          disabled={sending || loading}
          rows={2}
        />
        <button
          className="chat-message-send-btn"
          onClick={handleSendMessage}
          disabled={sending || loading || !inputValue.trim()}
        >
          전송
        </button>
      </div>
    </div>
  );
};

export default ChatMessageArea;
