import React, { useState } from 'react';
import { login, registerAccount, } from '../../services/DonorService'
/*
 LoginModal component: demo in-memory auth.
 Props:
 - showLogin, setShowLogin, setUser
*/
export default function LoginModal({ showLogin, setShowLogin, setUser }) {


  const [isLogin, setIsLogin] = useState(true);
  const [loginData, setLoginData] = useState({ email: '', password: '' });
  const [registerData, setRegisterData] = useState({ displayName: '', email: '', phoneNumber: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);


  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    if (isLogin) {
      try {
        if (!loginData.email || !loginData.password) {
          setError('Vui lòng điền đầy đủ thông tin!');
          setLoading(false);
          return;
        }

        const response = await login(loginData);
        const u = response.data
        setUser(u);
        setShowLogin(false);
        setLoginData({ email: '', password: '' });
        setIsLogin(true);
      } catch (e) {
        setError('Email hoặc mật khẩu không đúng!');
      } finally {
        setLoading(false);
      }
    } else {
      try {
        if (!registerData.displayName || !registerData.email || !registerData.phoneNumber || !registerData.password) {
          setError('Vui lòng điền đầy đủ thông tin!');
          setLoading(false);
          return;
        }
        if (registerData.password.length < 6) {
          setError('Mật khẩu phải có ít nhất 6 ký tự!');
          setLoading(false);
          return;
        }

        const res = await registerAccount(registerData);

        setUser(res.data);
        setShowLogin(false);
        setRegisterData({ displayName: '', email: '', phoneNumber: '', password: '' });
        setIsLogin(true);
      } catch (e) {
        setError('Email/Tên/SĐT đã được sử dụng.');
      } finally {
        setLoading(false);
      }
    }
  };


  const toggleMode = () => {
    setIsLogin(!isLogin);
    setError('');
    setLoginData({ email: '', password: '' });
    setRegisterData({ displayName: '', email: '', phoneNumber: '', password: '' });
  };
  const closeModal = () => {
    setIsLogin(true);
    setShowLogin(false);
    setError('');
    setLoginData({ email: '', password: '' });
    setRegisterData({ displayName: '', email: '', phoneNumber: '', password: '' });
  };


  if (!showLogin) return null;
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl p-8 max-w-md w-full mx-4">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold">{isLogin ? 'Đăng nhập' : 'Đăng ký'}</h2>
          <button className="text-gray-500 hover:text-gray-700" onClick={closeModal}>✕</button>
        </div>
        {error && (<div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4">{error}</div>)}
        <form onSubmit={handleSubmit} className="space-y-4">
          {isLogin && (<>
            <div><label className="block text-gray-700 font-medium mb-2">Email</label><input type="email" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={loginData.email} onChange={(e) => setLoginData({ ...loginData, email: e.target.value })} /></div>
            <div><label className="block text-gray-700 font-medium mb-2">Mật khẩu</label><input type="password" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={loginData.password} onChange={(e) => setLoginData({ ...loginData, password: e.target.value })} /></div>
          </>)}
          {!isLogin && (<>
            <div><label className="block text-gray-700 font-medium mb-2">Email</label><input type="email" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={registerData.email} onChange={(e) => setRegisterData({ ...registerData, email: e.target.value })} required /></div>
            <div><label className="block text-gray-700 font-medium mb-2">Mật khẩu</label><input type="password" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={registerData.password} onChange={(e) => setRegisterData({ ...registerData, password: e.target.value })} required /></div>
            <div><label className="block text-gray-700 font-medium mb-2">Tên người dùng</label><input type="text" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={registerData.displayName} onChange={(e) => setRegisterData({ ...registerData, displayName: e.target.value })} required /></div>
            <div><label className="block text-gray-700 font-medium mb-2">Số điện thoại</label><input type="tel" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={registerData.phoneNumber} onChange={(e) => setRegisterData({ ...registerData, phoneNumber: e.target.value })} required /></div>
          </>)}
          <button type="submit" disabled={loading} className={`w-full py-3 rounded-lg font-semibold transition-colors ${loading ? 'bg-gray-400 text-gray-200 cursor-not-allowed' : 'bg-green-600 text-white hover:bg-green-700'}`}>
            {loading ? (isLogin ? 'Đang đăng nhập...' : 'Đang đăng ký...') : (isLogin ? 'Đăng nhập' : 'Đăng ký')}
          </button>
        </form>
        <div className="text-center mt-4"><button className="text-green-600 hover:text-green-700 disabled:text-gray-400" onClick={toggleMode} disabled={loading}>{isLogin ? 'Chưa có tài khoản? Đăng ký' : 'Đã có tài khoản? Đăng nhập'}</button></div>
      </div>
    </div>
  );
}