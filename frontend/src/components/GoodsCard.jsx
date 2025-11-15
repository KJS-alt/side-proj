/**
 * 숫자를 천단위 구분 형식으로 변환
 * @param {number} num - 숫자
 * @returns {string} 포맷된 문자열
 */
const formatNumber = (num) => {
  if (!num) return '0';
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

/**
 * 날짜 문자열을 포맷 (YYYYMMDDHHmmss -> YYYY-MM-DD HH:mm)
 * @param {string} dateStr - 날짜 문자열
 * @returns {string} 포맷된 날짜
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
 * GoodsCard 컴포넌트 - 물건 정보 카드
 * @param {object} props.goods - 물건 정보
 */
function GoodsCard({ goods }) {

  return (
    <div className="border border-gray-300 rounded-lg p-4 shadow hover:shadow-lg transition cursor-pointer">
      {/* 물건명 */}
      <h3 className="text-lg font-bold text-gray-800 mb-2 line-clamp-2">
        {goods.goodsName || '물건명 없음'}
      </h3>

      {/* 상태 */}
      {goods.statusName && (
        <div className="mb-2">
          <span className="inline-block bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">
            {goods.statusName}
          </span>
        </div>
      )}

      {/* 가격 정보 */}
      <div className="space-y-1 mb-3 text-sm">
        {goods.appraisalPrice && (
          <div className="text-gray-600">
            <span className="font-semibold">감정가:</span> {formatNumber(goods.appraisalPrice)}원
          </div>
        )}
        {goods.minBidPrice && (
          <div className="text-red-600 font-bold">
            <span>최저입찰가:</span> {formatNumber(goods.minBidPrice)}원
          </div>
        )}
        {goods.feeRate && (
          <div className="text-gray-500 text-xs">
            {goods.feeRate}
          </div>
        )}
      </div>

      {/* 입찰 일정 */}
      <div className="space-y-1 mb-3 text-sm text-gray-600">
        {goods.bidStartDate && (
          <div>
            <span className="font-semibold">입찰시작:</span> {formatDate(goods.bidStartDate)}
          </div>
        )}
        {goods.bidCloseDate && (
          <div>
            <span className="font-semibold">입찰마감:</span> {formatDate(goods.bidCloseDate)}
          </div>
        )}
      </div>

      {/* 주소 */}
      {(goods.roadAddress || goods.address) && (
        <div className="mb-3 text-sm text-gray-600">
          <span className="font-semibold">주소:</span>{' '}
          {goods.roadAddress || goods.address}
        </div>
      )}

      {/* 카테고리 */}
      {goods.categoryName && (
        <div className="mb-3 text-xs text-gray-500">
          {goods.categoryName}
        </div>
      )}

      {/* 조회수, 관심수 */}
      {(goods.inquiryCount || goods.favoriteCount) && (
        <div className="flex items-center gap-2 text-xs text-gray-500">
          {goods.inquiryCount && (
            <span>조회 {formatNumber(goods.inquiryCount)}</span>
          )}
          {goods.favoriteCount && (
            <span>관심 {formatNumber(goods.favoriteCount)}</span>
          )}
        </div>
      )}
    </div>
  );
}

export default GoodsCard;

