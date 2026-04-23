import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { updateMyProfile, getMyDonations, getMyTransactions, changePassword } from '../services/UserService';
import LoadingSpinner from '../components/LoadingSpinner';

export default function ProfilePage() {
    const { user, refreshProfile } = useAuth();
    const [donations, setDonations] = useState([]);
    const [transactions, setTransactions] = useState([]);
    const [activeTab, setActiveTab] = useState('info');
    const [loading, setLoading] = useState(true);

    // Edit profile form
    const [editing, setEditing] = useState(false);
    const [profileForm, setProfileForm] = useState({});
    const [profileLoading, setProfileLoading] = useState(false);
    const [profileMsg, setProfileMsg] = useState({ type: '', text: '' });

    // Change password
    const [passwordForm, setPasswordForm] = useState({ currentPassword: '', newPassword: '', confirmationPassword: '' });
    const [passwordLoading, setPasswordLoading] = useState(false);
    const [passwordMsg, setPasswordMsg] = useState({ type: '', text: '' });

    const formatCurrency = (amount) => new Intl.NumberFormat('vi-VN').format(amount || 0) + ' ₫';

    useEffect(() => {
        if (user) {
            setProfileForm({
                phone: user.phone || '',
                address: user.address || '',
                fullName: user.fullName || '',
                name: user.orgName || '',
                websiteURL: user.websiteURL || '',
                description: user.description || '',
                email: user.email || '',
            });
        }
    }, [user]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [donRes, txnRes] = await Promise.allSettled([
                    getMyDonations(),
                    getMyTransactions(),
                ]);
                if (donRes.status === 'fulfilled') setDonations(donRes.value.data.content || []);
                if (txnRes.status === 'fulfilled') setTransactions(txnRes.value.data.content || []);
            } catch { /* ignore */ }
            setLoading(false);
        };
        fetchData();
    }, []);

    const handleUpdateProfile = async (e) => {
        e.preventDefault();
        setProfileLoading(true);
        setProfileMsg({ type: '', text: '' });
        try {
            await updateMyProfile(profileForm);
            await refreshProfile();
            setEditing(false);
            setProfileMsg({ type: 'success', text: 'Cập nhật thông tin thành công!' });
        } catch (err) {
            setProfileMsg({ type: 'error', text: err.response?.data?.message || 'Có lỗi xảy ra.' });
        }
        setProfileLoading(false);
    };

    const handleChangePassword = async (e) => {
        e.preventDefault();
        setPasswordMsg({ type: '', text: '' });
        if (passwordForm.newPassword.length < 6) {
            setPasswordMsg({ type: 'error', text: 'Mật khẩu mới phải có ít nhất 6 ký tự!' });
            return;
        }
        if (passwordForm.newPassword !== passwordForm.confirmationPassword) {
            setPasswordMsg({ type: 'error', text: 'Xác nhận mật khẩu không khớp!' });
            return;
        }
        setPasswordLoading(true);
        try {
            await changePassword(passwordForm);
            setPasswordForm({ currentPassword: '', newPassword: '', confirmationPassword: '' });
            setPasswordMsg({ type: 'success', text: 'Đổi mật khẩu thành công!' });
        } catch (err) {
            setPasswordMsg({ type: 'error', text: err.response?.data?.message || 'Mật khẩu hiện tại không đúng.' });
        }
        setPasswordLoading(false);
    };

    if (!user) return null;

    const isOrg = user.roleType === 'ORGANIZATION';

    const tabs = [
        { key: 'info', label: 'Thông tin cá nhân' },
        { key: 'donations', label: 'Lịch sử ủng hộ' },
        { key: 'transactions', label: 'Lịch sử giao dịch' },
        { key: 'password', label: 'Đổi mật khẩu' },
    ];

    return (
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            {/* Profile Header */}
            <div className="card p-6 mb-6">
                <div className="flex items-center gap-4">
                    <div className="w-16 h-16 rounded-full bg-primary-100 flex items-center justify-center">
                        <span className="text-primary-700 font-bold text-2xl">
                            {(isOrg ? user.orgName : user.fullName || user.username)?.[0]?.toUpperCase()}
                        </span>
                    </div>
                    <div>
                        <h1 className="text-2xl font-bold text-gray-800">
                            {isOrg ? user.orgName : (user.fullName || user.username)}
                        </h1>
                        <div className="flex items-center gap-2 mt-1">
                            <span className={`badge ${user.status === 'ACTIVE' ? 'badge-green' : user.status === 'PENDING' ? 'badge-yellow' : 'badge-red'}`}>
                                {user.status === 'ACTIVE' ? 'Hoạt động' : user.status === 'PENDING' ? 'Chờ duyệt' : 'Bị khóa'}
                            </span>
                            <span className="badge badge-blue">{isOrg ? 'Tổ chức' : 'Cá nhân'}</span>
                        </div>
                    </div>
                    <div className="ml-auto text-right">
                        <p className="text-sm text-gray-500">Tổng đã ủng hộ</p>
                        <p className="text-xl font-bold text-primary-600">{formatCurrency(user.totalDonatedAmount)}</p>
                    </div>
                </div>
            </div>

            {/* Tabs */}
            <div className="flex border-b border-gray-200 mb-6">
                {tabs.map((tab) => (
                    <button
                        key={tab.key}
                        onClick={() => setActiveTab(tab.key)}
                        className={`px-4 py-3 text-sm font-medium border-b-2 transition-colors ${activeTab === tab.key
                            ? 'border-primary-600 text-primary-600'
                            : 'border-transparent text-gray-500 hover:text-gray-700'
                            }`}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>

            {/* Info Tab */}
            {activeTab === 'info' && (
                <div className="card p-6">
                    {profileMsg.text && (
                        <div className={`px-4 py-3 rounded-lg mb-4 text-sm ${profileMsg.type === 'success' ? 'bg-green-50 text-green-700 border border-green-200' : 'bg-red-50 text-red-700 border border-red-200'}`}>
                            {profileMsg.text}
                        </div>
                    )}

                    {!editing ? (
                        <div>
                            <div className="flex justify-between items-center mb-6">
                                <h2 className="text-lg font-semibold text-gray-800">Thông tin tài khoản</h2>
                                <button onClick={() => setEditing(true)} className="btn-secondary text-sm">Chỉnh sửa</button>
                            </div>
                            <div className="grid md:grid-cols-2 gap-4">
                                <InfoItem label="Tên đăng nhập" value={user.username} />
                                <InfoItem label="Email" value={user.email} />
                                <InfoItem label="Số điện thoại" value={user.phone} />
                                {isOrg ? (
                                    <>
                                        <InfoItem label="Tên tổ chức" value={user.orgName} />
                                        <InfoItem label="Website" value={user.websiteURL} />
                                        <InfoItem label="Mô tả" value={user.description} className="md:col-span-2" />
                                    </>
                                ) : (
                                    <>
                                        <InfoItem label="Họ tên" value={user.fullName} />
                                        <InfoItem label="Địa chỉ" value={user.address} />
                                    </>
                                )}
                            </div>
                        </div>
                    ) : (
                        <form onSubmit={handleUpdateProfile} className="space-y-4">
                            <h2 className="text-lg font-semibold text-gray-800 mb-2">Chỉnh sửa thông tin</h2>
                            <div className="grid md:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                                    <input className="input-field" value={profileForm.email} onChange={(e) => setProfileForm({ ...profileForm, email: e.target.value })} />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Số điện thoại</label>
                                    <input className="input-field" value={profileForm.phone} onChange={(e) => setProfileForm({ ...profileForm, phone: e.target.value })} />
                                </div>
                                {isOrg ? (
                                    <>
                                        <div>
                                            <label className="block text-sm font-medium text-gray-700 mb-1">Tên tổ chức</label>
                                            <input className="input-field" value={profileForm.name} onChange={(e) => setProfileForm({ ...profileForm, name: e.target.value })} />
                                        </div>
                                        <div>
                                            <label className="block text-sm font-medium text-gray-700 mb-1">Website</label>
                                            <input className="input-field" value={profileForm.websiteURL} onChange={(e) => setProfileForm({ ...profileForm, websiteURL: e.target.value })} />
                                        </div>
                                        <div className="md:col-span-2">
                                            <label className="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
                                            <textarea className="input-field resize-none" rows="3" value={profileForm.description} onChange={(e) => setProfileForm({ ...profileForm, description: e.target.value })} />
                                        </div>
                                    </>
                                ) : (
                                    <>
                                        <div>
                                            <label className="block text-sm font-medium text-gray-700 mb-1">Họ tên</label>
                                            <input className="input-field" value={profileForm.fullName} onChange={(e) => setProfileForm({ ...profileForm, fullName: e.target.value })} />
                                        </div>
                                        <div>
                                            <label className="block text-sm font-medium text-gray-700 mb-1">Địa chỉ</label>
                                            <input className="input-field" value={profileForm.address} onChange={(e) => setProfileForm({ ...profileForm, address: e.target.value })} />
                                        </div>
                                    </>
                                )}
                            </div>
                            <div className="flex gap-3 pt-2">
                                <button type="submit" disabled={profileLoading} className="btn-primary text-sm">{profileLoading ? 'Đang lưu...' : 'Lưu thay đổi'}</button>
                                <button type="button" onClick={() => { setEditing(false); setProfileMsg({ type: '', text: '' }); }} className="btn-secondary text-sm">Hủy</button>
                            </div>
                        </form>
                    )}
                </div>
            )}

            {/* Donations Tab */}
            {activeTab === 'donations' && (
                <div className="card p-6">
                    <h2 className="text-lg font-semibold text-gray-800 mb-4">Lịch sử ủng hộ</h2>
                    {loading ? (
                        <LoadingSpinner />
                    ) : donations.length === 0 ? (
                        <p className="text-center text-gray-500 py-8">Bạn chưa ủng hộ dự án nào</p>
                    ) : (
                        <div className="space-y-3 max-h-[500px] overflow-y-auto">
                            {donations.map((d, i) => {
                                const statusMap = { SUCCESS: 'badge-green', PROCESSING: 'badge-yellow', FAILED: 'badge-red' };
                                const labelMap = { SUCCESS: 'Thành công', PROCESSING: 'Đang xử lý', FAILED: 'Thất bại' };
                                return (
                                    <div key={i} className="flex items-start justify-between p-4 bg-gray-50 rounded-xl">
                                        <div className="flex-1 min-w-0 mr-4">
                                            <p className="font-medium text-gray-800">{d.projectName}</p>
                                            {d.message && <p className="text-sm text-gray-500 mt-1 truncate">{d.message}</p>}
                                            <div className="flex items-center gap-2 mt-1">
                                                <span className="text-xs text-gray-400">{d.donationTime ? new Date(d.donationTime).toLocaleString('vi-VN') : ''}</span>
                                                <span className={statusMap[d.status] || 'badge-gray'}>{labelMap[d.status] || d.status}</span>
                                            </div>
                                        </div>
                                        <span className="text-primary-600 font-bold whitespace-nowrap">{formatCurrency(d.amount)}</span>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>
            )}

            {/* Transactions Tab */}
            {activeTab === 'transactions' && (
                <div className="card p-6">
                    <h2 className="text-lg font-semibold text-gray-800 mb-4">Lịch sử giao dịch</h2>
                    {loading ? (
                        <LoadingSpinner />
                    ) : transactions.length === 0 ? (
                        <p className="text-center text-gray-500 py-8">Chưa có giao dịch nào</p>
                    ) : (
                        <div className="space-y-3 max-h-[500px] overflow-y-auto">
                            {transactions.map((t, i) => {
                                const isSuccess = t.paymentStatus === 1;
                                return (
                                    <div key={i} className="flex items-start justify-between p-4 bg-gray-50 rounded-xl">
                                        <div className="flex-1 min-w-0 mr-4">
                                            <p className="font-medium text-gray-800">{t.projectName}</p>
                                            <p className="text-xs text-gray-500 mt-1">Cổng: {t.gatewayName}{t.gatewayTransactionNo ? ` • Mã GD: ${t.gatewayTransactionNo}` : ''}</p>
                                            <div className="flex items-center gap-2 mt-1">
                                                <span className="text-xs text-gray-400">{t.completedAt ? new Date(t.completedAt).toLocaleString('vi-VN') : (t.donationTime ? new Date(t.donationTime).toLocaleString('vi-VN') : '')}</span>
                                                <span className={isSuccess ? 'badge-green' : 'badge-red'}>{isSuccess ? 'Thành công' : 'Thất bại'}</span>
                                            </div>
                                        </div>
                                        <span className="text-primary-600 font-bold whitespace-nowrap">{formatCurrency(t.amount)}</span>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>
            )}

            {/* Password Tab */}
            {activeTab === 'password' && (
                <div className="card p-6 max-w-md">
                    <h2 className="text-lg font-semibold text-gray-800 mb-4">Đổi mật khẩu</h2>

                    {passwordMsg.text && (
                        <div className={`px-4 py-3 rounded-lg mb-4 text-sm ${passwordMsg.type === 'success' ? 'bg-green-50 text-green-700 border border-green-200' : 'bg-red-50 text-red-700 border border-red-200'}`}>
                            {passwordMsg.text}
                        </div>
                    )}

                    <form onSubmit={handleChangePassword} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Mật khẩu hiện tại</label>
                            <input
                                type="password"
                                className="input-field"
                                value={passwordForm.currentPassword}
                                onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
                                required
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Mật khẩu mới</label>
                            <input
                                type="password"
                                className="input-field"
                                value={passwordForm.newPassword}
                                onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                                required
                                minLength={6}
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Xác nhận mật khẩu mới</label>
                            <input
                                type="password"
                                className="input-field"
                                value={passwordForm.confirmationPassword}
                                onChange={(e) => setPasswordForm({ ...passwordForm, confirmationPassword: e.target.value })}
                                required
                            />
                        </div>
                        <button type="submit" disabled={passwordLoading} className="btn-primary text-sm w-full">
                            {passwordLoading ? 'Đang xử lý...' : 'Đổi mật khẩu'}
                        </button>
                    </form>
                </div>
            )}
        </div>
    );
}

function InfoItem({ label, value, className = '' }) {
    return (
        <div className={`bg-gray-50 rounded-lg p-3 ${className}`}>
            <p className="text-xs text-gray-500 mb-0.5">{label}</p>
            <p className="text-sm font-medium text-gray-800">{value || '—'}</p>
        </div>
    );
}
