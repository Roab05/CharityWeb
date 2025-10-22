import React, { useState } from 'react';
/*
 App.jsx - main orchestrator
 Keeps central state and passes props down to components (no Context used)
*/
import Header from './components/Header';
import Footer from './components/Footer';
import HomePage from './pages/HomePage';
import ProjectsPage from './pages/ProjectsPage';
import CategoriesPage from './pages/CategoriesPage';
import SearchPage from './pages/SearchPage';
import ProfilePage from './pages/ProfilePage';
import AboutPage from './pages/AboutPage';
import ContactPage from './pages/ContactPage';
import TermsPage from './pages/TermsPage';
import HelpPage from './pages/HelpPage';
import ProjectDetailPage from './pages/ProjectDetailPage';
import LoginModal from './components/modals/LoginModal';
import DonationModal from './components/modals/DonationModal';
import ThankYouModal from './components/modals/ThankYouModal';

export default function App(){
  const [currentPage,setCurrentPage] = useState('home');
  const [selectedProject,setSelectedProject] = useState(null);
  const [user,setUser] = useState(null);
  const [showLogin,setShowLogin] = useState(false);
  const [showDonation,setShowDonation] = useState(false);
  const [showThankYou,setShowThankYou] = useState(false);
  const [donationData,setDonationData] = useState(null);
  const [searchTerm,setSearchTerm] = useState('');

  const renderPage = () => {
    if (selectedProject) return <ProjectDetailPage project={selectedProject} setShowDonation={setShowDonation} />;
    switch (currentPage) {
      case 'home': return <HomePage setCurrentPage={setCurrentPage} setSelectedProject={setSelectedProject} />;
      case 'projects': return <ProjectsPage setSelectedProject={setSelectedProject} />;
      case 'categories': return <CategoriesPage setSelectedProject={setSelectedProject} />;
      case 'search': return <SearchPage searchTerm={searchTerm} setSelectedProject={setSelectedProject} />;
      case 'profile': return user ? <ProfilePage user={user} /> : <HomePage setCurrentPage={setCurrentPage} setSelectedProject={setSelectedProject} />;
      case 'about': return <AboutPage />;
      case 'contact': return <ContactPage />;
      case 'terms': return <TermsPage />;
      case 'help': return <HelpPage />;
      default: return <HomePage setCurrentPage={setCurrentPage} setSelectedProject={setSelectedProject} />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Header currentPage={currentPage} setCurrentPage={setCurrentPage} user={user} setUser={setUser} setShowLogin={setShowLogin} setSelectedProject={setSelectedProject} searchTerm={searchTerm} setSearchTerm={setSearchTerm} />
      {selectedProject && (<div className="bg-white border-b"><div className="container mx-auto px-4 py-2"><button className="text-green-600 hover:text-green-700 flex items-center" onClick={()=>setSelectedProject(null)}>← Quay lại</button></div></div>)}
      <main className="min-h-screen">{renderPage()}</main>
      <Footer setCurrentPage={setCurrentPage} setSelectedProject={setSelectedProject} />
      <LoginModal showLogin={showLogin} setShowLogin={setShowLogin} setUser={setUser} />
      <DonationModal showDonation={showDonation} setShowDonation={setShowDonation} project={selectedProject} setShowThankYou={setShowThankYou} setDonationData={setDonationData} />
      <ThankYouModal showThankYou={showThankYou} setShowThankYou={setShowThankYou} donationData={donationData} />
    </div>
  );
}
