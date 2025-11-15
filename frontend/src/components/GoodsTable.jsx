import React, { useState } from 'react';
import { Link } from 'react-router-dom';

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
 * GoodsTable 컴포넌트 - 물건 정보 테이블
 */
function GoodsTable({ goods, sortField, sortOrder, onSort }) {
  const [expandedRow, setExpandedRow] = useState(null);

  // 행 클릭 핸들러 (상세 정보 토글)
  const handleRowClick = (itemId) => {
    setExpandedRow(expandedRow === itemId ? null : itemId);
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
              입찰마감일
            </th>
            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700 border-b">
              소재지
            </th>
          </tr>
        </thead>
        <tbody>
          {goods.map((item, index) => (
            <React.Fragment key={item.historyNo || `goods-${index}`}>
              {/* 메인 행 */}
              <tr
                onClick={() => handleRowClick(item.historyNo)}
                className="hover:bg-gray-50 cursor-pointer border-b"
              >
                <td className="px-4 py-3 text-sm text-gray-600">
                  {item.historyNo || '-'}
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
                  {formatDate(item.bidCloseDate)}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600">
                  <div className="max-w-md truncate" title={item.address}>
                    {item.address || '-'}
                  </div>
                </td>
              </tr>

              {/* 확장 행 (상세 정보) */}
              {expandedRow === item.historyNo && (
                <tr className="bg-gray-50">
                  <td colSpan={6} className="px-4 py-4">
                    <div className="text-sm mb-4">
                      <div className="space-y-2">
                        <div>
                          <span className="font-semibold text-gray-700">물건소재지:</span>
                          <p className="text-gray-600 mt-1">{item.address || '-'}</p>
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                          <div>
                            <span className="font-semibold text-gray-700">최저입찰가:</span>
                            <p className="text-red-600 font-bold mt-1">{formatNumber(item.minBidPrice)}원</p>
                          </div>
                          <div>
                            <span className="font-semibold text-gray-700">감정가:</span>
                            <p className="text-gray-600 mt-1">{formatNumber(item.appraisalPrice)}원</p>
                          </div>
                        </div>
                      </div>
                    </div>
                    
                    {/* 구매하기 버튼 */}
                    {item.historyNo && (
                      <div className="text-center pt-4 border-t">
                        <Link
                          to={`/goods/${item.historyNo}`}
                          onClick={(e) => e.stopPropagation()}
                          className="inline-block bg-blue-600 text-white px-8 py-2 rounded-lg hover:bg-blue-700 transition font-semibold"
                        >
                          상세보기 / 구매하기
                        </Link>
                      </div>
                    )}
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

