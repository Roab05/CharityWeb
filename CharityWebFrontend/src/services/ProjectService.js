import api from './api';

export const createProject = (data) => api.post('/projects', data);

export const getProjects = (params) => api.get('/projects', { params });

export const getProjectCategories = () => api.get('/projects/categories');

export const getProjectById = (projectId) => api.get(`/projects/${projectId}`);

export const getOrganizationsForSelector = (name) => api.get('/projects/organizations', { params: { name } });

export const addOrganizationToProject = (projectId, data) => api.post(`/projects/${projectId}/organizations`, data);

export const createActivity = (projectId, data) => api.post(`/projects/${projectId}/activities`, data);

export const getProjectActivities = (projectId) => api.get(`/projects/${projectId}/activities`);

export const getProjectDonations = (projectId) => api.get(`/projects/${projectId}/donations`);

export const createDisbursement = (projectId, data) => api.post(`/projects/${projectId}/disbursements`, data);

export const getProjectDisbursements = (projectId) => api.get(`/projects/${projectId}/disbursements`);
