import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Link } from 'react-router-dom';

export default function AboutPage() {
    const { user } = useAuth();

    return (
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
            <div className="text-center mb-12">
                <h1 className="text-4xl font-bold text-gray-800 mb-4">Về GayQuy.vn</h1>
                <p className="text-lg text-gray-500 max-w-3xl mx-auto">
                    Nền tảng gây quỹ cộng đồng hàng đầu tại Việt Nam, kết nối những tấm lòng hảo tâm với các dự án ý nghĩa trên khắp cả nước.
                </p>
            </div>

            {/* Mission */}
            <div className="card p-8 mb-8">
                <h2 className="text-2xl font-bold text-primary-700 mb-4">Sứ mệnh</h2>
                <p className="text-gray-600 leading-relaxed">
                    Chúng tôi tin rằng mỗi đóng góp nhỏ đều có thể tạo nên sự thay đổi lớn. Thông qua nền tảng của mình,
                    chúng tôi mong muốn tạo ra một cộng đồng đoàn kết, nơi mọi người có thể dễ dàng tham gia vào các
                    hoạt động từ thiện và tạo ra tác động tích cực cho xã hội Việt Nam.
                </p>
            </div>

            {/* Values */}
            <h2 className="text-2xl font-bold text-gray-800 mb-6">Giá trị cốt lõi</h2>
            <div className="grid md:grid-cols-3 gap-6 mb-12">
                {[
                    { icon: '🎯', title: 'Minh bạch', desc: 'Mọi khoản đóng góp đều được theo dõi và báo cáo chi tiết, đảm bảo số tiền đến đúng nơi cần đến.' },
                    { icon: '🤝', title: 'Cộng đồng', desc: 'Kết nối hàng triệu người Việt cùng chung tay làm từ thiện, xây dựng xã hội tốt đẹp hơn.' },
                    { icon: '💚', title: 'Ý nghĩa', desc: 'Tạo ra những tác động tích cực và bền vững cho cộng đồng thông qua các dự án thiết thực.' },
                ].map((item) => (
                    <div key={item.title} className="card p-6 text-center">
                        <div className="text-5xl mb-4">{item.icon}</div>
                        <h3 className="text-lg font-semibold text-gray-800 mb-2">{item.title}</h3>
                        <p className="text-gray-500 text-sm">{item.desc}</p>
                    </div>
                ))}
            </div>

            {/* CTA */}
            {!user && (
                <div className="bg-gradient-to-r from-primary-600 to-teal-600 rounded-2xl p-8 text-center text-white">
                    <h2 className="text-2xl font-bold mb-3">Hãy cùng tạo nên sự khác biệt</h2>
                    <p className="text-white/80 mb-6">Tham gia cùng cộng đồng gây quỹ lớn nhất Việt Nam ngay hôm nay</p>
                    <Link to="/register" className="inline-block bg-white text-primary-700 px-8 py-3 rounded-xl font-semibold hover:bg-gray-50 transition-all">
                        Bắt đầu ngay
                    </Link>
                </div>
            )}
        </div>
    );
}
