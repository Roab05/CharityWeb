import api from './api';

export const getProjectCategories = (params) => {
    return api.get('/projects/categories', { params: params });
};
