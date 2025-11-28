import React, { useState } from 'react';
import { updatePassword } from '../../services/UserService';

/*
 UpdatePasswordModal component
 Props:
 - showUpdatePassword, setShowUpdatePassword: visibility control
 - user: current user object
*/
export default function UpdatePasswordModal({ showUpdatePassword, setShowUpdatePassword, user, setUser }) {
    const [formData, setFormData] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        if (!formData.currentPassword || !formData.newPassword || !formData.confirmPassword) {
            setError('Vui lòng điền đầy đủ thông tin!');
            setLoading(false);
            return;
        }

        if (formData.newPassword.length < 6) {
            setError('Mật khẩu mới phải có ít nhất 6 ký tự!');
            setLoading(false);
            return;
        }

        if (formData.newPassword !== formData.confirmPassword) {
            setError('Mật khẩu xác nhận không khớp!');
            setLoading(false);
            return;
        }

        try {
            await updatePassword({ id: user.id, currentPassword: formData.currentPassword, newPassword: formData.newPassword });

            // đăng xuất ra, bắt user đăng nhập lại
            setUser(null)

            setSuccess('Đổi mật khẩu thành công, vui lòng đăng nhập lại!');
            setFormData({ currentPassword: '', newPassword: '', confirmPassword: '' });

            setTimeout(() => {
                closeModal();
            }, 3000);

        } catch (e) {
            setError('Mật khẩu hiện tại không đúng hoặc có lỗi hệ thống.');
        } finally {
            setLoading(false);
        }
    };

    const closeModal = () => {
        setShowUpdatePassword(false);
        setError('');
        setSuccess('');
        setFormData({ currentPassword: '', newPassword: '', confirmPassword: '' });
    };

    if (!showUpdatePassword) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-xl p-8 max-w-md w-full mx-4">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-bold">Đổi mật khẩu</h2>
                    <button className="text-gray-500 hover:text-gray-700" onClick={closeModal}>✕</button>
                </div>

                {error && (<div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4">{error}</div>)}
                {success && (<div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg mb-4">{success}</div>)}

                {!success && <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-gray-700 font-medium mb-2">Mật khẩu hiện tại</label>
                        <input
                            type="password"
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-green-500"
                            value={formData.currentPassword}
                            onChange={(e) => setFormData({ ...formData, currentPassword: e.target.value })}
                        />
                    </div>
                    <div>
                        <label className="block text-gray-700 font-medium mb-2">Mật khẩu mới</label>
                        <input
                            type="password"
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-green-500"
                            value={formData.newPassword}
                            onChange={(e) => setFormData({ ...formData, newPassword: e.target.value })}
                        />
                    </div>
                    <div>
                        <label className="block text-gray-700 font-medium mb-2">Xác nhận mật khẩu mới</label>
                        <input
                            type="password"
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-green-500"
                            value={formData.confirmPassword}
                            onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className={`w-full py-3 rounded-lg font-semibold transition-colors ${loading ? 'bg-gray-400 text-gray-200 cursor-not-allowed' : 'bg-green-600 text-white hover:bg-green-700'}`}
                    >
                        {loading ? 'Đang xử lý...' : 'Đổi mật khẩu'}
                    </button>
                </form>}
            </div>
        </div>
    );
}