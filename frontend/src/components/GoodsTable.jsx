import React, { useState } from 'react';
import { addFavorite, deleteFavoriteByGoodsNo, isAuthenticated } from '../utils/api';

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
  if (!dateStr || dateStr.length < 8) return '-';
  
  const year = dateStr.substring(0, 4);
  const month = dateStr.substring(4, 6);
  const day = dateStr.substring(6, 8);
  
  if (dateStr.length >= 12) {
    const hour = dateStr.substring(8, 10);
    const minute = dateStr.substring(10, 12);
    return `${year}-${month}-${day} ${hour}:${minute}`;
  }
  
  return `${year}-${month}-${day}`;
};

/**
 * 지역 추출 (주소에서 시도 추출)
 */
const extractRegion = (address) => {
  if (!address) return '-';
  const parts = address.split(' ');
  return parts[0] || '-';
};

/**
 * GoodsTable 컴포넌트 - 물건 정보 테이블
 */
function GoodsTable({ goods, onFavoriteChange, sortField, sortOrder, onSort }) {
  const [expandedRow, setExpandedRow] = useState(null);
  const [loadingFavorite, setLoadingFavorite] = useState({});
  const isLoggedIn = isAuthenticated();

  // 행 클릭 핸들러 (상세 정보 토글)
  const handleRowClick = (goodsNo) => {
    setExpandedRow(expandedRow === goodsNo ? null : goodsNo);
  };

  // 관심물건 등록/삭제
  const handleFavoriteToggle = async (e, item) => {
    e.stopPropagation(); // 행 클릭 이벤트 전파 중지
    
    if (!isLoggedIn) {
      alert('로그인이 필요합니다.');
      return;
    }

    setLoadingFavorite(prev => ({ ...prev, [item.goodsNo]: true }));
    
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
      setLoadingFavorite(prev => ({ ...prev, [item.goodsNo]: false }));
    }
  };

  // 정렬 아이콘 표시
  const getSortIcon = (field) => {
    if (sortField !== field) return '⇅';
    return sortOrder === 'asc' ? '↑' : '↓';
  };

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full bg-white border border-gray-300">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700 border-b">
              물건이력번호
            </th>
            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700 border-b">
              물건관리번호
            </th>
            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700 border-b">
              물건명
            </th>
            <th 
              className="px-4 py-3 text-right text-sm font-semibold text-gray-700 border-b cursor-pointer hover:bg-gray-200"
              onClick={() => onSort('minBidPrice')}
            >
              최저입찰가 {getSortIcon('minBidPrice')}
            </th>
            <th 
              className="px-4 py-3 text-right text-sm font-semibold text-gray-700 border-b cursor-pointer hover:bg-gray-200"
              onClick={() => onSort('appraisalPrice')}
            >
              감정가 {getSortIcon('appraisalPrice')}
            </th>
            <th className="px-4 py-3 text-center text-sm font-semibold text-gray-700 border-b">
              비율
            </th>
            <th 
              className="px-4 py-3 text-center text-sm font-semibold text-gray-700 border-b cursor-pointer hover:bg-gray-200"
              onClick={() => onSort('bidCloseDate')}
            >
              입찰마감 {getSortIcon('bidCloseDate')}
            </th>
            <th className="px-4 py-3 text-center text-sm font-semibold text-gray-700 border-b">
              상태
            </th>
            <th className="px-4 py-3 text-center text-sm font-semibold text-gray-700 border-b">
              지역
            </th>
            {isLoggedIn && (
              <th className="px-4 py-3 text-center text-sm font-semibold text-gray-700 border-b">
                관심
              </th>
            )}
          </tr>
        </thead>
        <tbody>
          {goods.map((item, index) => (
            <React.Fragment key={item.historyNo || `goods-${index}`}>
              {/* 메인 행 */}
              <tr
                onClick={() => handleRowClick(item.goodsNo)}
                className="hover:bg-gray-50 cursor-pointer border-b"
              >
                <td className="px-4 py-3 text-sm text-gray-600">
                  {item.historyNo || '-'}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600">
                  {item.goodsNo || '-'}
                </td>
                <td className="px-4 py-3 text-sm text-gray-800">
                  <div className="max-w-md truncate" title={item.goodsName}>
                    {item.goodsName || '-'}
                  </div>
                </td>
                <td className="px-4 py-3 text-sm text-red-600 font-bold text-right">
                  {item.minBidPrice ? `${formatNumber(item.minBidPrice)}원` : '-'}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600 text-right">
                  {item.appraisalPrice ? `${formatNumber(item.appraisalPrice)}원` : '-'}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600 text-center">
                  {item.feeRate || '-'}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600 text-center">
                  {formatDate(item.bidCloseDate)}
                </td>
                <td className="px-4 py-3 text-center">
                  <span className="inline-block bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">
                    {item.statusName || '-'}
                  </span>
                </td>
                <td className="px-4 py-3 text-sm text-gray-600 text-center">
                  {extractRegion(item.roadAddress || item.address)}
                </td>
                {isLoggedIn && (
                  <td className="px-4 py-3 text-center">
                    <button
                      onClick={(e) => handleFavoriteToggle(e, item)}
                      disabled={loadingFavorite[item.goodsNo]}
                      className={`text-xs px-3 py-1 rounded transition ${
                        loadingFavorite[item.goodsNo]
                          ? 'bg-gray-300 cursor-not-allowed'
                          : 'bg-yellow-500 hover:bg-yellow-600 text-white'
                      }`}
                    >
                      {loadingFavorite[item.goodsNo] ? '...' : '★'}
                    </button>
                  </td>
                )}
              </tr>

              {/* 확장 행 (상세 정보) */}
              {expandedRow === item.goodsNo && (
                <tr className="bg-gray-50">
                  <td colSpan={isLoggedIn ? 9 : 8} className="px-4 py-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                      {/* 좌측 */}
                      <div className="space-y-2">
                        <div>
                          <span className="font-semibold text-gray-700">물건이력번호:</span>
                          <p className="text-gray-600 mt-1">{item.historyNo || '-'}</p>
                        </div>
                        <div>
                          <span className="font-semibold text-gray-700">물건 상세:</span>
                          <p className="text-gray-600 mt-1">{item.goodsDetail || '-'}</p>
                        </div>
                        <div>
                          <span className="font-semibold text-gray-700">지번 주소:</span>
                          <p className="text-gray-600 mt-1">{item.address || '-'}</p>
                        </div>
                        <div>
                          <span className="font-semibold text-gray-700">도로명 주소:</span>
                          <p className="text-gray-600 mt-1">{item.roadAddress || '-'}</p>
                        </div>
                        <div>
                          <span className="font-semibold text-gray-700">카테고리:</span>
                          <p className="text-gray-600 mt-1">{item.categoryName || '-'}</p>
                        </div>
                      </div>

                      {/* 우측 */}
                      <div className="space-y-2">
                        <div>
                          <span className="font-semibold text-gray-700">입찰 시작:</span>
                          <p className="text-gray-600 mt-1">{formatDate(item.bidStartDate)}</p>
                        </div>
                        <div>
                          <span className="font-semibold text-gray-700">입찰 방식:</span>
                          <p className="text-gray-600 mt-1">{item.bidMethodName || '-'}</p>
                        </div>
                        <div>
                          <span className="font-semibold text-gray-700">처분 방식:</span>
                          <p className="text-gray-600 mt-1">{item.saleTypeName || '-'}</p>
                        </div>
                        <div>
                          <span className="font-semibold text-gray-700">조회수/관심:</span>
                          <p className="text-gray-600 mt-1">
                            조회 {formatNumber(item.inquiryCount || 0)} / 
                            관심 {formatNumber(item.favoriteCount || 0)}
                          </p>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
              )}
            </React.Fragment>
          ))}
        </tbody>
      </table>

      {/* 빈 목록 */}
      {goods.length === 0 && (
        <div className="text-center py-8 text-gray-500">
          검색 결과가 없습니다.
        </div>
      )}
    </div>
  );
}

export default GoodsTable;

