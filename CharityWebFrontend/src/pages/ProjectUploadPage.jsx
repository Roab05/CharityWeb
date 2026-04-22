import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { createProject } from '../services/ProjectService';
import { uploadFile } from '../services/FileService';

export default function ProjectUploadPage() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        projectName: '',
        description: '',
        targetAmount: '',
        startDate: '',
        endDate: '',
        backgroundImageURL: '',
        bankAccountNo: '',
        categoryIds: [],
    });
    const [imageFile, setImageFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            let backgroundImageURL = formData.backgroundImageURL;
            if (imageFile) {
                const uploadRes = await uploadFile(imageFile);
                backgroundImageURL = uploadRes.data.url;
            }

            await createProject({
                ...formData,
                targetAmount: parseFloat(formData.targetAmount),
                backgroundImageURL,
            });

            setSuccess('Dự án đã được tạo thành công! Đang chờ quản trị viên phê duyệt.');
            setTimeout(() => navigate('/projects'), 2000);
        } catch (err) {
            setError(err.response?.data?.message || 'Có lỗi xảy ra khi tạo dự án.');
        }
        setLoading(false);
    };

    const today = new Date().toISOString().split('T')[0];

    return (
        <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-800">Tạo dự án gây quỹ</h1>
                <p className="text-gray-500 mt-2">Điền thông tin dự án để bắt đầu kêu gọi quyên góp</p>
            </div>

            {error && <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 text-sm">{error}</div>}
            {success && <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg mb-6 text-sm">{success}</div>}

            <form onSubmit={handleSubmit} className="card p-6 space-y-6">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1.5">Tên dự án <span className="text-red-500">*</span></label>
                    <input
                        name="projectName"
                        className="input-field"
                        value={formData.projectName}
                        onChange={handleChange}
                        required
                        placeholder="VD: Xây trường học cho trẻ em vùng cao"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1.5">Mô tả chi tiết <span className="text-red-500">*</span></label>
                    <textarea
                        name="description"
                        className="input-field resize-none"
                        rows="6"
                        value={formData.description}
                        onChange={handleChange}
                        required
                        placeholder="Mô tả về dự án, mục đích, đối tượng thụ hưởng..."
                    />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1.5">Mục tiêu gây quỹ (₫) <span className="text-red-500">*</span></label>
                        <input
                            type="number"
                            name="targetAmount"
                            className="input-field"
                            value={formData.targetAmount}
                            onChange={handleChange}
                            required
                            min="100000"
                            placeholder="10000000"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1.5">Số tài khoản ngân hàng</label>
                        <input
                            name="bankAccountNo"
                            className="input-field"
                            value={formData.bankAccountNo}
                            onChange={handleChange}
                            placeholder="VD: 123456789"
                        />
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1.5">Ngày bắt đầu <span className="text-red-500">*</span></label>
                        <input
                            type="date"
                            name="startDate"
                            className="input-field"
                            value={formData.startDate}
                            onChange={handleChange}
                            required
                            min={today}
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1.5">Ngày kết thúc <span className="text-red-500">*</span></label>
                        <input
                            type="date"
                            name="endDate"
                            className="input-field"
                            value={formData.endDate}
                            onChange={handleChange}
                            required
                            min={formData.startDate || today}
                        />
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1.5">Ảnh đại diện dự án</label>
                    <div className="flex gap-4 items-start">
                        <div className="flex-1">
                            <input
                                type="file"
                                accept="image/*"
                                className="input-field text-sm"
                                onChange={(e) => setImageFile(e.target.files[0])}
                            />
                            <p className="text-xs text-gray-400 mt-1">Hoặc nhập URL hình ảnh bên dưới</p>
                        </div>
                    </div>
                    <input
                        name="backgroundImageURL"
                        className="input-field mt-2"
                        value={formData.backgroundImageURL}
                        onChange={handleChange}
                        placeholder="https://example.com/image.jpg (không bắt buộc nếu đã upload ảnh)"
                    />
                </div>

                <div className="bg-amber-50 rounded-lg p-4 text-sm text-amber-700">
                    ⚠️ Dự án sẽ được gửi đến quản trị viên xét duyệt trước khi công khai. Quá trình xét duyệt có thể mất 1-3 ngày làm việc.
                </div>

                <button
                    type="submit"
                    disabled={loading}
                    className={`w-full py-3 rounded-xl font-semibold text-white transition-all ${loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-primary-600 hover:bg-primary-700 shadow-sm hover:shadow-md'
                        }`}
                >
                    {loading ? 'Đang tạo dự án...' : 'Tạo dự án'}
                </button>
            </form>
        </div>
    );
}
