import React from 'react';
/*
 ThankYouModal component
 Props:
 - showThankYou, setShowThankYou: control visibility
 - donationData: object with name, amount, projectName
*/
export default function ThankYouModal({ showThankYou, setShowThankYou, donationData }) {
  if (!showThankYou) return null;
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl p-8 max-w-md w-full mx-4 text-center">
        <div className="mb-6">
          <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg className="w-10 h-10 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" /></svg>
          </div>
          <h2 className="text-2xl font-bold text-gray-800 mb-2">Cảm ơn bạn!</h2>
          <p className="text-gray-600 mb-4">Đóng góp của bạn đã được ghi nhận thành công</p>
        </div>
        <div className="bg-gray-50 rounded-lg p-4 mb-6">
          <div className="flex justify-between items-center mb-2"><span className="text-gray-600">Người ủng hộ:</span><span className="font-semibold">{donationData?.name}</span></div>
          <div className="flex justify-between items-center mb-2"><span className="text-gray-600">Số tiền:</span><span className="font-semibold text-green-600">{parseInt(donationData?.amount || 0).toLocaleString('vi-VN')} VNĐ</span></div>
          <div className="flex justify-between items-center"><span className="text-gray-600">Dự án:</span><span className="font-semibold text-sm">{donationData?.projectName}</span></div>
        </div>
        <div className="text-sm text-gray-600 mb-6"><p>💚 Mỗi đóng góp của bạn đều tạo nên sự khác biệt</p></div>
        <button className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 transition-colors" onClick={() => setShowThankYou(false)}>Đóng</button>
      </div>
    </div>
  );
}
