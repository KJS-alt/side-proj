import { useState, useEffect, useCallback, useRef } from 'react';
import GoodsTable from '../components/GoodsTable';
import GoodsMobileCard from '../components/GoodsMobileCard';
import { getGoodsList, getGoodsFromDB, saveGoodsToDB, deleteAllGoods, getRefreshStatus } from '../utils/api';

/**
 * ListPage 컴포넌트 - 물건 목록 페이지 (표 형식)
 */
function ListPage() {
  const [goods, setGoods] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [pageNo, setPageNo] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [showFilters, setShowFilters] = useState(true);
  
  // 정렬 상태
  const [sortField, setSortField] = useState('');
  const [sortOrder, setSortOrder] = useState('desc');

  const numOfRows = 20; // 페이지당 건수

  // 보기 모드 상태
  const [viewMode, setViewMode] = useState('db'); // 'db' | 'api' | 'filtered'
  const [apiData, setApiData] = useState([]); // API에서 조회한 원본 데이터
  const [filteredData, setFilteredData] = useState([]); // 100개 필터링한 데이터

  const [lastSyncedAt, setLastSyncedAt] = useState(null);
  const [showRefreshBanner, setShowRefreshBanner] = useState(false);
  const bannerTimeoutRef = useRef(null);

  // 동기화 시각 포맷터 (한국어 표기)
  const formatSyncTime = (value) => {
    if (!value) return '동기화 대기중';
    try {
      return new Date(value).toLocaleString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    } catch {
      return value;
    }
  };
  // 필터 상태 (DB 저장 필드에 맞게)
  const [filters, setFilters] = useState({
    goodsName: '',        // 물건명
    minBidPriceFrom: '',  // 최저입찰가 시작
    minBidPriceTo: '',    // 최저입찰가 끝
    appraisalPriceFrom: '', // 감정가 시작
    appraisalPriceTo: '',   // 감정가 끝
  });

  // goodsNo를 기준으로 그룹화
  const groupByGoodsNo = (goodsList) => {
    const grouped = {};
    goodsList.forEach(item => {
      const key = item.goodsNo || item.historyNo;
      if (!grouped[key]) {
        grouped[key] = [];
      }
      grouped[key].push(item);
    });
    return grouped;
  };

  // 각 그룹에서 historyNo가 가장 큰 (최신) 항목 선택
  const filterLatestByHistory = (goodsList) => {
    const grouped = groupByGoodsNo(goodsList);
    return Object.values(grouped).map(group => {
      return group.reduce((latest, current) => {
        return (current.historyNo > latest.historyNo) ? current : latest;
      });
    });
  };

  // api 필터링하여 100개만 선택
  const selectTop100 = (goodsList) => {
    const filtered = filterLatestByHistory(goodsList);
    // 최대 100개로 제한
    return filtered.slice(0, 100);
  };

  const triggerRefreshBanner = useCallback(() => {
    setShowRefreshBanner(true);
    if (bannerTimeoutRef.current) {
      clearTimeout(bannerTimeoutRef.current);
    }
    bannerTimeoutRef.current = setTimeout(() => setShowRefreshBanner(false), 3000);
  }, []);

  // 백엔드에서 동기화 상태를 받아와 마지막 갱신 시각과 배너를 업데이트
  const fetchRefreshStatus = useCallback(async () => {
    try {
      const response = await getRefreshStatus();
      if (response?.success) {
        const latestSynced = response.lastSyncedAt || null;
        setLastSyncedAt(prev => {
          if (latestSynced && latestSynced !== prev) {
            triggerRefreshBanner();
          }
          return latestSynced;
        });
      }
    } catch (err) {
      console.error('갱신 상태 조회 실패:', err);
    }
  }, [triggerRefreshBanner]);

  // 초기 진입 시 상태 조회 + 배너 타이머 정리
  useEffect(() => {
    fetchRefreshStatus();
    return () => {
      if (bannerTimeoutRef.current) {
        clearTimeout(bannerTimeoutRef.current);
      }
    };
  }, [fetchRefreshStatus]);

  // 10초마다 상태를 재조회하여 마지막 갱신 시각을 최신으로 유지
  useEffect(() => {
    const statusPoller = setInterval(() => {
      fetchRefreshStatus();
    }, 10000);
    return () => clearInterval(statusPoller);
  }, [fetchRefreshStatus]);

  // API 조회 버튼
  const handleApiQuery = async () => {
    setIsLoading(true);
    setError('');
    
    try {
      // 모든 데이터 조회 (API 호출량을 1,000건으로 제한)
      const response = await getGoodsList(1, 1000, {});
      
      if (response.success && response.data?.items) {
        setApiData(response.data.items);
        setGoods(response.data.items);
        setViewMode('api');
        setTotalCount(response.data.items.length);
        setPageNo(1);
      } else {
        setError('API에서 물건을 조회할 수 없습니다.');
      }
    } catch (err) {
      console.error('API 조회 오류:', err);
      setError('API 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 100개 조회
  const handleFilterByHistory = () => {
    if (apiData.length === 0) {
      alert('먼저 API 조회를 실행해주세요.');
      return;
    }

    setIsLoading(true);
    
    try {
      const filtered = selectTop100(apiData);
      setFilteredData(filtered);
      setGoods(filtered);
      setViewMode('filtered');
      setTotalCount(filtered.length);
      setPageNo(1);
    } catch (err) {
      console.error('100개 필터링 오류:', err);
      setError('100개 필터링 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 데이터베이스에 저장
  const handleSaveToDB = async () => {
    if (filteredData.length === 0) {
      alert('먼저 100개 조회를 실행해주세요.');
      return;
    }

    if (!confirm(`${filteredData.length}개의 물건을 데이터베이스에 저장하시겠습니까?\n기존 데이터는 모두 삭제됩니다.`)) {
      return;
    }

    setIsLoading(true);
    
    try {
      const response = await saveGoodsToDB(filteredData);
      
      if (response.success) {
        alert(`${response.savedCount}개의 물건이 저장되었습니다.`);
        // DB 조회로 전환
        handleDBQuery();
        fetchRefreshStatus();
      } else {
        alert('저장 실패: ' + response.message);
      }
    } catch (err) {
      console.error('DB 저장 오류:', err);
      alert('DB 저장 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // DB 조회
  const handleDBQuery = async () => {
    setIsLoading(true);
    setError('');
    
    try {
      const response = await getGoodsFromDB();
      
      if (response.success && response.items) {
        setGoods(response.items);
        setViewMode('db');
        setTotalCount(response.items.length);
        setPageNo(1);
      } else {
        setError('DB에서 물건을 조회할 수 없습니다.');
      }
    } catch (err) {
      console.error('DB 조회 오류:', err);
      setError('DB 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 모든 데이터 삭제
  const handleDeleteAll = async () => {
    if (!confirm('정말로 모든 물건 데이터를 삭제하시겠습니까?')) {
      return;
    }

    setIsLoading(true);
    
    try {
      const response = await deleteAllGoods();
      
      if (response.success) {
        alert(`${response.deletedCount}개의 물건이 삭제되었습니다.`);
        // DB 조회로 전환
        handleDBQuery();
      } else {
        alert('삭제 실패: ' + response.message);
      }
    } catch (err) {
      console.error('DB 삭제 오류:', err);
      alert('DB 삭제 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 현재 viewMode에 따라 표시할 데이터 가져오기
  const getCurrentData = () => {
    let items = goods;
    
    // 클라이언트 사이드 정렬
    if (sortField) {
      items = [...items].sort((a, b) => {
        let aVal = a[sortField];
        let bVal = b[sortField];
        
        // 숫자 비교
        if (typeof aVal === 'number' && typeof bVal === 'number') {
          return sortOrder === 'asc' ? aVal - bVal : bVal - aVal;
        }
        
        // 문자열 비교
        if (typeof aVal === 'string' && typeof bVal === 'string') {
          return sortOrder === 'asc' 
            ? aVal.localeCompare(bVal) 
            : bVal.localeCompare(aVal);
        }
        
        return 0;
      });
    }
    
    // 필터 적용 (클라이언트 사이드)
    if (filters.goodsName) {
      items = items.filter(item => 
        item.goodsName && item.goodsName.includes(filters.goodsName)
      );
    }
    
    if (filters.minBidPriceFrom) {
      const priceFrom = parseInt(filters.minBidPriceFrom);
      items = items.filter(item => item.minBidPrice && item.minBidPrice >= priceFrom);
    }
    
    if (filters.minBidPriceTo) {
      const priceTo = parseInt(filters.minBidPriceTo);
      items = items.filter(item => item.minBidPrice && item.minBidPrice <= priceTo);
    }
    
    if (filters.appraisalPriceFrom) {
      const priceFrom = parseInt(filters.appraisalPriceFrom);
      items = items.filter(item => item.appraisalPrice && item.appraisalPrice >= priceFrom);
    }
    
    if (filters.appraisalPriceTo) {
      const priceTo = parseInt(filters.appraisalPriceTo);
      items = items.filter(item => item.appraisalPrice && item.appraisalPrice <= priceTo);
    }
    
    return items;
  };

  // 페이징 처리
  const getPaginatedData = () => {
    const items = getCurrentData();
    const startIdx = (pageNo - 1) * numOfRows;
    const endIdx = startIdx + numOfRows;
    return items.slice(startIdx, endIdx);
  };

  // 초기 로딩 (DB 조회 우선)
  useEffect(() => {
    handleDBQuery();
  }, []);

  // 필터 변경 핸들러
  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  // 검색 버튼 클릭
  const handleSearch = () => {
    setPageNo(1);
  };

  // 필터 초기화
  const handleResetFilters = () => {
    setFilters({
      goodsName: '',
      minBidPriceFrom: '',
      minBidPriceTo: '',
      appraisalPriceFrom: '',
      appraisalPriceTo: '',
    });
    setPageNo(1);
  };

  // 페이지 변경 핸들러
  const handlePageChange = (newPage) => {
    setPageNo(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // 정렬 핸들러
  const handleSort = (field) => {
    if (sortField === field) {
      // 같은 필드 클릭 시 정렬 순서 변경
      setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
    } else {
      // 다른 필드 클릭 시 해당 필드로 내림차순 정렬
      setSortField(field);
      setSortOrder('desc');
    }
  };

  // 전체 페이지 수 계산
  const filteredItems = getCurrentData();
  const totalPages = Math.ceil(filteredItems.length / numOfRows);

  // 페이지 번호 배열 생성 (최대 10개)
  const getPageNumbers = () => {
    const pages = [];
    const maxPages = 10;
    let startPage = Math.max(1, pageNo - Math.floor(maxPages / 2));
    let endPage = Math.min(totalPages, startPage + maxPages - 1);
    
    if (endPage - startPage + 1 < maxPages) {
      startPage = Math.max(1, endPage - maxPages + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  };

  return (
    <div>
      {/* 타이틀 */}
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">
          공매물건 목록
        </h1>
        <button
          onClick={() => setShowFilters(!showFilters)}
          className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600 transition"
        >
          {showFilters ? '필터 숨기기' : '필터 보기'}
        </button>
      </div>

      {showRefreshBanner && (
        <div className="mb-4 bg-green-100 border border-green-200 text-green-800 px-4 py-2 rounded">
          데이터베이스 갱신이 완료되었습니다.
        </div>
      )}

      <div className="mb-6 bg-blue-50 border border-blue-200 text-blue-900 px-4 py-3 rounded-lg flex flex-col md:flex-row md:items-center md:justify-between gap-2">
        <span className="text-sm text-gray-600">
          마지막 갱신: {formatSyncTime(lastSyncedAt)}
        </span>
      </div>

      {/* 버튼 그룹 */}
      <div className="bg-white p-4 rounded-lg shadow mb-6">
        <h2 className="text-lg font-bold text-gray-800 mb-3">데이터 관리</h2>
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
          <button
            onClick={handleApiQuery}
            disabled={isLoading}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            API 조회
          </button>
          <button
            onClick={handleFilterByHistory}
            disabled={isLoading || apiData.length === 0}
            className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            100개 조회
          </button>
          <button
            onClick={handleSaveToDB}
            disabled={isLoading || filteredData.length === 0}
            className="px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            데이터베이스에 저장
          </button>
          <button
            onClick={handleDBQuery}
            disabled={isLoading}
            className="px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            DB 조회
          </button>
          <button
            onClick={handleDeleteAll}
            disabled={isLoading}
            className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            모든 데이터 삭제
          </button>
          <div className="px-4 py-2 bg-gray-100 text-gray-700 rounded text-sm flex items-center justify-center">
            모드: {viewMode === 'db' ? 'DB' : viewMode === 'api' ? 'API' : '100개'}
          </div>
        </div>
      </div>

      {/* 필터 섹션 */}
      {showFilters && (
        <div className="bg-white p-6 rounded-lg shadow mb-6">
          <h2 className="text-lg font-bold text-gray-800 mb-4">검색 필터</h2>
          
          {/* 물건명 검색 */}
          <div className="mb-4">
            <label className="block text-sm text-gray-600 mb-1">물건명</label>
            <input
              type="text"
              name="goodsName"
              value={filters.goodsName}
              onChange={handleFilterChange}
              placeholder="물건명 검색"
              className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* 최저입찰가 필터 */}
          <div className="mb-4">
            <label className="block text-sm text-gray-600 mb-1">최저입찰가</label>
            <div className="flex gap-2 items-center">
              <input
                type="number"
                name="minBidPriceFrom"
                value={filters.minBidPriceFrom}
                onChange={handleFilterChange}
                placeholder="최소 금액"
                className="flex-1 px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <span className="text-gray-500">~</span>
              <input
                type="number"
                name="minBidPriceTo"
                value={filters.minBidPriceTo}
                onChange={handleFilterChange}
                placeholder="최대 금액"
                className="flex-1 px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* 감정가 필터 */}
          <div className="mb-4">
            <label className="block text-sm text-gray-600 mb-1">감정가</label>
            <div className="flex gap-2 items-center">
              <input
                type="number"
                name="appraisalPriceFrom"
                value={filters.appraisalPriceFrom}
                onChange={handleFilterChange}
                placeholder="최소 금액"
                className="flex-1 px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <span className="text-gray-500">~</span>
              <input
                type="number"
                name="appraisalPriceTo"
                value={filters.appraisalPriceTo}
                onChange={handleFilterChange}
                placeholder="최대 금액"
                className="flex-1 px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* 버튼 */}
          <div className="flex gap-3">
            <button
              onClick={handleSearch}
              className="flex-1 bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 transition"
            >
              검색
            </button>
            <button
              onClick={handleResetFilters}
              className="bg-gray-300 text-gray-700 px-6 py-2 rounded hover:bg-gray-400 transition"
            >
              초기화
            </button>
          </div>
        </div>
      )}

      {/* 결과 수 표시 */}
      {!isLoading && !error && (
        <div className="mb-4 text-gray-600">
          전체 <span className="font-bold text-blue-600">{filteredItems.length.toLocaleString()}</span>건
          {sortField && (
            <span className="ml-4 text-sm text-gray-500">
              (정렬: {sortField} {sortOrder === 'asc' ? '오름차순' : '내림차순'})
            </span>
          )}
        </div>
      )}

      {/* 로딩 상태 */}
      {isLoading && (
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">불러오는 중...</p>
        </div>
      )}

      {/* 에러 상태 */}
      {error && !isLoading && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {/* 물건 테이블 (데스크톱) */}
      {!isLoading && !error && goods.length > 0 && (
        <>
          {/* 데스크톱: 테이블 형식 */}
          <div className="hidden md:block">
            <GoodsTable 
              goods={getPaginatedData()}
              sortField={sortField}
              sortOrder={sortOrder}
              onSort={handleSort}
            />
          </div>

          {/* 모바일: 카드 형식 */}
          <div className="md:hidden grid grid-cols-1 gap-4">
            {getPaginatedData().map((item, index) => (
              <GoodsMobileCard 
                key={item.historyNo || item.goodsNo || `mobile-${index}`}
                item={item}
              />
            ))}
          </div>

          {/* 페이징 */}
          {totalPages > 1 && (
            <div className="mt-6 flex justify-center items-center gap-2">
              {/* 첫 페이지 */}
              <button
                onClick={() => handlePageChange(1)}
                disabled={pageNo === 1}
                className={`px-3 py-1 rounded ${
                  pageNo === 1
                    ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                    : 'bg-blue-600 text-white hover:bg-blue-700'
                }`}
              >
                ««
              </button>

              {/* 이전 페이지 */}
              <button
                onClick={() => handlePageChange(pageNo - 1)}
                disabled={pageNo === 1}
                className={`px-3 py-1 rounded ${
                  pageNo === 1
                    ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                    : 'bg-blue-600 text-white hover:bg-blue-700'
                }`}
              >
                이전
              </button>

              {/* 페이지 번호 버튼들 */}
              {getPageNumbers().map((num) => (
                <button
                  key={num}
                  onClick={() => handlePageChange(num)}
                  className={`px-3 py-1 rounded ${
                    pageNo === num
                      ? 'bg-blue-600 text-white font-bold'
                      : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                  }`}
                >
                  {num}
                </button>
              ))}

              {/* 다음 페이지 */}
              <button
                onClick={() => handlePageChange(pageNo + 1)}
                disabled={pageNo === totalPages}
                className={`px-3 py-1 rounded ${
                  pageNo === totalPages
                    ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                    : 'bg-blue-600 text-white hover:bg-blue-700'
                }`}
              >
                다음
              </button>

              {/* 마지막 페이지 */}
              <button
                onClick={() => handlePageChange(totalPages)}
                disabled={pageNo === totalPages}
                className={`px-3 py-1 rounded ${
                  pageNo === totalPages
                    ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                    : 'bg-blue-600 text-white hover:bg-blue-700'
                }`}
              >
                »»
              </button>
            </div>
          )}
        </>
      )}

      {/* 빈 목록 */}
      {!isLoading && !error && goods.length === 0 && (
        <div className="text-center py-12 text-gray-500">
          검색 결과가 없습니다.
        </div>
      )}
    </div>
  );
}

export default ListPage;
