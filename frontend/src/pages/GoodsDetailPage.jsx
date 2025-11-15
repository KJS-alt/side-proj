import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getGoodsDetail, createPurchase, getPurchasesByHistoryNo } from '../utils/api';
import PurchaseModal from '../components/PurchaseModal';

/**
 * GoodsDetailPage 컴포넌트
 * 물건 상세 페이지 - 물건 정보 표시 및 구매 기능
 */
function GoodsDetailPage() {
  const { historyNo } = useParams();
  const navigate = useNavigate();

  const [goods, setGoods] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [isPurchaseModalOpen, setIsPurchaseModalOpen] = useState(false);
  const [isAlreadyPurchased, setIsAlreadyPurchased] = useState(false);

  // 물건 상세 정보 조회
  useEffect(() => {
    const fetchGoodsDetail = async () => {
      setIsLoading(true);
      try {
        const response = await getGoodsDetail(historyNo);
        
        if (response.success && response.data) {
          setGoods(response.data);
        } else {
          setError('물건 정보를 불러올 수 없습니다.');
        }
      } catch (err) {
        console.error('물건 상세 조회 오류:', err);
        setError('물건 정보를 불러오는 중 오류가 발생했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchGoodsDetail();
  }, [historyNo]);

  useEffect(() => {
    const fetchPurchaseStatus = async () => {
      if (!historyNo) return;
      try {
        const response = await getPurchasesByHistoryNo(historyNo);
        if (response.success && Array.isArray(response.items)) {
          setIsAlreadyPurchased(response.items.length > 0);
        } else {
          setIsAlreadyPurchased(false);
        }
      } catch (err) {
        console.error('구매 상태 조회 오류:', err);
      }
    };
    fetchPurchaseStatus();
  }, [historyNo]);

  // 가격 포맷 (천단위 콤마)
  const formatPrice = (price) => {
    if (!price) return '0';
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  };

  // 구매하기 버튼 클릭
  const handlePurchaseClick = () => {
    if (isAlreadyPurchased) return;
    setIsPurchaseModalOpen(true);
  };

  // 구매 처리
  const handlePurchase = async () => {
    try {
      const response = await createPurchase(goods.historyNo, goods.minBidPrice);
      
      if (response.success) {
        alert('구매가 완료되었습니다!');
        setIsPurchaseModalOpen(false);
        setIsAlreadyPurchased(true);
        // 홈으로 이동
        navigate('/');
      } else {
        alert('구매에 실패했습니다: ' + (response.message || ''));
        if (response.message && response.message.includes('이미 구매')) {
          setIsAlreadyPurchased(true);
        }
      }
    } catch (err) {
      console.error('구매 오류:', err);
      alert('구매 중 오류가 발생했습니다.');
    }
  };

  // 로딩 중
  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p className="ml-4 text-gray-600">불러오는 중...</p>
      </div>
    );
  }

  // 에러
  if (error || !goods) {
    return (
      <div className="max-w-4xl mx-auto">
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error || '물건을 찾을 수 없습니다.'}
        </div>
        <button
          onClick={() => navigate('/')}
          className="mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
        >
          홈으로 돌아가기
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto">
      {/* 뒤로가기 버튼 */}
      <button
        onClick={() => navigate('/')}
        className="mb-6 text-blue-600 hover:text-blue-800 flex items-center"
      >
        ← 목록으로 돌아가기
      </button>

      {/* 물건 상세 정보 카드 */}
      <div className="bg-white rounded-lg shadow-lg p-8">
        {/* 헤더 */}
        <div className="border-b pb-4 mb-6">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">
            {goods.goodsName}
          </h1>
          <div className="flex items-center text-sm text-gray-600">
            <span className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full">
              {goods.statusName || '상태 정보 없음'}
            </span>
          </div>
        </div>

        {/* 가격 정보 */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
          <div className="bg-blue-50 p-6 rounded-lg">
            <p className="text-sm text-gray-600 mb-2">최저입찰가</p>
            <p className="text-3xl font-bold text-blue-600">
              {formatPrice(goods.minBidPrice)}
              <span className="text-xl ml-1">원</span>
            </p>
          </div>

          <div className="bg-gray-50 p-6 rounded-lg">
            <p className="text-sm text-gray-600 mb-2">감정가</p>
            <p className="text-2xl font-semibold text-gray-700">
              {formatPrice(goods.appraisalPrice)}
              <span className="text-lg ml-1">원</span>
            </p>
          </div>
        </div>

        {/* 물건 정보 */}
        <div className="space-y-4 mb-8">
          <div>
            <h3 className="text-lg font-semibold text-gray-800 mb-2">
              물건 정보
            </h3>
          </div>

          <div className="grid grid-cols-1 gap-4">
            <div className="border-l-4 border-blue-500 pl-4">
              <p className="text-sm text-gray-600 mb-1">물건이력번호</p>
              <p className="text-gray-800 font-mono">{goods.historyNo}</p>
            </div>

            <div className="border-l-4 border-blue-500 pl-4">
              <p className="text-sm text-gray-600 mb-1">물건소재지</p>
              <p className="text-gray-800">
                {goods.address || '주소 정보 없음'}
              </p>
            </div>
          </div>
        </div>

        {/* 구매하기 버튼 */}
        <div className="border-t pt-6">
          <button
            onClick={handlePurchaseClick}
            disabled={isAlreadyPurchased}
            className={`w-full py-4 text-lg font-semibold rounded-lg transition shadow-lg ${
              isAlreadyPurchased
                ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                : 'bg-blue-600 text-white hover:bg-blue-700'
            }`}
          >
            {isAlreadyPurchased ? '이미 구매한 물건입니다' : '구매하기'}
          </button>
          <p className="text-sm text-gray-500 text-center mt-3">
            {isAlreadyPurchased
              ? '이미 구매한 물건은 다시 구매할 수 없습니다.'
              : '최저입찰가로 구매가 진행됩니다'}
          </p>
        </div>
      </div>

      {/* 구매 확인 모달 */}
      <PurchaseModal
        isOpen={isPurchaseModalOpen}
        onClose={() => setIsPurchaseModalOpen(false)}
        goods={goods}
        onPurchase={handlePurchase}
      />
    </div>
  );
}

export default GoodsDetailPage;

