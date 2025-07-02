// src/components/chat/ChatApp.js
import React, { useState, useEffect, useCallback, useRef } from 'react';
import ChatRoomList from './ChatRoomList';
import ChatMessageArea from './ChatMessageArea';
import chatApiService from '../services/chatApi';

const ChatApp = ({ currentUser, onClose }) => {
  const [chatRooms, setChatRooms] = useState([]);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [connected, setConnected] = useState(true);

  // ✅ 채팅방 목록 로드
  const loadChatRooms = useCallback(async () => {
    if (!currentUser?.memberId) return;

    try {
      setLoading(true);
      const rooms = await chatApiService.getChatRooms();
      setChatRooms(rooms || []);
      
      // AI 채팅방이 없으면 생성
      const hasAiRoom = rooms.some(room => room.roomType === 'AI');
      if (!hasAiRoom) {
        await chatApiService.createAiRoom();
        const updatedRooms = await chatApiService.getChatRooms();
        setChatRooms(updatedRooms || []);
      }
    } catch (error) {
      console.error('채팅방 목록 로드 실패:', error);
      setConnected(false);
    } finally {
      setLoading(false);
    }
  }, [currentUser?.memberId]);

  // ✅ 특정 채팅방 선택
  const selectChatRoom = useCallback(async (room) => {
    if (!room || selectedRoom?.chatRoomId === room.chatRoomId) return;

    try {
      setSelectedRoom(room);
      setLoading(true);
      
      const messagesData = await chatApiService.getMessages(room.chatRoomId);
      setMessages(messagesData.content || messagesData || []);
    } catch (error) {
      console.error('메시지 로드 실패:', error);
    } finally {
      setLoading(false);
    }
  }, [selectedRoom?.chatRoomId]);

  // ✅ 메시지 전송
  const sendMessage = useCallback(async (messageText) => {
    if (!selectedRoom || !messageText.trim()) return;

    try {
      if (selectedRoom.roomType === 'AI') {
        // AI 메시지 전송
        await chatApiService.sendAiMessage(messageText.trim());
      } else {
        // 일반 메시지 전송
        await chatApiService.sendMessage(selectedRoom.chatRoomId, messageText.trim());
      }

      // 메시지 목록 새로고침
      setTimeout(async () => {
        try {
          const messagesData = await chatApiService.getMessages(selectedRoom.chatRoomId);
          setMessages(messagesData.content || messagesData || []);
        } catch (error) {
          console.error('메시지 새로고침 실패:', error);
        }
      }, 1000);

    } catch (error) {
      console.error('메시지 전송 실패:', error);
      throw error;
    }
  }, [selectedRoom]);

  // ✅ 컴포넌트 마운트 시 초기화
  useEffect(() => {
    loadChatRooms();
  }, [loadChatRooms]);

  // ✅ 로그인 검증
  if (!currentUser?.memberId) {
    return (
      <div style={styles.container}>
        <div style={styles.header}>
          <h3>💬 채팅</h3>
          <button onClick={onClose} style={styles.closeButton}>✕</button>
        </div>
        <div style={styles.loginRequired}>
          <p>🔒 로그인이 필요합니다</p>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      {/* 헤더 */}
      <div style={styles.header}>
        <h3>💬 채팅</h3>
        <div style={styles.headerRight}>
          <span style={{
            ...styles.statusIndicator,
            backgroundColor: connected ? '#4caf50' : '#f44336'
          }}>
            {connected ? '● 연결됨' : '○ 연결 중...'}
          </span>
          <button onClick={onClose} style={styles.closeButton}>✕</button>
        </div>
      </div>

      {/* 메인 콘텐츠 */}
      <div style={styles.content}>
        {/* 왼쪽: 채팅방 목록 */}
        <div style={styles.sidebar}>
          <ChatRoomList
            chatRooms={chatRooms}
            selectedRoom={selectedRoom}
            onRoomSelect={selectChatRoom}
            loading={loading}
            currentUser={currentUser}
          />
        </div>

        {/* 오른쪽: 메시지 영역 */}
        <div style={styles.messageArea}>
          <ChatMessageArea
            selectedRoom={selectedRoom}
            messages={messages}
            currentUser={currentUser}
            onSendMessage={sendMessage}
            loading={loading}
          />
        </div>
      </div>
    </div>
  );
};

// ✅ 스타일 정의
const styles = {
  container: {
    position: 'fixed',
    bottom: '80px',
    right: '20px',
    width: '700px',
    height: '500px',
    backgroundColor: 'white',
    borderRadius: '8px',
    boxShadow: '0 8px 32px rgba(0, 0, 0, 0.15)',
    display: 'flex',
    flexDirection: 'column',
    zIndex: 1000,
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
    overflow: 'hidden'
  },

  header: {
    padding: '15px 20px',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    color: 'white',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderRadius: '8px 8px 0 0'
  },

  headerRight: {
    display: 'flex',
    alignItems: 'center',
    gap: '10px'
  },

  statusIndicator: {
    fontSize: '11px',
    color: 'white',
    padding: '2px 6px',
    borderRadius: '10px',
    backgroundColor: '#4caf50'
  },

  closeButton: {
    background: 'rgba(255, 255, 255, 0.2)',
    border: 'none',
    color: 'white',
    width: '28px',
    height: '28px',
    borderRadius: '50%',
    cursor: 'pointer',
    fontSize: '14px'
  },

  content: {
    flex: 1,
    display: 'flex',
    overflow: 'hidden'
  },

  sidebar: {
    width: '200px',
    borderRight: '1px solid #eee',
    backgroundColor: '#f8f9fa'
  },

  messageArea: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column'
  },

  loginRequired: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
    color: '#666',
    textAlign: 'center'
  }
};

export default ChatApp;
