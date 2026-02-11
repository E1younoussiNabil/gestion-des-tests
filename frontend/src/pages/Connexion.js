import React, { useEffect, useState } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useForm } from 'react-hook-form';
import { LogIn, Key, AlertCircle } from 'lucide-react';

const Connexion = () => {
  const { user, login, requestOtp, verifyOtp } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(false);
  const [step, setStep] = useState('email');
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [codeSession, setCodeSession] = useState('');
  
  const {
    handleSubmit,
    formState: { errors }
  } = useForm();

  const onSubmit = async () => {
    setLoading(true);
    try {
      if (step === 'email') {
        const r = await requestOtp(email);
        if (r.success) {
          navigate('/connexion?step=otp');
        }
        return;
      }

      if (step === 'otp') {
        const r = await verifyOtp(email, otp);
        if (r.success) {
          navigate('/connexion?step=portal');
        }
        return;
      }

      if (step === 'codeSession') {
        const r = await login(codeSession);
        if (r.success) {
          navigate('/test');
        }
      }
    } catch (error) {
      console.error('Login error:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const params = new URLSearchParams(location.search || '');
    const stepParam = (params.get('step') || '').trim();

    if (user && user?.email) {
      setEmail(user.email);
    }

    if (user) {
      if (!stepParam || stepParam === 'email' || stepParam === 'otp') {
        navigate('/connexion?step=portal', { replace: true });
        return;
      }
    }

    if (stepParam) {
      setStep(stepParam);
    }
  }, [location.search, navigate, user]);

  return (
    <div className="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8 bg-gray-50">
      <div className="max-w-md w-full space-y-8">
        <div>
          <div className="mx-auto h-12 w-12 flex items-center justify-center rounded-full bg-blue-100">
            <Key className="h-6 w-6 text-blue-600" />
          </div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Connexion
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Accédez à votre espace candidat (résultats) ou commencez un test
          </p>
        </div>
        
        <div className="bg-white shadow-lg rounded-lg p-8">
          <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
            {step === 'email' && (
              <div>
                <label htmlFor="email" className="label">
                  Email
                </label>
                <div className="relative">
                  <input
                    id="email"
                    type="email"
                    autoComplete="email"
                    className={`input ${errors.email ? 'border-red-500' : ''}`}
                    placeholder="Entrez votre email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>
              </div>
            )}

            {step === 'otp' && (
              <div>
                <label htmlFor="otp" className="label">
                  Code reçu par email
                </label>
                <div className="relative">
                  <input
                    id="otp"
                    type="text"
                    autoComplete="one-time-code"
                    className={`input ${errors.otp ? 'border-red-500' : ''}`}
                    placeholder="Entrez le code (6 chiffres)"
                    value={otp}
                    onChange={(e) => setOtp(e.target.value)}
                  />
                </div>
                <div className="mt-3 text-sm text-gray-600">
                  <button
                    type="button"
                    className="text-blue-600 hover:text-blue-500 font-medium"
                    onClick={async () => {
                      setLoading(true);
                      try {
                        await requestOtp(email);
                      } finally {
                        setLoading(false);
                      }
                    }}
                  >
                    Renvoyer le code
                  </button>
                </div>
              </div>
            )}

            {step === 'portal' && (
              <div className="space-y-4">
                <button
                  type="button"
                  className="btn btn-primary w-full"
                  onClick={() => navigate('/resultats')}
                >
                  Voir mon historique / résultats
                </button>
                <button
                  type="button"
                  className="btn btn-primary w-full"
                  onClick={() => navigate('/resultats', { state: { openNewTestModal: true } })}
                >
                  Demander un nouveau test
                </button>
                <button
                  type="button"
                  className="btn btn-secondary w-full"
                  onClick={() => navigate('/connexion?step=codeSession')}
                >
                  J'ai un code de session (passer un test)
                </button>
              </div>
            )}

            {step === 'codeSession' && (
              <div>
                <label htmlFor="codeSession" className="label">
                  Code de session
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Key className="h-5 w-5 text-gray-400" />
                  </div>
                  <input
                    id="codeSession"
                    type="text"
                    autoComplete="off"
                    className={`input pl-10 ${errors.codeSession ? 'border-red-500' : ''}`}
                    placeholder="Entrez votre code de session"
                    value={codeSession}
                    onChange={(e) => setCodeSession(e.target.value)}
                  />
                </div>
                <div className="mt-3 text-sm text-gray-600">
                  <button
                    type="button"
                    className="text-blue-600 hover:text-blue-500 font-medium"
                    onClick={() => navigate('/connexion?step=portal')}
                  >
                    Retour
                  </button>
                </div>
              </div>
            )}

            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <div className="flex items-start">
                <AlertCircle className="h-5 w-5 text-blue-600 mt-0.5 mr-2 flex-shrink-0" />
                <div className="text-sm text-blue-800">
                  <p className="font-medium mb-1">Où trouver votre code de session ?</p>
                  <p> Votre code de session vous a été envoyé par email après votre inscription. 
                    Si vous ne l'avez pas reçu, vérifiez votre dossier spam ou contactez-nous.</p>
                </div>
              </div>
            </div>

            <div>
              <button
                type="submit"
                disabled={loading || step === 'portal'}
                className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <span className="absolute left-0 inset-y-0 flex items-center pl-3">
                  <LogIn className="h-5 w-5 text-blue-500 group-hover:text-blue-400" />
                </span>
                {loading ? 'Chargement...' : (step === 'email' ? 'Recevoir un code' : step === 'otp' ? 'Vérifier le code' : 'Se connecter')}
              </button>
            </div>
          </form>
        </div>

        <div className="text-center">
          <Link
            to="/"
            className="text-sm text-gray-500 hover:text-gray-700"
          >
            ← Retour à l'accueil
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Connexion;
