import React from 'react';

export default function ContactPage() {
    return (
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
            <h1 className="text-3xl font-bold text-gray-800 mb-8">Liên hệ với chúng tôi</h1>

            <div className="grid md:grid-cols-2 gap-8">
                {/* Contact Info */}
                <div className="space-y-6">
                    <div className="card p-6">
                        <h2 className="text-lg font-semibold text-gray-800 mb-4">Thông tin liên hệ</h2>
                        <div className="space-y-4">
                            {[
                                { icon: '📍', label: 'Địa chỉ', value: '123 Đường ABC, Cầu Giấy, Hà Nội' },
                                { icon: '📞', label: 'Hotline', value: '1900-1234 (24/7)' },
                                { icon: '✉️', label: 'Email', value: 'support@gayquy.vn' },
                                { icon: '🕒', label: 'Giờ làm việc', value: 'T2 - CN: 8:00 - 22:00' },
                            ].map((item) => (
                                <div key={item.label} className="flex items-start gap-3">
                                    <span className="text-xl">{item.icon}</span>
                                    <div>
                                        <p className="text-sm text-gray-500">{item.label}</p>
                                        <p className="font-medium text-gray-800">{item.value}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Contact Form */}
                <div className="card p-6">
                    <h2 className="text-lg font-semibold text-gray-800 mb-4">Gửi tin nhắn</h2>
                    <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Họ và tên *</label>
                            <input className="input-field" required />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Email *</label>
                            <input type="email" className="input-field" required />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Chủ đề *</label>
                            <input className="input-field" required />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Nội dung *</label>
                            <textarea rows="4" className="input-field resize-none" required />
                        </div>
                        <button type="submit" className="btn-primary w-full">Gửi tin nhắn</button>
                    </form>
                </div>
            </div>
        </div>
    );
}
