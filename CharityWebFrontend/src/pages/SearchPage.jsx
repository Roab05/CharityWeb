import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { getProjects } from '../services/ProjectService';
import ProjectCard from '../components/ProjectCard';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';

export default function SearchPage() {
    const [searchParams] = useSearchParams();
    const query = searchParams.get('q') || '';
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        const search = async () => {
            setLoading(true);
            try {
                // Backend doesn't have a search endpoint - fetch all and filter client-side
                const res = await getProjects({ status: 'ACTIVE', page: 0, size: 100 });
                const all = res.data.content || [];
                const filtered = all.filter(
                    (p) =>
                        p.projectName?.toLowerCase().includes(query.toLowerCase()) ||
                        p.description?.toLowerCase().includes(query.toLowerCase())
                );
                setProjects(filtered);
                setTotalPages(Math.ceil(filtered.length / 9));
            } catch {
                setProjects([]);
            }
            setLoading(false);
        };
        if (query) search();
        else { setProjects([]); setLoading(false); }
    }, [query]);

    const paginatedProjects = projects.slice(page * 9, (page + 1) * 9);

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-800">Kết quả tìm kiếm</h1>
                {query && (
                    <p className="text-gray-500 mt-2">
                        Tìm thấy <span className="font-semibold text-primary-600">{projects.length}</span> kết quả cho "{query}"
                    </p>
                )}
            </div>

            {loading ? (
                <LoadingSpinner text="Đang tìm kiếm..." />
            ) : paginatedProjects.length > 0 ? (
                <>
                    <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {paginatedProjects.map((p) => (
                            <ProjectCard key={p.projectId} project={p} />
                        ))}
                    </div>
                    <Pagination
                        currentPage={page}
                        totalPages={totalPages}
                        onPageChange={(p) => setPage(p)}
                    />
                </>
            ) : (
                <div className="text-center py-16">
                    <div className="text-6xl mb-4">🔍</div>
                    <h3 className="text-xl font-semibold text-gray-700 mb-2">Không tìm thấy kết quả</h3>
                    <p className="text-gray-500 mb-6">Không có dự án nào phù hợp với từ khóa "{query}"</p>
                    <div className="text-sm text-gray-400 space-y-1">
                        <p>💡 Gợi ý:</p>
                        <p>• Thử sử dụng từ khóa khác</p>
                        <p>• Kiểm tra chính tả</p>
                        <p>• Sử dụng từ khóa ngắn gọn hơn</p>
                    </div>
                </div>
            )}
        </div>
    );
}
