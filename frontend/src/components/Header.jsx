import { Link, useNavigate } from 'react-router-dom';
import { isAuthenticated, logout } from '../utils/api';

// 기능 토글 설정 (숨김처리 제어)
const FEATURES = {
  AUTH: false,      // 로그인/회원가입 기능
  FAVORITES: false  // 관심물건 기능
};

/**
 * Header 컴포넌트 - 전통적인 상단 메뉴바
 */
function Header() {
  const navigate = useNavigate();
  const isLoggedIn = isAuthenticated();

  // 로그아웃 핸들러
  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="bg-blue-600 text-white shadow-md">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* 로고 */}
          <Link to="/" className="text-xl font-bold hover:text-blue-200 transition">
            온비드 공매물건
          </Link>

          {/* 메뉴 */}
          <nav className="flex items-center gap-6">
            <Link 
              to="/" 
              className="hover:text-blue-200 transition"
            >
              홈
            </Link>
            <Link 
              to="/goods" 
              className="hover:text-blue-200 transition"
            >
              물건 목록
            </Link>
            <Link 
              to="/purchases" 
              className="hover:text-blue-200 transition"
            >
              구매 목록
            </Link>
            
            {/* 로그인 상태에 따라 다른 메뉴 표시 (AUTH 기능 활성화 시에만) */}
            {FEATURES.AUTH && (
              <>
                {isLoggedIn ? (
                  <>
                    {FEATURES.FAVORITES && (
                      <Link 
                        to="/favorites" 
                        className="hover:text-blue-200 transition"
                      >
                        관심물건
                      </Link>
                    )}
                    <Link 
                      to="/profile" 
                      className="hover:text-blue-200 transition"
                    >
                      내 정보
                    </Link>
                    <button
                      onClick={handleLogout}
                      className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded transition"
                    >
                      로그아웃
                    </button>
                  </>
                ) : (
                  <>
                    <Link 
                      to="/login" 
                      className="hover:text-blue-200 transition"
                    >
                      로그인
                    </Link>
                    <Link 
                      to="/register" 
                      className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded transition"
                    >
                      회원가입
                    </Link>
                  </>
                )}
              </>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
}

export default Header;

