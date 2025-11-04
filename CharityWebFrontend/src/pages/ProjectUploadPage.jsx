import React, { useState } from "react";
import { uploadProject } from "../services/ProjectService";

export default function ProjectUploadPage({ setCurrentPage }) {
  const [formData, setFormData] = useState({
    name: "",
    category: "",
    description: "",
    targetAmount: "",
    imageUrl: "",
    startDate: "",
    endDate: ""
  });

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    setFormData({
      ...formData,
      [name]: files ? files[0] : value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    uploadProject(formData);
    setCurrentPage('home');
  };

  return (
    <div className="flex justify-center items-start min-h-screen bg-gray-50 py-12 px-4">
      <div className="bg-white shadow-lg rounded-2xl p-8 w-full max-w-3xl">
        <h1 className="text-2xl font-bold text-green-700 mb-2">
          Đăng tải dự án gây quỹ
        </h1>

        <form onSubmit={handleSubmit} className="flex flex-col gap-6">
          {/* Tên dự án */}
          <div>
            <label className="block font-medium text-gray-700 mb-2">
              Tên dự án *
            </label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              className="w-full border border-gray-300 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-green-600"
            />
          </div>

          {/* Phân loại */}
          <div>
            <label className="block font-medium text-gray-700 mb-2">
              Phân loại *
            </label>
            <select
              name="category"
              value={formData.category}
              onChange={handleChange}
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

          {/* Mô tả */}
          <div>
            <label className="block font-medium text-gray-700 mb-2">
              Mô tả chi tiết *
            </label>
            <textarea
              name="description"
              rows="5"
              value={formData.description}
              onChange={handleChange}
              required
              className="w-full border border-gray-300 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-green-600 resize-none"
            ></textarea>
          </div>

          {/* Mục tiêu gây quỹ */}
          <div>
            <label className="block font-medium text-gray-700 mb-2">
              Mục tiêu gây quỹ (VNĐ) *
            </label>
            <input
              type="number"
              name="targetAmount"
              value={formData.targetAmount}
              onChange={handleChange}
              required
              min="1000"
              className="w-full border border-gray-300 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-green-600"
            />
          </div>

          {/* Ảnh minh họa */}
          <div>
            <label className="block font-medium text-gray-700 mb-2">
              Ảnh minh họa dự án (link URL) *
            </label>
            <input
              type="text"
              name="imageUrl"
              value={formData.imageUrl}
              onChange={handleChange}
              required
              className="w-full border border-gray-300 rounded-lg p-3 text-gray-600 focus:outline-none focus:ring-2 focus:ring-green-600"
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Ngày bắt đầu */}
            <div>
              <label className="block font-medium text-gray-700 mb-2">
                Ngày bắt đầu *
              </label>
              <input
                type="date"
                name="startDate"
                value={formData.startDate}
                onChange={handleChange}
                required
                min={new Date().toISOString().split("T")[0]}
                className="w-full border border-gray-300 rounded-lg p-3 text-gray-600 focus:outline-none focus:ring-2 focus:ring-green-600"
              />
            </div>
            {/* Ngày kết thúc */}
            <div>
              <label className="block font-medium text-gray-700 mb-2">
                Ngày kết thúc *
              </label>
              <input
                type="date"
                name="endDate"
                value={formData.endDate}
                onChange={handleChange}
                required
                min={new Date().toISOString().split("T")[0]}
                className="w-full border border-gray-300 rounded-lg p-3 text-gray-600 focus:outline-none focus:ring-2 focus:ring-green-600"
              />
            </div>
          </div>

          {/* Nút gửi */}
          <button
            type="submit"
            className="bg-green-600 hover:bg-green-700 text-white font-medium rounded-lg py-3 transition-all duration-200"
          >
            Đăng dự án
          </button>
        </form>
      </div>
    </div>
  );
}
