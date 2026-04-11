import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import Header from './components/Header';
import Footer from './components/Footer';
import ProtectedRoute from './components/ProtectedRoute';

import HomePage from './pages/HomePage';
import ProjectsPage from './pages/ProjectsPage';
import ProjectDetailPage from './pages/ProjectDetailPage';
import CategoriesPage from './pages/CategoriesPage';
import SearchPage from './pages/SearchPage';
import RegisterPage from './pages/RegisterPage';
import ProfilePage from './pages/ProfilePage';
import ProjectUploadPage from './pages/ProjectUploadPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import PaymentResultPage from './pages/PaymentResultPage';
import AboutPage from './pages/AboutPage';
import ContactPage from './pages/ContactPage';
import TermsPage from './pages/TermsPage';
import HelpPage from './pages/HelpPage';
import NotFoundPage from './pages/NotFoundPage';

function AppLayout({ children }) {
    return (
        <div className="min-h-screen bg-gray-50 flex flex-col">
            <Header />
            <main className="flex-1">{children}</main>
            <Footer />
        </div>
    );
}

export default function App() {
    return (
        <AuthProvider>
            <Routes>
                {/* Register page - no header/footer */}
                <Route path="/register" element={<RegisterPage />} />

                {/* All other pages with layout */}
                <Route path="/" element={<AppLayout><HomePage /></AppLayout>} />
                <Route path="/projects" element={<AppLayout><ProjectsPage /></AppLayout>} />
                <Route path="/projects/:projectId" element={<AppLayout><ProjectDetailPage /></AppLayout>} />
                <Route path="/categories" element={<AppLayout><CategoriesPage /></AppLayout>} />
                <Route path="/search" element={<AppLayout><SearchPage /></AppLayout>} />
                <Route path="/about" element={<AppLayout><AboutPage /></AppLayout>} />
                <Route path="/contact" element={<AppLayout><ContactPage /></AppLayout>} />
                <Route path="/terms" element={<AppLayout><TermsPage /></AppLayout>} />
                <Route path="/help" element={<AppLayout><HelpPage /></AppLayout>} />
                <Route path="/payment/result" element={<AppLayout><PaymentResultPage /></AppLayout>} />

                {/* Protected routes */}
                <Route
                    path="/profile"
                    element={
                        <AppLayout>
                            <ProtectedRoute>
                                <ProfilePage />
                            </ProtectedRoute>
                        </AppLayout>
                    }
                />
                <Route
                    path="/projects/new"
                    element={
                        <AppLayout>
                            <ProtectedRoute roles={['ORGANIZATION']}>
                                <ProjectUploadPage />
                            </ProtectedRoute>
                        </AppLayout>
                    }
                />
                <Route
                    path="/admin"
                    element={
                        <AppLayout>
                            <ProtectedRoute roles={['ADMIN']}>
                                <AdminDashboardPage />
                            </ProtectedRoute>
                        </AppLayout>
                    }
                />

                {/* 404 */}
                <Route path="*" element={<AppLayout><NotFoundPage /></AppLayout>} />
            </Routes>
        </AuthProvider>
    );
}
