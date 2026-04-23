import React from 'react';

export default function TermsPage() {
    return (
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
            <div className="card p-8">
                <h1 className="text-3xl font-bold text-gray-800 mb-8">Điều khoản sử dụng</h1>

                <div className="prose prose-gray max-w-none space-y-8">
                    <section>
                        <h2 className="text-xl font-semibold text-primary-700 mb-3">1. Giới thiệu</h2>
                        <p className="text-gray-600 leading-relaxed">
                            Chào mừng bạn đến với <strong>GayQuy.vn</strong>. Bằng việc sử dụng nền tảng của chúng tôi,
                            bạn đồng ý tuân thủ các điều khoản và điều kiện được nêu dưới đây.
                        </p>
                    </section>

                    <section>
                        <h2 className="text-xl font-semibold text-primary-700 mb-3">2. Quyền và nghĩa vụ</h2>
                        <h3 className="font-semibold text-gray-800 mb-2">Quyền của người dùng:</h3>
                        <ul className="list-disc list-inside text-gray-600 space-y-1 mb-4">
                            <li>Tạo và quản lý dự án gây quỹ</li>
                            <li>Ủng hộ các dự án ý nghĩa</li>
                            <li>Theo dõi tiến độ dự án và nhận cập nhật</li>
                            <li>Tương tác với cộng đồng</li>
                        </ul>
                        <h3 className="font-semibold text-gray-800 mb-2">Nghĩa vụ:</h3>
                        <ul className="list-disc list-inside text-gray-600 space-y-1">
                            <li>Cung cấp thông tin chính xác và trung thực</li>
                            <li>Không sử dụng nền tảng cho mục đích bất hợp pháp</li>
                            <li>Tôn trọng quyền riêng tư của người khác</li>
                            <li>Tuân thủ quy định pháp luật Việt Nam</li>
                        </ul>
                    </section>

                    <section>
                        <h2 className="text-xl font-semibold text-primary-700 mb-3">3. Quy định về dự án</h2>
                        <p className="text-gray-600 mb-2">Tất cả dự án trên nền tảng phải:</p>
                        <ul className="list-disc list-inside text-gray-600 space-y-1">
                            <li>Có mục đích thiện nguyện, xã hội hoặc cộng đồng</li>
                            <li>Công khai tiến độ và kết quả gây quỹ</li>
                            <li>Cam kết minh bạch về tài chính</li>
                            <li>Được quản trị viên phê duyệt trước khi công khai</li>
                        </ul>
                    </section>

                    <section>
                        <h2 className="text-xl font-semibold text-primary-700 mb-3">4. Quy trình thanh toán</h2>
                        <p className="text-gray-600 leading-relaxed">
                            Mọi giao dịch quyên góp được xử lý qua cổng thanh toán VNPay đảm bảo an toàn. Các khoản giải ngân
                            cho dự án phải được quản trị viên xem xét và phê duyệt.
                        </p>
                    </section>
                </div>
            </div>
        </div>
    );
}
