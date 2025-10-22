import React from 'react';
/*
 Header component
 Props:
 - currentPage, setCurrentPage: control which page is active
 - user, setUser: current user and setter
 - setShowLogin: function to show login modal
 - setSelectedProject: clear selected project when changing page
 - searchTerm, setSearchTerm: search input state
*/
export default function Header({ currentPage, setCurrentPage, user, setUser, setShowLogin, setSelectedProject, searchTerm, setSearchTerm }) {
  const handleSearch = (e) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      setCurrentPage('search');
      setSelectedProject(null);
    }
  };
  return (
    <header className="bg-white shadow-lg sticky top-0 z-50">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <div className="text-2xl font-bold text-green-600 cursor-pointer" onClick={() => { setCurrentPage('home'); setSelectedProject(null); }}>
            🌱 GayQuy.vn
          </div>
          <nav className="hidden md:flex space-x-8">
            <button className={`hover:text-green-600 transition-colors ${currentPage==='home'?'text-green-600 font-semibold':'text-gray-700'}`} onClick={() => { setCurrentPage('home'); setSelectedProject(null); }}>Trang chủ</button>
            <button className={`hover:text-green-600 transition-colors ${currentPage==='projects'?'text-green-600 font-semibold':'text-gray-700'}`} onClick={() => { setCurrentPage('projects'); setSelectedProject(null); }}>Dự án</button>
            <button className={`hover:text-green-600 transition-colors ${currentPage==='categories'?'text-green-600 font-semibold':'text-gray-700'}`} onClick={() => { setCurrentPage('categories'); setSelectedProject(null); }}>Phân loại</button>
          </nav>
          <div className="flex items-center space-x-4">
            <form onSubmit={handleSearch} className="relative">
              <input type="text" placeholder="Tìm kiếm dự án..." className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 w-64" value={searchTerm} onChange={(e)=>setSearchTerm(e.target.value)} />
              <button type="submit" className="absolute left-3 top-2.5 h-5 w-5 text-gray-400 hover:text-green-600 cursor-pointer">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" className="h-5 w-5"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
              </button>
            </form>
            {user ? (
              <div className="flex items-center space-x-2">
                <button className="text-green-600 hover:text-green-700" onClick={()=>setCurrentPage('profile')}>👤 {user.name}</button>
                <button className="text-red-600 hover:text-red-700" onClick={()=>setUser(null)}>Đăng xuất</button>
              </div>
            ) : (
              <button className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors" onClick={()=>setShowLogin(true)}>Đăng nhập</button>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}
