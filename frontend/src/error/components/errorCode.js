export const HTTP_ERROR_CODES = {
  // 4xx: 클라이언트 오류
  400: {
    title: '400 잘못된 요청',
    message: '잘못된 요청입니다. 입력하신 정보를 확인해주세요.',
  },
  401: {
    title: '401 인증 필요',
    message: '로그인이 필요합니다. 다시 로그인해주세요.',
  },
  402: {
    title: '402 결제 필요',
    message: '결제가 필요합니다. 결제 상태를 확인해주세요.',
  },
  403: {
    title: '403 접근 금지',
    message: '이 페이지에 접근할 권한이 없습니다.',
  },
  404: {
    title: '404 페이지를 찾을 수 없음',
    message: '요청하신 페이지가 존재하지 않습니다. URL을 확인해주세요.',
  },
  405: {
    title: '405 허용되지 않는 메서드',
    message: '지원되지 않는 요청 방식입니다.',
  },
  408: {
    title: '408 요청 시간 초과',
    message: '응답이 지연되었습니다. 잠시 후 다시 시도해주세요.',
  },
  409: {
    title: '409 충돌 발생',
    message: '현재 요청이 서버 상태와 충돌합니다.',
  },
  422: {
    title: '422 처리 불가',
    message: '전송된 데이터가 유효하지 않습니다. 입력 내용을 확인해주세요.',
  },
  429: {
    title: '429 너무 많은 요청',
    message: '짧은 시간에 요청이 많습니다. 잠시 후 다시 시도해주세요.',
  },

  // 5xx: 서버 오류
  500: {
    title: '500 서버 오류',
    message: '서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요.',
  },
  501: {
    title: '501 미구현 기능',
    message: '서버가 요청을 처리할 수 없는 기능입니다.',
  },
  502: {
    title: '502 잘못된 게이트웨이',
    message: '서버 연결에 문제가 있습니다. 잠시 후 다시 시도해주세요.',
  },
  503: {
    title: '503 서비스 이용 불가',
    message: '서비스가 일시적으로 중단되었습니다. 잠시 후 다시 시도해주세요.',
  },
  504: {
    title: '504 게이트웨이 시간 초과',
    message: '서버 응답이 지연되었습니다. 잠시 후 다시 시도해주세요.',
  },
  507: {
    title: '507 저장 공간 부족',
    message: '서버 저장 공간이 부족합니다.',
  },
};