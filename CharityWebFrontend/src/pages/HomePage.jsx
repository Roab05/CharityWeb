import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getProjects } from '../services/ProjectService';
import { getStatistics } from '../services/AdminService';
import ProjectCard from '../components/ProjectCard';
import LoadingSpinner from '../components/LoadingSpinner';

export default function HomePage() {
    const [projects, setProjects] = useState([]);
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [projectsRes, statsRes] = await Promise.allSettled([
                    getProjects({ status: 'ACTIVE', page: 0, size: 6 }),
                    getStatistics(),
                ]);
                if (projectsRes.status === 'fulfilled') {
                    setProjects(projectsRes.value.data.content || []);
                }
                if (statsRes.status === 'fulfilled') {
                    setStats(statsRes.value.data);
                }
            } catch { /* ignore */ }
            setLoading(false);
        };
        fetchData();
    }, []);

    return (
        <div>
            {/* Hero Section */}
            <section className="relative bg-gradient-to-br from-primary-600 via-primary-700 to-teal-700 text-white overflow-hidden">
                <div className="absolute inset-0 opacity-10">
                    <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1559027615-cd4628902d4a?w=1200&h=600&fit=crop')] bg-cover bg-center" />
                </div>
                <div className="absolute top-0 right-0 w-96 h-96 bg-white/5 rounded-full -translate-y-1/2 translate-x-1/3" />
                <div className="absolute bottom-0 left-0 w-64 h-64 bg-white/5 rounded-full translate-y-1/3 -translate-x-1/4" />

                <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 lg:py-28">
                    <div className="grid lg:grid-cols-2 gap-12 items-center">
                        <div className="text-center lg:text-left">
                            <div className="inline-flex items-center bg-white/15 rounded-full px-4 py-1.5 text-sm font-medium mb-6 backdrop-blur-sm">
                                🌟 Nền tảng gây quỹ #1 Việt Nam
                            </div>
                            <h1 className="text-4xl lg:text-5xl xl:text-6xl font-bold mb-6 leading-tight">
                                Cùng nhau tạo nên{' '}
                                <span className="text-emerald-200">sự thay đổi</span>
                            </h1>
                            <p className="text-lg lg:text-xl text-white/80 mb-8 max-w-xl">
                                Tham gia cộng đồng gây quỹ lớn nhất Việt Nam để hỗ trợ các dự án ý nghĩa cho giáo dục, y tế và môi trường.
                            </p>
                            <div className="flex flex-col sm:flex-row gap-4 justify-center lg:justify-start">
                                <Link
                                    to="/projects"
                                    className="bg-white text-primary-700 px-8 py-3.5 rounded-xl font-semibold hover:bg-gray-50 transition-all shadow-lg hover:shadow-xl text-center"
                                >
                                    Khám phá dự án
                                </Link>
                                <Link
                                    to="/register"
                                    className="border-2 border-white/40 text-white px-8 py-3.5 rounded-xl font-semibold hover:bg-white/10 transition-all text-center"
                                >
                                    Bắt đầu gây quỹ
                                </Link>
                            </div>
                        </div>
                        <div className="hidden lg:block">
                            <img
                                src="https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca?w=500&h=400&fit=crop"
                                alt="Tình nguyện viên"
                                className="rounded-2xl shadow-2xl rotate-2 hover:rotate-0 transition-transform duration-500"
                            />
                        </div>
                    </div>
                </div>
            </section>

            {/* Stats Section */}
            {stats && (
                <section className="relative -mt-8 z-10 max-w-5xl mx-auto px-4">
                    <div className="bg-white rounded-2xl shadow-xl p-6 grid grid-cols-2 md:grid-cols-4 gap-6">
                        <div className="text-center">
                            <p className="text-2xl lg:text-3xl font-bold text-primary-600">
                                {new Intl.NumberFormat('vi-VN', { notation: 'compact' }).format(stats.totalDonatedAmount || 0)}₫
                            </p>
                            <p className="text-sm text-gray-500 mt-1">Tổng quyên góp</p>
                        </div>
                        <div className="text-center">
                            <p className="text-2xl lg:text-3xl font-bold text-primary-600">{stats.activeProjectsCount || 0}</p>
                            <p className="text-sm text-gray-500 mt-1">Dự án đang hoạt động</p>
                        </div>
                        <div className="text-center">
                            <p className="text-2xl lg:text-3xl font-bold text-amber-600">{stats.pendingOrganizationsCount || 0}</p>
                            <p className="text-sm text-gray-500 mt-1">Tổ chức chờ duyệt</p>
                        </div>
                        <div className="text-center">
                            <p className="text-2xl lg:text-3xl font-bold text-blue-600">{stats.pendingProjectsCount || 0}</p>
                            <p className="text-sm text-gray-500 mt-1">Dự án chờ duyệt</p>
                        </div>
                    </div>
                </section>
            )}

            {/* Recent Projects */}
            <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
                <div className="flex justify-between items-center mb-10">
                    <div>
                        <h2 className="text-3xl font-bold text-gray-800">Dự án nổi bật</h2>
                        <p className="text-gray-500 mt-2">Cùng chung tay hỗ trợ các dự án ý nghĩa</p>
                    </div>
                    <Link to="/projects" className="text-primary-600 hover:text-primary-700 font-semibold text-sm flex items-center gap-1">
                        Xem tất cả
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                        </svg>
                    </Link>
                </div>

                {loading ? (
                    <LoadingSpinner text="Đang tải dự án..." />
                ) : projects.length > 0 ? (
                    <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {projects.map((p) => (
                            <ProjectCard key={p.projectId} project={p} />
                        ))}
                    </div>
                ) : (
                    <div className="text-center py-12 text-gray-500">
                        <p className="text-lg">Chưa có dự án nào đang hoạt động</p>
                    </div>
                )}
            </section>

            {/* Values Section */}
            <section className="bg-gray-50 py-16">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="text-center mb-12">
                        <h2 className="text-3xl font-bold text-gray-800">Tại sao chọn GayQuy.vn?</h2>
                        <p className="text-gray-500 mt-2 max-w-2xl mx-auto">
                            Chúng tôi cam kết mang đến trải nghiệm gây quỹ minh bạch, an toàn và hiệu quả
                        </p>
                    </div>
                    <div className="grid md:grid-cols-3 gap-8">
                        {[
                            {
                                icon: '🎯',
                                title: 'Minh bạch',
                                desc: 'Theo dõi chi tiết mọi khoản đóng góp và tiến trình sử dụng quỹ',
                            },
                            {
                                icon: '🔒',
                                title: 'An toàn',
                                desc: 'Thanh toán bảo mật qua VNPay, dữ liệu được mã hóa toàn diện',
                            },
                            {
                                icon: '💚',
                                title: 'Ý nghĩa',
                                desc: 'Kết nối hàng nghìn nhà hảo tâm với các dự án tạo tác động tích cực',
                            },
                        ].map((item) => (
                            <div key={item.title} className="bg-white rounded-2xl p-8 text-center hover:shadow-lg transition-shadow">
                                <div className="text-5xl mb-4">{item.icon}</div>
                                <h3 className="text-xl font-semibold text-gray-800 mb-3">{item.title}</h3>
                                <p className="text-gray-500">{item.desc}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* CTA Section */}
            <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
                <div className="bg-gradient-to-r from-primary-600 to-teal-600 rounded-3xl p-10 lg:p-16 text-center text-white">
                    <h2 className="text-3xl lg:text-4xl font-bold mb-4">Sẵn sàng tạo nên sự khác biệt?</h2>
                    <p className="text-lg text-white/80 mb-8 max-w-2xl mx-auto">
                        Dù bạn là cá nhân muốn đóng góp hay tổ chức muốn gây quỹ, GayQuy.vn luôn sẵn sàng đồng hành cùng bạn.
                    </p>
                    <div className="flex flex-col sm:flex-row gap-4 justify-center">
                        <Link
                            to="/projects"
                            className="bg-white text-primary-700 px-8 py-3 rounded-xl font-semibold hover:bg-gray-50 transition-all"
                        >
                            Ủng hộ ngay
                        </Link>
                        <Link
                            to="/register"
                            className="border-2 border-white/40 text-white px-8 py-3 rounded-xl font-semibold hover:bg-white/10 transition-all"
                        >
                            Đăng ký tổ chức
                        </Link>
                    </div>
                </div>
            </section>
        </div>
    );
}
