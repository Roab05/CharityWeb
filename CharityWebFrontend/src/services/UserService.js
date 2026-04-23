import api from './api';

export const getMyProfile = () => api.get('/users/me');

export const updateMyProfile = (data) => api.put('/users/me', data);

export const getMyDonations = () => api.get('/users/me/donations');

export const getMyTransactions = () => api.get('/users/me/transactions');

export const changePassword = (data) => api.post('/users/change-password', data);
