import React, { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { getMyManagedProjects } from '../services/ProjectService';
import ProjectCard from '../components/ProjectCard';
import LoadingSpinner from '../components/LoadingSpinner';
import { useNavigate } from 'react-router-dom';

export default function MyProjectsPage() {
    const { user } = useAuth();
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        if (!user || user.roleType !== 'ORGANIZATION') return;
        const fetchMyProjects = async () => {
            setLoading(true);
            try {
                const res = await getMyManagedProjects({ page: 0, size: 20 });
                setProjects(res.data.content || []);
            } catch {
                setProjects([]);
            }
            setLoading(false);
        };
        fetchMyProjects();
    }, [user]);

    if (!user || user.roleType !== 'ORGANIZATION') {
        return <div className="text-center py-16 text-gray-500">Bạn không có quyền truy cập trang này.</div>;
    }

    return (
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <h1 className="text-2xl font-bold mb-6 text-primary-700">Quản lý dự án của tôi</h1>
            {loading ? (
                <LoadingSpinner text="Đang tải dự án của bạn..." />
            ) : projects.length > 0 ? (
                <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {projects.map((p) => (
                        <ProjectCard key={p.projectId} project={p} isOwner />
                    ))}
                </div>
            ) : (
                <div className="text-center py-16 text-gray-500">Bạn chưa có dự án nào.</div>
            )}
        </div>
    );
}
