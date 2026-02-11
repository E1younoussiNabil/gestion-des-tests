import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { Calendar, Clock, Users, AlertCircle } from 'lucide-react';

const Inscription = () => {
  const navigate = useNavigate();
  const [creneaux, setCreneaux] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedCreneau, setSelectedCreneau] = useState(null);

  const [testMode, setTestMode] = useState('GENERAL');
  const [csThemes, setCsThemes] = useState([]);
  const [selectedCsThemeId, setSelectedCsThemeId] = useState('');
  
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm();

  useEffect(() => {
    fetchCreneaux();
  }, []);

  useEffect(() => {
    const fetchCsThemes = async () => {
      try {
        const resp = await fetch('/api/tests/themes-informatique');
        const data = await resp.json().catch(() => ({}));
        if (!resp.ok) {
          return;
        }
        setCsThemes(Array.isArray(data?.themes) ? data.themes : []);
      } catch (e) {
        // ignore
      }
    };
    fetchCsThemes();
  }, []);

  const fetchCreneaux = async () => {
    try {
      const response = await fetch('/api/creneaux/disponibles');
      if (response.ok) {
        const data = await response.json();
        setCreneaux(data.creneaux);
      }
    } catch (error) {
      console.error('Error fetching creneaux:', error);
      toast.error('Erreur lors du chargement des créneaux');
    }
  };

  const onSubmit = async (data) => {
    if (!selectedCreneau) {
      toast.error('Veuillez sélectionner un créneau horaire');
      return;
    }

    if (testMode === 'INFORMATIQUE' && !selectedCsThemeId) {
      toast.error('Veuillez choisir une spécialité Informatique');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch('/api/candidats/inscription', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          ...data,
          creneauId: selectedCreneau.id,
          modeTest: testMode,
          idThemeSpecialite: testMode === 'INFORMATIQUE' ? Number(selectedCsThemeId) : null
        }),
      });

      const result = await response.json();

      if (response.ok) {
        toast.success('Inscription enregistrée ! Votre inscription est en attente de validation par l\'administrateur.');
        // Reset form
        setSelectedCreneau(null);
        
        // Message informatif selon le statut de validation
        if (result.validationRequise) {
          toast('Vous recevrez un email avec votre code de session dès que votre inscription sera validée.', {
            duration: 8000,
            style: {
              background: '#F59E0B',
              color: 'white',
              fontWeight: 'bold'
            }
          });
        } else {
          // Cas où le candidat est déjà validé (rare)
          const codeSession = result.candidat.codeSession;
          toast.success(`Votre code de session : ${codeSession}`, {
            duration: 10000,
            style: {
              background: '#10B981',
              color: 'white',
              fontWeight: 'bold',
              fontSize: '16px'
            }
          });
          
          alert(`Inscription validée !\n\nVotre code de session est : ${codeSession}\n\nConservez ce code précieusement, il vous servira à passer le test.`);
        }

        setTimeout(() => {
          navigate('/');
        }, 1200);
      } else {
        toast.error(result.error || 'Erreur lors de l\'inscription');
      }
    } catch (error) {
      console.error('Inscription error:', error);
      toast.error('Erreur de connexion au serveur');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const formatTime = (timeString) => {
    return timeString.substring(0, 5);
  };

  return (
    <div className="min-h-screen py-12">
      <div className="container mx-auto px-4 max-w-4xl">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Inscription aux Tests
          </h1>
          <p className="text-xl text-gray-600">
            Remplissez le formulaire ci-dessous pour vous inscrire
          </p>
        </div>

        <div className="bg-white rounded-lg shadow-lg p-8">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <div className="border-b pb-6">
              <h2 className="text-2xl font-semibold text-gray-900 mb-6">
                Type de test
              </h2>

              <div className="grid md:grid-cols-2 gap-6">
                <div className="form-group">
                  <label className="label">Choix *</label>
                  <select
                    className="input"
                    value={testMode}
                    onChange={(e) => {
                      setTestMode(e.target.value);
                      setSelectedCsThemeId('');
                    }}
                  >
                    <option value="GENERAL">Culture générale</option>
                    <option value="INFORMATIQUE">Informatique (spécialité)</option>
                  </select>
                </div>

                {testMode === 'INFORMATIQUE' && (
                  <div className="form-group">
                    <label className="label">Branche Informatique *</label>
                    <select
                      className="input"
                      value={selectedCsThemeId}
                      onChange={(e) => setSelectedCsThemeId(e.target.value)}
                    >
                      <option value="">Choisir...</option>
                      {csThemes.map((t) => (
                        <option key={String(t?.id)} value={String(t?.id)}>
                          {t?.nom}
                        </option>
                      ))}
                    </select>
                  </div>
                )}
              </div>
            </div>

            {/* Informations personnelles */}
            <div className="border-b pb-6">
              <h2 className="text-2xl font-semibold text-gray-900 mb-6">
                Informations personnelles
              </h2>
              
              <div className="grid md:grid-cols-2 gap-6">
                <div className="form-group">
                  <label className="label">Nom *</label>
                  <input
                    type="text"
                    className={`input ${errors.nom ? 'border-red-500' : ''}`}
                    {...register('nom', { required: 'Le nom est obligatoire' })}
                  />
                  {errors.nom && (
                    <p className="error-message">{errors.nom.message}</p>
                  )}
                </div>

                <div className="form-group">
                  <label className="label">Prénom *</label>
                  <input
                    type="text"
                    className={`input ${errors.prenom ? 'border-red-500' : ''}`}
                    {...register('prenom', { required: 'Le prénom est obligatoire' })}
                  />
                  {errors.prenom && (
                    <p className="error-message">{errors.prenom.message}</p>
                  )}
                </div>

                <div className="form-group">
                  <label className="label">École *</label>
                  <input
                    type="text"
                    className={`input ${errors.ecole ? 'border-red-500' : ''}`}
                    {...register('ecole', { required: 'L\'école est obligatoire' })}
                  />
                  {errors.ecole && (
                    <p className="error-message">{errors.ecole.message}</p>
                  )}
                </div>

                <div className="form-group">
                  <label className="label">Filière</label>
                  <input
                    type="text"
                    className="input"
                    {...register('filiere')}
                  />
                </div>

                <div className="form-group">
                  <label className="label">Email *</label>
                  <input
                    type="email"
                    className={`input ${errors.email ? 'border-red-500' : ''}`}
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

                <div className="form-group">
                  <label className="label">Téléphone *</label>
                  <input
                    type="tel"
                    className={`input ${errors.gsm ? 'border-red-500' : ''}`}
                    {...register('gsm', { required: 'Le téléphone est obligatoire' })}
                  />
                  {errors.gsm && (
                    <p className="error-message">{errors.gsm.message}</p>
                  )}
                </div>
              </div>
            </div>

            {/* Sélection du créneau */}
            <div>
              <h2 className="text-2xl font-semibold text-gray-900 mb-6">
                Choix du créneau horaire
              </h2>

              {creneaux.length === 0 ? (
                <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                  <div className="flex items-center">
                    <AlertCircle className="h-5 w-5 text-yellow-600 mr-2" />
                    <p className="text-yellow-800">
                      Aucun créneau disponible pour le moment. Veuillez réessayer plus tard.
                    </p>
                  </div>
                </div>
              ) : (
                <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {creneaux.map((creneau) => (
                    <div
                      key={creneau.id}
                      className={`border rounded-lg p-4 cursor-pointer transition-all ${
                        selectedCreneau?.id === creneau.id
                          ? 'border-blue-500 bg-blue-50'
                          : 'border-gray-200 hover:border-gray-300'
                      }`}
                      onClick={() => setSelectedCreneau(creneau)}
                    >
                      <div className="flex items-center justify-between mb-2">
                        <Calendar className="h-5 w-5 text-gray-600" />
                        <span className={`text-sm px-2 py-1 rounded ${
                          creneau.placesDisponibles > 5 
                            ? 'bg-green-100 text-green-800'
                            : 'bg-yellow-100 text-yellow-800'
                        }`}>
                          {creneau.placesDisponibles} places
                        </span>
                      </div>
                      
                      <div className="space-y-2">
                        <p className="font-medium text-gray-900">
                          {formatDate(creneau.dateExam)}
                        </p>
                        <div className="flex items-center text-gray-600">
                          <Clock className="h-4 w-4 mr-1" />
                          <span className="text-sm">
                            {formatTime(creneau.heureDebut)} - {formatTime(creneau.heureFin)}
                          </span>
                        </div>
                        <div className="flex items-center text-gray-600">
                          <Users className="h-4 w-4 mr-1" />
                          <span className="text-sm">
                            Durée: {creneau.dureeMinutes} minutes
                          </span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}

              {selectedCreneau && (
                <div className="mt-4 p-4 bg-blue-50 border border-blue-200 rounded-lg">
                  <p className="text-blue-800">
                    <strong>Créneau sélectionné :</strong> {formatDate(selectedCreneau.dateExam)} 
                    de {formatTime(selectedCreneau.heureDebut)} à {formatTime(selectedCreneau.heureFin)}
                  </p>
                </div>
              )}
            </div>

            <div className="flex justify-end space-x-4 pt-6">
              <Link
                to="/"
                className="btn btn-secondary"
              >
                Annuler
              </Link>
              <button
                type="submit"
                disabled={loading || creneaux.length === 0}
                className="btn btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? 'Inscription en cours...' : 'S\'inscrire'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Inscription;
