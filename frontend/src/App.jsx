import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import HomePage from './pages/HomePage';
import ListPage from './pages/ListPage';
import GoodsDetailPage from './pages/GoodsDetailPage';
import PurchasesPage from './pages/PurchasesPage';

/**
 * App 컴포넌트 - 메인 애플리케이션 라우터
 */
function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        {/* 상단 헤더 */}
        <Header />
        
        {/* 메인 컨텐츠 영역 */}
        <main className="container mx-auto px-4 py-8">
          <Routes>
            {/* 홈 페이지 */}
            <Route path="/" element={<HomePage />} />
            
            {/* 물건 목록 페이지 */}
            <Route path="/goods" element={<ListPage />} />
            
            {/* 물건 상세 페이지 */}
            <Route path="/goods/:historyNo" element={<GoodsDetailPage />} />
            
            {/* 구매 목록 페이지 */}
            <Route path="/purchases" element={<PurchasesPage />} />
            
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
