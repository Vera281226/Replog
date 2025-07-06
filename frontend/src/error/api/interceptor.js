  import axios from 'axios';
  import Cookies from 'js-cookie';
  import { saveLastRequest } from '../utils/requestHistory';

  /* ──────────────────────────────────────────────
    Axios 전역 인터셉터 + 고정 딜레이 재시도 구현
  ─────────────────────────────────────────────── */

  /* 재시도 설정 */
  const MAX_RETRIES = 3;
  const FIXED_DELAY_MS = 333; // 1초 내 3회 재시도를 위한 고정 딜레이 (1000ms / 3 ≈ 333ms)

  /**
   * 재시도 대상 여부
   *  - 네트워크 오류(응답 객체 없음)
   *  - 5xx · 408 · 429 상태코드
   */
  function shouldRetry(error) {
    if (!error.response) return true;
    const status = error.response.status;
    return status >= 500 || status === 408 || status === 429;
  }

  const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    withCredentials: true,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  /* 요청 인터셉터 */
  api.interceptors.request.use(config => {
    // js-cookie로 CSRF 토큰 읽어서 헤더에 자동 추가
    const csrf = Cookies.get('XSRF-TOKEN');
    if (csrf) config.headers['X-XSRF-TOKEN'] = csrf;
    saveLastRequest({ method: config.method, url: config.url });
    return config;
  });

  /* 응답 인터셉터 */
  api.interceptors.response.use(
    response => response,
    error => {
      const config = error.config || {};

      /* ───── 고정 딜레이 재시도 로직 ───── */
      if (shouldRetry(error) && config) {
        config.__retryCount = config.__retryCount || 0;

        if (config.__retryCount < MAX_RETRIES) {
          config.__retryCount += 1;

          // 고정 딜레이 적용 (333ms)
          const delay = FIXED_DELAY_MS;

          return new Promise(resolve =>
            setTimeout(() => resolve(api(config)), delay)
          );
        }
      }

      return Promise.reject(error);
    },
  );

  export default api;