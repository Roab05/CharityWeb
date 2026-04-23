import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getProjects } from '../services/ProjectService';
import { getProjectCategories } from '../services/CategoryService';
import ProjectCard from '../components/ProjectCard';
import LoadingSpinner from '../components/LoadingSpinner';

// Đã bổ sung lại đầy đủ Icon và Màu sắc
const COLOR_PALETTES = [
    { color: 'from-blue-500 to-blue-600' },
    { color: 'from-red-500 to-red-600' },
    { color: 'from-green-500 to-green-600' },
    { color: 'from-purple-500 to-purple-600' },
    { color: 'from-amber-500 to-amber-600' },
    { color: 'from-teal-500 to-teal-600' },
    { color: 'from-indigo-500 to-indigo-600' },
    { color: 'from-pink-500 to-pink-600' },
];

export default function CategoriesPage() {
    const navigate = useNavigate();
    
    // Đã khai báo đầy đủ các State còn thiếu
    const [categories, setCategories] = useState([]);
    const [categoryProjects, setCategoryProjects] = useState([]); 
    const [selectedCategory, setSelectedCategory] = useState(null);

    const [loading, setLoading] = useState(true);
    const [catLoading, setCatLoading] = useState(true);
    const [projectsLoading, setProjectsLoading] = useState(false);

    // Hàm gọi API lấy dự án theo danh mục (Inline filter)
    const fetchProjectsByCategory = async (categoryId) => {
        setProjectsLoading(true);
        try {
            const res = await getProjects({ status: 'ACTIVE', categoryId, page: 0, size: 6 });
            setCategoryProjects(res.data.content || []);
        } catch {
            setCategoryProjects([]);
        }
        setProjectsLoading(false);
    };

    // Hàm xử lý khi người dùng click vào một thẻ danh mục
    const handleSelectCategory = (category) => {
        setSelectedCategory(category);
        fetchProjectsByCategory(category.id); // Dùng category.id chuẩn với backend
    };

    useEffect(() => {
        const fetchData = async () => {
            // 1. Tải danh sách danh mục
            setCatLoading(true);
            try {
                const catRes = await getProjectCategories({ page: 0, size: 12 });
                setCategories(catRes.data?.content || catRes.data || []);
            } catch (error) {
                console.error("Lỗi lấy danh mục:", error);
            }
            setCatLoading(false);

            // 2. Tải danh sách dự án nổi bật mặc định ban đầu
            setLoading(true);
            try {
                const projRes = await getProjects({ status: 'ACTIVE', page: 0, size: 6 });
                setCategoryProjects(projRes.data?.content || []);
            } catch { /* ignore */ }
            setLoading(false);
        };
        fetchData();
    }, []);

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="mb-10">
                <h1 className="text-3xl font-bold text-gray-800">Danh mục dự án</h1>
                <p className="text-gray-500 mt-2">Khám phá dự án theo lĩnh vực bạn quan tâm</p>
            </div>

            {catLoading ? (
                <LoadingSpinner />
            ) : categories.length > 0 ? (
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-12">
                    {categories.map((cat, index) => {
                        const style = COLOR_PALETTES[index % COLOR_PALETTES.length];
                        const isSelected = selectedCategory?.id === cat.id;
                        
                        return (
                            <button
                                key={cat.id}
                                onClick={() => handleSelectCategory(cat)}
                                // Thêm hiệu ứng viền sáng (ring) khi danh mục đang được chọn
                                className={`group relative overflow-hidden rounded-2xl p-6 text-center text-white transition-transform hover:scale-105 ${isSelected ? 'ring-4 ring-primary-400 ring-offset-2' : ''}`}
                            >
                                <div className={`absolute inset-0 bg-gradient-to-br ${style.color}`} />
                                <div className="absolute inset-0 bg-black/10 group-hover:bg-black/0 transition-colors" />
                                <div className="relative">
                                    <div className="text-4xl mb-3">{style.icon}</div>
                                    <h3 className="font-semibold text-lg">{cat.categoryName}</h3>
                                    <p className="text-xs opacity-80 mt-1 truncate">{cat.description}</p>
                                </div>
                            </button>
                        );
                    })}
                </div>
            ) : (
                <p className="text-center text-gray-500 py-8 mb-12">Chưa có danh mục nào được tạo.</p>
            )}

            <div>
                <div className="flex items-center justify-between gap-3 mb-6">
                    <h2 className="text-2xl font-bold text-gray-800">
                        {selectedCategory ? `Dự án ${selectedCategory.categoryName.toLocaleLowerCase('vi-VN')}` : 'Dự án nổi bật'}
                    </h2>
                    {selectedCategory && (
                        <button
                            onClick={() => navigate(`/projects?status=ACTIVE&categoryId=${selectedCategory.id}`)}
                            className="px-4 py-2 rounded-lg text-sm font-medium bg-white text-primary-700 border border-primary-200 hover:bg-primary-50 transition-colors"
                        >
                            Xem tất cả
                        </button>
                    )}
                </div>
                
                {loading || projectsLoading ? (
                    <LoadingSpinner />
                ) : categoryProjects.length > 0 ? (
                    <div className="grid md:grid-cols-3 gap-6">
                        {categoryProjects.map((p) => (
                            <ProjectCard key={p.projectId} project={p} />
                        ))}
                    </div>
                ) : (
                    <p className="text-center text-gray-500 py-8">Chưa có dự án thuộc danh mục này</p>
                )}
            </div>
        </div>
    );
}