import React, { useState, useEffect } from 'react';
import { updateUserInfo } from '../../services/UserService';

/*
 UpdateInfoModal component
 Props:
 - showUpdateInfo, setShowUpdateInfo: visibility control
 - user: current user object
 - setUser: to update user state in parent after successful update
*/
export default function UpdateUserInfoModal({ showUpdateInfo, setShowUpdateInfo, user, setUser }) {
    const [formData, setFormData] = useState({ displayName: '', phoneNumber: '' });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (user) {
            setFormData({
                displayName: user.displayName || '',
                phoneNumber: user.phoneNumber || '',
            });
        }
    }, [user, showUpdateInfo]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        try {
            if (!formData.displayName || !formData.phoneNumber) {
                setError('Vui lòng điền đầy đủ thông tin!');
                setLoading(false);
                return;
            }

            const response = await updateUserInfo({ id: user.id, displayName: formData.displayName, phoneNumber: formData.phoneNumber });

            setUser(response.data);
            setSuccess('Cập nhật thông tin thành công!');

            setTimeout(() => {
                closeModal();
            }, 3000);

        } catch (e) {
            setError('Có lỗi xảy ra, tên/SĐT đã tồn tại.');
        } finally {
            setLoading(false);
        }
    };

    const closeModal = () => {
        setShowUpdateInfo(false);
        setError('');
        setSuccess('');

        if (user) {
            setFormData({
                displayName: user.displayName || '',
                phoneNumber: user.phoneNumber || '',
            });
        }
    };

    if (!showUpdateInfo) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-xl p-8 max-w-md w-full mx-4">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-bold">Cập nhật thông tin</h2>
                    <button className="text-gray-500 hover:text-gray-700" onClick={closeModal}>✕</button>
                </div>

                {error && (<div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4">{error}</div>)}
                {success && (<div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg mb-4">{success}</div>)}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-gray-700 font-medium mb-2">Tên hiển thị</label>
                        <input
                            type="text"
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-green-500"
                            value={formData.displayName}
                            onChange={(e) => setFormData({ ...formData, displayName: e.target.value })}
                        />
                    </div>
                    <div>
                        <label className="block text-gray-700 font-medium mb-2">Số điện thoại</label>
                        <input
                            type="tel"
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-green-500"
                            value={formData.phoneNumber}
                            onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                        />
                    </div>

                    {!success && <button
                        type="submit"
                        disabled={loading}
                        className={`w-full py-3 rounded-lg font-semibold transition-colors ${loading ? 'bg-gray-400 text-gray-200 cursor-not-allowed' : 'bg-green-600 text-white hover:bg-green-700'}`}
                    >
                        {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
                    </button>}
                </form>
            </div>
        </div>
    );
}