import React from 'react';
import ProjectCard from '../components/ProjectCard';
import mockProjects from '../data/mockProjects';
/*
 CategoriesPage
 Props: setSelectedProject
*/
export default function CategoriesPage({ setSelectedProject }) {
  const categories = [
    { id: 'education', name: 'Giáo dục', icon: '📚', count: 1 },
    { id: 'wildlife', name: 'Động vật hoang dã', icon: '🦁', count: 1 },
    { id: 'social', name: 'Xã hội', icon: '🤝', count: 1 },
    { id: 'environment', name: 'Môi trường', icon: '🌱', count: 1 }
  ];
  return (
    <div className="py-8">
      <div className="container mx-auto px-4">
        <h1 className="text-3xl font-bold mb-8">Phân loại dự án</h1>
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
          {categories.map(c => (
            <div key={c.id} className="bg-white rounded-xl shadow-lg p-6 text-center hover:shadow-xl transition-shadow cursor-pointer">
              <div className="text-4xl mb-4">{c.icon}</div>
              <h3 className="text-xl font-semibold mb-2">{c.name}</h3>
              <p className="text-gray-600">{c.count} dự án</p>
            </div>
          ))}
        </div>
        <div className="mt-12">
          <h2 className="text-2xl font-bold mb-6">Dự án nổi bật</h2>
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {mockProjects.slice(0,3).map(p => <ProjectCard key={p.id} project={p} onClick={setSelectedProject} />)}
          </div>
        </div>
      </div>
    </div>
  );
}