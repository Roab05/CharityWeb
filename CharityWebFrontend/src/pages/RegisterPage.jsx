import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function RegisterPage() {
    const navigate = useNavigate();
    const { registerIndividual, registerOrganization } = useAuth();
    const [type, setType] = useState('individual');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const getRegisterErrorMessage = (err) => {
        if (!err.response) {
            return 'Không thể kết nối đến máy chủ (http://localhost:8080). Hãy bật backend rồi thử lại.';
        }
        const data = err.response.data;
        if (typeof data === 'string' && data.trim()) {
            return data;
        }
        return data?.message || data?.error || `Đăng ký thất bại (HTTP ${err.response.status}).`;
    };

    const [indivForm, setIndivForm] = useState({
        username: '', password: '', email: '', phone: '', fullName: '', address: '',
    });

    const [orgForm, setOrgForm] = useState({
        username: '', password: '', email: '', phone: '', name: '', doe: '', websiteURL: '', address: '', description: '',
    });

    const handleIndivSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await registerIndividual(indivForm);
            setSuccess('Đăng ký thành công! Tài khoản đang chờ kích hoạt.');
            setTimeout(() => navigate('/'), 2000);
        } catch (err) {
            setError(getRegisterErrorMessage(err));
        }
        setLoading(false);
    };

    const handleOrgSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await registerOrganization(orgForm);
            setSuccess('Đăng ký tổ chức thành công! Vui lòng chờ quản trị viên xác minh.');
            setTimeout(() => navigate('/'), 2000);
        } catch (err) {
            setError(getRegisterErrorMessage(err));
        }
        setLoading(false);
    };

    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4">
            <div className="max-w-xl w-full">
                <div className="text-center mb-8">
                    <Link to="/" className="inline-flex items-center space-x-2 mb-4">
                        <span className="text-3xl">🌱</span>
                        <span className="text-2xl font-bold text-primary-600">GayQuy.vn</span>
                    </Link>
                    <h1 className="text-2xl font-bold text-gray-800">Tạo tài khoản mới</h1>
                    <p className="text-gray-500 mt-1">Tham gia cộng đồng gây quỹ lớn nhất Việt Nam</p>
                </div>

                {/* Type Switch */}
                <div className="flex bg-gray-100 rounded-xl p-1 mb-6">
                    <button
                        onClick={() => { setType('individual'); setError(''); }}
                        className={`flex-1 py-2.5 rounded-lg text-sm font-medium transition-all ${type === 'individual' ? 'bg-white text-primary-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'
                            }`}
                    >
                        👤 Cá nhân
                    </button>
                    <button
                        onClick={() => { setType('organization'); setError(''); }}
                        className={`flex-1 py-2.5 rounded-lg text-sm font-medium transition-all ${type === 'organization' ? 'bg-white text-primary-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'
                            }`}
                    >
                        🏢 Tổ chức
                    </button>
                </div>

                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm">{error}</div>
                )}
                {success && (
                    <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg mb-4 text-sm">{success}</div>
                )}

                <div className="card p-6">
                    {type === 'individual' ? (
                        <form onSubmit={handleIndivSubmit} className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Tên đăng nhập <span className="text-red-500">*</span></label>
                                    <input className="input-field" value={indivForm.username} onChange={(e) => setIndivForm({ ...indivForm, username: e.target.value })} required />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Mật khẩu <span className="text-red-500">*</span></label>
                                    <input type="password" className="input-field" value={indivForm.password} onChange={(e) => setIndivForm({ ...indivForm, password: e.target.value })} required minLength={6} />
                                </div>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Họ tên <span className="text-red-500">*</span></label>
                                <input className="input-field" value={indivForm.fullName} onChange={(e) => setIndivForm({ ...indivForm, fullName: e.target.value })} required />
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Email <span className="text-red-500">*</span></label>
                                    <input type="email" className="input-field" value={indivForm.email} onChange={(e) => setIndivForm({ ...indivForm, email: e.target.value })} required />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Số điện thoại <span className="text-red-500">*</span></label>
                                    <input type="tel" className="input-field" value={indivForm.phone} onChange={(e) => setIndivForm({ ...indivForm, phone: e.target.value })} required />
                                </div>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Địa chỉ</label>
                                <input className="input-field" value={indivForm.address} onChange={(e) => setIndivForm({ ...indivForm, address: e.target.value })} />
                            </div>
                            <button type="submit" disabled={loading} className={`w-full py-3 rounded-lg font-semibold text-white transition-all ${loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-primary-600 hover:bg-primary-700'}`}>
                                {loading ? 'Đang đăng ký...' : 'Đăng ký cá nhân'}
                            </button>
                        </form>
                    ) : (
                        <form onSubmit={handleOrgSubmit} className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Tên đăng nhập <span className="text-red-500">*</span></label>
                                    <input className="input-field" value={orgForm.username} onChange={(e) => setOrgForm({ ...orgForm, username: e.target.value })} required />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Mật khẩu <span className="text-red-500">*</span></label>
                                    <input type="password" className="input-field" value={orgForm.password} onChange={(e) => setOrgForm({ ...orgForm, password: e.target.value })} required minLength={6} />
                                </div>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Tên tổ chức <span className="text-red-500">*</span></label>
                                <input className="input-field" value={orgForm.name} onChange={(e) => setOrgForm({ ...orgForm, name: e.target.value })} required />
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Email <span className="text-red-500">*</span></label>
                                    <input type="email" className="input-field" value={orgForm.email} onChange={(e) => setOrgForm({ ...orgForm, email: e.target.value })} required />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Số điện thoại <span className="text-red-500">*</span></label>
                                    <input type="tel" className="input-field" value={orgForm.phone} onChange={(e) => setOrgForm({ ...orgForm, phone: e.target.value })} required />
                                </div>
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Ngày thành lập <span className="text-red-500">*</span></label>
                                    <input type="date" className="input-field" value={orgForm.doe} onChange={(e) => setOrgForm({ ...orgForm, doe: e.target.value })} required />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Website</label>
                                    <input type="url" className="input-field" placeholder="https://" value={orgForm.websiteURL} onChange={(e) => setOrgForm({ ...orgForm, websiteURL: e.target.value })} />
                                </div>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Địa chỉ <span className="text-red-500">*</span></label>
                                <input className="input-field" value={orgForm.address} onChange={(e) => setOrgForm({ ...orgForm, address: e.target.value })} required />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Mô tả tổ chức</label>
                                <textarea className="input-field resize-none" rows="3" value={orgForm.description} onChange={(e) => setOrgForm({ ...orgForm, description: e.target.value })} placeholder="Giới thiệu về tổ chức của bạn..." />
                            </div>
                            <div className="bg-amber-50 rounded-lg p-3 text-sm text-amber-700">
                                ⚠️ Tài khoản tổ chức cần được quản trị viên xác minh trước khi sử dụng
                            </div>
                            <button type="submit" disabled={loading} className={`w-full py-3 rounded-lg font-semibold text-white transition-all ${loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-primary-600 hover:bg-primary-700'}`}>
                                {loading ? 'Đang đăng ký...' : 'Đăng ký tổ chức'}
                            </button>
                        </form>
                    )}
                </div>

                <p className="text-center text-sm text-gray-500 mt-4">
                    Đã có tài khoản?{' '}
                    <Link to="/" className="text-primary-600 hover:text-primary-700 font-medium">Đăng nhập</Link>
                </p>
            </div>
        </div>
    );
}
