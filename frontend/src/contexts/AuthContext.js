import React, { createContext, useContext, useState, useEffect } from 'react';
import toast from 'react-hot-toast';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Clear legacy persistent auth (localStorage) so users are not kept logged in after closing the browser.
    // New behavior: auth is stored in sessionStorage only.
    try {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    } catch (e) {
      // ignore
    }

    const userData = sessionStorage.getItem('user');
    const token = sessionStorage.getItem('token');
    
    if (userData) {
      try {
        const parsedUser = JSON.parse(userData);
        if (parsedUser?.role === 'admin' && (!token || token.trim() === '')) {
          sessionStorage.removeItem('user');
          sessionStorage.removeItem('token');
          setUser(null);
        } else {
          setUser(parsedUser);
        }
      } catch (error) {
        console.error('Error parsing user data:', error);
        sessionStorage.removeItem('user');
        sessionStorage.removeItem('token');
      }
    }

    if (!window.__adminJwtFetchPatched) {
      const originalFetch = window.fetch.bind(window);
      window.__adminJwtFetchPatched = true;
      window.fetch = async (input, init = {}) => {
        try {
          const url = typeof input === 'string' ? input : (input && input.url ? input.url : '');
          const isAdminApi = typeof url === 'string' && url.startsWith('/api/admin') && !url.startsWith('/api/admin/login');

          if (isAdminApi) {
            const t = sessionStorage.getItem('token');
            if (t) {
              const headers = new Headers(init.headers || {});
              headers.set('Authorization', `Bearer ${t}`);
              init = { ...init, headers };
            }
          }

          const response = await originalFetch(input, init);
          if (isAdminApi && response && response.status === 401) {
            sessionStorage.removeItem('token');
            sessionStorage.removeItem('user');
            setUser(null);
          }
          return response;
        } catch (e) {
          return originalFetch(input, init);
        }
      };
    }

    setLoading(false);
  }, []);

  const requestOtp = async (email) => {
    try {
      const response = await fetch('/api/candidats/otp/request', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email }),
      });

      const data = await response.json().catch(() => ({}));
      if (response.ok) {
        toast.success('Code envoyé par email');
        return { success: true };
      }

      const errorMessage = data.error || 'Erreur lors de l’envoi du code';
      toast.error(errorMessage);
      return { success: false, error: errorMessage };
    } catch (error) {
      console.error('OTP request error:', error);
      toast.error('Erreur de connexion au serveur');
      return { success: false, error: 'Erreur de connexion au serveur' };
    }
  };

  const verifyOtp = async (email, otp) => {
    try {
      const response = await fetch('/api/candidats/otp/verify', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, otp }),
      });

      const data = await response.json().catch(() => ({}));
      if (response.ok) {
        setUser(data.candidat);
        sessionStorage.setItem('user', JSON.stringify(data.candidat));
        toast.success('Connexion réussie!');
        return { success: true };
      }

      const errorMessage = data.error || 'Code incorrect';
      toast.error(errorMessage);
      return { success: false, error: errorMessage };
    } catch (error) {
      console.error('OTP verify error:', error);
      toast.error('Erreur de connexion au serveur');
      return { success: false, error: 'Erreur de connexion au serveur' };
    }
  };

  const login = async (codeSession) => {
    try {
      const response = await fetch('/api/candidats/connexion', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ codeSession }),
      });

      const contentType = response.headers.get('content-type') || '';
      let data = {};
      if (contentType.includes('application/json')) {
        data = await response.json();
      } else {
        const text = await response.text();
        console.error('Backend returned non-JSON response:', text);
        data = { error: text };
      }

      if (response.ok) {
        setUser(data.candidat);
        sessionStorage.setItem('user', JSON.stringify(data.candidat));
        toast.success('Connexion réussie!');
        return { success: true };
      } else {
        const errorMessage = data.error || `Erreur de connexion (${response.status})`;
        console.error('Login failed:', { status: response.status, data, errorMessage });
        toast.error(errorMessage);
        return { success: false, error: errorMessage };
      }
    } catch (error) {
      console.error('Login error:', error);
      toast.error('Erreur de connexion au serveur');
      return { success: false, error: 'Erreur de connexion au serveur' };
    }
  };

  const loginAdmin = async (username, password) => {
    try {
      const response = await fetch('/api/admin/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      const data = await response.json();

      if (response.ok) {
        const adminUser = { ...data.admin, role: 'admin' };
        setUser(adminUser);
        sessionStorage.setItem('user', JSON.stringify(adminUser));
        if (data.token) {
          sessionStorage.setItem('token', data.token);
        }
        toast.success('Connexion admin réussie!');
        return { success: true };
      } else {
        toast.error(data.error || 'Erreur de connexion admin');
        return { success: false, error: data.error };
      }
    } catch (error) {
      console.error('Admin login error:', error);
      toast.error('Erreur de connexion au serveur');
      return { success: false, error: 'Erreur de connexion au serveur' };
    }
  };

  const logout = () => {
    setUser(null);
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('user');
    toast.success('Déconnexion réussie');
  };

  const updateUser = (nextUser) => {
    setUser(nextUser);
    if (nextUser) {
      sessionStorage.setItem('user', JSON.stringify(nextUser));
    } else {
      sessionStorage.removeItem('user');
    }
  };

  const value = {
    user,
    login,
    requestOtp,
    verifyOtp,
    loginAdmin,
    updateUser,
    logout,
    loading,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
