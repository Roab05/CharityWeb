import React, { useState } from 'react';

export default function HelpPage() {
    const [activeTab, setActiveTab] = useState('faq');
    const [openFaq, setOpenFaq] = useState(null);

    const faqs = [
        { q: 'Làm thế nào để tạo dự án gây quỹ?', a: 'Bạn cần đăng ký tài khoản tổ chức, sau đó vào mục "Tạo dự án" và điền đầy đủ thông tin. Dự án sẽ được quản trị viên xem xét và phê duyệt trong 1-3 ngày làm việc.' },
        { q: 'Tôi có thể ủng hộ bằng hình thức nào?', a: 'Chúng tôi hỗ trợ thanh toán qua VNPay, bao gồm thẻ ngân hàng, ví điện tử và QR code. Tất cả giao dịch đều được bảo mật.' },
        { q: 'Làm sao để theo dõi tiến độ dự án?', a: 'Trên trang chi tiết dự án, bạn có thể xem tiến độ gây quỹ, các cập nhật từ tổ chức, danh sách ủng hộ và tình trạng giải ngân.' },
        { q: 'Quy trình giải ngân hoạt động như thế nào?', a: 'Tổ chức gửi yêu cầu giải ngân kèm lý do và bằng chứng. Quản trị viên sẽ xem xét và phê duyệt hoặc từ chối yêu cầu.' },
        { q: 'Tôi có thể bình luận trên dự án không?', a: 'Có! Bạn có thể bình luận và thích các bài cập nhật (activity) của dự án sau khi đăng nhập.' },
        { q: 'Sự khác biệt giữa tài khoản cá nhân và tổ chức?', a: 'Tài khoản cá nhân có thể ủng hộ dự án. Tài khoản tổ chức có thể tạo dự án gây quỹ, đăng cập nhật và yêu cầu giải ngân.' },
    ];

    return (
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
            <h1 className="text-3xl font-bold text-gray-800 mb-2">Trung tâm trợ giúp</h1>
            <p className="text-gray-500 mb-8">Tìm câu trả lời cho những thắc mắc phổ biến</p>

            {/* Tabs */}
            <div className="flex gap-2 mb-8">
                {[
                    { key: 'faq', label: 'Câu hỏi thường gặp' },
                    { key: 'guide', label: 'Hướng dẫn sử dụng' },
                    { key: 'support', label: 'Hỗ trợ' },
                ].map((tab) => (
                    <button
                        key={tab.key}
                        onClick={() => setActiveTab(tab.key)}
                        className={`px-4 py-2 rounded-lg text-sm font-medium transition-all ${activeTab === tab.key
                                ? 'bg-primary-600 text-white'
                                : 'bg-white text-gray-600 border border-gray-200 hover:border-primary-300'
                            }`}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>

            {activeTab === 'faq' && (
                <div className="space-y-3">
                    {faqs.map((item, i) => (
                        <div key={i} className="card overflow-hidden">
                            <button
                                className="w-full text-left p-4 flex justify-between items-center hover:bg-gray-50 transition-colors"
                                onClick={() => setOpenFaq(openFaq === i ? null : i)}
                            >
                                <span className="font-medium text-gray-800">{item.q}</span>
                                <svg className={`w-5 h-5 text-gray-400 transition-transform ${openFaq === i ? 'rotate-180' : ''}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                </svg>
                            </button>
                            {openFaq === i && (
                                <div className="px-4 pb-4 text-gray-600 text-sm leading-relaxed border-t border-gray-100 pt-3">
                                    {item.a}
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            )}

            {activeTab === 'guide' && (
                <div className="card p-6">
                    <h2 className="text-lg font-semibold text-gray-800 mb-4">Hướng dẫn sử dụng</h2>
                    <div className="space-y-6">
                        {[
                            { step: '1', title: 'Đăng ký tài khoản', desc: 'Chọn loại tài khoản cá nhân hoặc tổ chức và điền thông tin đăng ký.' },
                            { step: '2', title: 'Khám phá dự án', desc: 'Duyệt qua các dự án đang gây quỹ, lọc theo danh mục hoặc tìm kiếm.' },
                            { step: '3', title: 'Ủng hộ dự án', desc: 'Chọn dự án, nhập số tiền và hoàn tất thanh toán qua VNPay.' },
                            { step: '4', title: 'Theo dõi kết quả', desc: 'Xem lịch sử ủng hộ, theo dõi cập nhật dự án và tình trạng giải ngân.' },
                        ].map((item) => (
                            <div key={item.step} className="flex gap-4">
                                <div className="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center flex-shrink-0">
                                    <span className="text-primary-700 font-bold">{item.step}</span>
                                </div>
                                <div>
                                    <h3 className="font-medium text-gray-800">{item.title}</h3>
                                    <p className="text-sm text-gray-500 mt-1">{item.desc}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {activeTab === 'support' && (
                <div className="card p-6 text-center">
                    <div className="text-5xl mb-4">💬</div>
                    <h2 className="text-lg font-semibold text-gray-800 mb-2">Cần hỗ trợ thêm?</h2>
                    <p className="text-gray-500 mb-6">Liên hệ đội ngũ hỗ trợ qua các kênh sau:</p>
                    <div className="flex flex-col items-center gap-3">
                        <p>📧 Email: <a href="mailto:support@gayquy.vn" className="text-primary-600 hover:underline">support@gayquy.vn</a></p>
                        <p>📞 Hotline: <span className="font-medium">1900-1234</span> (24/7)</p>
                    </div>
                </div>
            )}
        </div>
    );
}
