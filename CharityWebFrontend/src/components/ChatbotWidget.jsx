import React, { useState, useRef, useEffect, useCallback } from 'react';
import ChatbotService from '../services/ChatbotService';

// --- Icons ---
const ChatIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
    </svg>
);

const CloseIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
    </svg>
);

const SendIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
    </svg>
);

const MinimizeIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M18 12H6" />
    </svg>
);

const BotAvatar = () => (
    <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center flex-shrink-0">
        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M9.75 3.104v5.714a2.25 2.25 0 01-.659 1.591L5 14.5M9.75 3.104c-.251.023-.501.05-.75.082m.75-.082a24.301 24.301 0 014.5 0m0 0v5.714a2.25 2.25 0 00.659 1.591L19 14.5M14.25 3.104c.251.023.501.05.75.082M19 14.5l-2.47 2.47a2.25 2.25 0 01-1.59.659H9.06a2.25 2.25 0 01-1.59-.659L5 14.5m14 0V17a2.25 2.25 0 01-2.25 2.25H7.25A2.25 2.25 0 015 17v-2.5" />
        </svg>
    </div>
);

// --- Typing indicator ---
function TypingIndicator() {
    return (
        <div className="flex items-start gap-2.5 px-4 py-2">
            <BotAvatar />
            <div className="bg-gray-100 rounded-2xl rounded-tl-sm px-4 py-3">
                <div className="flex gap-1">
                    <span className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0ms' }} />
                    <span className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '150ms' }} />
                    <span className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '300ms' }} />
                </div>
            </div>
        </div>
    );
}

// --- Message bubble ---
function MessageBubble({ message }) {
    const isUser = message.role === 'user';

    return (
        <div className={`flex items-end gap-2.5 px-4 py-1 ${isUser ? 'justify-end' : 'justify-start'}`}>
            {!isUser && <BotAvatar />}
            <div
                className={`max-w-[80%] px-4 py-2.5 text-sm leading-relaxed whitespace-pre-wrap break-words ${
                    isUser
                        ? 'bg-primary-600 text-white rounded-2xl rounded-br-sm'
                        : 'bg-gray-100 text-gray-800 rounded-2xl rounded-tl-sm'
                }`}
            >
                {message.content}
            </div>
        </div>
    );
}

// --- Suggestion chips ---
function SuggestionChips({ suggestions, onSelect, disabled }) {
    if (!suggestions || suggestions.length === 0) return null;

    return (
        <div className="px-4 py-2 flex flex-wrap gap-2">
            {suggestions.map((text, i) => (
                <button
                    key={i}
                    onClick={() => onSelect(text)}
                    disabled={disabled}
                    className="text-xs bg-primary-50 text-primary-700 border border-primary-200 rounded-full px-3 py-1.5 hover:bg-primary-100 transition-colors disabled:opacity-50 disabled:cursor-not-allowed text-left"
                >
                    {text}
                </button>
            ))}
        </div>
    );
}

// --- Main Chatbot Widget ---
export default function ChatbotWidget() {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);
    const [conversationId, setConversationId] = useState(null);
    const [suggestions, setSuggestions] = useState([]);
    const [hasGreeted, setHasGreeted] = useState(false);
    const [error, setError] = useState(null);

    const messagesEndRef = useRef(null);
    const inputRef = useRef(null);

    const scrollToBottom = useCallback(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, []);

    useEffect(() => {
        scrollToBottom();
    }, [messages, loading, scrollToBottom]);

    // Greet on first open
    useEffect(() => {
        if (isOpen && !hasGreeted) {
            setHasGreeted(true);
            setMessages([
                {
                    role: 'bot',
                    content: 'Xin chào! 👋 Tôi là trợ lý ảo của CharityWeb. Tôi có thể giúp bạn tìm hiểu về các dự án từ thiện, cách quyên góp, hoặc bất kỳ thắc mắc nào. Hãy hỏi tôi nhé!',
                },
            ]);
            loadSuggestions();
        }
        if (isOpen) {
            setTimeout(() => inputRef.current?.focus(), 100);
        }
    }, [isOpen, hasGreeted]);

    const loadSuggestions = async () => {
        try {
            const res = await ChatbotService.getSuggestions();
            setSuggestions(res.data.items || []);
        } catch {
            // silently ignore
        }
    };

    const sendMessage = async (text) => {
        const question = text.trim();
        if (!question || loading) return;

        setError(null);
        setInput('');
        setSuggestions([]);

        const userMsg = { role: 'user', content: question };
        setMessages((prev) => [...prev, userMsg]);
        setLoading(true);

        try {
            const res = await ChatbotService.ask(question, null, conversationId);
            const data = res.data;

            setConversationId(data.conversationId);

            const botMsg = { role: 'bot', content: data.answer };
            setMessages((prev) => [...prev, botMsg]);
        } catch (err) {
            const status = err.response?.status;
            let errMsg;
            if (status === 429) {
                errMsg = 'Bạn đã gửi quá nhiều tin nhắn. Vui lòng đợi một lát rồi thử lại.';
            } else if (status === 503) {
                errMsg = 'Dịch vụ chatbot đang tạm thời không khả dụng. Vui lòng thử lại sau.';
            } else {
                errMsg = 'Đã xảy ra lỗi. Vui lòng thử lại.';
            }
            setError(errMsg);
            setMessages((prev) => [
                ...prev,
                { role: 'bot', content: errMsg, isError: true },
            ]);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        sendMessage(input);
    };

    const handleSuggestionClick = (text) => {
        sendMessage(text);
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage(input);
        }
    };

    return (
        <>
            {/* Floating button */}
            {!isOpen && (
                <button
                    onClick={() => setIsOpen(true)}
                    className="fixed bottom-6 right-6 z-50 w-14 h-14 bg-primary-600 hover:bg-primary-700 text-white rounded-full shadow-lg hover:shadow-xl flex items-center justify-center transition-all duration-300 hover:scale-105 group"
                    aria-label="Mở chatbot"
                >
                    <ChatIcon />
                    {/* Pulse ring */}
                    <span className="absolute inset-0 rounded-full bg-primary-400 opacity-30 animate-ping" />
                </button>
            )}

            {/* Chat window */}
            {isOpen && (
                <div className="fixed bottom-6 right-6 z-50 w-[380px] max-w-[calc(100vw-2rem)] h-[560px] max-h-[calc(100vh-3rem)] bg-white rounded-2xl shadow-2xl border border-gray-200 flex flex-col overflow-hidden animate-slideUp">
                    {/* Header */}
                    <div className="bg-gradient-to-r from-primary-600 to-primary-700 text-white px-5 py-4 flex items-center justify-between flex-shrink-0">
                        <div className="flex items-center gap-3">
                            <div className="w-9 h-9 bg-white/20 rounded-full flex items-center justify-center">
                                <ChatIcon />
                            </div>
                            <div>
                                <h3 className="font-semibold text-sm">Trợ lý CharityWeb</h3>
                                <p className="text-xs text-primary-100">Luôn sẵn sàng hỗ trợ bạn</p>
                            </div>
                        </div>
                        <div className="flex items-center gap-1">
                            <button
                                onClick={() => setIsOpen(false)}
                                className="p-1.5 hover:bg-white/20 rounded-lg transition-colors"
                                aria-label="Thu nhỏ"
                            >
                                <MinimizeIcon />
                            </button>
                            <button
                                onClick={() => {
                                    setIsOpen(false);
                                    setMessages([]);
                                    setConversationId(null);
                                    setSuggestions([]);
                                    setHasGreeted(false);
                                    setError(null);
                                }}
                                className="p-1.5 hover:bg-white/20 rounded-lg transition-colors"
                                aria-label="Đóng"
                            >
                                <CloseIcon />
                            </button>
                        </div>
                    </div>

                    {/* Messages area */}
                    <div className="flex-1 overflow-y-auto py-3 space-y-1 scroll-smooth" style={{ scrollbarWidth: 'thin' }}>
                        {messages.map((msg, idx) => (
                            <MessageBubble key={idx} message={msg} />
                        ))}
                        {loading && <TypingIndicator />}
                        <div ref={messagesEndRef} />
                    </div>

                    {/* Suggestion chips */}
                    {!loading && suggestions.length > 0 && (
                        <SuggestionChips
                            suggestions={suggestions}
                            onSelect={handleSuggestionClick}
                            disabled={loading}
                        />
                    )}

                    {/* Input area */}
                    <form
                        onSubmit={handleSubmit}
                        className="flex items-center gap-2 px-4 py-3 border-t border-gray-100 bg-gray-50 flex-shrink-0"
                    >
                        <input
                            ref={inputRef}
                            type="text"
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={handleKeyDown}
                            placeholder="Nhập câu hỏi của bạn..."
                            disabled={loading}
                            maxLength={1000}
                            className="flex-1 text-sm bg-white border border-gray-200 rounded-xl px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:border-transparent disabled:opacity-50 transition-all"
                        />
                        <button
                            type="submit"
                            disabled={loading || !input.trim()}
                            className="p-2.5 bg-primary-600 text-white rounded-xl hover:bg-primary-700 disabled:opacity-40 disabled:cursor-not-allowed transition-all flex-shrink-0"
                            aria-label="Gửi"
                        >
                            <SendIcon />
                        </button>
                    </form>
                </div>
            )}
        </>
    );
}
