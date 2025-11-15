import { useState } from 'react';
import { Link } from 'react-router-dom';

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
function GoodsMobileCard({ item }) {
  const [showDetails, setShowDetails] = useState(false);

  return (
    <div className="border border-gray-300 rounded-lg p-4 shadow bg-white">
      {/* 물건명 */}
      <h3 className="text-base font-bold text-gray-800 mb-2 line-clamp-2">
        {item.goodsName || '물건명 없음'}
      </h3>

      {/* 물건이력번호 */}
      {item.historyNo && (
        <div className="mb-2 text-sm text-gray-600">
          물건이력번호: {item.historyNo}
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
            <span className="font-semibold text-gray-700">주소:</span>
            <p className="text-gray-600">{item.address || '-'}</p>
          </div>
          <div>
            <span className="font-semibold text-gray-700">입찰마감일:</span>
            <p className="text-gray-600">{formatDate(item.bidCloseDate)}</p>
          </div>
        </div>
      )}

      {/* 구매하기 버튼 */}
      {item.historyNo && (
        <Link
          to={`/goods/${item.historyNo}`}
          className="mt-3 w-full block text-center bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition font-semibold"
        >
          상세보기 / 구매하기
        </Link>
      )}

    </div>
  );
}

export default GoodsMobileCard;

