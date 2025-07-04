// src/store/slices/authSlice.js

import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'api/axios';

// 비동기 액션: 현재 사용자 정보 가져오기
export const fetchCurrentUser = createAsyncThunk(
  'auth/fetchCurrentUser',
  async (_, { rejectWithValue }) => {
    try {
      const response = await axios.get('/auth/current-user', {
        withCredentials: true,
        timeout: 10000
      });
      if (response.data.success) {
        return response.data.data;
      } else {
        return rejectWithValue(response.data.message || '인증 정보 로드 실패');
      }
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || err.message);
    }
  }
);

// 비동기 액션: 로그인
export const login = createAsyncThunk(
  'auth/login',
  async ({ memberId, password }, { rejectWithValue }) => {
    try {
      const response = await axios.post(
        '/auth/login',
        { memberId, 
          password, 
        },
        { withCredentials: true, timeout: 10000 }
      );
      if (response.data.success) {
        return response.data.data;
      } else {
        return rejectWithValue(response.data.message || '로그인 실패');
      }
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || err.message);
    }
  }
);

// 비동기 액션: 회원가입
export const register = createAsyncThunk(
  'auth/register',
  async (formData, { dispatch, rejectWithValue }) => {
    try {
      const response = await axios.post(
        '/member/signup',
        {
          id: formData.id,
          password: formData.password,
          name: formData.name,
          nickname: formData.nickname,
          email: formData.email,
          phone: formData.phone,
          address: formData.address,
          birthdate: formData.birthdate,
          gender: formData.gender,
          genres: formData.genres.map(g => g.value)
        },
        { withCredentials: true, timeout: 10000 }
      );
      if (response.data.success) {
        // 가입 후 자동 로그인 상태 동기화를 위해 사용자 정보 재조회
        await dispatch(fetchCurrentUser()).unwrap();
        return response.data.message || '회원가입이 완료되었습니다.';
      } else {
        return rejectWithValue(response.data.message || '회원가입 실패');
      }
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || err.message);
    }
  }
);

// 비동기 액션: 로그아웃
export const logout = createAsyncThunk(
  'auth/logout',
  async (_, { rejectWithValue }) => {
    try {
      await axios.post(
        '/api/auth/logout',
        {},
        { withCredentials: true, timeout: 5000 }
      );
      return;
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || err.message);
    }
  }
);

const initialState = {
  currentUser: null,
  isAuthenticated: false,
  loading: false,
  error: null,
  registerMessage: ''  // 회원가입 성공/실패 메시지 저장
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    // 수동 상태 초기화
    clearAuth(state) {
      state.currentUser = null;
      state.isAuthenticated = false;
      state.loading = false;
      state.error = null;
      state.registerMessage = '';
    }
  },
  extraReducers: (builder) => {
    builder
      // fetchCurrentUser
      .addCase(fetchCurrentUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCurrentUser.fulfilled, (state, action) => {
        state.currentUser = action.payload;
        state.isAuthenticated = true;
        state.loading = false;
      })
      .addCase(fetchCurrentUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // login
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.currentUser = action.payload;
        state.isAuthenticated = true;
        state.loading = false;
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // register
      .addCase(register.pending, (state) => {
        state.loading = true;
        state.error = null;
        state.registerMessage = '';
      })
      .addCase(register.fulfilled, (state, action) => {
        state.loading = false;
        state.registerMessage = action.payload;  // 성공 메시지
      })
      .addCase(register.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
        state.registerMessage = '';
      })

      // logout
      .addCase(logout.fulfilled, (state) => {
        state.currentUser = null;
        state.isAuthenticated = false;
      })
      .addCase(logout.rejected, (state, action) => {
        state.error = action.payload;
      });
  }
});

export const { clearAuth } = authSlice.actions;

// 셀렉터
export const selectCurrentUser      = (state) => state.auth.currentUser;
export const selectIsAuthenticated  = (state) => state.auth.isAuthenticated;
export const selectAuthLoading      = (state) => state.auth.loading;
export const selectAuthError        = (state) => state.auth.error;
export const selectRegisterMessage  = (state) => state.auth.registerMessage;

export default authSlice.reducer;
