import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import PrivateRoute from './components/PrivateRoute';
import HomePage from './pages/HomePage';
import ListPage from './pages/ListPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import FavoritesPage from './pages/FavoritesPage';
import ProfilePage from './pages/ProfilePage';

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
            
            {/* 로그인 페이지 */}
            <Route path="/login" element={<LoginPage />} />
            
            {/* 회원가입 페이지 */}
            <Route path="/register" element={<RegisterPage />} />
            
            {/* 관심물건 페이지 (인증 필요) */}
            <Route 
              path="/favorites" 
              element={
                <PrivateRoute>
                  <FavoritesPage />
                </PrivateRoute>
              } 
            />
            
            {/* 프로필/설정 페이지 (인증 필요) */}
            <Route 
              path="/profile" 
              element={
                <PrivateRoute>
                  <ProfilePage />
                </PrivateRoute>
              } 
            />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
