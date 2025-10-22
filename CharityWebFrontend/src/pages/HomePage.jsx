import React from 'react';
import ProjectCard from '../components/ProjectCard';
import mockProjects from '../data/mockProjects';
/*
 HomePage
 Props:
 - setCurrentPage, setSelectedProject passed from App
*/
export default function HomePage({ setCurrentPage, setSelectedProject }) {
  const recentProjects = mockProjects.slice(0, 3);
  return (
    <div>
      <section className="relative custom-green text-white py-20 overflow-hidden">
        <div className="absolute inset-0 opacity-20">
          <img src="https://images.unsplash.com/photo-1559027615-cd4628902d4a?w=1200&h=600&fit=crop" alt="Cộng đồng gây quỹ" className="w-full h-full object-cover" />
        </div>
        <div className="relative z-10 container mx-auto px-4">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div className="text-center lg:text-left">
              <h1 className="text-5xl font-bold mb-6">Cùng nhau tạo nên sự thay đổi</h1>
              <p className="text-xl mb-8">Tham gia cộng đồng gây quỹ lớn nhất Việt Nam để hỗ trợ các dự án ý nghĩa</p>
              <button className="bg-white text-green-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 transition-colors" onClick={() => setCurrentPage('projects')}>Khám phá dự án</button>
            </div>
            <div className="hidden lg:block">
              <div className="relative">
                <img src="https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca?w=500&h=400&fit=crop" alt="Tình nguyện viên giúp đỡ cộng đồng" className="rounded-2xl shadow-2xl" />
              </div>
            </div>
          </div>
        </div>
      </section>
      <section className="py-16 bg-gray-50">
        <div className="container mx-auto px-4">
          <div className="flex justify-between items-center mb-12">
            <h2 className="text-3xl font-bold text-gray-800">Dự án mới nhất</h2>
            <button className="text-green-600 hover:text-green-700 font-semibold" onClick={() => setCurrentPage('projects')}>Xem tất cả →</button>
          </div>
          <div className="grid md:grid-cols-3 gap-8">
            {recentProjects.map(p => <ProjectCard key={p.id} project={p} onClick={setSelectedProject} />)}
          </div>
        </div>
      </section>
      <section className="py-16">
        <div className="container mx-auto px-4">
          <div className="max-w-4xl mx-auto text-center">
            <h2 className="text-3xl font-bold mb-8">Về GayQuy.vn</h2>
            <p className="text-lg text-gray-600 mb-8">Chúng tôi là nền tảng gây quỹ cộng đồng hàng đầu tại Việt Nam, kết nối những tấm lòng hảo tâm với các dự án ý nghĩa. Từ giáo dục, y tế đến bảo vệ môi trường, chúng tôi tin rằng mỗi đóng góp nhỏ đều có thể tạo nên sự thay đổi lớn.</p>
            <div className="grid md:grid-cols-3 gap-8">
              <div className="text-center">
                <div className="text-4xl mb-4">🎯</div>
                <h3 className="text-xl font-semibold mb-2">Minh bạch</h3>
                <p className="text-gray-600">Theo dõi chi tiết tiến độ và sử dụng quỹ</p>
              </div>
              <div className="text-center">
                <div className="text-4xl mb-4">🤝</div>
                <h3 className="text-xl font-semibold mb-2">Cộng đồng</h3>
                <p className="text-gray-600">Kết nối hàng triệu người Việt</p>
              </div>
              <div className="text-center">
                <div className="text-4xl mb-4">💚</div>
                <h3 className="text-xl font-semibold mb-2">Ý nghĩa</h3>
                <p className="text-gray-600">Tạo ra tác động tích cực cho xã hội</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}