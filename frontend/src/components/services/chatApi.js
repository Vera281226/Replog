// src/services/chatApi.js

import axios from 'axios';

const api = axios.create({
  baseURL: '/api/chat',
  withCredentials: true,
  timeout: 10000,
});

// 응답 인터셉터: 데이터만 반환, 401 처리
api.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      alert('로그인이 필요합니다.');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

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
  // 채팅방 목록 조회
  async getChatRooms() {
    const key = 'chatRooms';
    const cached = getCached(key);
    if (cached) return cached;

    const rooms = await dedupe(key, () => api.get('/rooms'));
    setCached(key, rooms);
    return rooms;
  },

  // AI 채팅방 생성/조회
  async createAiRoom() {
    return dedupe('aiRoom', async () => {
      const room = await api.post('/rooms/ai');
      cache.delete('chatRooms');
      return room;
    });
  },

  // 메시지 조회 (첫 페이지만 캐시)
  async getMessages(roomId, page = 0, size = 50) {
    const key = `msgs_${roomId}_${page}_${size}`;
    if (page === 0) {
      const cached = getCached(key);
      if (cached) return cached;
    }

    const msgs = await dedupe(key, () =>
      api.get(`/rooms/${roomId}/messages`, { params: { page, size } })
    );
    if (page === 0) setCached(key, msgs);
    return msgs;
  },

  // AI 메시지 전송
  async sendAiMessage(message) {
    if (!message.trim()) throw new Error('메시지를 입력하세요.');
    await api.post(
      '/ai',
      `message=${encodeURIComponent(message.trim())}`,
      { headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, timeout: 30000 }
    );
    // 메시지 캐시 무효화
    cache.forEach((_, k) => { if (k.startsWith('msgs_')) cache.delete(k); });
  },

  // 일반 메시지 전송
  async sendMessage(roomId, text) {
    if (!roomId || !text.trim()) throw new Error('필수 값 누락');
    await api.post('/messages', { chatRoomId: roomId, messageText: text.trim() });
    // 해당 채팅방 메시지 캐시 무효화
    cache.forEach((_, k) => {
      if (k.startsWith(`msgs_${roomId}_`)) cache.delete(k);
    });
  },
};

export default chatApiService;
