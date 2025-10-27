import { useState } from 'react';
import ProjectCard from '../components/ProjectCard';
/*
 ProjectsPage
 Props: setSelectedProject
*/
export default function ProjectsPage({ projects, setSelectedProject }) {
  const [filter, setFilter] = useState('Tất cả');
  const filtered = filter === 'Tất cả' ? projects : projects.filter(p => p.category === filter);
  return (
    <div className="py-8">
      <div className="container mx-auto px-4">
        <h1 className="text-3xl font-bold mb-8">Tất cả dự án</h1>
        <div className="flex flex-wrap gap-4 mb-8">
          <button className={`px-4 py-2 rounded-lg ${filter === 'Tất cả' ? 'bg-green-600 text-white' : 'bg-gray-200 text-gray-700'}`} onClick={() => setFilter('Tất cả')}>Tất cả</button>
          <button className={`px-4 py-2 rounded-lg ${filter === 'Giáo dục' ? 'bg-green-600 text-white' : 'bg-gray-200 text-gray-700'}`} onClick={() => setFilter('Giáo dục')}>Giáo dục</button>
          <button className={`px-4 py-2 rounded-lg ${filter === 'Động vật hoang dã' ? 'bg-green-600 text-white' : 'bg-gray-200 text-gray-700'}`} onClick={() => setFilter('Động vật hoang dã')}>Động vật hoang dã</button>
          <button className={`px-4 py-2 rounded-lg ${filter === 'Xã hội' ? 'bg-green-600 text-white' : 'bg-gray-200 text-gray-700'}`} onClick={() => setFilter('Xã hội')}>Xã hội</button>
          <button className={`px-4 py-2 rounded-lg ${filter === 'Môi trường' ? 'bg-green-600 text-white' : 'bg-gray-200 text-gray-700'}`} onClick={() => setFilter('Môi trường')}>Môi trường</button>
          <button className={`px-4 py-2 rounded-lg ${filter === 'Khác' ? 'bg-green-600 text-white' : 'bg-gray-200 text-gray-700'}`} onClick={() => setFilter('Khác')}>Khác</button>
        </div>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {filtered.map(p => <ProjectCard key={p.id} project={p} onClick={setSelectedProject} />)}
        </div>
      </div>
    </div>
  );
}