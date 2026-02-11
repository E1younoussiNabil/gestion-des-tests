import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom';
import toast from 'react-hot-toast';
import { 
  BarChart3, 
  Trophy, 
  Clock, 
  CheckCircle, 
  XCircle, 
  BookOpen,
  TrendingUp,
  Calendar,
  Award
} from 'lucide-react';

const Resultats = () => {
  const { user, updateUser } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [sessions, setSessions] = useState([]);
  const [selectedSession, setSelectedSession] = useState(null);
  const [loading, setLoading] = useState(true);
  const [questionsDetails, setQuestionsDetails] = useState([]);

  const [showNewTestModal, setShowNewTestModal] = useState(false);
  const [creneauxDisponibles, setCreneauxDisponibles] = useState([]);
  const [selectedCreneauId, setSelectedCreneauId] = useState(null);
  const [testMode, setTestMode] = useState('GENERAL');
  const [csThemes, setCsThemes] = useState([]);
  const [selectedCsThemeId, setSelectedCsThemeId] = useState('');
  const [candidateForm, setCandidateForm] = useState({
    nom: '',
    prenom: '',
    email: '',
    gsm: '',
    ecole: '',
    filiere: ''
  });
  const [requestingNewTest, setRequestingNewTest] = useState(false);

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

  const ouvrirDemandeNouveauTest = async () => {
    try {
      const creneauxResp = await fetch('/api/creneaux/disponibles');
      if (!creneauxResp.ok) {
        toast.error('Impossible de charger les créneaux disponibles');
        return;
      }
      const creneauxData = await creneauxResp.json();
      const creneaux = Array.isArray(creneauxData?.creneaux) ? creneauxData.creneaux : [];
      if (creneaux.length === 0) {
        toast.error('Aucun créneau disponible pour le moment');
        return;
      }

      setCreneauxDisponibles(creneaux);
      setSelectedCreneauId(creneaux[0]?.id ?? null);
      setTestMode('GENERAL');
      setSelectedCsThemeId('');
      setCandidateForm({
        nom: user?.nom || '',
        prenom: user?.prenom || '',
        email: user?.email || '',
        gsm: user?.gsm || '',
        ecole: user?.ecole || '',
        filiere: user?.filiere || ''
      });
      setShowNewTestModal(true);
    } catch (e) {
      console.error('Error opening new test modal:', e);
      toast.error('Erreur de connexion au serveur');
    }
  };

  useEffect(() => {
    if (!user) {
      return;
    }
    if (location?.state?.openNewTestModal) {
      ouvrirDemandeNouveauTest();
      navigate(location.pathname, { replace: true, state: {} });
    }
  }, [location, navigate, user]);

  const soumettreDemandeNouveauTest = async () => {
    if (!selectedCreneauId) {
      toast.error('Veuillez sélectionner un créneau');
      return;
    }

    if (testMode === 'INFORMATIQUE' && !selectedCsThemeId) {
      toast.error('Veuillez choisir une spécialité Informatique');
      return;
    }

    setRequestingNewTest(true);
    try {
      const updateResp = await fetch(`/api/candidats/${user.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...user,
          ...candidateForm,
          email: user.email,
          id: user.id
        })
      });
      const updateData = await updateResp.json().catch(() => ({}));
      if (!updateResp.ok) {
        toast.error(updateData?.error || 'Erreur lors de la mise à jour du candidat');
        return;
      }
      if (updateData?.candidat) {
        updateUser(updateData.candidat);
      }

      const resp = await fetch('/api/candidats/demander-nouveau-test', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          candidatId: user.id,
          creneauId: selectedCreneauId,
          modeTest: testMode,
          idThemeSpecialite: testMode === 'INFORMATIQUE' ? Number(selectedCsThemeId) : null
        })
      });

      const data = await resp.json().catch(() => ({}));
      if (!resp.ok) {
        toast.error(data?.error || 'Erreur lors de la demande');
        return;
      }

      toast.success('Demande envoyée. Vous recevrez un email après validation admin.');
      setShowNewTestModal(false);
      navigate('/connexion');
    } catch (e) {
      console.error('Error requesting new test:', e);
      toast.error('Erreur de connexion au serveur');
    } finally {
      setRequestingNewTest(false);
    }
  };

  const downloadReport = async () => {
    if (!selectedSession?.id) {
      toast.error('Veuillez sélectionner une session');
      return;
    }

    try {
      const response = await fetch(`/api/resultats/session/${selectedSession.id}/rapport`);
      if (!response.ok) {
        let message = 'Erreur lors du téléchargement du rapport';
        try {
          const data = await response.json();
          message = data?.error || message;
        } catch (e) {
          // ignore
        }
        toast.error(message);
        return;
      }

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `rapport_session_${selectedSession.id}.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (error) {
      console.error('Error downloading report:', error);
      toast.error('Erreur de connexion au serveur');
    }
  };

  const fetchSessions = useCallback(async () => {
    try {
      const response = await fetch(`/api/resultats/candidat/${user.id}`);
      if (response.ok) {
        const data = await response.json();
        setSessions(data.sessions || []); 
        if (data.sessions && data.sessions.length > 0) {
          const mostRecent = data.sessions[0];
          await fetchSessionDetails(mostRecent.id);
        } else {
          setSelectedSession(null);
          setQuestionsDetails([]);
        }
      } else {
        const errorData = await response.json();
        toast.error(errorData.error || 'Erreur lors du chargement des résultats');
      }
    } catch (error) {
      console.error('Error fetching sessions:', error);
      toast.error('Erreur de connexion au serveur');
    } finally {
      setLoading(false);
    }
  }, [user.id]);

  useEffect(() => {
    if (!user) {
      navigate('/connexion');
      return;
    }
    // Forcer le rechargement des résultats à chaque visite de la page
    fetchSessions();
  }, [user, navigate, fetchSessions]);

  // Ajouter un écouteur d'événements pour recharger les résultats
  useEffect(() => {
    const handleRefresh = () => {
      fetchSessions();
    };

    // Écouter les événements de rafraîchissement personnalisés
    window.addEventListener('refreshResults', handleRefresh);
    
    // Rafraîchir automatiquement après 2 secondes pour être sûr
    const autoRefresh = setTimeout(() => {
      fetchSessions();
    }, 2000);
    
    return () => {
      window.removeEventListener('refreshResults', handleRefresh);
      clearTimeout(autoRefresh);
    };
  }, [fetchSessions]);

  const fetchSessionDetails = async (sessionId) => {
    try {
      const [sessionResp, detailsResp, rankResp] = await Promise.all([
        fetch(`/api/resultats/session/${sessionId}`),
        fetch(`/api/resultats/session/${sessionId}/details`),
        fetch(`/api/resultats/classement/candidat/${user.id}`)
      ]);

      if (!sessionResp.ok) {
        throw new Error('Erreur lors du chargement de la session');
      }

      const sessionData = await sessionResp.json();
      const detailsData = detailsResp.ok ? await detailsResp.json() : null;
      const rankData = rankResp.ok ? await rankResp.json() : null;

      const session = sessionData?.session || sessionData;
      const score = sessionData?.score || {};
      const reponses = Array.isArray(sessionData?.reponses) ? sessionData.reponses : [];

      const details = detailsData?.details || {};
      const statsParTheme = details?.statsParTheme || {};
      const qDetails = Array.isArray(detailsData?.questionsDetails) ? detailsData.questionsDetails : [];
      setQuestionsDetails(qDetails);

      const questionsCorrectes = qDetails.length > 0
        ? qDetails.filter(q => q?.estCorrect === true).length
        : 0;
      const questionsIncorrectes = qDetails.length > 0
        ? qDetails.filter(q => q?.estCorrect === false).length
        : 0;

      const dureeSeconds = reponses.reduce((sum, r) => sum + (Number(r?.tempsReponse) || 0), 0);
      const dureeTotale = dureeSeconds > 0
        ? `${Math.floor(dureeSeconds / 60)} min ${dureeSeconds % 60} s`
        : 'N/A';

      const rang = rankData?.rang;
      const totalClassement = rankData?.total;
      const classementStr = rang != null && totalClassement != null
        ? `${rang}/${totalClassement}`
        : 'N/A';

      const resultatsParTheme = Object.entries(statsParTheme).map(([nomTheme, stat]) => ({
        themeId: nomTheme,
        nomTheme,
        pourcentage: Number(stat?.pourcentage) || 0,
        scoreObtenu: Number(stat?.correctes) || 0,
        scoreMaximum: Number(stat?.total) || 0
      }));

      setSelectedSession({
        ...(session || {}),
        scoreTotal: score?.total ?? session?.scoreTotal,
        scoreMax: score?.max ?? session?.scoreMax,
        pourcentage: score?.pourcentage ?? session?.pourcentage,
        questionsCorrectes,
        questionsIncorrectes,
        dureeTotale,
        tempsMoyenParQuestion: details?.tempsMoyen != null ? `${details.tempsMoyen} s` : 'N/A',
        classement: classementStr,
        resultatsParTheme
      });
    } catch (error) {
      console.error('Error fetching session details:', error);
      toast.error('Erreur lors du chargement des détails');
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getScoreColor = (percentage) => {
    if (percentage >= 80) return 'text-green-600';
    if (percentage >= 60) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getScoreBgColor = (percentage) => {
    if (percentage >= 80) return 'bg-green-100';
    if (percentage >= 60) return 'bg-yellow-100';
    return 'bg-red-100';
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="loading"></div>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="min-h-screen py-8">
      <div className="container mx-auto px-4 max-w-6xl">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Mes Résultats
          </h1>
          <p className="text-xl text-gray-600">
            Consultez vos performances et progressez
          </p>
        </div>

        {sessions.length === 0 ? (
          <div className="bg-white rounded-lg shadow-lg p-12 text-center">
            <BarChart3 className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <h2 className="text-2xl font-semibold text-gray-900 mb-2">
              Aucun résultat disponible
            </h2>
            <p className="text-gray-600 mb-6">
              Vous n'avez pas encore passé de test. Commencez par passer votre premier test !
            </p>
            <button
              onClick={() => navigate('/test')}
              className="btn btn-primary"
            >
              Passer un test
            </button>
          </div>
        ) : (
          <div className="grid lg:grid-cols-3 gap-8">
            {/* Liste des sessions */}
            <div className="lg:col-span-1">
              <div className="bg-white rounded-lg shadow-lg p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4">
                  Historique des tests
                </h2>
                <div className="space-y-3">
                  {sessions.map((session) => (
                    <div
                      key={session.id}
                      onClick={() => fetchSessionDetails(session.id)}
                      className={`p-4 rounded-lg border cursor-pointer transition-all hover:shadow-md ${
                        selectedSession?.id === session.id
                          ? 'border-blue-500 bg-blue-50'
                          : 'border-gray-200 hover:border-gray-300'
                      }`}
                    >
                      <div className="flex justify-between items-start mb-2">
                        <div className="flex-1">
                          <p className="font-medium text-gray-900">
                            {formatDate(session.dateDebut)}
                          </p>
                          <p className="text-sm text-gray-600">
                            {session.scoreTotal}/{session.scoreMax} points
                          </p>
                        </div>
                        <div className={`px-2 py-1 rounded-full text-xs font-medium ${getScoreBgColor(session.pourcentage)} ${getScoreColor(session.pourcentage)}`}>
                          {session.pourcentage}%
                        </div>
                      </div>
                      <div className="flex items-center text-sm text-gray-600">
                        <Clock className="h-4 w-4 mr-1" />
                        {session.estTermine ? 'Terminé' : 'En cours'}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Détails de la session sélectionnée */}
            <div className="lg:col-span-2">
              {selectedSession && (
                <div className="space-y-6">
                  {/* Score global */}
                  <div className="bg-white rounded-lg shadow-lg p-8">
                    <div className="text-center">
                      <div className={`inline-flex items-center justify-center w-20 h-20 rounded-full mb-4 ${getScoreBgColor(selectedSession.pourcentage)}`}>
                        <Trophy className={`h-10 w-10 ${getScoreColor(selectedSession.pourcentage)}`} />
                      </div>
                      <h2 className="text-3xl font-bold text-gray-900 mb-2">
                        {selectedSession.pourcentage}%
                      </h2>
                      <p className="text-xl text-gray-600 mb-4">
                        {selectedSession.scoreTotal} / {selectedSession.scoreMax} points
                      </p>
                      <div className="grid grid-cols-3 gap-4 mt-6">
                        <div className="text-center">
                          <div className="flex items-center justify-center text-green-600 mb-1">
                            <CheckCircle className="h-5 w-5 mr-1" />
                            <span className="font-semibold">
                              {selectedSession.questionsCorrectes || 0}
                            </span>
                          </div>
                          <p className="text-sm text-gray-600">Correctes</p>
                        </div>
                        <div className="text-center">
                          <div className="flex items-center justify-center text-red-600 mb-1">
                            <XCircle className="h-5 w-5 mr-1" />
                            <span className="font-semibold">
                              {selectedSession.questionsIncorrectes || 0}
                            </span>
                          </div>
                          <p className="text-sm text-gray-600">Incorrectes</p>
                        </div>
                        <div className="text-center">
                          <div className="flex items-center justify-center text-gray-600 mb-1">
                            <Clock className="h-5 w-5 mr-1" />
                            <span className="font-semibold">
                              {selectedSession.dureeTotale || 'N/A'}
                            </span>
                          </div>
                          <p className="text-sm text-gray-600">Durée</p>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Performance par thème */}
                  <div className="bg-white rounded-lg shadow-lg p-6">
                    <h3 className="text-xl font-semibold text-gray-900 mb-4">
                      Performance par thème
                    </h3>
                    <div className="space-y-4">
                      {selectedSession.resultatsParTheme?.map((theme) => (
                        <div key={theme.themeId} className="border rounded-lg p-4">
                          <div className="flex justify-between items-center mb-2">
                            <div className="flex items-center">
                              <BookOpen className="h-5 w-5 text-gray-600 mr-2" />
                              <span className="font-medium text-gray-900">
                                {theme.nomTheme}
                              </span>
                            </div>
                            <span className={`font-semibold ${getScoreColor(theme.pourcentage)}`}>
                              {theme.pourcentage}%
                            </span>
                          </div>
                          <div className="w-full bg-gray-200 rounded-full h-2">
                            <div
                              className={`h-2 rounded-full transition-all duration-300 ${
                                theme.pourcentage >= 80 ? 'bg-green-600' :
                                theme.pourcentage >= 60 ? 'bg-yellow-600' : 'bg-red-600'
                              }`}
                              style={{ width: `${theme.pourcentage}%` }}
                            ></div>
                          </div>
                          <p className="text-sm text-gray-600 mt-1">
                            {theme.scoreObtenu}/{theme.scoreMaximum} points
                          </p>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Statistiques */}
                  <div className="bg-white rounded-lg shadow-lg p-6">
                    <h3 className="text-xl font-semibold text-gray-900 mb-4">
                      Statistiques détaillées
                    </h3>
                    <div className="grid grid-cols-2 gap-6">
                      <div className="flex items-center">
                        <Calendar className="h-8 w-8 text-gray-600 mr-3" />
                        <div>
                          <p className="text-sm text-gray-600">Date du test</p>
                          <p className="font-medium text-gray-900">
                            {formatDate(selectedSession.dateDebut)}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center">
                        <Clock className="h-8 w-8 text-gray-600 mr-3" />
                        <div>
                          <p className="text-sm text-gray-600">Temps moyen par question</p>
                          <p className="font-medium text-gray-900">
                            {selectedSession.tempsMoyenParQuestion || 'N/A'}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center">
                        <TrendingUp className="h-8 w-8 text-gray-600 mr-3" />
                        <div>
                          <p className="text-sm text-gray-600">Classement</p>
                          <p className="font-medium text-gray-900">
                            {selectedSession.classement || 'N/A'}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center">
                        <Award className="h-8 w-8 text-gray-600 mr-3" />
                        <div>
                          <p className="text-sm text-gray-600">Niveau atteint</p>
                          <p className="font-medium text-gray-900">
                            {selectedSession.pourcentage >= 80 ? 'Excellent' :
                             selectedSession.pourcentage >= 60 ? 'Bon' : 'À améliorer'}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Actions */}
                  <div className="flex justify-center space-x-4">
                    <button
                      onClick={ouvrirDemandeNouveauTest}
                      className="btn btn-primary"
                    >
                      Demander un nouveau test
                    </button>
                    <button
                      onClick={downloadReport}
                      className="btn btn-secondary"
                    >
                      Télécharger le rapport
                    </button>
                  </div>

                  {/* Détails questions */}
                  <div className="bg-white rounded-lg shadow-lg p-6">
                    <h3 className="text-xl font-semibold text-gray-900 mb-4">
                      Détails des réponses
                    </h3>

                    {questionsDetails.length === 0 ? (
                      <div className="text-gray-600">Aucun détail disponible.</div>
                    ) : (
                      <div className="space-y-6">
                        <div>
                          <h4 className="font-semibold text-green-700 mb-2">Questions correctes</h4>
                          <div className="space-y-3">
                            {questionsDetails.filter(q => q?.estCorrect === true).map((q) => (
                              <div key={q.sessionQuestionId} className="border rounded-lg p-4 bg-green-50">
                                <div className="font-medium text-gray-900 mb-2">{q.questionLibelle}</div>
                                <div className="text-sm text-gray-700">
                                  <span className="font-medium">Réponse correcte:</span>{' '}
                                  {(q.correctAnswers || []).join(' | ') || 'N/A'}
                                </div>
                              </div>
                            ))}
                            {questionsDetails.filter(q => q?.estCorrect === true).length === 0 && (
                              <div className="text-sm text-gray-600">Aucune.</div>
                            )}
                          </div>
                        </div>

                        <div>
                          <h4 className="font-semibold text-red-700 mb-2">Questions incorrectes</h4>
                          <div className="space-y-3">
                            {questionsDetails.filter(q => q?.estCorrect === false).map((q) => (
                              <div key={q.sessionQuestionId} className="border rounded-lg p-4 bg-red-50">
                                <div className="font-medium text-gray-900 mb-2">{q.questionLibelle}</div>
                                <div className="text-sm text-gray-700 mb-1">
                                  <span className="font-medium">Votre réponse:</span>{' '}
                                  {(q.candidateAnswers || []).join(' | ') || 'N/A'}
                                </div>
                                <div className="text-sm text-gray-700">
                                  <span className="font-medium">Réponse correcte:</span>{' '}
                                  {(q.correctAnswers || []).join(' | ') || 'N/A'}
                                </div>
                              </div>
                            ))}
                            {questionsDetails.filter(q => q?.estCorrect === false).length === 0 && (
                              <div className="text-sm text-gray-600">Aucune.</div>
                            )}
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        )}
      </div>

      {showNewTestModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 max-w-3xl w-full max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-start mb-4">
              <h3 className="text-lg font-semibold text-gray-900">Demander un nouveau test</h3>
              <button
                onClick={() => setShowNewTestModal(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                ×
              </button>
            </div>

            <div className="space-y-6">
              <div className="bg-gray-50 rounded-lg p-4">
                <h4 className="font-medium text-gray-900 mb-3">Vos informations</h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="label">Nom</label>
                    <input
                      className="input"
                      value={candidateForm.nom}
                      onChange={(e) => setCandidateForm(prev => ({ ...prev, nom: e.target.value }))}
                    />
                  </div>
                  <div>
                    <label className="label">Prénom</label>
                    <input
                      className="input"
                      value={candidateForm.prenom}
                      onChange={(e) => setCandidateForm(prev => ({ ...prev, prenom: e.target.value }))}
                    />
                  </div>
                  <div>
                    <label className="label">Email</label>
                    <input
                      className="input"
                      value={candidateForm.email}
                      disabled
                    />
                  </div>
                  <div>
                    <label className="label">Téléphone</label>
                    <input
                      className="input"
                      value={candidateForm.gsm}
                      onChange={(e) => setCandidateForm(prev => ({ ...prev, gsm: e.target.value }))}
                    />
                  </div>
                  <div>
                    <label className="label">École</label>
                    <input
                      className="input"
                      value={candidateForm.ecole}
                      onChange={(e) => setCandidateForm(prev => ({ ...prev, ecole: e.target.value }))}
                    />
                  </div>
                  <div>
                    <label className="label">Filière</label>
                    <input
                      className="input"
                      value={candidateForm.filiere}
                      onChange={(e) => setCandidateForm(prev => ({ ...prev, filiere: e.target.value }))}
                    />
                  </div>
                </div>
              </div>

              <div className="bg-white border rounded-lg p-4">
                <h4 className="font-medium text-gray-900 mb-3">Type de test</h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="label">Choix</label>
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
                    <div>
                      <label className="label">Branche Informatique</label>
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

              <div className="bg-white border rounded-lg p-4">
                <h4 className="font-medium text-gray-900 mb-3">Choisir un créneau</h4>
                <div className="space-y-2">
                  {creneauxDisponibles.map((c) => (
                    <label key={c.id} className="flex items-center p-3 border rounded-lg cursor-pointer">
                      <input
                        type="radio"
                        name="creneau"
                        className="mr-3"
                        checked={Number(selectedCreneauId) === Number(c.id)}
                        onChange={() => setSelectedCreneauId(c.id)}
                      />
                      <div className="text-sm text-gray-800">
                        <div className="font-medium">{c.dateExam} — {c.heureDebut} - {c.heureFin}</div>
                        <div className="text-xs text-gray-600">Places: {c.placesDisponibles}</div>
                      </div>
                    </label>
                  ))}
                </div>
              </div>

              <div className="flex justify-end gap-3">
                <button className="btn btn-secondary" onClick={() => setShowNewTestModal(false)}>
                  Annuler
                </button>
                <button
                  className="btn btn-primary disabled:opacity-50"
                  disabled={requestingNewTest}
                  onClick={soumettreDemandeNouveauTest}
                >
                  {requestingNewTest ? 'Envoi...' : 'Envoyer la demande'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Resultats;
