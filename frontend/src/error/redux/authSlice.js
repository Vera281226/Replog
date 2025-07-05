// src/store/slices/authSlice.js

import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'api/axios';

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

export const login = createAsyncThunk(
  'auth/login',
  async ({ memberId, password }, { dispatch, rejectWithValue }) => {
    try {
      const response = await axios.post(
        '/auth/login',
        { memberId, password },
        { withCredentials: true, timeout: 10000 }
      );
      if (response.data.success) {
        await dispatch(fetchCurrentUser()).unwrap();
        return response.data.data;
      } else {
        return rejectWithValue(response.data.message || '로그인 실패');
      }
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || err.message);
    }
  }
);

export const logout = createAsyncThunk(
  'auth/logout',
  async (_, { rejectWithValue }) => {
    try {
      await axios.post(
        '/auth/logout',
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
  loading: true, // ⭐️ 최초엔 true!
  error: null
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearAuth(state) {
      state.currentUser = null;
      state.isAuthenticated = false;
      state.loading = false;
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCurrentUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCurrentUser.fulfilled, (state, action) => {
        state.currentUser = action.payload;
        state.isAuthenticated = true;
        state.loading = false;
      })
      .addCase(fetchCurrentUser.rejected, (state) => {
        state.currentUser = null;
        state.isAuthenticated = false;
        state.loading = false;
      })
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

export const selectCurrentUser      = (state) => state.auth.currentUser;
export const selectIsAuthenticated  = (state) => state.auth.isAuthenticated;
export const selectAuthLoading      = (state) => state.auth.loading;
export const selectAuthError        = (state) => state.auth.error;

export default authSlice.reducer;