import React, { useState } from 'react';
import { addDonation } from '../../services/DonationService';
import { updateProjectAmount } from '../../services/ProjectService';
import { updateDonorTotalDonation } from '../../services/DonorService';
/*
 DonationModal component
 Props:
 - showDonation, setShowDonation: visibility control
 - project: selected project object
 - setShowThankYou, setDonationData: to show thank you and pass data
*/
export default function DonationModal({ user, showDonation, setShowDonation, project, setShowThankYou, setDonationData }) {
  const [formData, setFormData] = useState({ amount: '', message: '' });
  const handleSubmit = (e) => {
    e.preventDefault();

    setDonationData({ name: formData.name, amount: formData.amount, projectName: project?.name });
    setShowDonation(false);
    setShowThankYou(true);
    setFormData({ amount: '', message: '' });

    addDonation({ donor: user, project: project, amount: formData.amount, message: formData.message })

    updateProjectAmount({ id: project.id, amount: formData.amount });
    updateDonorTotalDonation({ id: user.id, amount: formData.amount })
  };
  if (!showDonation) return null;
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl p-8 max-w-md w-full mx-4">
        <div className="flex justify-between items-center mb-6"><h2 className="text-2xl font-bold">Ủng hộ dự án {project?.name.slice(0, 30)}{project?.name.length > 30 && '...'}</h2><button className="text-gray-500 hover:text-gray-700" onClick={() => setShowDonation(false)}>✕</button></div>
        {user &&
          <form onSubmit={handleSubmit} className="space-y-4">
            <div><label className="block text-gray-700 font-medium mb-2">Số tiền ủng hộ (VNĐ) *</label><input type="number" min="1000" className="w-full px-4 py-2 border border-gray-300 rounded-lg" value={formData.amount} onChange={(e) => setFormData({ ...formData, amount: e.target.value })} required /></div>
            <div><label className="block text-gray-700 font-medium mb-2">Lời nhắn</label><textarea className="w-full px-4 py-2 border border-gray-300 rounded-lg" rows="3" value={formData.message} onChange={(e) => setFormData({ ...formData, message: e.target.value })} placeholder="Gửi lời động viên đến dự án..." /></div>
            <button type="submit" className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 transition-colors">Xác nhận ủng hộ</button>
          </form>}
        {!user && <h2 className="text-2xl">Hãy đăng nhập để ủng hộ dự án này!</h2>}
      </div>
    </div>
  );
}
