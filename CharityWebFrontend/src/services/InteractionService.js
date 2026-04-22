import api from './api';

export const createInteraction = (activityId, data) =>
    api.post(`/activities/${activityId}/interactions`, data);

export const getActivityInteractions = (activityId) =>
    api.get(`/activities/${activityId}/interactions`);

export const deleteInteraction = (interactionId) =>
    api.delete(`/interactions/${interactionId}`);
