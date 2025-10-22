import React from 'react';
import ProjectCard from '../components/ProjectCard';
import mockProjects from '../data/mockProjects';
/*
 SearchPage
 Props: searchTerm, setSelectedProject
*/
export default function SearchPage({ searchTerm, setSelectedProject }) {
  const results = mockProjects.filter(project => project.name.toLowerCase().includes(searchTerm.toLowerCase()) || project.description.toLowerCase().includes(searchTerm.toLowerCase()));
  return (
    <div className="py-8">
      <div className="container mx-auto px-4">
        <div className="mb-8">
          <h1 className="text-3xl font-bold mb-2">Kết quả tìm kiếm</h1>
          <p className="text-gray-600">Tìm thấy <span className="font-semibold text-green-600">{results.length}</span> kết quả cho "{searchTerm}"</p>
        </div>
        {results.length > 0 ? (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {results.map(p => <ProjectCard key={p.id} project={p} onClick={setSelectedProject} />)}
          </div>
        ) : (
          <div className="text-center py-16">
            <div className="text-6xl mb-4">🔍</div>
            <h3 className="text-xl font-semibold mb-2">Không tìm thấy kết quả</h3>
            <p className="text-gray-600 mb-6">Không có dự án nào phù hợp với từ khóa "{searchTerm}"</p>
            <div className="space-y-2 text-sm text-gray-500">
              <p>💡 Gợi ý tìm kiếm:</p>
              <p>• Thử sử dụng từ khóa khác</p>
              <p>• Kiểm tra chính tả</p>
              <p>• Sử dụng từ khóa ngắn gọn hơn</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}