import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { User, LogOut, BookOpen } from 'lucide-react';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="bg-white shadow-lg">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center">
            <Link to={user?.role === 'admin' ? '/admin/candidats' : '/'} className="flex items-center space-x-2">
              <BookOpen className="h-8 w-8 text-blue-600" />
              <span className="text-xl font-bold text-gray-900">Gestion Tests</span>
            </Link>
          </div>

          <div className="hidden md:flex items-center space-x-8">
            <Link
              to="/classement"
              className={`text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                isActive('/classement') ? 'text-blue-600 bg-blue-50' : ''
              }`}
            >
              Classement
            </Link>
            
            {user && (
              <>
                {user.role === 'admin' ? (
                  <>
                    <Link
                      to="/admin/candidats"
                      className={`text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                        location.pathname.startsWith('/admin') ? 'text-blue-600 bg-blue-50' : ''
                      }`}
                    >
                      Admin
                    </Link>
                  </>
                ) : (
                  <>
                    <button
                      type="button"
                      onClick={() => navigate('/resultats', { state: { openNewTestModal: true } })}
                      className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium transition-colors"
                    >
                      Demander un nouveau test
                    </button>
                    <Link
                      to="/resultats"
                      className={`text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                        isActive('/resultats') ? 'text-blue-600 bg-blue-50' : ''
                      }`}
                    >
                      Résultats
                    </Link>
                  </>
                )}
              </>
            )}
          </div>

          <div className="flex items-center space-x-4">
            {user ? (
              <div className="flex items-center space-x-4">
                <div className="flex items-center space-x-2">
                  <User className="h-5 w-5 text-gray-600" />
                  <span className="text-sm font-medium text-gray-700">
                    {user.prenom} {user.nom}
                  </span>
                </div>
                <button
                  onClick={handleLogout}
                  className="flex items-center space-x-2 text-gray-600 hover:text-red-600 transition-colors"
                >
                  <LogOut className="h-5 w-5" />
                  <span className="text-sm">Déconnexion</span>
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-4">
                <Link
                  to="/inscription"
                  className="btn btn-secondary"
                >
                  Inscription
                </Link>
                <Link
                  to="/connexion"
                  className="btn btn-primary"
                >
                  Connexion
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
