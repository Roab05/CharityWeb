import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getProjects } from '../services/ProjectService';
import { getProjectCategories } from '../services/CategoryService'; // Nhớ import hàm này
import ProjectCard from '../components/ProjectCard';
import LoadingSpinner from '../components/LoadingSpinner';

// Bảng màu và icon dự phòng để trang trí cho các danh mục lấy từ DB
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
    const [categories, setCategories] = useState([]);
    const [featured, setFeatured] = useState([]);
    const [loading, setLoading] = useState(true);
    const [catLoading, setCatLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                // Lấy danh sách danh mục (Không truyền keyword để lấy tất cả, lấy 12 item đầu)
                const catRes = await getProjectCategories({ page: 0, size: 12 });
                // Cập nhật lấy .content vì API trả về Page<>
                setCategories(catRes.data.content || []);
            } catch (error) {
                console.error("Lỗi lấy danh mục:", error);
            }
            setCatLoading(false);

            try {
                // Lấy dự án nổi bật
                const projRes = await getProjects({ status: 'ACTIVE', page: 0, size: 3 });
                setFeatured(projRes.data.content || []);
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
                        // Gán màu và icon lặp lại theo thứ tự index
                        const style = COLOR_PALETTES[index % COLOR_PALETTES.length];
                        return (
                            <button
                                key={cat.categoryId}
                                // Click vào sẽ chuyển sang trang dự án và filter theo categoryId
                                onClick={() => navigate(`/projects?categoryId=${cat.categoryId}&status=ACTIVE`)}
                                className="group relative overflow-hidden rounded-2xl p-6 text-center text-white transition-transform hover:scale-105"
                            >
                                <div className={`absolute inset-0 bg-gradient-to-br ${style.color}`} />
                                <div className="absolute inset-0 bg-black/10 group-hover:bg-black/0 transition-colors" />
                                <div className="relative">
                                    <div className="text-4xl mb-3">{style.icon}</div>
                                    <h3 className="font-semibold text-lg">{cat.categoryName}</h3>
                                    {/* Có thể hiển thị thêm dòng mô tả nhỏ nếu muốn */}
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