import { useState, useEffect } from 'react';
import GoodsTable from '../components/GoodsTable';
import GoodsMobileCard from '../components/GoodsMobileCard';
import { getGoodsList } from '../utils/api';

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

  // 필터 상태
  const [filters, setFilters] = useState({
    // 기본 검색
    cltrNm: '',           // 물건명
    cltrMnmtNo: '',       // 물건관리번호
    
    // 가격
    goodsPriceFrom: '',   // 감정가 시작
    goodsPriceTo: '',     // 감정가 끝
    openPriceFrom: '',    // 최저입찰가 시작
    openPriceTo: '',      // 최저입찰가 끝
    
    // 입찰일자
    pbctBegnDtm: '',      // 입찰시작일 (YYYYMMDD)
    pbctClsDtm: '',       // 입찰종료일 (YYYYMMDD)
    
    // 카테고리
    ctgrHirkId: '',       // 카테고리 ID
  });

  // 물건 목록 조회
  const fetchGoods = async (signal) => {
    setIsLoading(true);
    setError('');
    
    try {
      // 빈 문자열 필터 제거
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== '') {
          acc[key] = value;
        }
        return acc;
      }, {});

      const response = await getGoodsList(pageNo, numOfRows, cleanFilters, signal);
      
      if (response.success && response.data?.items) {
        let items = response.data.items;
        
        // 클라이언트 사이드 정렬 (필요한 경우)
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
        
        setGoods(items);
        
        // totalCount는 response.data에 있음 (백엔드 Body 구조)
        const count = response.data?.totalCount || items.length;
        setTotalCount(count);
      } else {
        setError('물건 정보를 불러올 수 없습니다.');
        setGoods([]);
      }
    } catch (err) {
      // AbortError는 무시 (정상적인 취소)
      if (err.name === 'CanceledError' || err.name === 'AbortError') {
        console.log('API 요청이 취소되었습니다.');
        return;
      }
      
      console.error('물건 조회 오류:', err);
      setError('물건 정보를 불러오는 중 오류가 발생했습니다.');
      setGoods([]);
    } finally {
      setIsLoading(false);
    }
  };

  // 페이지 또는 필터 변경 시 물건 조회
  useEffect(() => {
    // AbortController 생성
    const abortController = new AbortController();
    
    fetchGoods(abortController.signal);
    
    // cleanup 함수: 컴포넌트가 언마운트되거나 의존성이 변경될 때 실행
    return () => {
      abortController.abort(); // 진행 중인 요청 취소
      console.log('API 요청 취소 (페이지 변경 또는 컴포넌트 언마운트)');
    };
  }, [pageNo]);

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
    // pageNo가 1이면 useEffect가 자동으로 호출되지 않으므로 수동 호출
    if (pageNo === 1) {
      const abortController = new AbortController();
      fetchGoods(abortController.signal);
    }
  };

  // 필터 초기화
  const handleResetFilters = () => {
    setFilters({
      cltrNm: '',
      cltrMnmtNo: '',
      goodsPriceFrom: '',
      goodsPriceTo: '',
      openPriceFrom: '',
      openPriceTo: '',
      pbctBegnDtm: '',
      pbctClsDtm: '',
      ctgrHirkId: '',
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

  // 정렬 변경 시 재조회
  useEffect(() => {
    if (sortField) {
      // AbortController 생성
      const abortController = new AbortController();
      
      fetchGoods(abortController.signal);
      
      // cleanup 함수
      return () => {
        abortController.abort();
        console.log('API 요청 취소 (정렬 변경)');
      };
    }
  }, [sortField, sortOrder]);

  // 전체 페이지 수 계산
  const totalPages = Math.ceil(totalCount / numOfRows);

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

      {/* 필터 섹션 */}
      {showFilters && (
        <div className="bg-white p-6 rounded-lg shadow mb-6">
          <h2 className="text-lg font-bold text-gray-800 mb-4">검색 필터</h2>
          
          {/* 기본 검색 */}
          <div className="mb-4">
            <h3 className="font-semibold text-gray-700 mb-2">기본 검색</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1">물건명</label>
                <input
                  type="text"
                  name="cltrNm"
                  value={filters.cltrNm}
                  onChange={handleFilterChange}
                  placeholder="물건명 검색"
                  className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">물건관리번호 (회차 공통)</label>
                <input
                  type="text"
                  name="cltrMnmtNo"
                  value={filters.cltrMnmtNo}
                  onChange={handleFilterChange}
                  placeholder="예: 2025-1234-001"
                  className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <p className="text-xs text-gray-500 mt-1">같은 물건의 모든 회차를 검색합니다</p>
              </div>
            </div>
          </div>

          {/* 가격 필터 */}
          <div className="mb-4">
            <h3 className="font-semibold text-gray-700 mb-2">가격 범위</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1">감정가</label>
                <div className="flex gap-2 items-center">
                  <input
                    type="number"
                    name="goodsPriceFrom"
                    value={filters.goodsPriceFrom}
                    onChange={handleFilterChange}
                    placeholder="최소"
                    className="flex-1 px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                  <span className="text-gray-500">~</span>
                  <input
                    type="number"
                    name="goodsPriceTo"
                    value={filters.goodsPriceTo}
                    onChange={handleFilterChange}
                    placeholder="최대"
                    className="flex-1 px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">최저입찰가</label>
                <div className="flex gap-2 items-center">
                  <input
                    type="number"
                    name="openPriceFrom"
                    value={filters.openPriceFrom}
                    onChange={handleFilterChange}
                    placeholder="최소"
                    className="flex-1 px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                  <span className="text-gray-500">~</span>
                  <input
                    type="number"
                    name="openPriceTo"
                    value={filters.openPriceTo}
                    onChange={handleFilterChange}
                    placeholder="최대"
                    className="flex-1 px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
            </div>
          </div>

          {/* 입찰일자 필터 */}
          <div className="mb-4">
            <h3 className="font-semibold text-gray-700 mb-2">입찰 일자</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1">입찰 시작일</label>
                <input
                  type="date"
                  name="pbctBegnDtm"
                  value={filters.pbctBegnDtm}
                  onChange={handleFilterChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">입찰 종료일</label>
                <input
                  type="date"
                  name="pbctClsDtm"
                  value={filters.pbctClsDtm}
                  onChange={handleFilterChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
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
          전체 <span className="font-bold text-blue-600">{totalCount.toLocaleString()}</span>건
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
              goods={goods}
              onFavoriteChange={fetchGoods}
              sortField={sortField}
              sortOrder={sortOrder}
              onSort={handleSort}
            />
          </div>

          {/* 모바일: 카드 형식 */}
          <div className="md:hidden grid grid-cols-1 gap-4">
            {goods.map((item, index) => (
              <GoodsMobileCard 
                key={item.historyNo || `mobile-${index}`}
                item={item}
                onFavoriteChange={fetchGoods}
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
