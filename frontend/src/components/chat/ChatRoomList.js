// src/components/chat/ChatRoomList.js
import React from 'react';

const ChatRoomList = ({ chatRooms, selectedRoom, onRoomSelect, loading, currentUser }) => {
  
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

  const truncateMessage = (message, maxLength = 30) => {
    if (!message) return '메시지가 없습니다';
    return message.length > maxLength ? message.substring(0, maxLength) + '...' : message;
  };

  if (loading) {
    return (
      <div style={styles.container}>
        <div style={styles.loading}>
          ⏳ 로딩 중...
        </div>
      </div>
    );
  }

  if (chatRooms.length === 0) {
    return (
      <div style={styles.container}>
        <div style={styles.empty}>
          <p>📭</p>
          <p>채팅방이 없습니다</p>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        채팅방 목록
      </div>
      
      <div style={styles.roomList}>
        {chatRooms.map((room) => (
          <div
            key={room.chatRoomId}
            style={{
              ...styles.roomItem,
              backgroundColor: selectedRoom?.chatRoomId === room.chatRoomId ? '#e3f2fd' : 'transparent'
            }}
            onClick={() => onRoomSelect(room)}
          >
            <div style={styles.roomHeader}>
              <span style={styles.roomIcon}>{getRoomIcon(room.roomType)}</span>
              <span style={styles.roomName}>{room.roomName}</span>
            </div>
            
            <div style={styles.roomInfo}>
              <div style={styles.lastMessage}>
                {truncateMessage(room.lastMessage)}
              </div>
              <div style={styles.roomMeta}>
                <span style={styles.participantCount}>
                  👥 {room.participantCount}
                </span>
                <span style={styles.lastTime}>
                  {formatLastMessageTime(room.lastMessageTime)}
                </span>
              </div>
            </div>
            
            {room.roomType === 'AI' && (
              <div style={styles.aiBadge}>AI</div>
            )}
          </div>
        ))}
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

  header: {
    padding: '12px 15px',
    fontWeight: 'bold',
    fontSize: '12px',
    color: '#666',
    borderBottom: '1px solid #eee',
    backgroundColor: '#f8f9fa'
  },

  roomList: {
    flex: 1,
    overflowY: 'auto'
  },

  roomItem: {
    padding: '12px 15px',
    cursor: 'pointer',
    borderBottom: '1px solid #f0f0f0',
    transition: 'background-color 0.2s',
    position: 'relative'
  },

  roomHeader: {
    display: 'flex',
    alignItems: 'center',
    marginBottom: '5px'
  },

  roomIcon: {
    fontSize: '16px',
    marginRight: '8px'
  },

  roomName: {
    fontSize: '14px',
    fontWeight: '500',
    color: '#333',
    flex: 1
  },

  roomInfo: {
    fontSize: '12px'
  },

  lastMessage: {
    color: '#666',
    marginBottom: '4px',
    lineHeight: '1.3'
  },

  roomMeta: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  },

  participantCount: {
    color: '#999',
    fontSize: '11px'
  },

  lastTime: {
    color: '#999',
    fontSize: '11px'
  },

  aiBadge: {
    position: 'absolute',
    top: '8px',
    right: '8px',
    background: '#4caf50',
    color: 'white',
    fontSize: '8px',
    padding: '2px 4px',
    borderRadius: '8px',
    fontWeight: 'bold'
  },

  loading: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
    color: '#666'
  },

  empty: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
    color: '#999',
    textAlign: 'center'
  }
};

export default ChatRoomList;
