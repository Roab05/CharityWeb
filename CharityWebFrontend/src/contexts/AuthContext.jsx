import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    const fetchProfile = useCallback(async () => {
        try {
            const token = localStorage.getItem('accessToken');
            if (!token) {
                setLoading(false);
                return;
            }
            const res = await api.get('/users/me');
            setUser(res.data);
        } catch {
            localStorage.removeItem('accessToken');
            setUser(null);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchProfile();
    }, [fetchProfile]);

    const login = async (username, password) => {
        const res = await api.post('/auth/login', { username, password });
        localStorage.setItem('accessToken', res.data.accessToken);
        await fetchProfile();
        return res.data;
    };

    const registerIndividual = async (data) => {
        return api.post('/auth/register/individual', data);
    };

    const registerOrganization = async (data) => {
        return api.post('/auth/register/organization', data);
    };

    const logout = async () => {
        try {
            await api.post('/auth/logout');
        } catch { /* ignore */ }
        localStorage.removeItem('accessToken');
        setUser(null);
    };

    const refreshProfile = () => fetchProfile();

    return (
        <AuthContext.Provider value={{
            user, loading, login,
            registerIndividual, registerOrganization,
            logout, refreshProfile
        }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);
