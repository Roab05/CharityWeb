import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getProjects } from '../services/ProjectService';
import ProjectCard from '../components/ProjectCard';
import LoadingSpinner from '../components/LoadingSpinner';

const CATEGORIES = [
    { icon: '📚', name: 'Giáo dục', color: 'from-blue-500 to-blue-600' },
    { icon: '🏥', name: 'Y tế', color: 'from-red-500 to-red-600' },
    { icon: '🌱', name: 'Môi trường', color: 'from-green-500 to-green-600' },
    { icon: '🤝', name: 'Xã hội', color: 'from-purple-500 to-purple-600' },
    { icon: '🦁', name: 'Động vật', color: 'from-amber-500 to-amber-600' },
    { icon: '🏠', name: 'Nhà ở', color: 'from-teal-500 to-teal-600' },
    { icon: '💡', name: 'Công nghệ', color: 'from-indigo-500 to-indigo-600' },
    { icon: '❤️', name: 'Khác', color: 'from-pink-500 to-pink-600' },
];

export default function CategoriesPage() {
    const navigate = useNavigate();
    const [featured, setFeatured] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchFeatured = async () => {
            try {
                const res = await getProjects({ status: 'ACTIVE', page: 0, size: 3 });
                setFeatured(res.data.content || []);
            } catch { /* ignore */ }
            setLoading(false);
        };
        fetchFeatured();
    }, []);

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="mb-10">
                <h1 className="text-3xl font-bold text-gray-800">Danh mục dự án</h1>
                <p className="text-gray-500 mt-2">Khám phá dự án theo lĩnh vực bạn quan tâm</p>
            </div>

            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-12">
                {CATEGORIES.map((cat) => (
                    <button
                        key={cat.name}
                        onClick={() => navigate(`/projects?status=ACTIVE`)}
                        className="group relative overflow-hidden rounded-2xl p-6 text-center text-white transition-transform hover:scale-105"
                    >
                        <div className={`absolute inset-0 bg-gradient-to-br ${cat.color}`} />
                        <div className="absolute inset-0 bg-black/10 group-hover:bg-black/0 transition-colors" />
                        <div className="relative">
                            <div className="text-4xl mb-3">{cat.icon}</div>
                            <h3 className="font-semibold text-lg">{cat.name}</h3>
                        </div>
                    </button>
                ))}
            </div>

            <div>
                <h2 className="text-2xl font-bold text-gray-800 mb-6">Dự án nổi bật</h2>
                {loading ? (
                    <LoadingSpinner />
                ) : featured.length > 0 ? (
                    <div className="grid md:grid-cols-3 gap-6">
                        {featured.map((p) => (
                            <ProjectCard key={p.projectId} project={p} />
                        ))}
                    </div>
                ) : (
                    <p className="text-center text-gray-500 py-8">Chưa có dự án nổi bật</p>
                )}
            </div>
        </div>
    );
}
