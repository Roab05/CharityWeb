import React from 'react';
import { useNavigate } from 'react-router-dom';
import { API_BASE_URL } from '../services/api';

const STATUS_CONFIG = {
    ACTIVE: { label: 'Đang gây quỹ', className: 'badge-green' },
    PENDING: { label: 'Chờ duyệt', className: 'badge-yellow' },
    COMPLETED: { label: 'Hoàn thành', className: 'badge-blue' },
    REJECTED: { label: 'Từ chối', className: 'badge-red' },
    SUSPENDED: { label: 'Tạm dừng', className: 'badge-gray' },
};

export default function ProjectCard({ project, isOwner = false }) {
    const navigate = useNavigate();

    const progress = project.targetAmount > 0
        ? Math.min((project.currentAmount / project.targetAmount) * 100, 100)
        : 0;

    const statusCfg = STATUS_CONFIG[project.status] || STATUS_CONFIG.PENDING;

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('vi-VN').format(amount || 0) + ' ₫';
    };

    const getImageUrl = (url) => {
        if (!url) return 'https://images.unsplash.com/photo-1532629345422-7515f3d16bb6?w=400&h=300&fit=crop';
        if (url.startsWith('http')) return url;
        return `${API_BASE_URL.replace('/api/v1', '')}/api/v1/images/${url}`;
    };

    const getDaysLeft = () => {
        if (!project.endDate) return null;
        const end = new Date(project.endDate);
        const now = new Date();
        const diff = Math.ceil((end - now) / (1000 * 60 * 60 * 24));
        if (diff < 0) return 'Đã kết thúc';
        if (diff === 0) return 'Hôm nay kết thúc';
        return `Còn ${diff} ngày`;
    };

    const daysLeft = getDaysLeft();

    return (
        <div
            className="card overflow-hidden cursor-pointer group"
            onClick={() => navigate(isOwner ? `/my-projects/${project.projectId}` : `/projects/${project.projectId}`)}
        >
            <div className="relative overflow-hidden">
                <img
                    src={getImageUrl(project.backgroundImageURL)}
                    alt={project.projectName}
                    className="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-500"
                    onError={(e) => { e.target.src = 'https://images.unsplash.com/photo-1532629345422-7515f3d16bb6?w=400&h=300&fit=crop'; }}
                />
                <div className="absolute top-3 left-3">
                    <span className={statusCfg.className}>{statusCfg.label}</span>
                </div>
                {project.categories?.length > 0 && (
                    <div className="absolute top-3 right-3">
                        <span className="badge bg-white/90 text-gray-700 backdrop-blur-sm">
                            {project.categories[0].categoryName}
                        </span>
                    </div>
                )}
            </div>

            <div className="p-5">
                <h3 className="text-lg font-semibold text-gray-800 mb-2 line-clamp-2 group-hover:text-primary-600 transition-colors">
                    {project.projectName}
                </h3>

                {project.organizationNames?.length > 0 && (
                    <p className="text-xs text-gray-500 mb-3">
                        bởi {project.organizationNames.join(', ')}
                    </p>
                )}

                <p className="text-sm text-gray-500 mb-4 line-clamp-2">{project.description}</p>

                {/* Progress */}
                <div className="mb-3">
                    <div className="flex justify-between text-xs text-gray-500 mb-1.5">
                        <span>Đã gây quỹ</span>
                        <span className="font-medium text-gray-700">{Math.round(progress)}%</span>
                    </div>
                    <div className="w-full bg-gray-100 rounded-full h-2">
                        <div
                            className="progress-bar h-2 rounded-full transition-all duration-500"
                            style={{ width: `${progress}%` }}
                        />
                    </div>
                </div>

                <div className="flex justify-between items-center">
                    <div>
                        <p className="text-primary-600 font-bold text-sm">{formatCurrency(project.currentAmount)}</p>
                        <p className="text-xs text-gray-400">/ {formatCurrency(project.targetAmount)}</p>
                    </div>
                    {daysLeft && (
                        <span className={`text-xs font-medium ${daysLeft === 'Đã kết thúc' ? 'text-red-500' : 'text-gray-500'}`}>
                            ⏰ {daysLeft}
                        </span>
                    )}
                </div>
            </div>
        </div>
    );
}
