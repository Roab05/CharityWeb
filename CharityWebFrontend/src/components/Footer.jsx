import React from 'react';
import { Link } from 'react-router-dom';

export default function Footer() {
    return (
        <footer className="bg-gray-900 text-gray-300">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
                
                {/* Newsletter Subscription */}
                <div className="border-b border-gray-800 pb-8 mb-10 flex flex-col md:flex-row justify-between items-center">
                    <div className="mb-4 md:mb-0 text-center md:text-left">
                        <h3 className="text-xl font-bold text-white mb-1">Đăng ký nhận tin tức</h3>
                        <p className="text-sm text-gray-400">Cập nhật những dự án thiện nguyện và hoạt động mới nhất.</p>
                    </div>
                    <div className="flex w-full md:w-auto">
                        <input 
                            type="email" 
                            placeholder="Nhập email của bạn..." 
                            className="px-4 py-2.5 bg-gray-800 text-white rounded-l-lg focus:outline-none focus:ring-2 focus:ring-primary-500 border border-gray-700 w-full md:w-72 text-sm" 
                        />
                        <button className="px-5 py-2.5 bg-primary-600 text-white rounded-r-lg hover:bg-primary-700 transition-colors text-sm font-medium">
                            Đăng ký
                        </button>
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
                    {/* Brand & Socials */}
                    <div className="md:col-span-1">
                        <div className="flex items-center space-x-2 mb-4">
                            <span className="text-2xl">🌱</span>
                            <span className="text-xl font-bold text-white">GayQuy.vn</span>
                        </div>
                        <p className="text-sm text-gray-400 leading-relaxed mb-6">
                            Nền tảng gây quỹ cộng đồng hàng đầu Việt Nam. Kết nối tấm lòng, lan tỏa yêu thương đến mọi miền.
                        </p>
                        
                        {/* Biểu tượng mạng xã hội */}
                        <div className="flex space-x-4">
                            <a href="#" className="text-gray-400 hover:text-white transition-colors">
                                <span className="sr-only">Facebook</span>
                                <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 24 24"><path fillRule="evenodd" d="M22 12c0-5.523-4.477-10-10-10S2 6.477 2 12c0 4.991 3.657 9.128 8.438 9.878v-6.987h-2.54V12h2.54V9.797c0-2.506 1.492-3.89 3.777-3.89 1.094 0 2.238.195 2.238.195v2.46h-1.26c-1.243 0-1.63.771-1.63 1.562V12h2.773l-.443 2.89h-2.33v6.988C18.343 21.128 22 16.991 22 12z" clipRule="evenodd" /></svg>
                            </a>
                            <a href="#" className="text-gray-400 hover:text-white transition-colors">
                                <span className="sr-only">YouTube</span>
                                <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 24 24"><path fillRule="evenodd" d="M19.812 5.418c.861.23 1.538.907 1.768 1.768C21.998 8.746 22 12 22 12s0 3.255-.418 4.814a2.504 2.504 0 0 1-1.768 1.768c-1.56.419-7.814.419-7.814.419s-6.255 0-7.814-.419a2.505 2.505 0 0 1-1.768-1.768C2 15.255 2 12 2 12s0-3.255.417-4.814a2.507 2.507 0 0 1 1.768-1.768C5.744 5 11.998 5 11.998 5s6.255 0 7.814.418ZM15.194 12 10 15V9l5.194 3Z" clipRule="evenodd" /></svg>
                            </a>
                            <a href="#" className="text-gray-400 hover:text-white transition-colors">
                                <span className="sr-only">GitHub</span>
                                <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 24 24"><path fillRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" clipRule="evenodd" /></svg>
                            </a>
                        </div>
                    </div>

                    {/* Quick Links */}
                    <div>
                        <h4 className="text-white font-semibold mb-4">Khám phá</h4>
                        <ul className="space-y-3">
                            <li><Link to="/projects" className="text-sm hover:text-primary-400 transition-colors">Tất cả dự án</Link></li>
                            <li><Link to="/categories" className="text-sm hover:text-primary-400 transition-colors">Danh mục quyên góp</Link></li>
                            <li><Link to="/success-stories" className="text-sm hover:text-primary-400 transition-colors">Câu chuyện thành công</Link></li>
                            <li><Link to="/about" className="text-sm hover:text-primary-400 transition-colors">Về chúng tôi</Link></li>
                        </ul>
                    </div>

                    {/* Support */}
                    <div>
                        <h4 className="text-white font-semibold mb-4">Hỗ trợ & Pháp lý</h4>
                        <ul className="space-y-3">
                            <li><Link to="/help" className="text-sm hover:text-primary-400 transition-colors">Trung tâm trợ giúp (FAQ)</Link></li>
                            <li><Link to="/contact" className="text-sm hover:text-primary-400 transition-colors">Liên hệ hỗ trợ</Link></li>
                            <li><Link to="/terms" className="text-sm hover:text-primary-400 transition-colors">Điều khoản sử dụng</Link></li>
                            <li><Link to="/privacy" className="text-sm hover:text-primary-400 transition-colors">Chính sách bảo mật</Link></li>
                        </ul>
                    </div>

                    {/* Contact Info */}
                    <div>
                        <h4 className="text-white font-semibold mb-4">Thông tin liên hệ</h4>
                        <ul className="space-y-3 text-sm">
                            <li className="flex items-start space-x-3">
                                <span className="text-lg">📍</span>
                                <span className="leading-relaxed">123 Đường ABC, Cầu Giấy, Hà Nội, Việt Nam</span>
                            </li>
                            <li className="flex items-center space-x-3">
                                <span className="text-lg">📞</span>
                                <span>1900-1234 (8:00 - 22:00)</span>
                            </li>
                            <li className="flex items-center space-x-3">
                                <span className="text-lg">✉️</span>
                                <span>support@gayquy.vn</span>
                            </li>
                        </ul>
                    </div>
                </div>

                {/* Bottom Bar */}
                <div className="border-t border-gray-800 mt-12 pt-8 flex flex-col md:flex-row justify-between items-center text-sm text-gray-500">
                    <p>&copy; {new Date().getFullYear()} GayQuy.vn. Tất cả quyền được bảo lưu.</p>
                    <p className="mt-2 md:mt-0 font-medium hover:text-gray-400 transition-colors cursor-pointer">
                        Thiết kế & Phát triển bởi Nhóm 03
                    </p>
                </div>
            </div>
        </footer>
    );
}