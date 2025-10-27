import ProjectCard from '../components/ProjectCard';
/*
 CategoriesPage
 Props: setSelectedProject
*/
export default function CategoriesPage({ projects, setSelectedProject }) {

  let educationCount = 0;
  let wildlifeCount = 0;
  let socialCount = 0;
  let environmentCount = 0;
  let otherCount = 0;

  projects && projects.forEach(p => {
    switch (p.category) {
      case 'Giáo dục':
        educationCount++;
        break;
      case 'Động vật hoang dã':
        wildlifeCount++;
        break;
      case 'Xã hội':
        socialCount++;
        break;
      case 'Môi trường':
        environmentCount++;
        break;
      case 'Khác':
        otherCount++;
        break;
    }
  });

  const categories = [
    { id: 'education', name: 'Giáo dục', icon: '📚', count: educationCount },
    { id: 'wildlife', name: 'Động vật hoang dã', icon: '🦁', count: wildlifeCount },
    { id: 'social', name: 'Xã hội', icon: '🤝', count: socialCount },
    { id: 'environment', name: 'Môi trường', icon: '🌱', count: environmentCount },
    { id: 'other', name: 'Khác', icon: '❤️‍🩹', count: otherCount }
  ];
  return (
    <div className="py-8">
      <div className="container mx-auto px-4">
        <h1 className="text-3xl font-bold mb-8">Phân loại</h1>
        <div className="grid md:grid-cols-2 lg:grid-cols-5 gap-6">
          {categories.map(c => (
            <div key={c.id} className="bg-white rounded-xl shadow-lg p-6 text-center hover:shadow-xl transition-shadow cursor-pointer">
              <div className="text-4xl mb-4">{c.icon}</div>
              <h3 className="text-xl font-semibold mb-2">{c.name}</h3>
              <p className="text-gray-600">{c.count} dự án</p>
            </div>
          ))}
        </div>
        <div className="mt-12">
          <h2 className="text-2xl font-bold mb-6">Nổi bật</h2>
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {projects.slice(0, 3).map(p => <ProjectCard key={p.id} project={p} onClick={setSelectedProject} />)}
          </div>
        </div>
      </div>
    </div>
  );
}