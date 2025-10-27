import React, { useState, useEffect } from 'react';
import { getByProjectId as getDonationsByProjectId } from '../services/DonationService';
import { getProject, getProjectDaysLeft, getProjectState } from '../services/ProjectService';
export default function ProjectDetailPage({ project, setShowDonation }) {

    const [donations, setDonations] = useState([]);
    const [currentProject, setCurrentProject] = useState(project);
    const [progressPercentage, setProgressPercentage] = useState(0);
    const [daysLeft, setDaysLeft] = useState(null);
    const [state, setState] = useState(null);

    const fetchData = async () => {
        try {
            const response1 = await getDonationsByProjectId(project.id);
            const response2 = await getProject(project.id);
            const stateRes = await getProjectState(currentProject.id);
            const daysRes = await getProjectDaysLeft(currentProject.id);

            const updatedProject = response2.data;

            setDonations(response1.data.reverse());
            donations.sort((a, b) => new Date(b.date) - new Date(a.date));
            setCurrentProject(updatedProject);
            setProgressPercentage(updatedProject.currentAmount / updatedProject.targetAmount * 100);
            setState(stateRes.data);
            setDaysLeft(daysRes.data);

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
                            <img src={project.imageUrl} alt={project.name} className="w-full h-64 object-cover rounded-xl mb-6" />
                            <h1 className="text-3xl font-bold mb-4">{project.name}</h1>
                            <div className="mb-6">
                                <div className="flex justify-between text-sm text-gray-600 mb-2"><span>Tiến độ gây quỹ</span><span>{Math.round(progressPercentage * 100) / 100}%</span></div>
                                <div className="w-full bg-gray-200 rounded-full h-3">
                                    <div className="progress-bar h-3 rounded-full" style={{ width: `${Math.min(progressPercentage, 100)}%` }}></div>
                                </div>
                                <div className="flex justify-between mt-2">
                                    <span className="text-green-600 font-semibold text-lg">{currentProject.currentAmount.toLocaleString('vi-VN')} VNĐ</span>
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
                                <p className="text-gray-700 leading-relaxed mb-6 whitespace-pre-line">{project.description}</p>
                            </div>
                        </div>
                        <div className="lg:col-span-1">
                            <div className="bg-white rounded-xl shadow-lg p-6 sticky top-24">
                                <button className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 transition-colors mb-6" onClick={() => setShowDonation(true)}>Ủng hộ ngay</button>
                                <h3 className="text-lg font-semibold mb-4">Danh sách ủng hộ</h3>
                                <div className="space-y-4 max-h-96 overflow-y-auto">
                                    {donations.map((donation, i) => (<div key={i} className="border-b border-gray-100 pb-4">
                                        <div className="flex justify-between items-start mb-2">
                                            <span className="font-medium">{donation.donor.displayName}</span>
                                            <span className="text-green-600 font-semibold text-sm">{donation.amount.toLocaleString('vi-VN')} VNĐ</span>
                                        </div>
                                        <p className="text-gray-600 text-sm mb-1">{donation.message}</p>
                                        <span className="text-gray-400 text-xs">{donation.date}</span>
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