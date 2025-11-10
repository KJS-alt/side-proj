import { useState } from 'react';
import { addFavorite, isAuthenticated } from '../utils/api';

/**
 * 숫자를 천단위 구분 형식으로 변환
 */
const formatNumber = (num) => {
  if (!num) return '0';
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

/**
 * 날짜 문자열을 포맷
 */
const formatDate = (dateStr) => {
  if (!dateStr || dateStr.length < 8) return '-';
  
  const year = dateStr.substring(0, 4);
  const month = dateStr.substring(4, 6);
  const day = dateStr.substring(6, 8);
  
  return `${year}-${month}-${day}`;
};

/**
 * GoodsMobileCard 컴포넌트 - 모바일용 카드 형식
 */
function GoodsMobileCard({ item, onFavoriteChange }) {
  const [isLoading, setIsLoading] = useState(false);
  const [showDetails, setShowDetails] = useState(false);
  const isLoggedIn = isAuthenticated();

  // 관심물건 등록
  const handleFavoriteToggle = async () => {
    if (!isLoggedIn) {
      alert('로그인이 필요합니다.');
      return;
    }

    setIsLoading(true);
    
    try {
      await addFavorite({
        historyNo: item.historyNo,
        goodsNo: item.goodsNo,
        goodsName: item.goodsName,
        minBidPrice: item.minBidPrice,
        bidCloseDate: item.bidCloseDate,
      });
      
      alert('관심물건에 등록되었습니다.');
      if (onFavoriteChange) onFavoriteChange();
    } catch (error) {
      console.error('관심물건 처리 실패:', error);
      if (error.response?.data?.message) {
        alert(error.response.data.message);
      } else {
        alert('처리 중 오류가 발생했습니다.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="border border-gray-300 rounded-lg p-4 shadow bg-white">
      {/* 물건명 */}
      <h3 className="text-base font-bold text-gray-800 mb-2 line-clamp-2">
        {item.goodsName || '물건명 없음'}
      </h3>

      {/* 상태 */}
      {item.statusName && (
        <div className="mb-2">
          <span className="inline-block bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">
            {item.statusName}
          </span>
        </div>
      )}

      {/* 가격 정보 */}
      <div className="space-y-1 mb-3 text-sm">
        {item.minBidPrice && (
          <div className="text-red-600 font-bold">
            최저입찰가: {formatNumber(item.minBidPrice)}원
          </div>
        )}
        {item.appraisalPrice && (
          <div className="text-gray-600">
            감정가: {formatNumber(item.appraisalPrice)}원
          </div>
        )}
        {item.feeRate && (
          <div className="text-gray-500 text-xs">
            {item.feeRate}
          </div>
        )}
      </div>

      {/* 입찰 마감일 */}
      {item.bidCloseDate && (
        <div className="mb-3 text-sm text-gray-600">
          <span className="font-semibold">입찰마감:</span> {formatDate(item.bidCloseDate)}
        </div>
      )}

      {/* 상세정보 토글 버튼 */}
      <button
        onClick={() => setShowDetails(!showDetails)}
        className="text-sm text-blue-600 hover:text-blue-700 mb-2"
      >
        {showDetails ? '▲ 상세정보 숨기기' : '▼ 상세정보 보기'}
      </button>

      {/* 상세 정보 */}
      {showDetails && (
        <div className="mt-3 pt-3 border-t border-gray-200 space-y-2 text-sm">
          <div>
            <span className="font-semibold text-gray-700">물건이력번호:</span>
            <p className="text-gray-600">{item.historyNo || '-'}</p>
          </div>
          <div>
            <span className="font-semibold text-gray-700">물건관리번호:</span>
            <p className="text-gray-600">{item.goodsNo || '-'}</p>
          </div>
          <div>
            <span className="font-semibold text-gray-700">주소:</span>
            <p className="text-gray-600">{item.roadAddress || item.address || '-'}</p>
          </div>
          <div>
            <span className="font-semibold text-gray-700">카테고리:</span>
            <p className="text-gray-600">{item.categoryName || '-'}</p>
          </div>
        </div>
      )}

      {/* 관심등록 버튼 */}
      {isLoggedIn && (
        <button
          onClick={handleFavoriteToggle}
          disabled={isLoading}
          className={`mt-3 w-full px-4 py-2 rounded transition ${
            isLoading
              ? 'bg-gray-300 cursor-not-allowed'
              : 'bg-yellow-500 hover:bg-yellow-600 text-white'
          }`}
        >
          {isLoading ? '처리중...' : '★ 관심등록'}
        </button>
      )}
    </div>
  );
}

export default GoodsMobileCard;

