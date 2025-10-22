import React from 'react';
export default function ProjectDetailPage({ project, setShowDonation }) {
  const progressPercentage = (project.currentAmount / project.targetAmount) * 100;
  const mockSupporters = [
    { name: "Nguyễn Văn A", amount: 1000000, message: "Chúc dự án thành công!", date: "2024-01-15" },
    { name: "Trần Thị B", amount: 500000, message: "Ủng hộ dự án ý nghĩa", date: "2024-01-14" },
    { name: "Lê Văn C", amount: 2000000, message: "Mong dự án sớm hoàn thành", date: "2024-01-13" }
  ];
  return (
    <div className="py-8">
      <div className="container mx-auto px-4">
        <div className="max-w-6xl mx-auto">
          <div className="grid lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2">
              <img src={project.image} alt={project.name} className="w-full h-64 object-cover rounded-xl mb-6" />
              <h1 className="text-3xl font-bold mb-4">{project.name}</h1>
              <div className="mb-6">
                <div className="flex justify-between text-sm text-gray-600 mb-2"><span>Tiến độ gây quỹ</span><span>{Math.round(progressPercentage)}%</span></div>
                <div className="w-full bg-gray-200 rounded-full h-3">
                  <div className="progress-bar h-3 rounded-full" style={{width: `${Math.min(progressPercentage,100)}%`}}></div>
                </div>
                <div className="flex justify-between mt-2">
                  <span className="text-green-600 font-semibold text-lg">{project.currentAmount.toLocaleString('vi-VN')} VNĐ</span>
                  <span className="text-gray-500">Mục tiêu: {project.targetAmount.toLocaleString('vi-VN')} VNĐ</span>
                </div>
              </div>
              <div className="flex items-center space-x-6 mb-6 text-sm text-gray-600">
                <span>👥 {project.supporters} người ủng hộ</span>
                {project.isActive ? (<span>⏰ Còn {project.daysLeft} ngày</span>) : (<span className="text-red-600">❌ Đã kết thúc</span>)}
              </div>
              <div className="prose max-w-none">
                <h2 className="text-xl font-semibold mb-4">Về dự án</h2>
                <p className="text-gray-700 leading-relaxed mb-6">{project.description}</p>
                <p className="text-gray-700 leading-relaxed">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
              </div>
            </div>
            <div className="lg:col-span-1">
              <div className="bg-white rounded-xl shadow-lg p-6 sticky top-24">
                {project.isActive && <button className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 transition-colors mb-6" onClick={()=>setShowDonation(true)}>Ủng hộ ngay</button>}
                <h3 className="text-lg font-semibold mb-4">Danh sách ủng hộ</h3>
                <div className="space-y-4 max-h-96 overflow-y-auto">
                  {mockSupporters.map((s,i)=>(<div key={i} className="border-b border-gray-100 pb-4">
                    <div className="flex justify-between items-start mb-2">
                      <span className="font-medium">{s.name}</span>
                      <span className="text-green-600 font-semibold text-sm">{s.amount.toLocaleString('vi-VN')} VNĐ</span>
                    </div>
                    <p className="text-gray-600 text-sm mb-1">{s.message}</p>
                    <span className="text-gray-400 text-xs">{s.date}</span>
                  </div>))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}