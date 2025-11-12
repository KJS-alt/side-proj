import { useState } from 'react';
import PropTypes from 'prop-types';

/**
 * PurchaseModal 컴포넌트
 * 물건 구매 확인 및 처리 모달
 */
function PurchaseModal({ isOpen, onClose, goods, onPurchase }) {
  const [isProcessing, setIsProcessing] = useState(false);

  // 모달이 열려있지 않으면 렌더링하지 않음
  if (!isOpen || !goods) return null;

  // 구매 확인 버튼 클릭
  const handleConfirm = async () => {
    setIsProcessing(true);
    try {
      await onPurchase();
    } finally {
      setIsProcessing(false);
    }
  };

  // 가격 포맷 (천단위 콤마)
  const formatPrice = (price) => {
    if (!price) return '0';
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="bg-white rounded-lg shadow-xl max-w-lg w-full mx-4">
        {/* 모달 헤더 */}
        <div className="px-6 py-4 border-b">
          <h3 className="text-xl font-bold text-gray-800">구매 확인</h3>
        </div>

        {/* 모달 내용 */}
        <div className="px-6 py-4">
          <div className="mb-4">
            <p className="text-gray-700 mb-2">
              다음 물건을 구매하시겠습니까?
            </p>
          </div>

          {/* 물건 정보 */}
          <div className="bg-gray-50 p-4 rounded-lg mb-4">
            <div className="mb-3">
              <p className="text-sm text-gray-600 mb-1">물건명</p>
              <p className="font-semibold text-gray-800">{goods.goodsName}</p>
            </div>

            <div className="mb-3">
              <p className="text-sm text-gray-600 mb-1">물건소재지</p>
              <p className="text-gray-800">{goods.address || '정보 없음'}</p>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-gray-600 mb-1">최저입찰가</p>
                <p className="font-bold text-blue-600">
                  {formatPrice(goods.minBidPrice)}원
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-600 mb-1">감정가</p>
                <p className="text-gray-700">
                  {formatPrice(goods.appraisalPrice)}원
                </p>
              </div>
            </div>
          </div>

          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <p className="text-sm text-blue-800">
              ℹ️ 최저입찰가로 구매가 진행됩니다.
            </p>
          </div>
        </div>

        {/* 모달 푸터 (버튼) */}
        <div className="px-6 py-4 border-t flex justify-end gap-3">
          <button
            onClick={onClose}
            disabled={isProcessing}
            className="px-5 py-2 text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            취소
          </button>
          <button
            onClick={handleConfirm}
            disabled={isProcessing}
            className="px-5 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isProcessing ? '처리중...' : '구매하기'}
          </button>
        </div>
      </div>
    </div>
  );
}

PurchaseModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  goods: PropTypes.object,
  onPurchase: PropTypes.func.isRequired,
};

export default PurchaseModal;

