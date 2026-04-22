import api from './api';

const ChatbotService = {
    ask(question, projectId = null, conversationId = null) {
        return api.post('/chatbot/ask', {
            question,
            projectId,
            conversationId,
            locale: 'vi-VN',
        });
    },

    getSuggestions(projectId = null) {
        const params = projectId ? { projectId } : {};
        return api.get('/chatbot/suggestions', { params });
    },
};

export default ChatbotService;
