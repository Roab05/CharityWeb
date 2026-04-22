import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import {
    getStatistics,
    getPendingOrganizations,
    verifyOrganization,
    getPendingProjects,
    approveProject,
    createCategory,
    getCategories,
    updateCategory,
    getPendingDisbursements,
    updateDisbursementStatus,
} from '../services/AdminService';
import LoadingSpinner from '../components/LoadingSpinner';
import ConfirmModal from '../components/modals/ConfirmModal';

export default function AdminDashboardPage() {
    const { user } = useAuth();
    const [activeTab, setActiveTab] = useState('overview');
    const [stats, setStats] = useState(null);
    const [pendingOrgs, setPendingOrgs] = useState([]);
    const [pendingProjects, setPendingProjects] = useState([]);
    const [pendingDisbursements, setPendingDisbursements] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);

    // Category management
    const [showCategoryForm, setShowCategoryForm] = useState(false);
    const [categoryForm, setCategoryForm] = useState({ categoryName: '', description: '' });
    const [categoryLoading, setCategoryLoading] = useState(false);
    const [categoryError, setCategoryError] = useState('');
    const [categorySuccess, setCategorySuccess] = useState('');
    const [editingCategory, setEditingCategory] = useState(null);
    const [editForm, setEditForm] = useState({ categoryName: '', description: '' });

    // Confirm modal
    const [confirmModal, setConfirmModal] = useState({ show: false, title: '', message: '', onConfirm: null });
    const [actionLoading, setActionLoading] = useState(false);

    const formatCurrency = (amount) => new Intl.NumberFormat('vi-VN').format(amount || 0) + ' ₫';

    const fetchAll = async () => {
        setLoading(true);
        try {
            const [statsRes, orgsRes, projectsRes, disbRes, categoriesRes] = await Promise.allSettled([
                getStatistics(),
                getPendingOrganizations(),
                getPendingProjects(),
                getPendingDisbursements(),
                getCategories(),
            ]);
            if (statsRes.status === 'fulfilled') setStats(statsRes.value.data);
            if (orgsRes.status === 'fulfilled') setPendingOrgs(orgsRes.value.data || []);
            if (projectsRes.status === 'fulfilled') setPendingProjects(projectsRes.value.data || []);
            if (disbRes.status === 'fulfilled') setPendingDisbursements(disbRes.value.data || []);
            if (categoriesRes.status === 'fulfilled') setCategories(categoriesRes.value.data || []);
        } catch { /* ignore */ }
        setLoading(false);
    };

    useEffect(() => {
        fetchAll();
    }, []);

    const handleVerifyOrg = async (orgId, status) => {
        setActionLoading(true);
        try {
            await verifyOrganization(orgId, { status });
            fetchAll();
        } catch { /* ignore */ }
        setActionLoading(false);
        setConfirmModal({ show: false });
    };

    const handleApproveProject = async (projectId, status) => {
        setActionLoading(true);
        try {
            await approveProject(projectId, { status });
            fetchAll();
        } catch { /* ignore */ }
        setActionLoading(false);
        setConfirmModal({ show: false });
    };

    const handleDisbursementStatus = async (disbursementId, status) => {
        setActionLoading(true);
        try {
            await updateDisbursementStatus(disbursementId, { status });
            fetchAll();
        } catch { /* ignore */ }
        setActionLoading(false);
        setConfirmModal({ show: false });
    };

    const handleCreateCategory = async (e) => {
        e.preventDefault();
        setCategoryError('');
        setCategorySuccess('');
        setCategoryLoading(true);
        try {
            await createCategory(categoryForm);
            setCategoryForm({ categoryName: '', description: '' });
            setShowCategoryForm(false);
            setCategorySuccess('Tạo danh mục thành công.');
            await fetchAll();
        } catch (err) {
            setCategoryError(err.response?.data?.message || 'Không thể tạo danh mục. Vui lòng thử lại.');
        }
        setCategoryLoading(false);
    };

    const handleEditCategoryClick = (cat) => {
        setEditingCategory(cat.id);
        setEditForm({ categoryName: cat.categoryName, description: cat.description || '' });
    };

    const handleUpdateCategory = async (e, id) => {
        e.preventDefault();
        setCategoryError('');
        setCategorySuccess('');
        try {
            await updateCategory(id, editForm);
            setEditingCategory(null);
            setCategorySuccess('Cập nhật danh mục thành công.');
            await fetchAll();
        } catch (err) {
            setCategoryError(err.response?.data?.message || 'Không thể cập nhật danh mục. Vui lòng thử lại.');
        }
    };

    if (loading) return <div className="min-h-screen flex items-center justify-center"><LoadingSpinner size="lg" text="Đang tải..." /></div>;

    const tabs = [
        { key: 'overview', label: '📊 Tổng quan' },
        { key: 'organizations', label: `🏢 Tổ chức (${pendingOrgs.length})` },
        { key: 'projects', label: `📋 Dự án (${pendingProjects.length})` },
        { key: 'disbursements', label: `💰 Giải ngân (${pendingDisbursements.length})` },
        { key: 'categories', label: `📂 Danh mục (${categories.length})` },
    ];

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="flex items-center justify-between mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-gray-800">Bảng quản trị</h1>
                    <p className="text-gray-500 mt-1">Xin chào, {user?.username}</p>
                </div>
                <button onClick={fetchAll} className="btn-secondary text-sm">🔄 Làm mới</button>
            </div>

            {/* Tabs */}
            <div className="flex flex-wrap gap-2 mb-6">
                {tabs.map((tab) => (
                    <button
                        key={tab.key}
                        onClick={() => setActiveTab(tab.key)}
                        className={`px-4 py-2 rounded-lg text-sm font-medium transition-all ${activeTab === tab.key
                                ? 'bg-primary-600 text-white shadow-sm'
                                : 'bg-white text-gray-600 border border-gray-200 hover:border-primary-300'
                            }`}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>

            {/* Overview */}
            {activeTab === 'overview' && stats && (
                <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
                    <StatCard title="Tổng quyên góp" value={formatCurrency(stats.totalDonatedAmount)} color="text-primary-600" bg="bg-primary-50" />
                    <StatCard title="Dự án đang hoạt động" value={stats.activeProjectsCount} color="text-blue-600" bg="bg-blue-50" />
                    <StatCard title="Tổ chức chờ duyệt" value={stats.pendingOrganizationsCount} color="text-amber-600" bg="bg-amber-50" />
                    <StatCard title="Dự án chờ duyệt" value={stats.pendingProjectsCount} color="text-purple-600" bg="bg-purple-50" />
                </div>
            )}

            {/* Pending Organizations */}
            {activeTab === 'organizations' && (
                <div className="space-y-4">
                    {pendingOrgs.length === 0 ? (
                        <p className="text-center text-gray-500 py-12">Không có tổ chức nào đang chờ duyệt</p>
                    ) : (
                        pendingOrgs.map((org) => (
                            <div key={org.accountId} className="card p-6">
                                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                                    <div>
                                        <h3 className="font-semibold text-gray-800 text-lg">{org.orgName || org.username}</h3>
                                        <div className="text-sm text-gray-500 space-y-1 mt-2">
                                            <p>📧 {org.email} • 📞 {org.phone}</p>
                                            {org.websiteURL && <p>🌐 {org.websiteURL}</p>}
                                            {org.description && <p className="text-gray-600 mt-1">{org.description}</p>}
                                        </div>
                                    </div>
                                    <div className="flex gap-2 flex-shrink-0">
                                        <button
                                            onClick={() => setConfirmModal({
                                                show: true,
                                                title: 'Xác minh tổ chức',
                                                message: `Phê duyệt tổ chức "${org.orgName || org.username}"?`,
                                                onConfirm: () => handleVerifyOrg(org.accountId, 'ACTIVE'),
                                            })}
                                            className="px-4 py-2 bg-green-600 text-white rounded-lg text-sm font-medium hover:bg-green-700 transition-colors"
                                        >
                                            ✓ Phê duyệt
                                        </button>
                                        <button
                                            onClick={() => setConfirmModal({
                                                show: true,
                                                title: 'Từ chối tổ chức',
                                                message: `Từ chối tổ chức "${org.orgName || org.username}"?`,
                                                onConfirm: () => handleVerifyOrg(org.accountId, 'BANNED'),
                                            })}
                                            className="px-4 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 transition-colors"
                                        >
                                            ✕ Từ chối
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            )}

            {/* Pending Projects */}
            {activeTab === 'projects' && (
                <div className="space-y-4">
                    {pendingProjects.length === 0 ? (
                        <p className="text-center text-gray-500 py-12">Không có dự án nào đang chờ duyệt</p>
                    ) : (
                        pendingProjects.map((p) => (
                            <div key={p.projectId} className="card p-6">
                                <div className="flex flex-col md:flex-row md:items-start justify-between gap-4">
                                    <div className="flex-1">
                                        <h3 className="font-semibold text-gray-800 text-lg">{p.projectName}</h3>
                                        {p.organizationNames?.length > 0 && (
                                            <p className="text-sm text-gray-500">bởi {p.organizationNames.join(', ')}</p>
                                        )}
                                        <p className="text-sm text-gray-600 mt-2 line-clamp-3">{p.description}</p>
                                        <div className="flex gap-4 mt-2 text-sm text-gray-500">
                                            <span>🎯 Mục tiêu: {formatCurrency(p.targetAmount)}</span>
                                            {p.startDate && <span>📅 {new Date(p.startDate).toLocaleDateString('vi-VN')} - {p.endDate ? new Date(p.endDate).toLocaleDateString('vi-VN') : 'N/A'}</span>}
                                        </div>
                                    </div>
                                    <div className="flex gap-2 flex-shrink-0">
                                        <button
                                            onClick={() => setConfirmModal({
                                                show: true,
                                                title: 'Phê duyệt dự án',
                                                message: `Phê duyệt dự án "${p.projectName}"?`,
                                                onConfirm: () => handleApproveProject(p.projectId, 'ACTIVE'),
                                            })}
                                            className="px-4 py-2 bg-green-600 text-white rounded-lg text-sm font-medium hover:bg-green-700 transition-colors"
                                        >
                                            ✓ Phê duyệt
                                        </button>
                                        <button
                                            onClick={() => setConfirmModal({
                                                show: true,
                                                title: 'Từ chối dự án',
                                                message: `Từ chối dự án "${p.projectName}"?`,
                                                onConfirm: () => handleApproveProject(p.projectId, 'REJECTED'),
                                            })}
                                            className="px-4 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 transition-colors"
                                        >
                                            ✕ Từ chối
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            )}

            {/* Pending Disbursements */}
            {activeTab === 'disbursements' && (
                <div className="space-y-4">
                    {pendingDisbursements.length === 0 ? (
                        <p className="text-center text-gray-500 py-12">Không có yêu cầu giải ngân nào đang chờ</p>
                    ) : (
                        pendingDisbursements.map((d) => (
                            <div key={d.disbursementId} className="card p-6">
                                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                                    <div>
                                        <p className="font-bold text-lg text-gray-800">{formatCurrency(d.amount)}</p>
                                        <p className="text-sm text-gray-600 mt-1"><span className="font-medium">Lý do:</span> {d.reason}</p>
                                        <p className="text-sm text-gray-600"><span className="font-medium">Người nhận:</span> {d.recipientInfo}</p>
                                        {d.evidenceURL && (
                                            <a href={d.evidenceURL} target="_blank" rel="noopener noreferrer" className="text-sm text-primary-600 hover:underline">Xem bằng chứng</a>
                                        )}
                                        <p className="text-xs text-gray-400 mt-1">Dự án: {d.projectId}</p>
                                    </div>
                                    <div className="flex gap-2 flex-shrink-0">
                                        <button
                                            onClick={() => setConfirmModal({
                                                show: true,
                                                title: 'Phê duyệt giải ngân',
                                                message: `Phê duyệt giải ngân ${formatCurrency(d.amount)}?`,
                                                onConfirm: () => handleDisbursementStatus(d.disbursementId, 'APPROVED'),
                                            })}
                                            className="px-4 py-2 bg-green-600 text-white rounded-lg text-sm font-medium hover:bg-green-700 transition-colors"
                                        >
                                            ✓ Duyệt
                                        </button>
                                        <button
                                            onClick={() => setConfirmModal({
                                                show: true,
                                                title: 'Từ chối giải ngân',
                                                message: `Từ chối giải ngân ${formatCurrency(d.amount)}?`,
                                                onConfirm: () => handleDisbursementStatus(d.disbursementId, 'REJECTED'),
                                            })}
                                            className="px-4 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 transition-colors"
                                        >
                                            ✕ Từ chối
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            )}

            {/* Categories */}
            {activeTab === 'categories' && (
                <div>
                    {categoryError && (
                        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm">{categoryError}</div>
                    )}
                    {categorySuccess && (
                        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg mb-4 text-sm">{categorySuccess}</div>
                    )}
                    {!showCategoryForm ? (
                        <button onClick={() => setShowCategoryForm(true)} className="btn-primary text-sm mb-6">+ Tạo danh mục mới</button>
                    ) : (
                        <form onSubmit={handleCreateCategory} className="card p-6 mb-6 space-y-4">
                            <h3 className="font-semibold text-gray-800">Tạo danh mục mới</h3>
                            <input
                                type="text"
                                className="input-field"
                                placeholder="Tên danh mục"
                                value={categoryForm.categoryName}
                                onChange={(e) => setCategoryForm({ ...categoryForm, categoryName: e.target.value })}
                                required
                            />
                            <textarea
                                className="input-field resize-none"
                                rows="3"
                                placeholder="Mô tả danh mục..."
                                value={categoryForm.description}
                                onChange={(e) => setCategoryForm({ ...categoryForm, description: e.target.value })}
                            />
                            <div className="flex gap-3">
                                <button type="submit" disabled={categoryLoading} className="btn-primary text-sm">{categoryLoading ? 'Đang tạo...' : 'Tạo danh mục'}</button>
                                <button type="button" onClick={() => setShowCategoryForm(false)} className="btn-secondary text-sm">Hủy</button>
                            </div>
                        </form>
                    )}
                    <div className="bg-blue-50 rounded-xl p-4 text-sm text-blue-700">
                        💡 Danh mục được sử dụng để phân loại dự án. Bạn có thể tạo danh mục mới tại đây.
                    </div>

                    <div className="mt-6 card p-6">
                        <h3 className="font-semibold text-gray-800 mb-4">Danh sách danh mục ({categories.length})</h3>
                        {categories.length === 0 ? (
                            <p className="text-sm text-gray-500">Chưa có danh mục nào.</p>
                        ) : (
                            <div className="space-y-3">
                                {categories.map((cat) => (
                                    <div key={cat.id} className="border border-gray-200 rounded-lg p-4 flex justify-between items-start hover:border-primary-300 transition-colors">
                                        {editingCategory === cat.id ? (
                                            <form className="w-full flex flex-col gap-3" onSubmit={(e) => handleUpdateCategory(e, cat.id)}>
                                                <input
                                                    type="text"
                                                    className="input-field"
                                                    value={editForm.categoryName}
                                                    onChange={(e) => setEditForm({...editForm, categoryName: e.target.value})}
                                                    required
                                                />
                                                <textarea
                                                    className="input-field resize-none"
                                                    rows="2"
                                                    value={editForm.description}
                                                    onChange={(e) => setEditForm({...editForm, description: e.target.value})}
                                                />
                                                <div className="flex gap-2">
                                                    <button type="submit" className="px-4 py-2 bg-primary-600 text-white rounded-lg text-sm font-medium hover:bg-primary-700">Lưu</button>
                                                    <button type="button" onClick={() => setEditingCategory(null)} className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-300">Hủy</button>
                                                </div>
                                            </form>
                                        ) : (
                                            <>
                                                <div className="flex-1">
                                                    <p className="font-semibold text-gray-800 text-lg">{cat.categoryName}</p>
                                                    <p className="text-xs text-gray-500 mt-1">ID: {cat.id}</p>
                                                    {cat.description && <p className="text-sm text-gray-600 mt-2">{cat.description}</p>}
                                                </div>
                                                <button
                                                    onClick={() => handleEditCategoryClick(cat)}
                                                    className="ml-4 px-4 py-2 text-sm font-medium text-primary-600 bg-primary-50 rounded-lg hover:bg-primary-100 transition-colors"
                                                >
                                                    Sửa
                                                </button>
                                            </>
                                        )}
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            )}

            <ConfirmModal
                show={confirmModal.show}
                title={confirmModal.title}
                message={confirmModal.message}
                onConfirm={confirmModal.onConfirm}
                onClose={() => setConfirmModal({ show: false })}
                loading={actionLoading}
                variant="primary"
                confirmText="Xác nhận"
            />
        </div>
    );
}

function StatCard({ title, value, color, bg }) {
    return (
        <div className={`${bg} rounded-2xl p-6`}>
            <p className="text-sm text-gray-600 mb-2">{title}</p>
            <p className={`text-3xl font-bold ${color}`}>{value}</p>
        </div>
    );
}
