import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import LoginModal from './modals/LoginModal';

export default function Header() {
    const { user, logout } = useAuth();
    const location = useLocation();
    const navigate = useNavigate();
    const [showLogin, setShowLogin] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
    const [userMenuOpen, setUserMenuOpen] = useState(false);

    const handleSearch = (e) => {
        e.preventDefault();
        if (searchTerm.trim()) {
            navigate(`/search?q=${encodeURIComponent(searchTerm.trim())}`);
            setSearchTerm('');
            setMobileMenuOpen(false);
        }
    };

    const handleLogout = async () => {
        await logout();
        setUserMenuOpen(false);
        navigate('/');
    };

    const getRoleLabel = () => {
        switch (user?.roleType) {
            case 'ADMIN': return 'Quản trị viên';
            case 'ORGANIZATION': return 'Tổ chức';
            default: return 'Cá nhân';
        }
    };

    const getDisplayName = () => {
        if (user?.roleType === 'ORGANIZATION') return user.orgName || user.username;
        return user?.fullName || user?.username;
    };

    const isActiveRoute = (path) => {
        if (path === '/') return location.pathname === '/';
        return location.pathname === path || location.pathname.startsWith(`${path}/`);
    };

    const navLinkClass = (path, baseClass = 'text-gray-700') => {
        const active = isActiveRoute(path);
        return `${baseClass} px-2 py-2 rounded-lg text-sm lg:text-base transition-all whitespace-nowrap ${active
            ? 'text-primary-700 font-bold bg-primary-50'
            : 'font-medium hover:text-primary-600 hover:bg-primary-50'
        }`;
    };

    return (
        <>
            <header className="bg-white shadow-sm sticky top-0 z-50 border-b border-gray-100">
                <div className="w-full mx-auto px-4 sm:px-6 lg:px-8">
                    {/* SỬA Ở ĐÂY: Thay h-16 thành py-5 (hoặc py-6 nếu muốn cao hơn nữa) */}
                    <div className="flex items-center justify-between py-5">
                        
                        {/* Logo */}
                        <Link to="/" className="flex items-center space-x-2 flex-shrink-0">
                            <span className="text-2xl">🌱</span>
                            <span className="text-xl font-bold text-primary-600">GayQuy.vn</span>
                        </Link>

                        {/* Desktop Nav - Đã căn chỉnh để trên 1 dòng, không bị bẻ chữ */}
                        <nav className="hidden md:flex items-center justify-center gap-2 lg:gap-6 flex-1 px-4 whitespace-nowrap">
                            <Link to="/" className={navLinkClass('/')}>
                                Trang chủ
                            </Link>
                            <Link to="/explore" className={navLinkClass('/explore')}>
                                Khám phá
                            </Link>
                            <Link to="/projects" className={navLinkClass('/projects')}>
                                Dự án
                            </Link>
                            <Link to="/categories" className={navLinkClass('/categories')}>
                                Danh mục
                            </Link>
                            <Link to="/about" className={navLinkClass('/about')}>
                                Giới thiệu
                            </Link>
                            
                            {user?.roleType === 'ORGANIZATION' && (
                                <>
                                    <Link to="/my-projects" className={navLinkClass('/my-projects', 'text-gray-700')}>
                                        Quản lý dự án
                                    </Link>
                                    <Link to="/projects/new" className={navLinkClass('/projects/new')}>
                                        Tạo dự án
                                    </Link>
                                </>
                            )}
                            {user?.roleType === 'ADMIN' && (
                                <Link to="/admin" className={navLinkClass('/admin', 'text-amber-600')}>
                                    Quản trị
                                </Link>
                            )}
                        </nav>

                        {/* Search + User */}
                        <div className="flex items-center space-x-3">
                            <form onSubmit={handleSearch} className="hidden sm:block relative">
                                <input
                                    type="text"
                                    placeholder="Tìm kiếm dự án..."
                                    className="pl-9 pr-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent w-52 lg:w-64 bg-gray-50"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                                <svg className="absolute left-2.5 top-3 h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                </svg>
                            </form>

                            {user ? (
                                <div className="relative">
                                    <button
                                        onClick={() => setUserMenuOpen(!userMenuOpen)}
                                        className="flex items-center space-x-2 px-3 py-1.5 rounded-lg hover:bg-gray-50 transition-colors"
                                    >
                                        <div className="w-9 h-9 rounded-full bg-primary-100 flex items-center justify-center">
                                            <span className="text-primary-700 font-semibold text-sm">
                                                {getDisplayName()?.charAt(0)?.toUpperCase()}
                                            </span>
                                        </div>
                                        <span className="hidden lg:block text-sm font-medium text-gray-700 max-w-[120px] truncate">
                                            {getDisplayName()}
                                        </span>
                                        <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                        </svg>
                                    </button>

                                    {userMenuOpen && (
                                        <>
                                            <div className="fixed inset-0 z-10" onClick={() => setUserMenuOpen(false)} />
                                            <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-gray-100 py-2 z-20">
                                                <div className="px-4 py-2 border-b border-gray-100">
                                                    <p className="text-sm font-semibold text-gray-800 truncate">{getDisplayName()}</p>
                                                    <p className="text-xs text-gray-500">{getRoleLabel()}</p>
                                                </div>
                                                <Link
                                                    to="/profile"
                                                    onClick={() => setUserMenuOpen(false)}
                                                    className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                                                >
                                                    👤 Trang cá nhân
                                                </Link>
                                                {user.roleType === 'ORGANIZATION' && (
                                                    <Link
                                                        to="/projects/new"
                                                        onClick={() => setUserMenuOpen(false)}
                                                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                                                    >
                                                        📝 Tạo dự án mới
                                                    </Link>
                                                )}
                                                {user.roleType === 'ADMIN' && (
                                                    <Link
                                                        to="/admin"
                                                        onClick={() => setUserMenuOpen(false)}
                                                        className="block px-4 py-2 text-sm text-amber-600 hover:bg-amber-50 transition-colors"
                                                    >
                                                        ⚙️ Bảng quản trị
                                                    </Link>
                                                )}
                                                <hr className="my-1 border-gray-100" />
                                                <button
                                                    onClick={handleLogout}
                                                    className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
                                                >
                                                    🚪 Đăng xuất
                                                </button>
                                            </div>
                                        </>
                                    )}
                                </div>
                            ) : (
                                <div className="flex items-center space-x-2">
                                    <button
                                        onClick={() => setShowLogin(true)}
                                        className="text-base font-medium text-gray-700 hover:text-primary-600 px-3 py-2 transition-colors"
                                    >
                                        Đăng nhập
                                    </button>
                                    <Link
                                        to="/register"
                                        className="text-base font-medium bg-primary-600 text-white px-5 py-2.5 rounded-lg hover:bg-primary-700 transition-colors"
                                    >
                                        Đăng ký
                                    </Link>
                                </div>
                            )}

                            {/* Mobile menu button */}
                            <button
                                className="md:hidden p-2 rounded-lg hover:bg-gray-100"
                                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                            >
                                <svg className="w-6 h-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    {mobileMenuOpen ? (
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                    ) : (
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                                    )}
                                </svg>
                            </button>
                        </div>
                    </div>

                    {/* Mobile menu */}
                    {mobileMenuOpen && (
                        <div className="md:hidden pb-4 border-t border-gray-100 pt-3">
                            <form onSubmit={handleSearch} className="mb-3 sm:hidden">
                                <input
                                    type="text"
                                    placeholder="Tìm kiếm dự án..."
                                    className="w-full pl-9 pr-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 bg-gray-50"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                            </form>
                            <div className="space-y-1">
                                <Link to="/" onClick={() => setMobileMenuOpen(false)} className="block px-3 py-2 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50">Trang chủ</Link>
                                <Link to="/projects" onClick={() => setMobileMenuOpen(false)} className="block px-3 py-2 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50">Dự án</Link>
                                <Link to="/categories" onClick={() => setMobileMenuOpen(false)} className="block px-3 py-2 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50">Danh mục</Link>
                                {user?.roleType === 'ORGANIZATION' && (
                                    <Link to="/projects/new" onClick={() => setMobileMenuOpen(false)} className="block px-3 py-2 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50">Tạo dự án</Link>
                                )}
                                {user?.roleType === 'ADMIN' && (
                                    <Link to="/admin" onClick={() => setMobileMenuOpen(false)} className="block px-3 py-2 rounded-lg text-sm font-medium text-amber-600 hover:bg-amber-50">Quản trị</Link>
                                )}
                            </div>
                        </div>
                    )}
                </div>
            </header>

            <LoginModal show={showLogin} onClose={() => setShowLogin(false)} />
        </>
    );
}