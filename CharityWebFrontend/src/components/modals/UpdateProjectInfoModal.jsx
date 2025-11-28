import React, { useState, useEffect } from 'react';
import { updateProjectInfo } from '../../services/ProjectService';
/*
 UpdateProjectInfoModal component
 Props:
 - showModal, setShowModal: visibility control
 - project: current project object to edit
 - onUpdateSuccess: callback function to refresh data after update
*/
export default function UpdateProjectInfoModal({ showProjectUpdate, setShowProjectUpdate, project }) {
    const [formData, setFormData] = useState({
        id: '',
        name: '',
        category: '',
        description: '',
        targetAmount: '',
        imageUrl: '',
        endDate: null
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (project && showProjectUpdate) {
            setFormData({
                id: project.id,
                name: project.name || '',
                category: project.category || '',
                description: project.description || '',
                targetAmount: project.targetAmount || '',
                imageUrl: project.imageUrl || '',
                endDate: project.endDate ? new Date(project.endDate).toISOString().split('T')[0] : ''
            });
        }
    }, [project, showProjectUpdate]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const response = await updateProjectInfo(formData);

            setShowProjectUpdate(false);
            alert('Cập nhật dự án thành công!');
        } catch (err) {
            console.error(err);
            setError('Có lỗi xảy ra khi cập nhật dự án.');
        } finally {
            setLoading(false);
        }
    };

    if (!showProjectUpdate) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 z-[1000]">
            <div className="bg-white rounded-xl p-8 max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto shadow-2xl">
                <div className="flex justify-between items-center mb-6 border-b pb-4">
                    <h2 className="text-2xl font-bold text-gray-800">Chỉnh sửa thông tin dự án</h2>
                    <button className="text-gray-400 hover:text-gray-600 transition-colors" onClick={() => setShowProjectUpdate(false)}>
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>

                {error && <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm">{error}</div>}

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label className="block text-gray-700 font-semibold mb-2 text-sm">Tên dự án<span className="text-red-500">*</span></label>
                        <input
                            type="text"
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            required
                        />
                    </div>

                    <div>
                        <label className="block font-medium text-gray-700 mb-2">
                            Phân loại<span className="text-red-500">*</span>
                        </label>
                        <select
                            name="category"
                            value={formData.category}
                            onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                            required
                            className="w-full border border-gray-300 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-green-600"
                        >
                            <option value="">-- Chọn phân loại --</option>
                            <option value="Giáo dục">Giáo dục</option>
                            <option value="Động vật hoang dã">Động vật hoang dã</option>
                            <option value="Xã hội">Xã hội</option>
                            <option value="Môi trường">Môi trường</option>
                            <option value="Khác">Khác</option>
                        </select>
                    </div>

                    <div>
                        <label className="block text-gray-700 font-semibold mb-2 text-sm">Mô tả chi tiết<span className="text-red-500">*</span></label>
                        <textarea
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                            rows="6"
                            value={formData.description}
                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                            required
                        />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                        <div>
                            <label className="block text-gray-700 font-semibold mb-2 text-sm">Mục tiêu gây quỹ (VNĐ)<span className="text-red-500">*</span></label>
                            <input
                                type="number"
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                                value={formData.targetAmount}
                                onChange={(e) => setFormData({ ...formData, targetAmount: e.target.value })}
                                required
                            />
                        </div>
                        <div>
                            <label className="block text-gray-700 font-semibold mb-2 text-sm">Ngày kết thúc<span className="text-red-500">*</span></label>
                            <input
                                type="date"
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                                value={formData.endDate}
                                onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                                required
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-gray-700 font-semibold mb-2 text-sm">Ảnh minh họa dự án (link URL)*</label>
                        <input
                            type="text"
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                            value={formData.imageUrl}
                            onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
                            placeholder="https://example.com/image.jpg"
                        />
                    </div>

                    <div className="pt-6 flex gap-3 border-t mt-2">
                        <button
                            type="button"
                            onClick={() => setShowProjectUpdate(false)}
                            className="w-1/3 bg-gray-100 text-gray-700 py-3 rounded-lg font-semibold hover:bg-gray-200 transition-colors"
                        >
                            Hủy
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className={`w-2/3 bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors shadow-lg hover:shadow-xl ${loading ? 'opacity-70 cursor-not-allowed' : ''}`}
                        >
                            {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}