import React, { useState, useEffect } from 'react';
import { getByProjectId as getDonationsByProjectId } from '../services/DonationService';
import { getProject, getProjectCurrentAmount, getProjectDaysLeft, getProjectState } from '../services/ProjectService';

export default function ProjectDetailPage({ user, project, setShowDonation, setShowProjectUpdate, setShowDeleteProject }) {

    const [donations, setDonations] = useState([]);
    const [currentProject, setCurrentProject] = useState(project);
    const [currentAmount, setCurrentAmount] = useState(0);
    const [progressPercentage, setProgressPercentage] = useState(0);
    const [daysLeft, setDaysLeft] = useState(null);
    const [state, setState] = useState(null);

    const fetchData = async () => {
        try {
            const donationsRes = await getDonationsByProjectId(currentProject.id);
            const projectRes = await getProject(currentProject.id);
            const amountRes = await getProjectCurrentAmount(currentProject.id)
            const stateRes = await getProjectState(currentProject.id);
            const daysRes = await getProjectDaysLeft(currentProject.id);

            const updatedProject = projectRes.data;

            setDonations(donationsRes.data);
            setCurrentProject(updatedProject);
            setCurrentAmount(amountRes.data);
            setState(stateRes.data);
            setDaysLeft(daysRes.data);

            setProgressPercentage(amountRes.data / updatedProject.targetAmount * 100);

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
                <div className="max-w-6xl mx-auto">
                    <div className="grid lg:grid-cols-3 gap-8">
                        <div className="lg:col-span-2">
                            <img src={currentProject.imageUrl} alt={currentProject.name} className="w-full h-64 object-cover rounded-xl mb-6" />

                            <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-4 gap-4">
                                <h1 className="text-3xl font-bold">{currentProject.name}</h1>
                                {user?.admin && <div className="flex gap-3">
                                    <button
                                        className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium text-sm whitespace-nowrap"
                                        onClick={() => setShowProjectUpdate(true)}
                                    >
                                        Chỉnh sửa thông tin
                                    </button>
                                    <button
                                        className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors font-medium text-sm whitespace-nowrap"
                                        onClick={() => setShowDeleteProject(true)}
                                    >
                                        Xóa dự án
                                    </button>
                                </div>}
                            </div>


                            <div className="mb-6">
                                <div className="flex justify-between text-sm text-gray-600 mb-2"><span>Tiến độ gây quỹ</span><span>{Math.round(progressPercentage * 100) / 100}%</span></div>
                                <div className="w-full bg-gray-200 rounded-full h-3">
                                    <div className="progress-bar h-3 rounded-full" style={{ width: `${Math.min(progressPercentage, 100)}%` }}></div>
                                </div>
                                <div className="flex justify-between mt-2">
                                    <span className="text-green-600 font-semibold text-lg">{currentAmount.toLocaleString('vi-VN')} VNĐ</span>
                                    <span className="text-gray-500">Mục tiêu: {currentProject.targetAmount.toLocaleString('vi-VN')} VNĐ</span>
                                </div>
                            </div>
                            <div className="flex items-center space-x-6 mb-6 text-sm text-gray-600">
                                <span>👥 {donations.length} lượt ủng hộ</span>
                                {state === 0 && <span>⏰ Sắp diễn ra</span>}
                                {state === 1 && <span>⏰ Còn {daysLeft} ngày</span>}
                                {state === 2 && <span className="text-red-600"> ❌ Đã kết thúc</span>}
                            </div>
                            <div className="prose max-w-none">
                                <h2 className="text-xl font-semibold mb-4">Về dự án</h2>
                                <p className="text-gray-700 leading-relaxed mb-6 whitespace-pre-line">{currentProject.description}</p>
                            </div>
                        </div>
                        {state > 0 && <div className="lg:col-span-1">
                            <div className="bg-white rounded-xl shadow-lg p-6 sticky top-24">
                                {state == 1 && <button className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 transition-colors mb-6" onClick={() => setShowDonation(true)}>Ủng hộ ngay</button>}
                                <h3 className="text-lg font-semibold mb-4">Danh sách ủng hộ ({donations.length})</h3>
                                <div className="space-y-4 max-h-96 overflow-y-auto">
                                    {donations.map((donation, i) => (<div key={i} className="border-b border-gray-100 pb-4">
                                        <div className="flex justify-between items-start mb-2">
                                            <span className="font-medium">{donation.user.displayName}</span>
                                            <span className="text-green-600 font-semibold text-sm">{donation.amount.toLocaleString('vi-VN')} VNĐ</span>
                                        </div>
                                        <p className="text-gray-600 text-sm mb-1">{donation.message}</p>
                                        <span className="text-gray-400 text-xs">{donation.dateTime}</span>
                                    </div>))}
                                </div>
                            </div>
                        </div>}
                    </div>
                </div>
            </div>
        </div>
    );
}