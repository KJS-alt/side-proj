import axios from 'axios';

// axios 인스턴스 생성
const api = axios.create({
  baseURL: 'http://localhost:8081/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

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

// ========== DB 물건 API (구매용) ==========

/**
 * DB에서 물건 목록 조회
 * @returns {Promise} 물건 목록
 */
export const getGoodsFromDB = async () => {
  const response = await api.get('/goods/db');
  return response.data;
};

/**
 * DB에서 특정 물건 상세 조회
 * @param {number} historyNo - 물건이력번호
 * @returns {Promise} 물건 상세 정보
 */
export const getGoodsDetail = async (historyNo) => {
  const response = await api.get(`/goods/db/${historyNo}`);
  return response.data;
};

/**
 * 물건 목록을 DB에 저장
 * @param {Array} goods - 저장할 물건 목록
 * @returns {Promise} 저장 결과
 */
export const saveGoodsToDB = async (goods) => {
  const response = await api.post('/goods/db/batch', goods);
  return response.data;
};

/**
 * 모든 물건 삭제
 * @returns {Promise} 삭제 결과
 */
export const deleteAllGoods = async () => {
  const response = await api.delete('/goods/db/all');
  return response.data;
};

// ========== 구매 API ==========

/**
 * 구매 생성
 * @param {number} historyNo - 물건이력번호
 * @param {number} purchasePrice - 구매가격
 * @returns {Promise} 구매 결과
 */
export const createPurchase = async (historyNo, purchasePrice) => {
  const response = await api.post('/purchases', {
    historyNo,
    purchasePrice,
  });
  return response.data;
};

/**
 * 특정 물건의 구매 이력 조회
 * @param {number} historyNo - 물건이력번호
 * @returns {Promise} 구매 이력 목록
 */
export const getPurchasesByHistoryNo = async (historyNo) => {
  const response = await api.get(`/purchases/${historyNo}`);
  return response.data;
};

/**
 * 전체 구매 이력 조회
 * @returns {Promise} 구매 이력 목록
 */
export const getAllPurchases = async () => {
  const response = await api.get('/purchases');
  return response.data;
};

export default api;

