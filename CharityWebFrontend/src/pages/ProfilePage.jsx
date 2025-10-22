import React from 'react';
export default function ProfilePage({ user }) {
  const totalDonated = user.donations.reduce((s,d)=>s+d.amount,0);
  return (
    <div className="py-8">
      <div className="container mx-auto px-4">
        <div className="max-w-4xl mx-auto">
          <div className="bg-white rounded-xl shadow-lg p-8 mb-8">
            <h1 className="text-3xl font-bold mb-6">Thông tin cá nhân</h1>
            <div className="grid md:grid-cols-2 gap-6 mb-8">
              <div><label className="block text-gray-700 font-medium mb-2">Họ và tên</label><p className="text-lg">{user.name}</p></div>
              <div><label className="block text-gray-700 font-medium mb-2">Email</label><p className="text-lg">{user.email}</p></div>
              <div><label className="block text-gray-700 font-medium mb-2">Số điện thoại</label><p className="text-lg">{user.phone}</p></div>
              <div><label className="block text-gray-700 font-medium mb-2">Tổng đã ủng hộ</label><p className="text-lg text-green-600 font-semibold">{totalDonated.toLocaleString('vi-VN')} VNĐ</p></div>
            </div>
          </div>
          <div className="bg-white rounded-xl shadow-lg p-8">
            <h2 className="text-2xl font-bold mb-6">Lịch sử ủng hộ</h2>
            <div className="space-y-4">
              {user.donations.map((d,i)=>(<div key={i} className="border border-gray-200 rounded-lg p-4">
                <div className="flex justify-between items-start">
                  <div><h3 className="font-semibold text-lg">{d.projectName}</h3><p className="text-gray-600">{d.date}</p></div>
                  <span className="text-green-600 font-semibold text-lg">{d.amount.toLocaleString('vi-VN')} VNĐ</span>
                </div>
              </div>))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}