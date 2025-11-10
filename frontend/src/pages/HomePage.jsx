import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import GoodsCard from '../components/GoodsCard';
import { getGoodsList } from '../utils/api';

/**
 * HomePage 컴포넌트 - 메인 페이지
 */
function HomePage() {
  const [goods, setGoods] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  // 최근 물건 6개 조회
  useEffect(() => {
    const fetchRecentGoods = async () => {
      setIsLoading(true);
      try {
        const response = await getGoodsList(1, 6);
        
        if (response.success && response.data?.items) {
          setGoods(response.data.items);
        } else {
          setError('물건 정보를 불러올 수 없습니다.');
        }
      } catch (err) {
        console.error('물건 조회 오류:', err);
        setError('물건 정보를 불러오는 중 오류가 발생했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchRecentGoods();
  }, []);

  return (
    <div>
      {/* 메인 타이틀 섹션 */}
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold text-gray-800 mb-4">
          온비드 공매물건 조회
        </h1>
        <p className="text-lg text-gray-600 mb-8">
          한국자산관리공사 공매물건을 간편하게 조회하세요
        </p>
        
        {/* 물건 목록 바로가기 버튼 */}
        <Link
          to="/goods"
          className="inline-block bg-blue-600 text-white px-8 py-3 rounded-lg text-lg font-semibold hover:bg-blue-700 transition"
        >
          전체 물건 보기
        </Link>
      </div>

      {/* 최근 물건 섹션 */}
      <div className="mt-12">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">
          최근 공매물건
        </h2>

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

        {/* 물건 목록 */}
        {!isLoading && !error && goods.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {goods.map((item) => (
              <GoodsCard 
                key={item.goodsNo} 
                goods={item}
              />
            ))}
          </div>
        )}

        {/* 빈 목록 */}
        {!isLoading && !error && goods.length === 0 && (
          <div className="text-center py-12 text-gray-500">
            표시할 물건이 없습니다.
          </div>
        )}
      </div>

      {/* 안내 섹션 */}
      <div className="mt-16 bg-blue-50 p-8 rounded-lg">
        <h3 className="text-xl font-bold text-gray-800 mb-4">
          서비스 안내
        </h3>
        <ul className="space-y-2 text-gray-700">
          <li>• 한국자산관리공사(캠코) 온비드 공매물건을 조회할 수 있습니다.</li>
          <li>• 회원가입 후 관심물건을 등록하여 관리할 수 있습니다.</li>
          <li>• 최근 6개월 이내의 물건만 조회 가능합니다.</li>
        </ul>
      </div>
    </div>
  );
}

export default HomePage;

