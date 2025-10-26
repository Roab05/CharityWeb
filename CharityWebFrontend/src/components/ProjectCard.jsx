import React from 'react';
import { useState, useEffect } from 'react';
import { getByProjectId } from '../services/DonationService';
/*
 ProjectCard component
 Props:
 - project: project object to display
 - onClick: handler when card is clicked (usually sets selectedProject)
*/
export default function ProjectCard({ project, onClick }) {

  const [donations, setDonations] = useState([]);
  const [progressPercentage, setProgressPercentage] = useState(0);

  const fetchDonations = async () => {
    try {
      const response = await getByProjectId(project.id);
      setDonations(response.data);
    } catch (error) {
      console.error("Error fetching donations:", error);
    }
  };

  useEffect(() => {
    fetchDonations();

    setProgressPercentage(project.currentAmount / project.targetAmount * 100);

    const interval = setInterval(fetchDonations, 3000);

    return () => clearInterval(interval);
  }, []);
  return (
    <div className="project-card bg-white rounded-xl shadow-lg overflow-hidden cursor-pointer" onClick={() => { onClick(project); window.scrollTo(0, 0) }}>
      <img src={project.imageUrl} alt={project.name} className="w-full h-48 object-cover" />
      <div className="p-6">
        <h3 className="text-xl font-semibold mb-2">{project.name}</h3>
        <p className="text-gray-600 mb-4 line-clamp-2">{project.description}</p>
        <div className="mb-4">
          <div className="flex justify-between text-sm text-gray-600 mb-1">
            <span>Đã gây quỹ</span>
            <span>{Math.round(progressPercentage)}%</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div className="progress-bar h-2 rounded-full" style={{ width: `${Math.min(progressPercentage, 100)}%` }}></div>
          </div>
        </div>
        <div className="flex justify-between items-center text-sm">
          <span className="text-green-600 font-semibold">
            {project.currentAmount.toLocaleString('vi-VN')} VNĐ
          </span>
          <span className="text-gray-500">{donations.length} lượt ủng hộ</span>
        </div>
        {/* <div className="mt-2 text-sm">
          {project.active ? (<span className="text-orange-600">⏰ Còn {project.daysLeft} ngày</span>) : (<span className="text-red-600">❌ Đã kết thúc</span>)}
        </div> */}
      </div>
    </div>
  );
}
