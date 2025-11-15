import { Link } from 'react-router-dom';

/**
 * Header 컴포넌트 - 전통적인 상단 메뉴바
 */
function Header() {
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
          </nav>
        </div>
      </div>
    </header>
  );
}

export default Header;

