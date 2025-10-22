import React, { useState } from 'react';
/*
 DonationModal component
 Props:
 - showDonation, setShowDonation: visibility control
 - project: selected project object
 - setShowThankYou, setDonationData: to show thank you and pass data
*/
export default function DonationModal({ showDonation, setShowDonation, project, setShowThankYou, setDonationData }) {
  const [formData,setFormData] = useState({name:'',phone:'',email:'',amount:'',message:''});
  const handleSubmit = (e) => {
    e.preventDefault();
    setDonationData({ name: formData.name, amount: formData.amount, projectName: project?.name });
    setShowDonation(false);
    setShowThankYou(true);
    setFormData({name:'',phone:'',email:'',amount:'',message:''});
  };
  if (!showDonation) return null;
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl p-8 max-w-md w-full mx-4">
        <div className="flex justify-between items-center mb-6"><h2 className="text-2xl font-bold">Ủng hộ dự án</h2><button className="text-gray-500 hover:text-gray-700" onClick={()=>setShowDonation(false)}>✕</button></div>
        <div className="mb-6"><h3 className="font-semibold text-lg">{project?.name}</h3></div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div><label className="block text-gray-700 font-medium mb-2">Họ và tên *</label><input type="text" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={formData.name} onChange={(e)=>setFormData({...formData,name:e.target.value})} required/></div>
          <div><label className="block text-gray-700 font-medium mb-2">Số điện thoại *</label><input type="tel" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={formData.phone} onChange={(e)=>setFormData({...formData,phone:e.target.value})} required/></div>
          <div><label className="block text-gray-700 font-medium mb-2">Email *</label><input type="email" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={formData.email} onChange={(e)=>setFormData({...formData,email:e.target.value})} required/></div>
          <div><label className="block text-gray-700 font-medium mb-2">Số tiền ủng hộ (VNĐ) *</label><input type="number" min="10000" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={formData.amount} onChange={(e)=>setFormData({...formData,amount:e.target.value})} required/></div>
          <div><label className="block text-gray-700 font-medium mb-2">Lời nhắn</label><textarea className="w-full px-4 py-2 border border-gray-300 rounded-lg" rows="3" value={formData.message} onChange={(e)=>setFormData({...formData,message:e.target.value})} placeholder="Gửi lời động viên đến dự án..."/></div>
          <button type="submit" className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 transition-colors">Xác nhận ủng hộ</button>
        </form>
      </div>
    </div>
  );
}
