// src/components/chat/ChatRoomList.js
import React from 'react';

const ChatRoomList = ({ chatRooms, selectedRoom, onRoomSelect, onLeaveRoom, loading, currentUser }) => {
  
  const formatLastMessageTime = (dateTime) => {
    if (!dateTime) return '';
    const now = new Date();
    const messageTime = new Date(dateTime);
    const diffMinutes = Math.floor((now - messageTime) / (1000 * 60));
    
    if (diffMinutes < 1) return '방금';
    if (diffMinutes < 60) return `${diffMinutes}분 전`;
    if (diffMinutes < 1440) return `${Math.floor(diffMinutes / 60)}시간 전`;
    return messageTime.toLocaleDateString();
  };

  const getRoomIcon = (roomType) => {
    switch (roomType) {
      case 'AI': return '🤖';
      case 'PARTY': return '🎬';
      default: return '💬';
    }
  };

  const truncateMessage = (message, maxLength = 20) => {
    if (!message) return '메시지가 없습니다';
    return message.length > maxLength ? message.substring(0, maxLength) + '...' : message;
  };

  const handleLeaveClick = (e, roomId) => {
    e.stopPropagation(); // 채팅방 선택 이벤트 방지
    onLeaveRoom(roomId);
  };

  if (loading) {
    return (
      <div style={styles.container}>
        <div style={styles.loadingContainer}>
          <div style={styles.loadingSpinner}></div>
          <p>로딩 중...</p>
        </div>
      </div>
    );
  }

  if (!chatRooms || chatRooms.length === 0) {
    return (
      <div style={styles.container}>
        <div style={styles.emptyContainer}>
          <p>채팅방이 없습니다</p>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h4 style={styles.headerTitle}>채팅방 목록</h4>
      </div>
      
      <div style={styles.roomList}>
        {chatRooms.map((room) => (
          <div
            key={room.chatRoomId}
            style={{
              ...styles.roomItem,
              ...(selectedRoom?.chatRoomId === room.chatRoomId ? styles.selectedRoom : {})
            }}
            onClick={() => onRoomSelect(room)}
          >
            <div style={styles.roomInfo}>
              <div style={styles.roomHeader}>
                <span style={styles.roomIcon}>{getRoomIcon(room.roomType)}</span>
                <span style={styles.roomName}>{truncateMessage(room.roomName, 15)}</span>
                {room.roomType !== 'AI' && (
                  <button
                    style={styles.leaveButton}
                    onClick={(e) => handleLeaveClick(e, room.chatRoomId)}
                    title="채팅방 나가기"
                  >
                    ✕
                  </button>
                )}
              </div>
              
              <div style={styles.roomDetails}>
                <span style={styles.participantCount}>
                  👥 {room.participantCount || 0}명
                </span>
                {room.lastMessageTime && (
                  <span style={styles.lastMessageTime}>
                    {formatLastMessageTime(room.lastMessageTime)}
                  </span>
                )}
              </div>
              
              {room.lastMessage && (
                <div style={styles.lastMessage}>
                  {truncateMessage(room.lastMessage)}
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

// 스타일 정의
const styles = {
  container: {
    height: '100%',
    display: 'flex',
    flexDirection: 'column',
    backgroundColor: '#f8f9fa'
  },

  header: {
    padding: '15px 10px',
    borderBottom: '1px solid #eee',
    backgroundColor: '#fff'
  },

  headerTitle: {
    margin: 0,
    fontSize: '14px',
    fontWeight: '600',
    color: '#333'
  },

  roomList: {
    flex: 1,
    overflowY: 'auto',
    padding: '5px'
  },

  roomItem: {
    padding: '12px 10px',
    margin: '2px 0',
    backgroundColor: '#fff',
    borderRadius: '6px',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    border: '1px solid transparent',
    position: 'relative'
  },

  selectedRoom: {
    backgroundColor: '#e3f2fd',
    borderColor: '#2196f3'
  },

  roomInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px'
  },

  roomHeader: {
    display: 'flex',
    alignItems: 'center',
    gap: '6px',
    position: 'relative'
  },

  roomIcon: {
    fontSize: '16px',
    flexShrink: 0
  },

  roomName: {
    fontSize: '13px',
    fontWeight: '500',
    color: '#333',
    flex: 1,
    minWidth: 0
  },

  leaveButton: {
    position: 'absolute',
    right: '0',
    top: '50%',
    transform: 'translateY(-50%)',
    background: 'rgba(244, 67, 54, 0.1)',
    border: 'none',
    color: '#f44336',
    width: '20px',
    height: '20px',
    borderRadius: '50%',
    cursor: 'pointer',
    fontSize: '12px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    transition: 'all 0.2s ease',
    opacity: 0.7
  },

  roomDetails: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    fontSize: '11px',
    color: '#666'
  },

  participantCount: {
    fontSize: '10px',
    color: '#666'
  },

  lastMessageTime: {
    fontSize: '10px',
    color: '#999'
  },

  lastMessage: {
    fontSize: '11px',
    color: '#888',
    marginTop: '2px'
  },

  loadingContainer: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
    color: '#666'
  },

  loadingSpinner: {
    width: '20px',
    height: '20px',
    border: '2px solid #f3f3f3',
    borderTop: '2px solid #2196f3',
    borderRadius: '50%',
    animation: 'spin 1s linear infinite'
  },

  emptyContainer: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
    color: '#666',
    fontSize: '14px'
  }
};

export default ChatRoomList;
