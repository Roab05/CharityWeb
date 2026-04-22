import api from './api';

const toCategoryId = (name = '') => {
    const base = name
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLowerCase()
        .trim()
        .replace(/[^a-z0-9\s-]/g, '')
        .replace(/\s+/g, '-');

    return base || `category-${Date.now()}`;
};

export const getStatistics = () => api.get('/admin/statistics');

export const getPendingOrganizations = () => api.get('/admin/organizations/pending');

export const verifyOrganization = (orgId, data) =>
    api.put(`/admin/organizations/${orgId}/verify`, data);

export const getPendingProjects = () => api.get('/admin/projects/pending');

export const approveProject = (projectId, data) =>
    api.put(`/admin/projects/${projectId}/approve`, data);

export const updateUserStatus = (userId, data) =>
    api.put(`/admin/users/${userId}/status`, data);

export const createCategory = (data) => {
    const payload = {
        ...data,
        id: data?.id?.trim() || toCategoryId(data?.categoryName),
    };
    return api.post('/admin/categories', payload);
};

export const updateCategory = (categoryId, data) =>
    api.put(`/admin/categories/${categoryId}`, data);

export const getPendingDisbursements = () => api.get('/admin/disbursements/pending');

export const updateDisbursementStatus = (disbursementId, data) =>
    api.put(`/admin/disbursements/${disbursementId}/status`, data);

export const getCategories = () => api.get('/projects/categories');
