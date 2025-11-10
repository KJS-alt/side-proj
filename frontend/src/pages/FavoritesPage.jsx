import { useState, useEffect } from 'react';
import { getFavorites, deleteFavorite } from '../utils/api';

/**
 * 숫자를 천단위 구분 형식으로 변환
 */
const formatNumber = (num) => {
  if (!num) return '0';
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

/**
 * 날짜 문자열을 포맷 (YYYYMMDDHHmmss -> YYYY-MM-DD HH:mm)
 */
const formatDate = (dateStr) => {
  if (!dateStr || dateStr.length < 12) return '-';
  
  const year = dateStr.substring(0, 4);
  const month = dateStr.substring(4, 6);
  const day = dateStr.substring(6, 8);
  const hour = dateStr.substring(8, 10);
  const minute = dateStr.substring(10, 12);
  
  return `${year}-${month}-${day} ${hour}:${minute}`;
};

/**
 * FavoritesPage 컴포넌트 - 관심물건 페이지
 */
function FavoritesPage() {
  const [favorites, setFavorites] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  // 관심물건 목록 조회
  const fetchFavorites = async () => {
    setIsLoading(true);
    setError('');
    
    try {
      const response = await getFavorites();
      
      if (response.success && response.data) {
        setFavorites(response.data);
      } else {
        setError('관심물건을 불러올 수 없습니다.');
        setFavorites([]);
      }
    } catch (err) {
      console.error('관심물건 조회 오류:', err);
      setError('관심물건을 불러오는 중 오류가 발생했습니다.');
      setFavorites([]);
    } finally {
      setIsLoading(false);
    }
  };

  // 페이지 로드 시 관심물건 조회
  useEffect(() => {
    fetchFavorites();
  }, []);

  // 관심물건 삭제 핸들러
  const handleDelete = async (id, goodsName) => {
    if (!confirm(`"${goodsName}"을(를) 관심물건에서 삭제하시겠습니까?`)) {
      return;
    }

    try {
      const response = await deleteFavorite(id);
      
      if (response.success) {
        alert('관심물건에서 삭제되었습니다.');
        // 목록 새로고침
        fetchFavorites();
      } else {
        alert(response.message || '삭제에 실패했습니다.');
      }
    } catch (err) {
      console.error('관심물건 삭제 오류:', err);
      if (err.response?.data?.message) {
        alert(err.response.data.message);
      } else {
        alert('삭제 중 오류가 발생했습니다.');
      }
    }
  };

  return (
    <div>
      {/* 타이틀 */}
      <h1 className="text-3xl font-bold text-gray-800 mb-6">
        내 관심물건
      </h1>

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

      {/* 관심물건 목록 */}
      {!isLoading && !error && favorites.length > 0 && (
        <>
          <div className="mb-4 text-gray-600">
            전체 <span className="font-bold text-blue-600">{favorites.length}</span>건
          </div>

          <div className="space-y-4">
            {favorites.map((favorite) => (
              <div 
                key={favorite.id}
                className="bg-white border border-gray-300 rounded-lg p-4 shadow hover:shadow-md transition"
              >
                <div className="flex justify-between items-start">
                  {/* 물건 정보 */}
                  <div className="flex-1">
                    {/* 물건명 */}
                    <h3 className="text-lg font-bold text-gray-800 mb-2">
                      {favorite.goodsName || '물건명 없음'}
                    </h3>

                    {/* 가격 정보 */}
                    {favorite.minBidPrice && (
                      <div className="text-red-600 font-bold mb-2">
                        최저입찰가: {formatNumber(favorite.minBidPrice)}원
                      </div>
                    )}

                    {/* 입찰 마감일 */}
                    {favorite.bidCloseDate && (
                      <div className="text-sm text-gray-600 mb-2">
                        <span className="font-semibold">입찰마감:</span>{' '}
                        {formatDate(favorite.bidCloseDate)}
                      </div>
                    )}

                    {/* 물건이력번호 */}
                    {favorite.historyNo && (
                      <div className="text-xs text-gray-500">
                        물건이력번호: {favorite.historyNo}
                      </div>
                    )}
                    
                    {/* 물건관리번호 */}
                    {favorite.goodsNo && (
                      <div className="text-xs text-gray-500">
                        물건관리번호: {favorite.goodsNo}
                      </div>
                    )}

                    {/* 등록일 */}
                    {favorite.createdAt && (
                      <div className="text-xs text-gray-500 mt-1">
                        등록일: {new Date(favorite.createdAt).toLocaleDateString('ko-KR')}
                      </div>
                    )}
                  </div>

                  {/* 삭제 버튼 */}
                  <button
                    onClick={() => handleDelete(favorite.id, favorite.goodsName)}
                    className="ml-4 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition flex-shrink-0"
                  >
                    삭제
                  </button>
                </div>
              </div>
            ))}
          </div>
        </>
      )}

      {/* 빈 목록 */}
      {!isLoading && !error && favorites.length === 0 && (
        <div className="text-center py-12">
          <div className="text-gray-400 mb-4">
            <svg 
              className="mx-auto h-16 w-16" 
              fill="none" 
              viewBox="0 0 24 24" 
              stroke="currentColor"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" 
              />
            </svg>
          </div>
          <p className="text-gray-500 text-lg mb-4">
            등록된 관심물건이 없습니다.
          </p>
          <a 
            href="/goods"
            className="inline-block bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 transition"
          >
            물건 둘러보기
          </a>
        </div>
      )}
    </div>
  );
}

export default FavoritesPage;

