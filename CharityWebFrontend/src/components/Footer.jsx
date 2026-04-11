import React from 'react';
import { Link } from 'react-router-dom';

export default function Footer() {
    return (
        <footer className="bg-gray-900 text-gray-300">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
                    {/* Brand */}
                    <div className="md:col-span-1">
                        <div className="flex items-center space-x-2 mb-4">
                            <span className="text-2xl">🌱</span>
                            <span className="text-xl font-bold text-white">GayQuy.vn</span>
                        </div>
                        <p className="text-sm text-gray-400 leading-relaxed">
                            Nền tảng gây quỹ cộng đồng hàng đầu Việt Nam. Kết nối tấm lòng, lan tỏa yêu thương.
                        </p>
                    </div>

                    {/* Quick Links */}
                    <div>
                        <h4 className="text-white font-semibold mb-4">Khám phá</h4>
                        <ul className="space-y-2">
                            <li><Link to="/projects" className="text-sm hover:text-white transition-colors">Tất cả dự án</Link></li>
                            <li><Link to="/categories" className="text-sm hover:text-white transition-colors">Danh mục</Link></li>
                            <li><Link to="/about" className="text-sm hover:text-white transition-colors">Về chúng tôi</Link></li>
                        </ul>
                    </div>

                    {/* Support */}
                    <div>
                        <h4 className="text-white font-semibold mb-4">Hỗ trợ</h4>
                        <ul className="space-y-2">
                            <li><Link to="/help" className="text-sm hover:text-white transition-colors">Trung tâm trợ giúp</Link></li>
                            <li><Link to="/contact" className="text-sm hover:text-white transition-colors">Liên hệ</Link></li>
                            <li><Link to="/terms" className="text-sm hover:text-white transition-colors">Điều khoản sử dụng</Link></li>
                        </ul>
                    </div>

                    {/* Contact Info */}
                    <div>
                        <h4 className="text-white font-semibold mb-4">Liên hệ</h4>
                        <ul className="space-y-2 text-sm">
                            <li className="flex items-center space-x-2">
                                <span>📍</span>
                                <span>123 Đường ABC, Cầu Giấy, Hà Nội</span>
                            </li>
                            <li className="flex items-center space-x-2">
                                <span>📞</span>
                                <span>1900-1234</span>
                            </li>
                            <li className="flex items-center space-x-2">
                                <span>✉️</span>
                                <span>support@gayquy.vn</span>
                            </li>
                        </ul>
                    </div>
                </div>

                <div className="border-t border-gray-800 mt-10 pt-8 text-center text-sm text-gray-500">
                    <p>&copy; {new Date().getFullYear()} GayQuy.vn. Tất cả quyền được bảo lưu.</p>
                </div>
            </div>
        </footer>
    );
}
