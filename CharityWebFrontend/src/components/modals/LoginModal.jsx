import React, { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';

export default function LoginModal({ show, onClose }) {
    const { login } = useAuth();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        if (!username || !password) {
            setError('Vui lòng điền đầy đủ thông tin!');
            return;
        }
        setLoading(true);
        try {
            await login(username, password);
            onClose();
            setUsername('');
            setPassword('');
        } catch (err) {
            const msg = err.response?.data?.message || 'Tên đăng nhập hoặc mật khẩu không đúng!';
            setError(msg);
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        onClose();
        setError('');
        setUsername('');
        setPassword('');
    };

    if (!show) return null;

    return (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4" onClick={handleClose}>
            <div className="bg-white rounded-2xl p-8 max-w-md w-full shadow-2xl" onClick={(e) => e.stopPropagation()}>
                <div className="flex justify-between items-center mb-6">
                    <div>
                        <h2 className="text-2xl font-bold text-gray-800">Đăng nhập</h2>
                        <p className="text-sm text-gray-500 mt-1">Chào mừng bạn trở lại!</p>
                    </div>
                    <button className="text-gray-400 hover:text-gray-600 transition-colors p-1" onClick={handleClose}>
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>

                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1.5">Tên đăng nhập</label>
                        <input
                            type="text"
                            className="input-field"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="Nhập tên đăng nhập"
                            autoFocus
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1.5">Mật khẩu</label>
                        <input
                            type="password"
                            className="input-field"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Nhập mật khẩu"
                        />
                    </div>
                    <button
                        type="submit"
                        disabled={loading}
                        className={`w-full py-3 rounded-lg font-semibold text-white transition-all duration-200 ${loading
                                ? 'bg-gray-400 cursor-not-allowed'
                                : 'bg-primary-600 hover:bg-primary-700 shadow-sm hover:shadow-md'
                            }`}
                    >
                        {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
                    </button>
                </form>

                <div className="text-center mt-6 text-sm text-gray-500">
                    Chưa có tài khoản?{' '}
                    <a href="/register" className="text-primary-600 hover:text-primary-700 font-medium" onClick={handleClose}>
                        Đăng ký ngay
                    </a>
                </div>
            </div>
        </div>
    );
}
