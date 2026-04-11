import api from './api';

export const updateActivityStatus = (activityId, data) =>
    api.put(`/activities/${activityId}/status`, data);

export const updateActivity = (activityId, data) =>
    api.put(`/activities/${activityId}`, data);
