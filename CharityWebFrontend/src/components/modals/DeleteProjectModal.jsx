import React, { useState } from 'react';
import { deleteProject } from '../../services/ProjectService';

/*
 DeleteProjectModal component
 Props:
 - showModal, setShowModal: visibility control
 - project: project object to delete
 - onDeleteSuccess: callback function to redirect or refresh after delete
*/
export default function DeleteProjectModal({ showDeleteProject, setShowDeleteProject, setSelectedProject, project }) {
    const [loading, setLoading] = useState(false);

    const handleDelete = async () => {
        setLoading(true);
        try {
            await deleteProject(project.id);
            setSelectedProject(null);

            setTimeout(() => {
                setShowDeleteProject(false);
            }, 3000);

        } catch (err) {
            console.error(err);
            alert('Có lỗi xảy ra khi xóa dự án. Vui lòng thử lại sau.');
        } finally {
            setLoading(false);
        }
    };

    if (!showDeleteProject) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 z-[1000]">
            <div className="bg-white rounded-xl p-8 max-w-md w-full mx-4 shadow-2xl animate-fadeIn">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-bold text-red-600 flex items-center gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                        </svg>
                        {project ? (<>Xóa dự án?</>) : (<>ĐÃ XÓA DỰ ÁN!</>)}
                    </h2>
                    <button className="text-gray-400 hover:text-gray-600 transition-colors" onClick={() => setShowDeleteProject(false)}>
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>

                {project && <div className="text-gray-700 mb-8 leading-relaxed">
                    <p className="mb-4">Bạn có chắc chắn muốn xóa dự án:</p>
                    <div className="bg-red-50 p-4 rounded-lg border border-red-100 mb-4">
                        <strong className="text-red-800 text-lg block">{project?.name}</strong>
                    </div>
                    <p className="text-sm text-gray-500 italic">
                        Hành động này <span className="font-bold text-red-500">không thể hoàn tác</span>. Toàn bộ dữ liệu gây quỹ và thông tin liên quan đến dự án này sẽ bị xóa vĩnh viễn khỏi hệ thống.
                    </p>
                </div>}

                {project && <div className="flex space-x-3">
                    <button
                        onClick={() => setShowDeleteProject(false)}
                        className="w-1/2 bg-gray-100 text-gray-700 py-3 rounded-lg font-semibold hover:bg-gray-200 transition-colors"
                    >
                        Hủy bỏ
                    </button>
                    <button
                        onClick={handleDelete}
                        disabled={loading}
                        className={`w-1/2 bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition-colors shadow-md hover:shadow-lg ${loading ? 'opacity-70 cursor-not-allowed' : ''}`}
                    >
                        {loading ? 'Đang xóa...' : 'Xác nhận xóa'}
                    </button>
                </div>}
            </div>
        </div>
    );
}