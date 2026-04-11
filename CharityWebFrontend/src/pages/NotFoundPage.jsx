import React from 'react';
import { Link } from 'react-router-dom';

export default function NotFoundPage() {
    return (
        <div className="min-h-[60vh] flex items-center justify-center px-4">
            <div className="text-center">
                <div className="text-8xl mb-6">🔍</div>
                <h1 className="text-4xl font-bold text-gray-800 mb-3">404</h1>
                <p className="text-lg text-gray-500 mb-8">Trang bạn tìm kiếm không tồn tại</p>
                <Link to="/" className="btn-primary inline-block">
                    Về trang chủ
                </Link>
            </div>
        </div>
    );
}
