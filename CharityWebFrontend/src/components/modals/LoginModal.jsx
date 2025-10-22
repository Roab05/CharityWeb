import React, { useState } from 'react';
import UserManager from '../../utils/UserManager';
import { getDonor, registerAccount, } from '../../services/DonorService'
/*
 LoginModal component: demo in-memory auth.
 Props:
 - showLogin, setShowLogin, setUser
*/
export default function LoginModal({ showLogin, setShowLogin, setUser }) {


  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({ name: '', email: '', phone: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);


  const handleSubmit = (e) => {
    e.preventDefault();
    setError(''); setLoading(true);
    setTimeout(() => {
      if (isLogin) {
        const u = UserManager.login(formData.email, formData.password);
        if (u) { setUser(u); setShowLogin(false); setFormData({ name: '', email: '', phone: '', password: '' }); }
        else setError('Email hoặc mật khẩu không đúng!');
      } else {
        if (!formData.name || !formData.email || !formData.phone || !formData.password) { setError('Vui lòng điền đầy đủ thông tin!'); setLoading(false); return; }
        if (formData.password.length < 6) { setError('Mật khẩu phải có ít nhất 6 ký tự!'); setLoading(false); return; }
        // const res = DonorService.registerAccount(formData);
        if (res.success) { setUser(res.user); setShowLogin(false); setFormData({ name: '', email: '', phone: '', password: '' }); }
        else setError(res.message);
      }
      setLoading(false);
    }, 500);
  };


  const toggleMode = () => { setIsLogin(!isLogin); setError(''); setFormData({ name: '', email: '', phone: '', password: '' }); };
  const closeModal = () => { setShowLogin(false); setError(''); setFormData({ name: '', email: '', phone: '', password: '' }); };


  if (!showLogin) return null;
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl p-8 max-w-md w-full mx-4">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold">{isLogin ? 'Đăng nhập' : 'Đăng ký'}</h2>
          <button className="text-gray-500 hover:text-gray-700" onClick={closeModal}>✕</button>
        </div>
        {error && (<div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4">{error}</div>)}
        {isLogin && (<div className="bg-blue-50 border border-blue-200 text-blue-700 px-4 py-3 rounded-lg mb-4"><p className="text-sm font-medium mb-2">Tài khoản demo:</p><p className="text-xs">Email: admin@gayquy.vn | Mật khẩu: 123456</p><p className="text-xs">Email: user@gayquy.vn | Mật khẩu: password</p></div>)}
        <form onSubmit={handleSubmit} className="space-y-4">
          {!isLogin && (<>
            <div><label className="block text-gray-700 font-medium mb-2">Họ và tên</label><input type="text" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={formData.name} onChange={(e) => setFormData({ ...formData, name: e.target.value })} required /></div>
            <div><label className="block text-gray-700 font-medium mb-2">Số điện thoại</label><input type="tel" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={formData.phone} onChange={(e) => setFormData({ ...formData, phone: e.target.value })} required /></div>
          </>)}
          <div><label className="block text-gray-700 font-medium mb-2">Email</label><input type="email" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={formData.email} onChange={(e) => setFormData({ ...formData, email: e.target.value })} required /></div>
          <div><label className="block text-gray-700 font-medium mb-2">Mật khẩu</label><input type="password" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={formData.password} onChange={(e) => setFormData({ ...formData, password: e.target.value })} required /></div>
          <button type="submit" disabled={loading} className={`w-full py-3 rounded-lg font-semibold transition-colors ${loading ? 'bg-gray-400 text-gray-200 cursor-not-allowed' : 'bg-green-600 text-white hover:bg-green-700'}`}>
            {loading ? (isLogin ? 'Đang đăng nhập...' : 'Đang đăng ký...') : (isLogin ? 'Đăng nhập' : 'Đăng ký')}
          </button>
        </form>
        <div className="text-center mt-4"><button className="text-green-600 hover:text-green-700 disabled:text-gray-400" onClick={toggleMode} disabled={loading}>{isLogin ? 'Chưa có tài khoản? Đăng ký' : 'Đã có tài khoản? Đăng nhập'}</button></div>
      </div>
    </div>
  );
}
