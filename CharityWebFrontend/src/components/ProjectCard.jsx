import { useState, useEffect } from 'react';
import { getByProjectId as getDonationsByProjectId } from '../services/DonationService';
import { getProject, getProjectCurrentAmount, getProjectDaysLeft, getProjectDonationCount, getProjectState } from '../services/ProjectService';
/*
 ProjectCard component
 Props:
 - project: project object to display
 - onClick: handler when card is clicked (usually sets selectedProject)
*/
export default function ProjectCard({ project, onClick }) {

  const [currentProject, setCurrentProject] = useState(project);
  const [progressPercentage, setProgressPercentage] = useState(0);
  const [daysLeft, setDaysLeft] = useState(null);
  const [currentAmount, setCurrentAmount] = useState(0);
  const [donationCount, setDonationCount] = useState(0);
  const [state, setState] = useState(null);

  const fetchData = async () => {
    try {
      const projectRes = await getProject(currentProject.id);
      const amountRes = await getProjectCurrentAmount(currentProject.id)
      const donRes = await getProjectDonationCount(currentProject.id)
      const stateRes = await getProjectState(currentProject.id);
      const daysRes = await getProjectDaysLeft(currentProject.id);


      const updatedProject = projectRes.data;

      setCurrentProject(updatedProject);
      setCurrentAmount(amountRes.data);
      setDonationCount(donRes.data);
      setState(stateRes.data);
      setDaysLeft(daysRes.data);

      setProgressPercentage(amountRes.data / updatedProject.targetAmount * 100);

    } catch (error) {
      console.error("Error fetching donations:", error);
    }
  };

  useEffect(() => {
    fetchData();

    setProgressPercentage(currentAmount / currentProject.targetAmount * 100);

    const interval = setInterval(fetchData, 3000);

    return () => clearInterval(interval);
  }, []);
  return (
    <div className="project-card bg-white rounded-xl shadow-lg overflow-hidden cursor-pointer" onClick={() => { onClick(currentProject); window.scrollTo(0, 0) }}>
      <img src={currentProject.imageUrl} alt={currentProject.name} className="w-full h-48 object-cover" />
      <div className="p-6">
        <h3 className="text-xl font-semibold mb-2">{currentProject.name}</h3>
        <p className="text-gray-600 mb-4 line-clamp-2">{currentProject.description}</p>
        <div className="mb-4">
          <div className="flex justify-between text-sm text-gray-600 mb-1">
            <span>Đã gây quỹ</span>
            <span>{Math.round(progressPercentage * 100) / 100}%</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div className="progress-bar h-2 rounded-full" style={{ width: `${Math.min(progressPercentage, 100)}%` }}></div>
          </div>
        </div>
        <div className="flex justify-between items-center text-sm">
          <span className="text-green-600 font-semibold">
            {currentAmount.toLocaleString('vi-VN')} VNĐ
          </span>
          <span className="text-gray-500">{donationCount} lượt ủng hộ</span>
        </div>
        <div className="mt-2 text-sm">
          {state === 0 && <span>⏰ Sắp diễn ra </span>}
          {state === 1 && <span className="text-orange-600">⏰ Còn {daysLeft} ngày</span>}
          {state === 2 && <span className="text-red-600"> ❌ Đã kết thúc</span>}
        </div>
      </div>
    </div>
  );
}
