import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { getProjectById, getProjectActivities, getProjectDonations, getProjectDisbursements as getProjectDisbursementsFromProject } from '../services/ProjectService';
import { createActivity } from '../services/ProjectService';
import { getActivityInteractions, createInteraction, deleteInteraction } from '../services/InteractionService';
import { createDisbursement } from '../services/DisbursementService';
import { uploadFile } from '../services/FileService';
import { API_BASE_URL } from '../services/api';
import DonationModal from '../components/modals/DonationModal';
import LoadingSpinner from '../components/LoadingSpinner';

const STATUS_CONFIG = {
    ACTIVE: { label: 'Đang gây quỹ', className: 'badge-green' },
    PENDING: { label: 'Chờ duyệt', className: 'badge-yellow' },
    COMPLETED: { label: 'Hoàn thành', className: 'badge-blue' },
    REJECTED: { label: 'Từ chối', className: 'badge-red' },
    SUSPENDED: { label: 'Tạm dừng', className: 'badge-gray' },
};

export default function ProjectDetailPage() {
    const { projectId } = useParams();
    const { user } = useAuth();
    const [project, setProject] = useState(null);
    const [activities, setActivities] = useState([]);
    const [donations, setDonations] = useState([]);
    const [disbursements, setDisbursements] = useState([]);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('about');
    const [showDonation, setShowDonation] = useState(false);

    // Activity form
    const [showActivityForm, setShowActivityForm] = useState(false);
    const [activityForm, setActivityForm] = useState({ title: '', content: '', imageURL: '' });
    const [activityLoading, setActivityLoading] = useState(false);
    const [activityImageFile, setActivityImageFile] = useState(null);

    // Disbursement form
    const [showDisbursementForm, setShowDisbursementForm] = useState(false);
    const [disbursementForm, setDisbursementForm] = useState({ amount: '', reason: '', evidenceURL: '', recipientInfo: '' });
    const [disbursementLoading, setDisbursementLoading] = useState(false);

    // Interaction states
    const [interactionsByActivity, setInteractionsByActivity] = useState({});
    const [commentTexts, setCommentTexts] = useState({});

    const formatCurrency = (amount) => new Intl.NumberFormat('vi-VN').format(amount || 0) + ' ₫';

    const getImageUrl = (url) => {
        if (!url) return null;
        if (url.startsWith('http')) return url;
        return `${API_BASE_URL.replace('/api/v1', '')}/api/v1/images/${url}`;
    };

    const fetchProject = async () => {
        try {
            const res = await getProjectById(projectId);
            setProject(res.data);
        } catch { /* ignore */ }
    };

    const fetchActivities = async () => {
        try {
            const res = await getProjectActivities(projectId);
            setActivities(res.data || []);
        } catch { /* ignore */ }
    };

    const fetchDonations = async () => {
        try {
            const res = await getProjectDonations(projectId);
            setDonations(res.data || []);
        } catch { /* ignore */ }
    };

    const fetchDisbursements = async () => {
        try {
            const res = await getProjectDisbursementsFromProject(projectId);
            setDisbursements(res.data || []);
        } catch { /* ignore */ }
    };

    useEffect(() => {
        const fetchAll = async () => {
            setLoading(true);
            await Promise.allSettled([fetchProject(), fetchActivities(), fetchDonations(), fetchDisbursements()]);
            setLoading(false);
        };
        fetchAll();
    }, [projectId]);

    const fetchInteractions = async (activityId) => {
        try {
            const res = await getActivityInteractions(activityId);
            setInteractionsByActivity((prev) => ({ ...prev, [activityId]: res.data || [] }));
        } catch { /* ignore */ }
    };

    const handleCreateActivity = async (e) => {
        e.preventDefault();
        setActivityLoading(true);
        try {
            let imageURL = activityForm.imageURL;
            if (activityImageFile) {
                const uploadRes = await uploadFile(activityImageFile);
                imageURL = uploadRes.data;
            }
            await createActivity(projectId, { ...activityForm, imageURL });
            setActivityForm({ title: '', content: '', imageURL: '' });
            setActivityImageFile(null);
            setShowActivityForm(false);
            fetchActivities();
        } catch { /* ignore */ }
        setActivityLoading(false);
    };

    const handleCreateDisbursement = async (e) => {
        e.preventDefault();
        setDisbursementLoading(true);
        try {
            await createDisbursement(projectId, {
                ...disbursementForm,
                amount: parseFloat(disbursementForm.amount),
            });
            setDisbursementForm({ amount: '', reason: '', evidenceURL: '', recipientInfo: '' });
            setShowDisbursementForm(false);
            fetchDisbursements();
        } catch { /* ignore */ }
        setDisbursementLoading(false);
    };

    const handleComment = async (activityId) => {
        const content = commentTexts[activityId]?.trim();
        if (!content) return;
        try {
            await createInteraction(activityId, { type: 'COMMENT', content });
            setCommentTexts((prev) => ({ ...prev, [activityId]: '' }));
            fetchInteractions(activityId);
        } catch { /* ignore */ }
    };

    const handleLike = async (activityId) => {
        try {
            await createInteraction(activityId, { type: 'LIKE', content: null });
            fetchInteractions(activityId);
        } catch { /* ignore */ }
    };

    const handleDeleteInteraction = async (interactionId, activityId) => {
        try {
            await deleteInteraction(interactionId);
            fetchInteractions(activityId);
        } catch { /* ignore */ }
    };

    if (loading) return <div className="min-h-screen flex items-center justify-center"><LoadingSpinner size="lg" text="Đang tải dự án..." /></div>;
    if (!project) return (
        <div className="min-h-screen flex items-center justify-center">
            <div className="text-center">
                <div className="text-6xl mb-4">😔</div>
                <h2 className="text-2xl font-bold text-gray-700 mb-2">Không tìm thấy dự án</h2>
                <Link to="/projects" className="text-primary-600 hover:text-primary-700 font-medium">← Quay lại danh sách</Link>
            </div>
        </div>
    );

    const progress = project.targetAmount > 0 ? Math.min((project.currentAmount / project.targetAmount) * 100, 100) : 0;
    const statusCfg = STATUS_CONFIG[project.status] || STATUS_CONFIG.PENDING;
    const isOrg = user?.roleType === 'ORGANIZATION';
    const isOwnerOrg = isOrg && project.organizationNames?.some((name) => name === user.orgName);
    const isActive = project.status === 'ACTIVE';
    const endDate = project.endDate ? new Date(project.endDate) : null;
    const daysLeft = endDate ? Math.ceil((endDate - new Date()) / (1000 * 60 * 60 * 24)) : null;

    const tabs = [
        { key: 'about', label: 'Giới thiệu' },
        { key: 'activities', label: `Cập nhật (${activities.length})` },
        { key: 'donations', label: `Ủng hộ (${donations.length})` },
        { key: 'disbursements', label: `Giải ngân (${disbursements.length})` },
    ];

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            {/* Breadcrumb */}
            <nav className="flex items-center text-sm text-gray-500 mb-6">
                <Link to="/projects" className="hover:text-primary-600">Dự án</Link>
                <svg className="w-4 h-4 mx-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" /></svg>
                <span className="text-gray-800 font-medium truncate max-w-xs">{project.projectName}</span>
            </nav>

            <div className="grid lg:grid-cols-3 gap-8">
                {/* Main Content */}
                <div className="lg:col-span-2">
                    {/* Hero Image */}
                    <div className="relative rounded-2xl overflow-hidden mb-6">
                        <img
                            src={getImageUrl(project.backgroundImageURL) || 'https://images.unsplash.com/photo-1532629345422-7515f3d16bb6?w=800&h=400&fit=crop'}
                            alt={project.projectName}
                            className="w-full h-72 lg:h-80 object-cover"
                            onError={(e) => { e.target.src = 'https://images.unsplash.com/photo-1532629345422-7515f3d16bb6?w=800&h=400&fit=crop'; }}
                        />
                        <div className="absolute top-4 left-4 flex gap-2">
                            <span className={statusCfg.className}>{statusCfg.label}</span>
                            {project.categories?.map((cat) => (
                                <span key={cat.id || cat.categoryName} className="badge bg-white/90 text-gray-700 backdrop-blur-sm">{cat.categoryName}</span>
                            ))}
                        </div>
                    </div>

                    <h1 className="text-2xl lg:text-3xl font-bold text-gray-800 mb-3">{project.projectName}</h1>

                    {project.organizationNames?.length > 0 && (
                        <p className="text-gray-500 mb-6">bởi <span className="font-medium text-gray-700">{project.organizationNames.join(', ')}</span></p>
                    )}

                    {/* Tabs */}
                    <div className="flex border-b border-gray-200 mb-6 overflow-x-auto">
                        {tabs.map((tab) => (
                            <button
                                key={tab.key}
                                onClick={() => {
                                    setActiveTab(tab.key);
                                    if (tab.key === 'activities') activities.forEach((a) => fetchInteractions(a.activityId));
                                }}
                                className={`px-4 py-3 text-sm font-medium whitespace-nowrap border-b-2 transition-colors ${activeTab === tab.key
                                        ? 'border-primary-600 text-primary-600'
                                        : 'border-transparent text-gray-500 hover:text-gray-700'
                                    }`}
                            >
                                {tab.label}
                            </button>
                        ))}
                    </div>

                    {/* Tab Content */}
                    {activeTab === 'about' && (
                        <div>
                            <p className="text-gray-700 leading-relaxed whitespace-pre-line">{project.description}</p>
                            <div className="grid grid-cols-2 gap-4 mt-6">
                                <div className="bg-gray-50 rounded-xl p-4">
                                    <p className="text-xs text-gray-500 mb-1">Ngày bắt đầu</p>
                                    <p className="font-medium text-gray-800">{project.startDate ? new Date(project.startDate).toLocaleDateString('vi-VN') : 'N/A'}</p>
                                </div>
                                <div className="bg-gray-50 rounded-xl p-4">
                                    <p className="text-xs text-gray-500 mb-1">Ngày kết thúc</p>
                                    <p className="font-medium text-gray-800">{endDate ? endDate.toLocaleDateString('vi-VN') : 'N/A'}</p>
                                </div>
                                {project.bankAccountNo && (
                                    <div className="bg-gray-50 rounded-xl p-4 col-span-2">
                                        <p className="text-xs text-gray-500 mb-1">Số tài khoản ngân hàng</p>
                                        <p className="font-medium text-gray-800">{project.bankAccountNo}</p>
                                    </div>
                                )}
                            </div>
                        </div>
                    )}

                    {activeTab === 'activities' && (
                        <div className="space-y-6">
                            {isOwnerOrg && (
                                <div>
                                    {!showActivityForm ? (
                                        <button onClick={() => setShowActivityForm(true)} className="btn-primary text-sm">+ Đăng cập nhật mới</button>
                                    ) : (
                                        <form onSubmit={handleCreateActivity} className="card p-6 space-y-4">
                                            <h3 className="font-semibold text-gray-800">Đăng cập nhật mới</h3>
                                            <input
                                                type="text"
                                                className="input-field"
                                                placeholder="Tiêu đề cập nhật"
                                                value={activityForm.title}
                                                onChange={(e) => setActivityForm({ ...activityForm, title: e.target.value })}
                                                required
                                            />
                                            <textarea
                                                className="input-field resize-none"
                                                rows="4"
                                                placeholder="Nội dung cập nhật..."
                                                value={activityForm.content}
                                                onChange={(e) => setActivityForm({ ...activityForm, content: e.target.value })}
                                                required
                                            />
                                            <div>
                                                <label className="block text-sm text-gray-600 mb-1">Hình ảnh (không bắt buộc)</label>
                                                <input
                                                    type="file"
                                                    accept="image/*"
                                                    className="text-sm"
                                                    onChange={(e) => setActivityImageFile(e.target.files[0])}
                                                />
                                            </div>
                                            <div className="flex gap-3">
                                                <button type="submit" disabled={activityLoading} className="btn-primary text-sm">
                                                    {activityLoading ? 'Đang đăng...' : 'Đăng cập nhật'}
                                                </button>
                                                <button type="button" onClick={() => setShowActivityForm(false)} className="btn-secondary text-sm">Hủy</button>
                                            </div>
                                        </form>
                                    )}
                                </div>
                            )}

                            {activities.length === 0 ? (
                                <p className="text-center text-gray-500 py-8">Chưa có cập nhật nào</p>
                            ) : (
                                activities.map((activity) => (
                                    <div key={activity.activityId} className="card p-6">
                                        <h3 className="font-semibold text-gray-800 text-lg mb-2">{activity.title}</h3>
                                        {activity.imageURL && (
                                            <img
                                                src={getImageUrl(activity.imageURL)}
                                                alt={activity.title}
                                                className="w-full h-48 object-cover rounded-lg mb-3"
                                                onError={(e) => { e.target.style.display = 'none'; }}
                                            />
                                        )}
                                        <p className="text-gray-600 whitespace-pre-line mb-4">{activity.content}</p>

                                        {/* Interactions */}
                                        <div className="border-t pt-4">
                                            <div className="flex gap-3 mb-3">
                                                {user && (
                                                    <>
                                                        <button onClick={() => handleLike(activity.activityId)} className="text-sm text-gray-500 hover:text-red-500 transition-colors flex items-center gap-1">
                                                            ❤️ Thích
                                                        </button>
                                                    </>
                                                )}
                                                <button
                                                    onClick={() => fetchInteractions(activity.activityId)}
                                                    className="text-sm text-gray-500 hover:text-primary-600 transition-colors"
                                                >
                                                    💬 Bình luận ({interactionsByActivity[activity.activityId]?.filter((i) => i.type === 'COMMENT').length || 0})
                                                </button>
                                            </div>

                                            {/* Comment input */}
                                            {user && (
                                                <div className="flex gap-2 mb-3">
                                                    <input
                                                        type="text"
                                                        className="input-field text-sm flex-1"
                                                        placeholder="Viết bình luận..."
                                                        value={commentTexts[activity.activityId] || ''}
                                                        onChange={(e) => setCommentTexts((prev) => ({ ...prev, [activity.activityId]: e.target.value }))}
                                                        onKeyDown={(e) => e.key === 'Enter' && handleComment(activity.activityId)}
                                                    />
                                                    <button
                                                        onClick={() => handleComment(activity.activityId)}
                                                        className="px-4 py-2 bg-primary-600 text-white rounded-lg text-sm hover:bg-primary-700 transition-colors"
                                                    >
                                                        Gửi
                                                    </button>
                                                </div>
                                            )}

                                            {/* Interactions list */}
                                            {interactionsByActivity[activity.activityId]?.length > 0 && (
                                                <div className="space-y-2 max-h-60 overflow-y-auto">
                                                    {interactionsByActivity[activity.activityId].map((interaction) => (
                                                        <div key={interaction.interactionId} className="flex items-start gap-2 text-sm">
                                                            {interaction.type === 'LIKE' ? (
                                                                <p className="text-gray-500">❤️ <span className="font-medium">{interaction.displayName || interaction.username}</span> đã thích</p>
                                                            ) : (
                                                                <div className="flex-1 bg-gray-50 rounded-lg p-2.5">
                                                                    <div className="flex justify-between items-start">
                                                                        <span className="font-medium text-gray-800">{interaction.displayName || interaction.username}</span>
                                                                        {user && (interaction.userId === user.accountId) && (
                                                                            <button
                                                                                onClick={() => handleDeleteInteraction(interaction.interactionId, activity.activityId)}
                                                                                className="text-xs text-red-400 hover:text-red-600"
                                                                            >
                                                                                Xóa
                                                                            </button>
                                                                        )}
                                                                    </div>
                                                                    <p className="text-gray-600 mt-0.5">{interaction.content}</p>
                                                                    <p className="text-xs text-gray-400 mt-1">{new Date(interaction.createdAt).toLocaleString('vi-VN')}</p>
                                                                </div>
                                                            )}
                                                        </div>
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                ))
                            )}
                        </div>
                    )}

                    {activeTab === 'donations' && (
                        <div className="space-y-3">
                            {donations.length === 0 ? (
                                <p className="text-center text-gray-500 py-8">Chưa có lượt ủng hộ nào</p>
                            ) : (
                                donations.map((d, i) => (
                                    <div key={i} className="flex items-start justify-between p-4 bg-gray-50 rounded-xl">
                                        <div>
                                            <p className="font-medium text-gray-800">{d.donorName || 'Ẩn danh'}</p>
                                            {d.message && <p className="text-sm text-gray-500 mt-1">{d.message}</p>}
                                            <p className="text-xs text-gray-400 mt-1">{d.donationTime ? new Date(d.donationTime).toLocaleString('vi-VN') : ''}</p>
                                        </div>
                                        <span className="text-primary-600 font-bold whitespace-nowrap">{formatCurrency(d.amount)}</span>
                                    </div>
                                ))
                            )}
                        </div>
                    )}

                    {activeTab === 'disbursements' && (
                        <div className="space-y-4">
                            {isOwnerOrg && (
                                <div>
                                    {!showDisbursementForm ? (
                                        <button onClick={() => setShowDisbursementForm(true)} className="btn-primary text-sm">+ Yêu cầu giải ngân</button>
                                    ) : (
                                        <form onSubmit={handleCreateDisbursement} className="card p-6 space-y-4">
                                            <h3 className="font-semibold text-gray-800">Yêu cầu giải ngân</h3>
                                            <input
                                                type="number"
                                                className="input-field"
                                                placeholder="Số tiền giải ngân (₫)"
                                                value={disbursementForm.amount}
                                                onChange={(e) => setDisbursementForm({ ...disbursementForm, amount: e.target.value })}
                                                required
                                                min="1"
                                            />
                                            <textarea
                                                className="input-field resize-none"
                                                rows="3"
                                                placeholder="Lý do giải ngân..."
                                                value={disbursementForm.reason}
                                                onChange={(e) => setDisbursementForm({ ...disbursementForm, reason: e.target.value })}
                                                required
                                            />
                                            <input
                                                type="text"
                                                className="input-field"
                                                placeholder="URL bằng chứng (không bắt buộc)"
                                                value={disbursementForm.evidenceURL}
                                                onChange={(e) => setDisbursementForm({ ...disbursementForm, evidenceURL: e.target.value })}
                                            />
                                            <input
                                                type="text"
                                                className="input-field"
                                                placeholder="Thông tin người nhận"
                                                value={disbursementForm.recipientInfo}
                                                onChange={(e) => setDisbursementForm({ ...disbursementForm, recipientInfo: e.target.value })}
                                                required
                                            />
                                            <div className="flex gap-3">
                                                <button type="submit" disabled={disbursementLoading} className="btn-primary text-sm">
                                                    {disbursementLoading ? 'Đang gửi...' : 'Gửi yêu cầu'}
                                                </button>
                                                <button type="button" onClick={() => setShowDisbursementForm(false)} className="btn-secondary text-sm">Hủy</button>
                                            </div>
                                        </form>
                                    )}
                                </div>
                            )}

                            {disbursements.length === 0 ? (
                                <p className="text-center text-gray-500 py-8">Chưa có giải ngân nào</p>
                            ) : (
                                disbursements.map((d) => {
                                    const statusMap = { PENDING: 'badge-yellow', APPROVED: 'badge-green', REJECTED: 'badge-red' };
                                    const labelMap = { PENDING: 'Chờ duyệt', APPROVED: 'Đã duyệt', REJECTED: 'Từ chối' };
                                    return (
                                        <div key={d.disbursementId} className="card p-4">
                                            <div className="flex justify-between items-start mb-2">
                                                <p className="font-semibold text-gray-800">{formatCurrency(d.amount)}</p>
                                                <span className={statusMap[d.status] || 'badge-gray'}>{labelMap[d.status] || d.status}</span>
                                            </div>
                                            <p className="text-sm text-gray-600 mb-1"><span className="font-medium">Lý do:</span> {d.reason}</p>
                                            {d.recipientInfo && <p className="text-sm text-gray-600 mb-1"><span className="font-medium">Người nhận:</span> {d.recipientInfo}</p>}
                                            {d.evidenceURL && (
                                                <a href={d.evidenceURL} target="_blank" rel="noopener noreferrer" className="text-sm text-primary-600 hover:underline">Xem bằng chứng</a>
                                            )}
                                            <p className="text-xs text-gray-400 mt-2">{d.disbursementTime ? new Date(d.disbursementTime).toLocaleString('vi-VN') : ''}</p>
                                        </div>
                                    );
                                })
                            )}
                        </div>
                    )}
                </div>

                {/* Sidebar */}
                <div className="lg:col-span-1">
                    <div className="sticky top-20 space-y-6">
                        {/* Progress Card */}
                        <div className="card p-6">
                            <div className="mb-4">
                                <div className="flex justify-between text-sm mb-2">
                                    <span className="text-gray-500">Tiến độ gây quỹ</span>
                                    <span className="font-semibold text-gray-700">{Math.round(progress * 100) / 100}%</span>
                                </div>
                                <div className="w-full bg-gray-100 rounded-full h-3">
                                    <div className="progress-bar h-3 rounded-full transition-all" style={{ width: `${progress}%` }} />
                                </div>
                            </div>
                            <p className="text-2xl font-bold text-primary-600 mb-1">{formatCurrency(project.currentAmount)}</p>
                            <p className="text-sm text-gray-500 mb-4">mục tiêu {formatCurrency(project.targetAmount)}</p>

                            <div className="grid grid-cols-2 gap-3 mb-4 text-center">
                                <div className="bg-gray-50 rounded-lg p-3">
                                    <p className="text-lg font-bold text-gray-800">{donations.length}</p>
                                    <p className="text-xs text-gray-500">Lượt ủng hộ</p>
                                </div>
                                <div className="bg-gray-50 rounded-lg p-3">
                                    <p className="text-lg font-bold text-gray-800">{daysLeft !== null ? (daysLeft > 0 ? daysLeft : 0) : '∞'}</p>
                                    <p className="text-xs text-gray-500">Ngày còn lại</p>
                                </div>
                            </div>

                            {isActive && user && (
                                <button
                                    onClick={() => setShowDonation(true)}
                                    className="w-full bg-primary-600 text-white py-3 rounded-xl font-semibold hover:bg-primary-700 transition-all shadow-sm hover:shadow-md"
                                >
                                    💚 Ủng hộ ngay
                                </button>
                            )}
                            {isActive && !user && (
                                <p className="text-center text-sm text-gray-500">Đăng nhập để ủng hộ dự án này</p>
                            )}
                            {!isActive && (
                                <p className="text-center text-sm text-gray-500">Dự án hiện không nhận quyên góp</p>
                            )}
                        </div>

                        {/* Recent Donations */}
                        {donations.length > 0 && (
                            <div className="card p-6">
                                <h3 className="font-semibold text-gray-800 mb-4">Ủng hộ gần đây</h3>
                                <div className="space-y-3 max-h-64 overflow-y-auto">
                                    {donations.slice(0, 10).map((d, i) => (
                                        <div key={i} className="flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center flex-shrink-0">
                                                <span className="text-primary-700 text-xs font-bold">{(d.donorName || 'A')[0]}</span>
                                            </div>
                                            <div className="flex-1 min-w-0">
                                                <p className="text-sm font-medium text-gray-800 truncate">{d.donorName || 'Ẩn danh'}</p>
                                                <p className="text-xs text-primary-600 font-semibold">{formatCurrency(d.amount)}</p>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            <DonationModal show={showDonation} onClose={() => setShowDonation(false)} project={project} user={user} />
        </div>
    );
}
