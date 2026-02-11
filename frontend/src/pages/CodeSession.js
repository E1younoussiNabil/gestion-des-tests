import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { Search, ArrowLeft, Key, Clock } from 'lucide-react';

const CodeSession = () => {
  const [loading, setLoading] = useState(false);
  const [codeSession, setCodeSession] = useState(null);
  const [candidatInfo, setCandidatInfo] = useState(null);
  
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm();

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      const response = await fetch('/api/candidats/search?term=' + encodeURIComponent(data.email));
      
      if (response.ok) {
        const result = await response.json();
        const candidats = result.candidats;
        
        // Chercher le candidat par email exact
        const candidat = candidats.find(c => c.email.toLowerCase() === data.email.toLowerCase());
        
        if (candidat) {
          if (candidat.estValide && candidat.codeSession) {
            setCodeSession(candidat.codeSession);
            setCandidatInfo(candidat);
            toast.success('Code de session trouvé !');
          } else {
            // Candidat trouvé mais pas encore validé
            setCandidatInfo(candidat);
            toast.error('Votre inscription est en attente de validation par l\'administrateur.', {
              duration: 6000,
              style: {
                background: '#F59E0B',
                color: 'white'
              }
            });
          }
        } else {
          toast.error('Aucun candidat trouvé avec cet email');
        }
      } else {
        toast.error('Erreur lors de la recherche');
      }
    } catch (error) {
      console.error('Error:', error);
      toast.error('Erreur de connexion au serveur');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setCodeSession(null);
    setCandidatInfo(null);
  };

  return (
    <div className="min-h-screen py-12">
      <div className="container mx-auto px-4 max-w-2xl">
        <div className="mb-8">
          <Link to="/" className="inline-flex items-center text-blue-600 hover:text-blue-800 mb-4">
            <ArrowLeft className="h-4 w-4 mr-2" />
            Retour à l'accueil
          </Link>
          
          <div className="text-center">
            <div className="inline-flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4">
              <Key className="h-8 w-8 text-blue-600" />
            </div>
            <h1 className="text-3xl font-bold text-gray-900 mb-4">
              Récupérer votre Code de Session
            </h1>
            <p className="text-gray-600">
              Entrez votre email pour retrouver votre code de session
            </p>
          </div>
        </div>

        {!codeSession && candidatInfo ? (
          <div className="bg-white rounded-lg shadow-lg p-8">
            <div className="text-center">
              <div className="inline-flex items-center justify-center w-20 h-20 bg-yellow-100 rounded-full mb-6">
                <Clock className="h-10 w-10 text-yellow-600" />
              </div>
              
              <h2 className="text-2xl font-bold text-gray-900 mb-4">
                Inscription en Attente de Validation
              </h2>
              
              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6 mb-6">
                <p className="text-lg text-gray-700 mb-4">
                  Bonjour <strong>{candidatInfo.prenom} {candidatInfo.nom}</strong>,
                </p>
                <p className="text-gray-600 mb-4">
                  Votre inscription a bien été enregistrée mais elle est en attente de validation par l'administrateur.
                </p>
                <div className="bg-white rounded-lg p-4">
                  <p className="text-sm text-gray-600">
                    <strong>Informations :</strong><br/>
                    • École : {candidatInfo.ecole}<br/>
                    • Email : {candidatInfo.email}<br/>
                    • Statut : <span className="inline-block px-2 py-1 bg-yellow-100 text-yellow-800 text-xs font-medium rounded">En attente de validation</span>
                  </p>
                </div>
              </div>
              
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
                <p className="text-sm text-blue-800">
                  <strong>Prochaines étapes :</strong><br/>
                  1. L'administrateur va valider votre inscription<br/>
                  2. Vous recevrez un email avec votre code de session<br/>
                  3. Revenez sur cette page pour récupérer votre code
                </p>
              </div>
              
              <div className="flex flex-col sm:flex-row gap-4">
                <button
                  onClick={resetForm}
                  className="btn btn-primary flex-1"
                >
                  Rechercher un autre email
                </button>
                <Link
                  to="/"
                  className="btn btn-secondary flex-1 text-center"
                >
                  Retour à l'accueil
                </Link>
              </div>
            </div>
          </div>
        ) : !codeSession ? (
          <div className="bg-white rounded-lg shadow-lg p-8">
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
              <div>
                <label className="label">Email *</label>
                <input
                  type="email"
                  className={`input ${errors.email ? 'border-red-500' : ''}`}
                  placeholder="votre.email@example.com"
                  {...register('email', { 
                    required: 'L\'email est obligatoire',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Email invalide'
                    }
                  })}
                />
                {errors.email && (
                  <p className="error-message">{errors.email.message}</p>
                )}
              </div>

              <button
                type="submit"
                disabled={loading}
                className="btn btn-primary w-full disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
              >
                {loading ? (
                  <>
                    <div className="loading mr-2"></div>
                    Recherche en cours...
                  </>
                ) : (
                  <>
                    <Search className="h-4 w-4 mr-2" />
                    Rechercher mon code
                  </>
                )}
              </button>
            </form>
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow-lg p-8">
            <div className="text-center">
              <div className="inline-flex items-center justify-center w-20 h-20 bg-green-100 rounded-full mb-6">
                <Key className="h-10 w-10 text-green-600" />
              </div>
              
              <h2 className="text-2xl font-bold text-gray-900 mb-4">
                Code de Session Trouvé !
              </h2>
              
              <div className="bg-gray-50 border-2 border-gray-200 rounded-lg p-6 mb-6">
                <p className="text-sm text-gray-600 mb-2">Votre code de session :</p>
                <div className="text-3xl font-mono font-bold text-blue-600 tracking-wider">
                  {codeSession}
                </div>
              </div>
              
              {candidatInfo && (
                <div className="text-left bg-blue-50 rounded-lg p-4 mb-6">
                  <h3 className="font-semibold text-gray-900 mb-2">Informations du candidat :</h3>
                  <div className="space-y-1 text-sm">
                    <p><span className="font-medium">Nom :</span> {candidatInfo.nom} {candidatInfo.prenom}</p>
                    <p><span className="font-medium">École :</span> {candidatInfo.ecole}</p>
                    <p><span className="font-medium">Email :</span> {candidatInfo.email}</p>
                    <p><span className="font-medium">Statut :</span> 
                      <span className={`ml-2 px-2 py-1 rounded text-xs font-medium ${
                        candidatInfo.estValide 
                          ? 'bg-green-100 text-green-800' 
                          : 'bg-yellow-100 text-yellow-800'
                      }`}>
                        {candidatInfo.estValide ? 'Validé' : 'En attente de validation'}
                      </span>
                    </p>
                  </div>
                </div>
              )}
              
              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-6">
                <p className="text-sm text-yellow-800">
                  <strong>Important :</strong> Conservez ce code précieusement. 
                  Il vous servira à vous identifier le jour du test.
                </p>
              </div>
              
              <div className="flex flex-col sm:flex-row gap-4">
                <button
                  onClick={() => navigator.clipboard.writeText(codeSession)}
                  className="btn btn-secondary flex-1"
                >
                  Copier le code
                </button>
                <button
                  onClick={resetForm}
                  className="btn btn-primary flex-1"
                >
                  Rechercher un autre code
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default CodeSession;
