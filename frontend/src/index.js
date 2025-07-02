import React from 'react';
import ReactDOM from 'react-dom/client';
import AppRouter from './App';
import { Provider } from 'react-redux';
import store from './error/redux/store';
import './error/utils/globalErrorListener';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <Provider store={store}>
    <AppRouter />
  </Provider>
);