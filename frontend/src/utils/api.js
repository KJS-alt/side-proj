import axios from 'axios';

// axios 인스턴스 생성
const api = axios.create({
  baseURL: 'http://localhost:8081/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: JWT 토큰을 자동으로 헤더에 추가
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터: 401 에러 시 로그아웃 처리
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // 인증 실패 시 토큰 삭제
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ========== 사용자 API ==========

/**
 * 회원가입
 * @param {string} email - 이메일
 * @param {string} password - 비밀번호
 * @param {string} username - 사용자명
 */
export const registerUser = async (email, password, username) => {
  const response = await api.post('/users/register', {
    email,
    password,
    username,
  });
  return response.data;
};

/**
 * 로그인
 * @param {string} email - 이메일
 * @param {string} password - 비밀번호
 */
export const loginUser = async (email, password) => {
  const response = await api.post('/users/login', {
    email,
    password,
  });
  return response.data;
};

/**
 * 내 정보 조회
 */
export const getMyInfo = async () => {
  const response = await api.get('/users/me');
  return response.data;
};

/**
 * 로그아웃 (로컬 토큰 삭제)
 */
export const logout = () => {
  localStorage.removeItem('token');
};

/**
 * 로그인 상태 확인
 */
export const isAuthenticated = () => {
  return !!localStorage.getItem('token');
};

// ========== 공매물건 API ==========

/**
 * 물건 목록 조회
 * @param {number} pageNo - 페이지 번호 (기본값: 1)
 * @param {number} numOfRows - 페이지당 건수 (기본값: 10)
 * @param {object} filters - 필터 옵션 (sido, sgk 등)
 * @param {AbortSignal} signal - 요청 취소를 위한 signal (선택사항)
 */
export const getGoodsList = async (pageNo = 1, numOfRows = 10, filters = {}, signal = null) => {
  const params = {
    pageNo,
    numOfRows,
    ...filters,
  };
  
  const config = { params };
  
  // AbortSignal이 있으면 설정에 추가
  if (signal) {
    config.signal = signal;
  }
  
  const response = await api.get('/goods', config);
  return response.data;
};

/**
 * 물건 목록만 간단히 조회
 * @param {number} pageNo - 페이지 번호
 * @param {number} numOfRows - 페이지당 건수
 * @param {object} filters - 필터 옵션
 * @param {AbortSignal} signal - 요청 취소를 위한 signal (선택사항)
 */
export const getGoodsItems = async (pageNo = 1, numOfRows = 10, filters = {}, signal = null) => {
  const params = {
    pageNo,
    numOfRows,
    ...filters,
  };
  
  const config = { params };
  
  // AbortSignal이 있으면 설정에 추가
  if (signal) {
    config.signal = signal;
  }
  
  const response = await api.get('/goods/items', config);
  return response.data;
};

// ========== 관심물건 API ==========

/**
 * 내 관심물건 목록 조회
 */
export const getFavorites = async () => {
  const response = await api.get('/favorites');
  return response.data;
};

/**
 * 관심물건 등록
 * @param {object} favorite - 관심물건 정보
 */
export const addFavorite = async (favorite) => {
  const response = await api.post('/favorites', favorite);
  return response.data;
};

/**
 * 관심물건 삭제 (ID로)
 * @param {number} id - 관심물건 ID
 */
export const deleteFavorite = async (id) => {
  const response = await api.delete(`/favorites/${id}`);
  return response.data;
};

/**
 * 관심물건 삭제 (물건번호로)
 * @param {string} goodsNo - 물건번호
 */
export const deleteFavoriteByGoodsNo = async (goodsNo) => {
  const response = await api.delete(`/favorites/goods/${goodsNo}`);
  return response.data;
};

/**
 * 관심물건 여부 확인
 * @param {string} goodsNo - 물건번호
 */
export const checkFavorite = async (goodsNo) => {
  const response = await api.get(`/favorites/check/${goodsNo}`);
  return response.data;
};

// ========== 내 정보 수정 API ==========

/**
 * 내 정보 수정
 * @param {object} data - 수정할 정보 (username, currentPassword, newPassword)
 */
export const updateMyInfo = async (data) => {
  const response = await api.put('/users/me', data);
  return response.data;
};

/**
 * 회원 탈퇴
 * @param {string} password - 비밀번호 확인
 */
export const deleteMyAccount = async (password) => {
  const response = await api.delete('/users/me', {
    data: { password }
  });
  return response.data;
};

export default api;

