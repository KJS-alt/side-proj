import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getMyInfo, updateMyInfo, deleteMyAccount, logout } from '../utils/api';

/**
 * ProfilePage 컴포넌트 - 프로필/설정 페이지
 */
function ProfilePage() {
  const navigate = useNavigate();
  
  // 사용자 정보
  const [userInfo, setUserInfo] = useState({
    email: '',
    username: ''
  });
  
  // 수정 폼
  const [updateForm, setUpdateForm] = useState({
    username: '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  
  // 탈퇴 폼
  const [deletePassword, setDeletePassword] = useState('');
  
  // 상태
  const [isLoading, setIsLoading] = useState(true);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  
  // 탈퇴 확인 모달
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  // 내 정보 조회
  useEffect(() => {
    fetchMyInfo();
  }, []);

  const fetchMyInfo = async () => {
    try {
      const response = await getMyInfo();
      if (response.success && response.data) {
        setUserInfo(response.data);
        setUpdateForm(prev => ({ ...prev, username: response.data.username }));
      }
    } catch (err) {
      console.error('내 정보 조회 실패:', err);
      setError('정보를 불러올 수 없습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 폼 입력 핸들러
  const handleUpdateChange = (e) => {
    setUpdateForm({
      ...updateForm,
      [e.target.name]: e.target.value
    });
    setError('');
    setSuccessMessage('');
  };

  // 정보 수정 제출
  const handleUpdateSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessMessage('');

    // 비밀번호 변경 시 확인
    if (updateForm.newPassword) {
      if (updateForm.newPassword !== updateForm.confirmPassword) {
        setError('새 비밀번호가 일치하지 않습니다.');
        return;
      }
      if (!updateForm.currentPassword) {
        setError('현재 비밀번호를 입력해주세요.');
        return;
      }
    }

    setIsUpdating(true);

    try {
      const data = {
        username: updateForm.username
      };

      // 비밀번호 변경하는 경우에만 추가
      if (updateForm.newPassword) {
        data.currentPassword = updateForm.currentPassword;
        data.newPassword = updateForm.newPassword;
      }

      const response = await updateMyInfo(data);
      
      if (response.success) {
        setSuccessMessage('정보가 수정되었습니다.');
        setUserInfo(response.data);
        // 비밀번호 필드 초기화
        setUpdateForm({
          ...updateForm,
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        });
      }
    } catch (err) {
      console.error('정보 수정 실패:', err);
      setError(err.response?.data?.message || '정보 수정에 실패했습니다.');
    } finally {
      setIsUpdating(false);
    }
  };

  // 회원 탈퇴 처리
  const handleDeleteAccount = async () => {
    if (!deletePassword) {
      setError('비밀번호를 입력해주세요.');
      return;
    }

    setIsDeleting(true);
    setError('');

    try {
      const response = await deleteMyAccount(deletePassword);
      
      if (response.success) {
        alert('회원 탈퇴가 완료되었습니다. 그동안 이용해주셔서 감사합니다.');
        logout();
        navigate('/');
      }
    } catch (err) {
      console.error('회원 탈퇴 실패:', err);
      setError(err.response?.data?.message || '회원 탈퇴에 실패했습니다.');
    } finally {
      setIsDeleting(false);
      setShowDeleteModal(false);
      setDeletePassword('');
    }
  };

  if (isLoading) {
    return (
      <div className="text-center py-12">
        <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p className="mt-4 text-gray-600">불러오는 중...</p>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">내 정보 관리</h1>

      {/* 성공 메시지 */}
      {successMessage && (
        <div className="mb-6 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded">
          {successMessage}
        </div>
      )}

      {/* 에러 메시지 */}
      {error && (
        <div className="mb-6 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {/* 정보 수정 폼 */}
      <div className="bg-white border border-gray-300 rounded-lg p-6 mb-6">
        <h2 className="text-xl font-bold text-gray-800 mb-4">정보 수정</h2>
        
        <form onSubmit={handleUpdateSubmit}>
          {/* 이메일 (수정 불가) */}
          <div className="mb-4">
            <label className="block text-gray-700 font-semibold mb-2">이메일</label>
            <input
              type="email"
              value={userInfo.email}
              disabled
              className="w-full px-4 py-2 border border-gray-300 rounded bg-gray-100 text-gray-600"
            />
            <p className="text-xs text-gray-500 mt-1">이메일은 변경할 수 없습니다</p>
          </div>

          {/* 사용자명 */}
          <div className="mb-4">
            <label className="block text-gray-700 font-semibold mb-2">사용자명</label>
            <input
              type="text"
              name="username"
              value={updateForm.username}
              onChange={handleUpdateChange}
              className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          {/* 비밀번호 변경 (선택) */}
          <div className="border-t border-gray-200 pt-4 mt-4">
            <h3 className="font-semibold text-gray-700 mb-3">비밀번호 변경 (선택사항)</h3>
            
            <div className="mb-4">
              <label className="block text-gray-700 mb-2">현재 비밀번호</label>
              <input
                type="password"
                name="currentPassword"
                value={updateForm.currentPassword}
                onChange={handleUpdateChange}
                className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="비밀번호 변경 시 입력"
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 mb-2">새 비밀번호</label>
              <input
                type="password"
                name="newPassword"
                value={updateForm.newPassword}
                onChange={handleUpdateChange}
                className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="새 비밀번호 (6자 이상)"
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 mb-2">새 비밀번호 확인</label>
              <input
                type="password"
                name="confirmPassword"
                value={updateForm.confirmPassword}
                onChange={handleUpdateChange}
                className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="새 비밀번호 재입력"
              />
            </div>
          </div>

          {/* 수정 버튼 */}
          <button
            type="submit"
            disabled={isUpdating}
            className={`w-full py-3 rounded font-semibold transition ${
              isUpdating
                ? 'bg-gray-400 cursor-not-allowed'
                : 'bg-blue-600 hover:bg-blue-700 text-white'
            }`}
          >
            {isUpdating ? '수정 중...' : '정보 수정'}
          </button>
        </form>
      </div>

      {/* 회원 탈퇴 */}
      <div className="bg-red-50 border border-red-300 rounded-lg p-6">
        <h2 className="text-xl font-bold text-red-800 mb-2">회원 탈퇴</h2>
        <p className="text-sm text-red-700 mb-4">
          회원 탈퇴 시 모든 정보가 삭제되며, 복구할 수 없습니다.
        </p>
        <button
          onClick={() => setShowDeleteModal(true)}
          className="bg-red-600 hover:bg-red-700 text-white px-6 py-2 rounded font-semibold transition"
        >
          회원 탈퇴
        </button>
      </div>

      {/* 탈퇴 확인 모달 */}
      {showDeleteModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-xl font-bold text-gray-800 mb-4">회원 탈퇴 확인</h3>
            <p className="text-gray-600 mb-4">
              정말로 탈퇴하시겠습니까? 모든 데이터가 삭제되며 복구할 수 없습니다.
            </p>
            
            <div className="mb-4">
              <label className="block text-gray-700 font-semibold mb-2">비밀번호 확인</label>
              <input
                type="password"
                value={deletePassword}
                onChange={(e) => setDeletePassword(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-red-500"
                placeholder="비밀번호 입력"
              />
            </div>

            <div className="flex gap-3">
              <button
                onClick={() => {
                  setShowDeleteModal(false);
                  setDeletePassword('');
                  setError('');
                }}
                className="flex-1 bg-gray-300 hover:bg-gray-400 text-gray-800 py-2 rounded font-semibold transition"
              >
                취소
              </button>
              <button
                onClick={handleDeleteAccount}
                disabled={isDeleting}
                className={`flex-1 py-2 rounded font-semibold transition ${
                  isDeleting
                    ? 'bg-gray-400 cursor-not-allowed text-white'
                    : 'bg-red-600 hover:bg-red-700 text-white'
                }`}
              >
                {isDeleting ? '처리 중...' : '탈퇴하기'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ProfilePage;

