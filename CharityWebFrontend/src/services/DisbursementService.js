import api from './api';

export const createDisbursement = (projectId, data) =>
    api.post(`/projects/${projectId}/disbursements`, data);

export const getProjectDisbursements = (projectId) =>
    api.get(`/projects/${projectId}/disbursements`);

export const getDisbursementById = (disbursementId) =>
    api.get(`/disbursements/${disbursementId}`);
