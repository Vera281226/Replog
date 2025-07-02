import { combineReducers } from '@reduxjs/toolkit';
import errorReducer from './errorSlice';
import authReducer from './authSlice';

const rootReducer = combineReducers({
  error: errorReducer,
  auth: authReducer,
});

export default rootReducer;