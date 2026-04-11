import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import LoginModal from './modals/LoginModal';

export default function Header() {
    const { user, logout } = useAuth();
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

    return (
        <>
            <header className="bg-white shadow-sm sticky top-0 z-50 border-b border-gray-100">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-16">
                        {/* Logo */}
                        <Link to="/" className="flex items-center space-x-2 flex-shrink-0">
                            <span className="text-2xl">🌱</span>
                            <span className="text-xl font-bold text-primary-600">GayQuy.vn</span>
                        </Link>

                        {/* Desktop Nav */}
                        <nav className="hidden md:flex items-center space-x-1">
                            <Link to="/" className="px-3 py-2 rounded-lg text-sm font-medium text-gray-700 hover:text-primary-600 hover:bg-primary-50 transition-all">
                                Trang chủ
                            </Link>
                            <Link to="/projects" className="px-3 py-2 rounded-lg text-sm font-medium text-gray-700 hover:text-primary-600 hover:bg-primary-50 transition-all">
                                Dự án
                            </Link>
                            <Link to="/categories" className="px-3 py-2 rounded-lg text-sm font-medium text-gray-700 hover:text-primary-600 hover:bg-primary-50 transition-all">
                                Danh mục
                            </Link>
                            {user?.roleType === 'ORGANIZATION' && (
                                <Link to="/projects/new" className="px-3 py-2 rounded-lg text-sm font-medium text-gray-700 hover:text-primary-600 hover:bg-primary-50 transition-all">
                                    Tạo dự án
                                </Link>
                            )}
                            {user?.roleType === 'ADMIN' && (
                                <Link to="/admin" className="px-3 py-2 rounded-lg text-sm font-medium text-amber-600 hover:text-amber-700 hover:bg-amber-50 transition-all">
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
                                    className="pl-9 pr-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent w-52 lg:w-64 bg-gray-50"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                                <svg className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                </svg>
                            </form>

                            {user ? (
                                <div className="relative">
                                    <button
                                        onClick={() => setUserMenuOpen(!userMenuOpen)}
                                        className="flex items-center space-x-2 px-3 py-1.5 rounded-lg hover:bg-gray-50 transition-colors"
                                    >
                                        <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center">
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
                                        className="text-sm font-medium text-gray-700 hover:text-primary-600 px-3 py-2 transition-colors"
                                    >
                                        Đăng nhập
                                    </button>
                                    <Link
                                        to="/register"
                                        className="text-sm font-medium bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors"
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
