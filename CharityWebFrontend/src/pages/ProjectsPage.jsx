import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { getProjects } from '../services/ProjectService';
import ProjectCard from '../components/ProjectCard';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';

const STATUS_OPTIONS = [
    { value: '', label: 'Tất cả trạng thái' },
    { value: 'ACTIVE', label: 'Đang gây quỹ' },
    { value: 'COMPLETED', label: 'Đã hoàn thành' },
    { value: 'PENDING', label: 'Chờ duyệt' },
];

export default function ProjectsPage() {
    const [searchParams, setSearchParams] = useSearchParams();
    const [projects, setProjects] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);

    const page = parseInt(searchParams.get('page') || '0');
    const status = searchParams.get('status') || 'ACTIVE';
    const categoryId = searchParams.get('categoryId') || '';

    useEffect(() => {
        const fetchProjects = async () => {
            setLoading(true);
            try {
                const params = { page, size: 9 };
                if (status) params.status = status;
                if (categoryId) params.categoryId = categoryId;
                const res = await getProjects(params);
                setProjects(res.data.content || []);
                setTotalPages(res.data.totalPages || 0);
            } catch {
                setProjects([]);
            }
            setLoading(false);
        };
        fetchProjects();
    }, [page, status, categoryId]);

    const updateParams = (updates) => {
        const newParams = new URLSearchParams(searchParams);
        Object.entries(updates).forEach(([key, value]) => {
            if (value) newParams.set(key, value);
            else newParams.delete(key);
        });
        if (updates.status !== undefined || updates.categoryId !== undefined) {
            newParams.set('page', '0');
        }
        setSearchParams(newParams);
    };

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            {/* Header */}
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-800">Tất cả dự án</h1>
                <p className="text-gray-500 mt-2">Khám phá và ủng hộ các dự án ý nghĩa</p>
            </div>

            {/* Filters */}
            <div className="flex flex-wrap gap-3 mb-8">
                {STATUS_OPTIONS.map((opt) => (
                    <button
                        key={opt.value}
                        onClick={() => updateParams({ status: opt.value })}
                        className={`px-4 py-2 rounded-lg text-sm font-medium transition-all ${status === opt.value
                                ? 'bg-primary-600 text-white shadow-sm'
                                : 'bg-white text-gray-600 border border-gray-200 hover:border-primary-300 hover:text-primary-600'
                            }`}
                    >
                        {opt.label}
                    </button>
                ))}
                {categoryId && (
                    <button
                        onClick={() => updateParams({ categoryId: '' })}
                        className="px-4 py-2 rounded-lg text-sm font-medium bg-red-50 text-red-600 border border-red-200 hover:bg-red-100 transition-all flex items-center gap-1"
                    >
                        ✕ Xóa bộ lọc danh mục
                    </button>
                )}
            </div>

            {/* Projects Grid */}
            {loading ? (
                <LoadingSpinner text="Đang tải dự án..." />
            ) : projects.length > 0 ? (
                <>
                    <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {projects.map((p) => (
                            <ProjectCard key={p.projectId} project={p} />
                        ))}
                    </div>
                    <Pagination
                        currentPage={page}
                        totalPages={totalPages}
                        onPageChange={(newPage) => updateParams({ page: newPage.toString() })}
                    />
                </>
            ) : (
                <div className="text-center py-16">
                    <div className="text-6xl mb-4">📭</div>
                    <h3 className="text-xl font-semibold text-gray-700 mb-2">Không tìm thấy dự án</h3>
                    <p className="text-gray-500">Thử thay đổi bộ lọc để xem thêm dự án</p>
                </div>
            )}
        </div>
    );
}
