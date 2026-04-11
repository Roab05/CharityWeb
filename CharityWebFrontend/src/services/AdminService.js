import api from './api';

export const getStatistics = () => api.get('/admin/statistics');

export const getPendingOrganizations = () => api.get('/admin/organizations/pending');

export const verifyOrganization = (orgId, data) =>
    api.put(`/admin/organizations/${orgId}/verify`, data);

export const getPendingProjects = () => api.get('/admin/projects/pending');

export const approveProject = (projectId, data) =>
    api.put(`/admin/projects/${projectId}/approve`, data);

export const updateUserStatus = (userId, data) =>
    api.put(`/admin/users/${userId}/status`, data);

export const createCategory = (data) => api.post('/admin/categories', data);

export const updateCategory = (categoryId, data) =>
    api.put(`/admin/categories/${categoryId}`, data);

export const getPendingDisbursements = () => api.get('/admin/disbursements/pending');

export const updateDisbursementStatus = (disbursementId, data) =>
    api.put(`/admin/disbursements/${disbursementId}/status`, data);
