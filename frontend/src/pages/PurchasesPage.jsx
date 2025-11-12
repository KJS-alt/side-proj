import { useState, useEffect } from 'react';
import { getAllPurchases } from '../utils/api';
import { Link } from 'react-router-dom';

/**
 * PurchasesPage 컴포넌트 - 구매 목록 페이지
 * 구매한 물건 목록을 표시
 */
function PurchasesPage() {
  const [purchases, setPurchases] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  // 구매 목록 조회
  useEffect(() => {
    const fetchPurchases = async () => {
      setIsLoading(true);
      setError('');
      
      try {
        const response = await getAllPurchases();
        
        if (response.success && response.items) {
          setPurchases(response.items);
        } else {
          setError('구매 목록을 불러올 수 없습니다.');
        }
      } catch (err) {
        console.error('구매 목록 조회 오류:', err);
        setError('구매 목록을 불러오는 중 오류가 발생했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchPurchases();
  }, []);

  // 가격 포맷 (천단위 콤마)
  const formatNumber = (num) => {
    if (!num) return '0';
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  };

  // 날짜 포맷
  const formatDate = (dateStr) => {
    if (!dateStr) return '-';
    
    try {
      const date = new Date(dateStr);
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return dateStr;
    }
  };

  // 상태 뱃지 색상
  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'COMPLETED':
        return '완료';
      case 'PENDING':
        return '대기중';
      case 'CANCELLED':
        return '취소됨';
      default:
        return status;
    }
  };

  return (
    <div>
      {/* 타이틀 */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-800">구매 목록</h1>
        <p className="text-gray-600 mt-2">구매한 물건 내역을 확인할 수 있습니다.</p>
      </div>

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

      {/* 구매 목록 */}
      {!isLoading && !error && purchases.length === 0 && (
        <div className="text-center py-12 bg-gray-50 rounded-lg">
          <p className="text-gray-600 text-lg">구매한 물건이 없습니다.</p>
          <Link
            to="/list"
            className="inline-block mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            물건 목록 보기
          </Link>
        </div>
      )}

      {/* 구매 목록 테이블 */}
      {!isLoading && !error && purchases.length > 0 && (
        <>
          {/* 총 구매 건수 */}
          <div className="mb-4">
            <span className="text-gray-600">
              총 <span className="font-bold text-blue-600">{purchases.length.toLocaleString()}</span>건의 구매 내역
            </span>
          </div>

          {/* 데스크톱: 테이블 형식 */}
          <div className="hidden md:block bg-white rounded-lg shadow overflow-hidden">
            <table className="min-w-full">
              <thead className="bg-gray-100">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">
                    물건이력번호
                  </th>
                  <th className="px-6 py-3 text-right text-sm font-semibold text-gray-700">
                    구매가격
                  </th>
                  <th className="px-6 py-3 text-center text-sm font-semibold text-gray-700">
                    상태
                  </th>
                  <th className="px-6 py-3 text-center text-sm font-semibold text-gray-700">
                    구매일시
                  </th>
                  <th className="px-6 py-3 text-center text-sm font-semibold text-gray-700">
                    상세보기
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {purchases.map((purchase) => (
                  <tr key={purchase.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 text-sm text-gray-800">
                      {purchase.historyNo}
                    </td>
                    <td className="px-6 py-4 text-sm text-red-600 font-bold text-right">
                      {formatNumber(purchase.purchasePrice)}원
                    </td>
                    <td className="px-6 py-4 text-center">
                      <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${getStatusColor(purchase.purchaseStatus)}`}>
                        {getStatusText(purchase.purchaseStatus)}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600 text-center">
                      {formatDate(purchase.createdAt)}
                    </td>
                    <td className="px-6 py-4 text-center">
                      <Link
                        to={`/goods/${purchase.historyNo}`}
                        className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                      >
                        상세보기
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* 모바일: 카드 형식 */}
          <div className="md:hidden space-y-4">
            {purchases.map((purchase) => (
              <div key={purchase.id} className="bg-white rounded-lg shadow p-4">
                <div className="flex justify-between items-start mb-3">
                  <div>
                    <p className="text-xs text-gray-500">물건이력번호</p>
                    <p className="text-sm font-semibold text-gray-800">{purchase.historyNo}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusColor(purchase.purchaseStatus)}`}>
                    {getStatusText(purchase.purchaseStatus)}
                  </span>
                </div>
                
                <div className="mb-3">
                  <p className="text-xs text-gray-500">구매가격</p>
                  <p className="text-lg font-bold text-red-600">
                    {formatNumber(purchase.purchasePrice)}원
                  </p>
                </div>
                
                <div className="mb-3">
                  <p className="text-xs text-gray-500">구매일시</p>
                  <p className="text-sm text-gray-700">{formatDate(purchase.createdAt)}</p>
                </div>
                
                <Link
                  to={`/goods/${purchase.historyNo}`}
                  className="block w-full text-center bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition text-sm font-medium"
                >
                  상세보기
                </Link>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
}

export default PurchasesPage;

