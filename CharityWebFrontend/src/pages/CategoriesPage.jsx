import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getProjectCategories, getProjects } from '../services/ProjectService';
import ProjectCard from '../components/ProjectCard';
import LoadingSpinner from '../components/LoadingSpinner';

const CATEGORY_THEME = [
    { icon: '📚', color: 'from-blue-500 to-blue-600' },
    { icon: '🏥', color: 'from-red-500 to-red-600' },
    { icon: '🌱', color: 'from-green-500 to-green-600' },
    { icon: '🤝', color: 'from-purple-500 to-purple-600' },
    { icon: '🦁', color: 'from-amber-500 to-amber-600' },
    { icon: '🏠', color: 'from-teal-500 to-teal-600' },
    { icon: '💡', color: 'from-indigo-500 to-indigo-600' },
    { icon: '❤️', color: 'from-pink-500 to-pink-600' },
];

const FALLBACK_CATEGORIES = [
    { id: 'education', categoryName: 'GIÁO DỤC' },
    { id: 'health', categoryName: 'Y TẾ' },
    { id: 'environment', categoryName: 'MÔI TRƯỜNG' },
    { id: 'society', categoryName: 'XÃ HỘI' },
    { id: 'animal', categoryName: 'ĐỘNG VẬT' },
    { id: 'housing', categoryName: 'NHÀ Ở' },
    { id: 'technology', categoryName: 'CÔNG NGHỆ' },
    { id: 'other', categoryName: 'KHÁC' },
];

const getCategoryTheme = (index) => CATEGORY_THEME[index % CATEGORY_THEME.length];

export default function CategoriesPage() {
    const navigate = useNavigate();
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [categoryProjects, setCategoryProjects] = useState([]);
    const [projectsLoading, setProjectsLoading] = useState(false);
    const [loading, setLoading] = useState(true);

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

    const handleSelectCategory = (category) => {
        setSelectedCategory(category);
        fetchProjectsByCategory(category.id);
    };

    useEffect(() => {
        const fetchData = async () => {
            try {
                const categoriesRes = await getProjectCategories();
                const apiCategories = categoriesRes.data || [];
                const categoryList = apiCategories.length > 0 ? apiCategories : FALLBACK_CATEGORIES;
                setCategories(categoryList);

                if (categoryList.length > 0) {
                    setSelectedCategory(categoryList[0]);
                    await fetchProjectsByCategory(categoryList[0].id);
                }
            } catch {
                setCategories(FALLBACK_CATEGORIES);
                if (FALLBACK_CATEGORIES.length > 0) {
                    setSelectedCategory(FALLBACK_CATEGORIES[0]);
                    await fetchProjectsByCategory(FALLBACK_CATEGORIES[0].id);
                }
            }
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

            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-12">
                {categories.map((cat, index) => {
                    const theme = getCategoryTheme(index);
                    const isSelected = selectedCategory?.id === cat.id;
                    return (
                    <button
                        key={cat.id}
                            onClick={() => handleSelectCategory(cat)}
                            className={`group relative overflow-hidden rounded-2xl p-6 text-center text-white transition-transform hover:scale-105 ${isSelected ? 'ring-4 ring-primary-200 scale-[1.02]' : ''}`}
                    >
                        <div className={`absolute inset-0 bg-gradient-to-br ${theme.color}`} />
                        <div className="absolute inset-0 bg-black/10 group-hover:bg-black/0 transition-colors" />
                        <div className="relative">
                            <div className="text-4xl mb-3">{theme.icon}</div>
                            <h3 className="font-semibold text-lg">{cat.categoryName}</h3>
                        </div>
                    </button>
                    );
                })}
            </div>

            <div>
                <div className="flex items-center justify-between gap-3 mb-6">
                    <h2 className="text-2xl font-bold text-gray-800">
                        {selectedCategory ? `Dự án ${selectedCategory.categoryName.toLocaleLowerCase('vi-VN')}` : 'Dự án theo danh mục'}
                    </h2>
                    {selectedCategory && (
                        <button
                            onClick={() => navigate(`/projects?status=ACTIVE&categoryId=${encodeURIComponent(selectedCategory.id)}`)}
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
