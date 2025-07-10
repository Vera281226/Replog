// src/components/chat/ChatApp.js
import React, { useState, useEffect, useCallback } from 'react';
import ChatRoomList from './ChatRoomList';
import ChatMessageArea from './ChatMessageArea';
import chatApiService from '../services/chatApi';
import './css/ChatApp.css';

const ChatApp = ({ currentUser, onClose }) => {
  const [chatRooms, setChatRooms] = useState([]);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [connected, setConnected] = useState(true);

  // 채팅방 목록 로드
  const loadChatRooms = useCallback(async () => {
    if (!currentUser?.memberId) return;
    try {
      setLoading(true);
      const rooms = await chatApiService.getChatRooms();
      setChatRooms(rooms || []);
      const hasAiRoom = rooms.some(room => room.roomType === 'AI');
      if (!hasAiRoom) {
        await chatApiService.createAiRoom();
        const updatedRooms = await chatApiService.getChatRooms();
        setChatRooms(updatedRooms || []);
      }
    } catch (error) {
      setConnected(false);
    } finally {
      setLoading(false);
    }
  }, [currentUser?.memberId]);

  // 채팅방 선택
  const selectChatRoom = useCallback(async (room) => {
    if (!room || selectedRoom?.chatRoomId === room.chatRoomId) return;
    try {
      setSelectedRoom(room);
      setLoading(true);
      const messagesData = await chatApiService.getMessages(room.chatRoomId);
      setMessages(messagesData.content || messagesData || []);
    } catch (error) {
      // 에러 처리
    } finally {
      setLoading(false);
    }
  }, [selectedRoom?.chatRoomId]);

  // 메시지 전송
  const sendMessage = useCallback(async (messageText) => {
    if (!selectedRoom || !messageText.trim()) return;
    try {
      if (selectedRoom.roomType === 'AI') {
        await chatApiService.sendAiMessage(messageText.trim());
      } else {
        await chatApiService.sendMessage(selectedRoom.chatRoomId, messageText.trim());
      }
      setTimeout(async () => {
        try {
          const messagesData = await chatApiService.getMessages(selectedRoom.chatRoomId);
          setMessages(messagesData.content || messagesData || []);
        } catch {}
      }, 1000);
    } catch (error) {
      throw error;
    }
  }, [selectedRoom]);

  // 채팅방 나가기
  const handleLeaveRoom = useCallback(async (roomId) => {
    if (!roomId) return;
    const room = chatRooms.find(r => r.chatRoomId === roomId);
    if (room && room.roomType === 'AI') {
      alert('AI 채팅방은 나갈 수 없습니다.');
      return;
    }
    if (!window.confirm('정말로 채팅방을 나가시겠습니까?')) return;
    try {
      await chatApiService.leaveChatRoom(roomId);
      await loadChatRooms();
      if (selectedRoom?.chatRoomId === roomId) {
        setSelectedRoom(null);
        setMessages([]);
      }
      alert('채팅방에서 나왔습니다.');
    } catch (error) {
      alert('채팅방 나가기에 실패했습니다: ' + error.message);
    }
  }, [chatRooms, selectedRoom, loadChatRooms]);

  useEffect(() => {
    loadChatRooms();
  }, [loadChatRooms]);

  if (!currentUser?.memberId) {
    return (
      <div className="chat-login-required">
        <div>🔒 로그인이 필요합니다</div>
      </div>
    );
  }

  return (
    <div className="chat-app-container">
      <div className="chat-app-header">
        <h3>채팅</h3>
        <div className="chat-app-header-right">
          <span className="status-indicator">
            {connected ? '● 연결됨' : '● 연결 끊김'}
          </span>
          <button className="close-chat-btn" onClick={onClose} aria-label="닫기">✕</button>
        </div>
      </div>
      <div className="chat-app-content">
        <ChatRoomList
          chatRooms={chatRooms}
          selectedRoom={selectedRoom}
          onRoomSelect={selectChatRoom}
          onLeaveRoom={handleLeaveRoom}
          loading={loading}
          currentUser={currentUser}
        />
        <ChatMessageArea
          selectedRoom={selectedRoom}
          messages={messages}
          currentUser={currentUser}
          onSendMessage={sendMessage}
          onLeaveRoom={handleLeaveRoom}
          loading={loading}
        />
      </div>
    </div>
  );
};

export default ChatApp;
