import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import {
    getProjectById,
    getProjectActivities,
    getProjectDonations,
    getProjectDisbursements as getProjectDisbursementsFromProject,
    createActivity,
    getOrganizationsForSelector,
    addOrganizationToProject,
} from '../services/ProjectService';
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
    const [evidenceFile, setEvidenceFile] = useState(null);
    const [disbursementError, setDisbursementError] = useState('');

    // Co-manager organization form
    const [showCoManagerForm, setShowCoManagerForm] = useState(false);
    const [orgSearch, setOrgSearch] = useState('');
    const [orgOptions, setOrgOptions] = useState([]);
    const [orgLoading, setOrgLoading] = useState(false);
    const [selectedOrgId, setSelectedOrgId] = useState('');
    const [selectedOrgName, setSelectedOrgName] = useState('');
    const [coManagerLoading, setCoManagerLoading] = useState(false);
    const [coManagerMsg, setCoManagerMsg] = useState({ type: '', text: '' });

    const [interactionsByActivity, setInteractionsByActivity] = useState({});
    const [commentTexts, setCommentTexts] = useState({});

    const formatCurrency = (amount) => new Intl.NumberFormat('vi-VN').format(amount || 0) + ' ₫';

    const getImageUrl = (url) => {
        if (!url) return null;
        if (url.startsWith('http')) return url;
        return `${API_BASE_URL.replace('/api/v1', '')}/api/v1/images/${url}`;
    };

    // ==========================================
    // LOGIC TÍNH TOÁN QUỸ KHẢ DỤNG CHUẨN XÁC
    // ==========================================
    const pendingAmount = disbursements
        .filter(d => d.status === 'PENDING')
        .reduce((sum, d) => sum + d.amount, 0);

    const availableAmount = (project?.currentAmount || 0) - (project?.disbursedAmount || 0) - pendingAmount;

    // ==========================================

    const fetchProject = async () => {
        try {
            const res = await getProjectById(projectId);
            setProject(res.data);
        } catch { /* ignore */ }
    };

    const fetchActivities = async () => {
        try {
            const res = await getProjectActivities(projectId);
            setActivities(res.data.content || []);
        } catch { /* ignore */ }
    };

    const fetchDonations = async () => {
        try {
            const res = await getProjectDonations(projectId);
            setDonations(res.data.content || []);
        } catch { /* ignore */ }
    };

    const fetchDisbursements = async () => {
        try {
            const res = await getProjectDisbursementsFromProject(projectId);
            setDisbursements(res.data.content || []);
        } catch { /* ignore */ }
    };

    const isManager = user?.roleType === 'ORGANIZATION' && 
                      project?.organizationNames?.some(name => 
                          name === user.orgName || 
                          name === user.name || 
                          name === user.fullName || 
                          name === user.username
                      );

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
            setInteractionsByActivity((prev) => ({ ...prev, [activityId]: res.data?.content || [] }));
        } catch { /* ignore */ }
    };

    useEffect(() => {
        if (activities.length === 0) return;
        activities.forEach((activity) => fetchInteractions(activity.activityId));
    }, [activities]);

    useEffect(() => {
        if (!showCoManagerForm) return;
        const timer = setTimeout(async () => {
            setOrgLoading(true);
            try {
                const keyword = orgSearch.trim();
                const res = await getOrganizationsForSelector(keyword || undefined);
                const excluded = new Set(project?.organizationNames || []);
                const filtered = (res.data || []).filter((org) => !excluded.has(org.name));
                setOrgOptions(filtered);
            } catch {
                setOrgOptions([]);
            }
            setOrgLoading(false);
        }, 300);
        return () => clearTimeout(timer);
    }, [showCoManagerForm, orgSearch, project?.organizationNames]);

    const handleCreateActivity = async (e) => {
        e.preventDefault();
        setActivityLoading(true);
        try {
            let imageURL = activityForm.imageURL;
            if (activityImageFile) {
                const uploadRes = await uploadFile(activityImageFile);
                imageURL = uploadRes.data.url;
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
        setDisbursementError(''); 
        setDisbursementLoading(true);

        const requestedAmount = parseFloat(disbursementForm.amount);

        // KIỂM TRA SO VỚI QUỸ KHẢ DỤNG THAY VÌ TỔNG QUỸ
        if (requestedAmount > availableAmount) {
            setDisbursementError(`Số tiền yêu cầu (${formatCurrency(requestedAmount)}) vượt quá quỹ khả dụng hiện tại (${formatCurrency(availableAmount)}).`);
            setDisbursementLoading(false);
            return;
        }

        try {
            let finalEvidenceURL = disbursementForm.evidenceURL;
            if (evidenceFile) {
                const uploadRes = await uploadFile(evidenceFile);
                finalEvidenceURL = uploadRes.data.url;
            }

            await createDisbursement(projectId, {
                ...disbursementForm,
                evidenceURL: finalEvidenceURL,
                amount: requestedAmount,
            });
            
            setDisbursementForm({ amount: '', reason: '', evidenceURL: '', recipientInfo: '' });
            setEvidenceFile(null);
            setShowDisbursementForm(false);
            alert('Yêu cầu giải ngân đã được gửi đến Quản trị viên để xét duyệt!');
            fetchDisbursements(); // Tải lại danh sách sẽ cập nhật luôn số dư
        } catch (error) {
            setDisbursementError(error.response?.data?.message || 'Có lỗi xảy ra khi tạo yêu cầu giải ngân. Vui lòng thử lại.');
        }
        setDisbursementLoading(false);
    };

    const handleAddCoManager = async (e) => {
        e.preventDefault();
        if (!selectedOrgId) {
            setCoManagerMsg({ type: 'error', text: 'Vui lòng chọn một tổ chức từ danh sách.' });
            return;
        }
        setCoManagerLoading(true);
        setCoManagerMsg({ type: '', text: '' });
        try {
            await addOrganizationToProject(projectId, { organizationId: selectedOrgId });
            setCoManagerMsg({ type: 'success', text: 'Đã thêm tổ chức đồng quản lý.' });
            setSelectedOrgId('');
            setSelectedOrgName('');
            setOrgSearch('');
            setShowCoManagerForm(false);
            fetchProject();
        } catch (error) {
            setCoManagerMsg({ type: 'error', text: error?.response?.data?.message || 'Không thể thêm tổ chức. Vui lòng thử lại.' });
        }
        setCoManagerLoading(false);
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
    const isActive = project.status === 'ACTIVE';
    const endDate = project.endDate ? new Date(project.endDate) : null;
    const daysLeft = endDate ? Math.ceil((endDate - new Date()) / (1000 * 60 * 60 * 24)) : null;

    const tabs = [
        { key: 'about', label: 'Giới thiệu' },
        { key: 'activities', label: `Bài đăng (${activities.length})` },
        { key: 'donations', label: `Ủng hộ (${donations.length})` },
        { key: 'disbursements', label: `Giải ngân (${disbursements.length})` },
    ];

    const getActivityStats = (activityId) => {
        const interactions = interactionsByActivity[activityId] || [];
        return {
            likes: interactions.filter((i) => i.type === 'LIKE').length,
            dislikes: interactions.filter((i) => i.type === 'DISLIKE').length,
            comments: interactions.filter((i) => i.type === 'COMMENT').length,
            commentList: interactions.filter((i) => i.type === 'COMMENT'),
        };
    };

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <nav className="flex items-center text-sm text-gray-500 mb-6">
                <Link to="/projects" className="hover:text-primary-600">Dự án</Link>
                <svg className="w-4 h-4 mx-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" /></svg>
                <span className="text-gray-800 font-medium truncate max-w-xs">{project.projectName}</span>
            </nav>

            <div className="grid lg:grid-cols-3 gap-8">
                <div className="lg:col-span-2">
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
                                <span key={cat} className="badge bg-white/90 text-gray-700 backdrop-blur-sm">{cat}</span>
                            ))}
                        </div>
                    </div>

                    <h1 className="text-2xl lg:text-3xl font-bold text-gray-800 mb-3">{project.projectName}</h1>

                    {project.organizationNames?.length > 0 && (
                        <p className="text-gray-500 mb-6">bởi <span className="font-medium text-gray-700">{project.organizationNames.join(', ')}</span></p>
                    )}

                    <div className="grid grid-cols-4 overflow-x-auto border-b border-gray-200 mb-6">
                        {tabs.map((tab) => (
                            <button
                                key={tab.key}
                                onClick={() => {
                                    setActiveTab(tab.key);
                                    if (tab.key === 'activities') activities.forEach((a) => fetchInteractions(a.activityId));
                                }}
                                className={`w-full px-4 py-3 text-sm text-center whitespace-nowrap border-b-2 transition-colors ${activeTab === tab.key
                                        ? 'border-primary-600 text-primary-600 font-bold'
                                        : 'border-transparent text-gray-500 font-medium hover:text-gray-700'
                                    }`}
                            >
                                {tab.label}
                            </button>
                        ))}
                    </div>

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

                            {/* Show activities in About tab as "Bài đăng" */}
                            {activities.length > 0 && (
                                <div className="mt-10 border-t pt-8">
                                    <h2 className="text-xl font-bold text-gray-800 mb-6">Bài đăng</h2>
                                    <div className="space-y-6">
                                        {activities.map((activity) => {
                                            const stats = getActivityStats(activity.activityId);
                                            const userInteractions = (interactionsByActivity[activity.activityId] || []).filter((i) => i.userId === user?.accountId);
                                            const hasLiked = userInteractions.some((i) => i.type === 'LIKE');
                                            return (
                                                <div key={activity.activityId} className="card p-6 bg-gray-50">
                                                    <div className="flex justify-between items-start mb-2">
                                                        <h3 className="font-semibold text-gray-800 text-lg">{activity.title}</h3>
                                                        <div className="flex items-center gap-2">
                                                            <span className="text-xs text-gray-500 bg-white px-2 py-1 rounded border">
                                                                {new Date(activity.createdAt || new Date()).toLocaleString('vi-VN')}
                                                            </span>
                                                        </div>
                                                    </div>
                                                    {activity.imageURL && (
                                                        <img
                                                            src={getImageUrl(activity.imageURL)}
                                                            alt={activity.title}
                                                            className="w-full h-48 object-cover rounded-lg mb-3"
                                                            onError={(e) => { e.target.style.display = 'none'; }}
                                                        />
                                                    )}
                                                    <p className="text-gray-600 whitespace-pre-line mb-4">{activity.content}</p>

                                                    <div className="border-t pt-4">
                                                        <div className="flex flex-wrap gap-3 mb-3">
                                                            {user && (
                                                                <>
                                                                    <button onClick={() => handleLike(activity.activityId)} className="text-sm text-gray-500 hover:text-red-500 transition-colors flex items-center gap-1">
                                                                        {hasLiked ? '💔 Bỏ thích' : '❤️ Thích'} ({stats.likes})
                                                                    </button>
                                                                </>
                                                            )}
                                                            <button
                                                                onClick={() => fetchInteractions(activity.activityId)}
                                                                className="text-sm text-gray-500 hover:text-primary-600 transition-colors"
                                                            >
                                                                💬 Bình luận ({stats.comments})
                                                            </button>
                                                        </div>

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

                                                        {stats.commentList.length > 0 && (
                                                            <div className="space-y-2 max-h-60 overflow-y-auto">
                                                                {stats.commentList.map((interaction) => (
                                                                    <div key={interaction.interactionId} className="flex items-start gap-2 text-sm">
                                                                        <div className="flex-1 bg-white rounded-lg p-2.5 border border-gray-100">
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
                                                                    </div>
                                                                ))}
                                                            </div>
                                                        )}
                                                    </div>
                                                </div>
                                            );
                                        })}
                                    </div>
                                </div>
                            )}
                        </div>
                    )}

                    {activeTab === 'activities' && (
                        <div className="space-y-6">
                            {isManager && (
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
                                <p className="text-center text-gray-500 py-8">Chưa có tiến độ nào</p>
                            ) : (
                                activities.map((activity) => {
                                    const stats = getActivityStats(activity.activityId);
                                    const userInteractions = (interactionsByActivity[activity.activityId] || []).filter((i) => i.userId === user?.accountId);
                                    const hasLiked = userInteractions.some((i) => i.type === 'LIKE');
                                    return (
                                        <div key={activity.activityId} className="card p-6">
                                            <div className="flex justify-between items-start mb-2">
                                                <h3 className="font-semibold text-gray-800 text-lg">{activity.title}</h3>
                                                <div className="flex items-center gap-2">
                                                    <span className="text-sm text-gray-500">
                                                        {new Date(activity.createdAt || new Date()).toLocaleString('vi-VN')}
                                                    </span>
                                                </div>
                                            </div>
                                            {activity.imageURL && (
                                                <img
                                                    src={getImageUrl(activity.imageURL)}
                                                    alt={activity.title}
                                                    className="w-full h-48 object-cover rounded-lg mb-3"
                                                    onError={(e) => { e.target.style.display = 'none'; }}
                                                />
                                            )}
                                            <p className="text-gray-600 whitespace-pre-line mb-4">{activity.content}</p>

                                            <div className="border-t pt-4">
                                                <div className="flex flex-wrap gap-3 mb-3">
                                                    {user && (
                                                        <>
                                                            <button onClick={() => handleLike(activity.activityId)} className="text-sm text-gray-500 hover:text-red-500 transition-colors flex items-center gap-1">
                                                                {hasLiked ? '💔 Bỏ thích' : '❤️ Thích'} ({stats.likes})
                                                            </button>
                                                        </>
                                                    )}
                                                    <button
                                                        onClick={() => fetchInteractions(activity.activityId)}
                                                        className="text-sm text-gray-500 hover:text-primary-600 transition-colors"
                                                    >
                                                        💬 Bình luận ({stats.comments})
                                                    </button>
                                                </div>

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

                                                {stats.commentList.length > 0 && (
                                                    <div className="space-y-2 max-h-60 overflow-y-auto">
                                                        {stats.commentList.map((interaction) => (
                                                            <div key={interaction.interactionId} className="flex items-start gap-2 text-sm">
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
                                                            </div>
                                                        ))}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    );
                                })
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
                            
                            {/* HIỂN THỊ QUỸ KHẢ DỤNG CHUẨN XÁC */}
                            {isManager && (
                                <div className="flex items-center justify-between bg-emerald-50 border border-emerald-200 p-4 rounded-xl mb-4">
                                    <div>
                                        <p className="font-semibold text-emerald-800">Quỹ khả dụng (Còn lại)</p>
                                        <p className="text-xs text-emerald-600 mt-0.5">
                                            Đã trừ các khoản đang chờ duyệt: {formatCurrency(pendingAmount)}
                                        </p>
                                    </div>
                                    <p className="text-2xl font-bold text-emerald-700">
                                        {formatCurrency(availableAmount)}
                                    </p>
                                </div>
                            )}

                            {isManager && (
                                <div>
                                    {!showDisbursementForm ? (
                                        <button onClick={() => setShowDisbursementForm(true)} className="btn-primary text-sm">+ Yêu cầu giải ngân</button>
                                    ) : (
                                        <form onSubmit={handleCreateDisbursement} className="card p-6 space-y-4">
                                            <h3 className="font-semibold text-gray-800">Yêu cầu giải ngân</h3>
                                            
                                            {disbursementError && (
                                                <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
                                                    {disbursementError}
                                                </div>
                                            )}

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
                                            
                                            <div>
                                                <label className="block text-sm font-medium text-gray-700 mb-1.5">Bằng chứng (Hình ảnh/Hóa đơn)</label>
                                                <input
                                                    type="file"
                                                    accept="image/*"
                                                    className="input-field text-sm"
                                                    onChange={(e) => setEvidenceFile(e.target.files[0])}
                                                />
                                                <p className="text-xs text-gray-400 mt-1 mb-2">Hoặc nhập URL hình ảnh bên dưới</p>
                                                <input
                                                    type="text"
                                                    className="input-field"
                                                    placeholder="https://example.com/hoa-don.jpg"
                                                    value={disbursementForm.evidenceURL}
                                                    onChange={(e) => setDisbursementForm({ ...disbursementForm, evidenceURL: e.target.value })}
                                                />
                                            </div>

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
                                                <a href={getImageUrl(d.evidenceURL)} target="_blank" rel="noopener noreferrer" className="text-sm text-primary-600 hover:underline">Xem bằng chứng</a>
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

                            {isActive && user && !isManager && (
                                <button
                                    onClick={() => setShowDonation(true)}
                                    className="w-full bg-primary-600 text-white py-3 rounded-xl font-semibold hover:bg-primary-700 transition-all shadow-sm hover:shadow-md"
                                >
                                    💚 Ủng hộ ngay
                                </button>
                            )}
                            {isManager && (
                                <div className="bg-blue-50 text-blue-700 text-sm text-center py-3 px-4 rounded-xl mt-4 border border-blue-100">
                                    Bạn đang là quản lý của dự án này.
                                </div>
                            )}
                            {isActive && !user && (
                                <p className="text-center text-sm text-gray-500">Đăng nhập để ủng hộ dự án này</p>
                            )}
                            {!isActive && (
                                <p className="text-center text-sm text-gray-500">Dự án hiện không nhận quyên góp</p>
                            )}
                        </div>

                        {/* Co-Managers Card (Chỉ hiển thị cho Manager) */}
                        {isManager && (
                            <div className="card p-6">
                                <h3 className="font-semibold text-gray-800 mb-3">Đồng quản lý dự án</h3>

                                {coManagerMsg.text && (
                                    <div className={`text-sm rounded-lg px-3 py-2 mb-3 border ${coManagerMsg.type === 'success'
                                            ? 'bg-green-50 text-green-700 border-green-200'
                                            : 'bg-red-50 text-red-700 border-red-200'
                                        }`}>
                                        {coManagerMsg.text}
                                    </div>
                                )}

                                <p className="text-sm text-gray-500 mb-3">
                                    Các tổ chức đang quản lý: <span className="font-medium text-gray-700">{(project.organizationNames || []).join(', ') || 'Chưa có'}</span>
                                </p>

                                {!showCoManagerForm ? (
                                    <button
                                        type="button"
                                        onClick={() => {
                                            setShowCoManagerForm(true);
                                            setCoManagerMsg({ type: '', text: '' });
                                        }}
                                        className="btn-primary text-sm w-full"
                                    >
                                        + Thêm tổ chức đồng quản lý
                                    </button>
                                ) : (
                                    <form onSubmit={handleAddCoManager} className="space-y-3">
                                        <input
                                            type="text"
                                            className="input-field"
                                            placeholder="Tìm theo tên tổ chức..."
                                            value={orgSearch}
                                            onChange={(e) => {
                                                setOrgSearch(e.target.value);
                                                setSelectedOrgId('');
                                                setSelectedOrgName('');
                                            }}
                                        />

                                        <div className="border border-gray-200 rounded-lg max-h-40 overflow-y-auto bg-white">
                                            {orgLoading ? (
                                                <p className="text-sm text-gray-500 px-3 py-2">Đang tìm tổ chức...</p>
                                            ) : orgOptions.length === 0 ? (
                                                <p className="text-sm text-gray-500 px-3 py-2">Không tìm thấy tổ chức phù hợp.</p>
                                            ) : (
                                                orgOptions.map((org) => (
                                                    <button
                                                        key={org.organizationId}
                                                        type="button"
                                                        onClick={() => {
                                                            setSelectedOrgId(org.organizationId);
                                                            setSelectedOrgName(org.name);
                                                        }}
                                                        className={`w-full text-left px-3 py-2 text-sm hover:bg-gray-50 transition-colors ${selectedOrgId === org.organizationId ? 'bg-primary-50 text-primary-700 font-medium' : 'text-gray-700'
                                                            }`}
                                                    >
                                                        {org.name}
                                                    </button>
                                                ))
                                            )}
                                        </div>

                                        {selectedOrgName && (
                                            <p className="text-sm text-gray-600">
                                                Đã chọn: <span className="font-medium text-primary-700">{selectedOrgName}</span>
                                            </p>
                                        )}

                                        <div className="flex gap-2">
                                            <button
                                                type="submit"
                                                disabled={coManagerLoading}
                                                className="btn-primary text-sm flex-1"
                                            >
                                                {coManagerLoading ? 'Đang thêm...' : 'Thêm tổ chức'}
                                            </button>
                                            <button
                                                type="button"
                                                onClick={() => {
                                                    setShowCoManagerForm(false);
                                                    setOrgSearch('');
                                                    setOrgOptions([]);
                                                    setSelectedOrgId('');
                                                    setSelectedOrgName('');
                                                }}
                                                className="btn-secondary text-sm"
                                            >
                                                Hủy
                                            </button>
                                        </div>
                                    </form>
                                )}
                            </div>
                        )}

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