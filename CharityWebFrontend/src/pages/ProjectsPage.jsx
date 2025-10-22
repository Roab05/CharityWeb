import React, { useState } from 'react';
import ProjectCard from '../components/ProjectCard';
import mockProjects from '../data/mockProjects';
/*
 ProjectsPage
 Props: setSelectedProject
*/
export default function ProjectsPage({ setSelectedProject }) {
  const [filter, setFilter] = useState('all');
  const filtered = filter === 'all' ? mockProjects : mockProjects.filter(p => p.category === filter);
  return (
    <div className="py-8">
      <div className="container mx-auto px-4">
        <h1 className="text-3xl font-bold mb-8">Tất cả dự án</h1>
        <div className="flex flex-wrap gap-4 mb-8">
          <button className={`px-4 py-2 rounded-lg ${filter==='all'?'bg-green-600 text-white':'bg-gray-200 text-gray-700'}`} onClick={()=>setFilter('all')}>Tất cả</button>
          <button className={`px-4 py-2 rounded-lg ${filter==='education'?'bg-green-600 text-white':'bg-gray-200 text-gray-700'}`} onClick={()=>setFilter('education')}>Giáo dục</button>
          <button className={`px-4 py-2 rounded-lg ${filter==='wildlife'?'bg-green-600 text-white':'bg-gray-200 text-gray-700'}`} onClick={()=>setFilter('wildlife')}>Động vật hoang dã</button>
          <button className={`px-4 py-2 rounded-lg ${filter==='social'?'bg-green-600 text-white':'bg-gray-200 text-gray-700'}`} onClick={()=>setFilter('social')}>Xã hội</button>
          <button className={`px-4 py-2 rounded-lg ${filter==='environment'?'bg-green-600 text-white':'bg-gray-200 text-gray-700'}`} onClick={()=>setFilter('environment')}>Môi trường</button>
        </div>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {filtered.map(p => <ProjectCard key={p.id} project={p} onClick={setSelectedProject} />)}
        </div>
      </div>
    </div>
  );
}