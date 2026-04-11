import api from './api';

export const initiateDonation = (projectId, data) =>
    api.post(`/projects/${projectId}/donations`, data);

export const getProjectDonations = (projectId) =>
    api.get(`/projects/${projectId}/donations`);
