// src/services/chatApi.js
import api from '../../error/api/interceptor'; // 공통 인터셉터 적용된 인스턴스 사용

// 중복 요청 방지 및 간단 캐시
const inFlight = new Map();
const cache = new Map();
const CACHE_TTL = 5 * 60 * 1000;

function dedupe(key, fn) {
  if (inFlight.has(key)) return inFlight.get(key);
  const p = fn().finally(() => inFlight.delete(key));
  inFlight.set(key, p);
  return p;
}

function getCached(key) {
  const entry = cache.get(key);
  if (entry && Date.now() - entry.ts < CACHE_TTL) return entry.value;
  return null;
}

function setCached(key, value) {
  cache.set(key, { value, ts: Date.now() });
}

const chatApiService = {

// 채팅방 나가기
async leaveChatRoom(roomId) {
    if (!roomId) throw new Error('채팅방 ID가 필요합니다.');
    try {
        await api.delete(`/chat/rooms/${roomId}/participants`);
        // 채팅방 관련 캐시 무효화
        cache.delete('chatRooms');
        cache.forEach((_, key) => {
            if (key.startsWith(`msgs_${roomId}_`)) {
                cache.delete(key);
            }
        });
        return { success: true };
    } catch (error) {
        console.error('채팅방 나가기 실패:', error);
        throw new Error(error.response?.data?.message || '채팅방 나가기에 실패했습니다.');
    }
},

// 채팅방 참가자 정보 조회
async getRoomParticipants(roomId) {
  if (!roomId) throw new Error('채팅방 ID가 필요합니다.');
  
  const key = `participants_${roomId}`;
  const cached = getCached(key);
  if (cached) return cached;
  
  try {
    const response = await api.get(`/chat/rooms/${roomId}/participants`);
    const data = response.data || response;
    
    setCached(key, data);
    return data;
  } catch (error) {
    console.error('참가자 정보 조회 실패:', error);
    throw new Error(error.response?.data?.message || '참가자 정보를 가져올 수 없습니다.');
  }
},
  // 채팅방 목록 조회
  async getChatRooms() {
    const key = 'chatRooms';
    const cached = getCached(key);
    if (cached) return cached;

    const rooms = await dedupe(key, () => api.get('/chat/rooms'));
    setCached(key, rooms.data || rooms); // 인터셉터에서 data만 반환한다면 rooms로, 아니면 rooms.data로
    return rooms.data || rooms;
  },

  // AI 채팅방 생성/조회
  async createAiRoom() {
    return dedupe('aiRoom', async () => {
      const room = await api.post('/chat/rooms/ai');
      cache.delete('chatRooms');
      return room.data || room;
    });
  },

  // 메시지 조회
  async getMessages(roomId, page = 0, size = 50) {
    const key = `msgs_${roomId}_${page}_${size}`;
    if (page === 0) {
      const cached = getCached(key);
      if (cached) return cached;
    }

    const msgs = await dedupe(key, () =>
      api.get(`/chat/rooms/${roomId}/messages`, { params: { page, size } })
    );
    if (page === 0) setCached(key, msgs.data || msgs);
    return msgs.data || msgs;
  },

  // AI 메시지 전송
  async sendAiMessage(message) {
    if (!message.trim()) throw new Error('메시지를 입력하세요.');
    await api.post(
      '/chat/ai',
      `message=${encodeURIComponent(message.trim())}`,
      { headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, timeout: 30000 }
    );
    // 메시지 캐시 무효화
    cache.forEach((_, k) => { if (k.startsWith('msgs_')) cache.delete(k); });
  },

  // 일반 메시지 전송
  async sendMessage(roomId, text) {
    if (!roomId || !text.trim()) throw new Error('필수 값 누락');
    await api.post('/chat/messages', { chatRoomId: roomId, messageText: text.trim() });
    // 해당 채팅방 메시지 캐시 무효화
    cache.forEach((_, k) => {
      if (k.startsWith(`msgs_${roomId}_`)) cache.delete(k);
    });
  },
};

export default chatApiService;
