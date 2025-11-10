import { Navigate } from 'react-router-dom';
import { isAuthenticated } from '../utils/api';

/**
 * PrivateRoute 컴포넌트 - 인증이 필요한 페이지 보호
 * @param {React.ReactNode} props.children - 보호할 컴포넌트
 */
function PrivateRoute({ children }) {
  // 로그인 상태 확인
  if (!isAuthenticated()) {
    // 로그인되지 않았으면 로그인 페이지로 리디렉션
    return <Navigate to="/login" replace />;
  }

  // 로그인되었으면 자식 컴포넌트 렌더링
  return children;
}

export default PrivateRoute;

