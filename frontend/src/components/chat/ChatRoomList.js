import React from 'react';
import './css/ChatRoomList.css';

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
    e.stopPropagation();
    onLeaveRoom(roomId);
  };

  if (loading) {
    return (
      <div className="chat-room-list">
        <div className="chat-room-list-loading">로딩 중...</div>
      </div>
    );
  }

  if (!chatRooms.length) {
    return (
      <div className="chat-room-list">
        <div className="chat-room-list-empty">채팅방이 없습니다</div>
      </div>
    );
  }

  return (
    <div className="chat-room-list">
      <ul>
        {chatRooms.map(room => (
          <li
            key={room.chatRoomId}
            className={`chat-room-item${selectedRoom?.chatRoomId === room.chatRoomId ? ' selected' : ''}`}
            onClick={() => onRoomSelect(room)}
          >
            <span className="chat-room-icon">{getRoomIcon(room.roomType)}</span>
            <div className="chat-room-info">
              <div className="chat-room-title">{room.roomName}</div>
              <div className="chat-room-last">
                <span className="chat-room-last-message">
                  {truncateMessage(room.lastMessage)}
                </span>
                <span className="chat-room-last-time">
                  {formatLastMessageTime(room.lastMessageTime)}
                </span>
              </div>
            </div>
            {room.roomType !== 'AI' && (
              <button
                className="chat-room-leave-btn"
                onClick={e => handleLeaveClick(e, room.chatRoomId)}
              >
                나가기
              </button>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ChatRoomList;
