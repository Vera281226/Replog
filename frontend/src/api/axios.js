// src/api/axios.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    // Authorization: `Bearer ${localStorage.getItem("accessToken")}` // 토큰 필요 시
  },
});

export default api;
