import api from './api';

export const getProjectCategories = () => api.get('/projects/categories');
