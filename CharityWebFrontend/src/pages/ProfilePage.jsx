import { useState, useEffect } from "react";
import { getByDonorId, } from "../services/DonationService";
import { getDonor } from "../services/DonorService";

export default function ProfilePage({ user }) {

  const [donations, setDonations] = useState([]);
  const [currentUser, setCurrentUser] = useState(user);

  const fetchData = async () => {
    try {
      const response1 = await getByDonorId(user.id);
      const response2 = await getDonor(user.id);

      setDonations(response1.data.reverse());
      donations.sort((a, b) => new Date(b.date) - new Date(a.date));
      setCurrentUser(response2.data)
    } catch (error) {
      console.error("Error fetching donations:", error);
    }
  };

  useEffect(() => {
    fetchData();

    const interval = setInterval(fetchData, 3000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="py-8">
      <div className="container mx-auto px-4">
        <div className="max-w-4xl mx-auto">
          <div className="bg-white rounded-xl shadow-lg p-8 mb-8">
            <h1 className="text-3xl font-bold mb-6">Thông tin cá nhân</h1>
            <div className="grid md:grid-cols-2 gap-6 mb-8">
              <div><label className="block text-gray-700 font-medium mb-2">Tên người dùng</label><p className="text-lg">{user.displayName}</p></div>
              <div><label className="block text-gray-700 font-medium mb-2">Email</label><p className="text-lg">{user.email}</p></div>
              <div><label className="block text-gray-700 font-medium mb-2">Số điện thoại</label><p className="text-lg">{user.phoneNumber}</p></div>
              <div><label className="block text-gray-700 font-medium mb-2">Tổng đã ủng hộ</label><p className="text-lg text-green-600 font-semibold">{currentUser.totalDonation.toLocaleString('vi-VN')} VNĐ</p></div>
            </div>
          </div>
          <div className="bg-white rounded-xl shadow-lg p-8">
            <h2 className="text-2xl font-bold mb-6">Lịch sử ủng hộ</h2>
            <div className="space-y-4 max-h-96 overflow-y-auto">
              {donations.map((donation, i) => (<div key={i} className="border border-gray-200 rounded-lg p-4">
                <div className="flex justify-between items-start">
                  <div className="w-4/5">
                    <h3 className="font-semibold text-lg">{donation.project.name}</h3>
                    <p className="text-gray-600 text-sm">{donation.date}</p>
                    <p className="break-words whitespace-pre-wrap text-gray-700 mt-1">
                      {donation.message}
                    </p>
                  </div>
                  <span className="text-green-600 font-semibold text-lg">{donation.amount.toLocaleString('vi-VN')} VNĐ</span>
                </div>
              </div>))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}