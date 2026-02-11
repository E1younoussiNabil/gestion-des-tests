import React, { useState, useEffect } from 'react';
import { 
  BarChart3, 
  Search, 
  Download, 
  Eye,
  Calendar,
  TrendingUp,
  Users,
  Clock,
  Award
} from 'lucide-react';
import toast from 'react-hot-toast';

const ResultsManagement = () => {
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterDate, setFilterDate] = useState('');
  const [selectedSession, setSelectedSession] = useState(null);
  const [showDetails, setShowDetails] = useState(false);
  const [detailsLoading, setDetailsLoading] = useState(false);

  useEffect(() => {
    fetchSessions();
  }, []);

  const fetchSessions = async () => {
    try {
      setError(null);
      const response = await fetch('/api/admin/resultats/sessions');
      if (response.ok) {
        const data = await response.json();
        setSessions(data);
      } else {
        let message = 'Erreur lors du chargement des résultats';
        try {
          const err = await response.json();
          message = err?.error || message;
        } catch (e) {
          // ignore
        }
        setError(message);
        toast.error(message);
      }
    } catch (error) {
      console.error('Error fetching sessions:', error);
      setError('Erreur de connexion au serveur');
      toast.error('Erreur lors du chargement des résultats');
    } finally {
      setLoading(false);
    }
  };

  const openDetails = async (session) => {
    setShowDetails(true);
    setSelectedSession(session);
    setDetailsLoading(true);
    try {
      const response = await fetch(`/api/admin/resultats/session/${session.id}/details`);
      if (response.ok) {
        const data = await response.json();
        setSelectedSession(data);
      } else {
        let message = 'Erreur lors du chargement des détails';
        try {
          const err = await response.json();
          message = err?.error || message;
        } catch (e) {
          // ignore
        }
        toast.error(message);
      }
    } catch (e) {
      console.error('Error fetching session details:', e);
      toast.error('Erreur de connexion au serveur');
    } finally {
      setDetailsLoading(false);
    }
  };

  const filteredSessions = sessions.filter(session => {
    const matchesSearch = 
      session.candidat?.nom?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      session.candidat?.prenom?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      session.candidat?.email?.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesDate = !filterDate || 
      new Date(session.dateDebut).toDateString() === new Date(filterDate).toDateString();
    
    return matchesSearch && matchesDate;
  });

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'short',
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

  const exportResults = async () => {
    try {
      const response = await fetch('/api/admin/resultats/export', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          filters: {
            searchTerm,
            filterDate
          }
        })
      });

      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `resultats_${new Date().toISOString().split('T')[0]}.csv`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        toast.success('Résultats exportés avec succès');
      } else {
        toast.error('Erreur lors de l\'export');
      }
    } catch (error) {
      console.error('Error exporting results:', error);
      toast.error('Erreur de connexion au serveur');
    }
  };

  const calculateStats = () => {
    const completed = sessions.filter(s => s.estTermine);
    const totalScore = completed.reduce((sum, s) => sum + s.pourcentage, 0);
    const averageScore = completed.length > 0 ? (totalScore / completed.length).toFixed(1) : 0;
    
    return {
      total: sessions.length,
      completed: completed.length,
      inProgress: sessions.filter(s => !s.estTermine).length,
      averageScore
    };
  };

  const stats = calculateStats();

  if (loading) {
    return (
      <div className="p-6">
        <div className="flex items-center justify-center h-64">
          <div className="loading"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-6">
        <div className="bg-white rounded-lg shadow p-6">
          <h1 className="text-3xl font-bold text-gray-900">Gestion des Résultats</h1>
          <p className="text-red-600 mt-4">{error}</p>
          <div className="mt-4">
            <button onClick={() => {
              setLoading(true);
              fetchSessions();
            }} className="btn btn-primary">
              Réessayer
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Gestion des Résultats</h1>
        <p className="text-gray-600 mt-2">Consultez et analysez les résultats des tests</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Sessions</p>
              <p className="text-2xl font-bold text-gray-900">{stats.total}</p>
            </div>
            <Users className="h-8 w-8 text-blue-600" />
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Terminées</p>
              <p className="text-2xl font-bold text-green-600">{stats.completed}</p>
            </div>
            <Award className="h-8 w-8 text-green-600" />
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">En cours</p>
              <p className="text-2xl font-bold text-yellow-600">{stats.inProgress}</p>
            </div>
            <Clock className="h-8 w-8 text-yellow-600" />
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Score Moyen</p>
              <p className="text-2xl font-bold text-purple-600">{stats.averageScore}%</p>
            </div>
            <TrendingUp className="h-8 w-8 text-purple-600" />
          </div>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div className="flex-1 max-w-md">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
              <input
                type="text"
                placeholder="Rechercher un candidat..."
                className="input pl-10"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2">
              <Calendar className="h-5 w-5 text-gray-600" />
              <input
                type="date"
                className="input"
                value={filterDate}
                onChange={(e) => setFilterDate(e.target.value)}
              />
            </div>
            <button
              onClick={exportResults}
              className="btn btn-secondary flex items-center"
            >
              <Download className="h-4 w-4 mr-2" />
              Exporter
            </button>
          </div>
        </div>
      </div>

      {/* Results Table */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Candidat
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Date
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Score
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Durée
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Statut
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredSessions.map((session) => (
                <tr key={session.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {session.candidat?.prenom} {session.candidat?.nom}
                      </div>
                      <div className="text-sm text-gray-500">{session.candidat?.email}</div>
                      <div className="text-xs text-gray-400">{session.candidat?.ecole}</div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {formatDate(session.dateDebut)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center space-x-2">
                      <span className={`text-sm font-bold ${getScoreColor(session.pourcentage)}`}>
                        {session.pourcentage}%
                      </span>
                      <span className="text-xs text-gray-500">
                        ({session.scoreTotal}/{session.scoreMax})
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {session.dureeTotale || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      session.estTermine 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-yellow-100 text-yellow-800'
                    }`}>
                      {session.estTermine ? 'Terminé' : 'En cours'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex items-center gap-3">
                      <button
                        onClick={() => openDetails(session)}
                        className="text-blue-600 hover:text-blue-900"
                        title="Voir les détails"
                      >
                        <Eye className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => openDetails(session)}
                        className="btn btn-secondary"
                      >
                        Détails résultats
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        {filteredSessions.length === 0 && (
          <div className="text-center py-12">
            <BarChart3 className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">Aucun résultat trouvé</p>
          </div>
        )}
      </div>

      {/* Session Details Modal */}
      {showDetails && selectedSession && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-start mb-4">
              <h3 className="text-lg font-semibold text-gray-900">
                Détails de la session
              </h3>
              <button
                onClick={() => setShowDetails(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                ×
              </button>
            </div>

            {detailsLoading ? (
              <div className="flex items-center justify-center h-48">
                <div className="loading"></div>
              </div>
            ) : (
            <div className="space-y-4">
              {/* Candidat Info */}
              <div className="bg-gray-50 rounded-lg p-4">
                <h4 className="font-medium text-gray-900 mb-2">Candidat</h4>
                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <span className="text-gray-600">Nom:</span>
                    <span className="ml-2 font-medium">
                      {selectedSession.candidat?.prenom} {selectedSession.candidat?.nom}
                    </span>
                  </div>
                  <div>
                    <span className="text-gray-600">Email:</span>
                    <span className="ml-2">{selectedSession.candidat?.email}</span>
                  </div>
                  <div>
                    <span className="text-gray-600">École:</span>
                    <span className="ml-2">{selectedSession.candidat?.ecole}</span>
                  </div>
                  <div>
                    <span className="text-gray-600">Filière:</span>
                    <span className="ml-2">{selectedSession.candidat?.filiere || 'N/A'}</span>
                  </div>
                </div>
              </div>

              {/* Performance Summary */}
              <div className="bg-blue-50 rounded-lg p-4">
                <h4 className="font-medium text-gray-900 mb-2">Performance</h4>
                <div className="grid grid-cols-3 gap-4 text-sm">
                  <div className="text-center">
                    <div className={`text-2xl font-bold ${getScoreColor(selectedSession.pourcentage)}`}>
                      {selectedSession.pourcentage}%
                    </div>
                    <div className="text-gray-600">Score</div>
                  </div>
                  <div className="text-center">
                    <div className="text-2xl font-bold text-gray-900">
                      {selectedSession.scoreTotal}/{selectedSession.scoreMax}
                    </div>
                    <div className="text-gray-600">Points</div>
                  </div>
                  <div className="text-center">
                    <div className="text-2xl font-bold text-gray-900">
                      {selectedSession.dureeTotale || 'N/A'}
                    </div>
                    <div className="text-gray-600">Durée</div>
                  </div>
                </div>
              </div>

              {/* Timeline */}
              <div>
                <h4 className="font-medium text-gray-900 mb-2">Chronologie</h4>
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600">Début:</span>
                    <span className="font-medium">{formatDate(selectedSession.dateDebut)}</span>
                  </div>
                  {selectedSession.dateFin && (
                    <div className="flex justify-between">
                      <span className="text-gray-600">Fin:</span>
                      <span className="font-medium">{formatDate(selectedSession.dateFin)}</span>
                    </div>
                  )}
                  <div className="flex justify-between">
                    <span className="text-gray-600">Statut:</span>
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      selectedSession.estTermine 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-yellow-100 text-yellow-800'
                    }`}>
                      {selectedSession.estTermine ? 'Terminé' : 'En cours'}
                    </span>
                  </div>
                </div>
              </div>

              {/* Performance by Theme */}
              {selectedSession.resultatsParTheme && selectedSession.resultatsParTheme.length > 0 && (
                <div>
                  <h4 className="font-medium text-gray-900 mb-2">Performance par thème</h4>
                  <div className="space-y-2">
                    {selectedSession.resultatsParTheme.map((theme, index) => (
                      <div key={index} className="flex items-center justify-between p-2 bg-gray-50 rounded">
                        <span className="text-sm font-medium">{theme.nomTheme}</span>
                        <div className="flex items-center space-x-2">
                          <div className="w-24 bg-gray-200 rounded-full h-2">
                            <div
                              className={`h-2 rounded-full ${
                                theme.pourcentage >= 80 ? 'bg-green-600' :
                                theme.pourcentage >= 60 ? 'bg-yellow-600' : 'bg-red-600'
                              }`}
                              style={{ width: `${theme.pourcentage}%` }}
                            ></div>
                          </div>
                          <span className={`text-sm font-bold ${getScoreColor(theme.pourcentage)}`}>
                            {theme.pourcentage}%
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {selectedSession.tempsMoyenParQuestion && (
                <div>
                  <h4 className="font-medium text-gray-900 mb-2">Temps moyen</h4>
                  <div className="text-sm text-gray-700">
                    {selectedSession.tempsMoyenParQuestion}
                  </div>
                </div>
              )}

              {/* Détails questions */}
              {Array.isArray(selectedSession.questionsDetails) && (
                <div>
                  <h4 className="font-medium text-gray-900 mb-2">Détails des réponses</h4>
                  {selectedSession.questionsDetails.length === 0 ? (
                    <div className="text-sm text-gray-600">Aucun détail disponible.</div>
                  ) : (
                    <div className="space-y-4">
                      <div>
                        <div className="text-sm font-semibold text-green-700 mb-2">Questions correctes</div>
                        <div className="space-y-2">
                          {selectedSession.questionsDetails.filter(q => q?.estCorrect === true).map((q) => (
                            <div key={q.sessionQuestionId} className="p-3 rounded border bg-green-50">
                              <div className="text-sm font-medium text-gray-900 mb-1">{q.questionLibelle}</div>
                              <div className="text-xs text-gray-700">
                                <span className="font-medium">Réponse correcte:</span>{' '}
                                {(q.correctAnswers || []).join(' | ') || 'N/A'}
                              </div>
                            </div>
                          ))}
                          {selectedSession.questionsDetails.filter(q => q?.estCorrect === true).length === 0 && (
                            <div className="text-sm text-gray-600">Aucune.</div>
                          )}
                        </div>
                      </div>

                      <div>
                        <div className="text-sm font-semibold text-red-700 mb-2">Questions incorrectes</div>
                        <div className="space-y-2">
                          {selectedSession.questionsDetails.filter(q => q?.estCorrect === false).map((q) => (
                            <div key={q.sessionQuestionId} className="p-3 rounded border bg-red-50">
                              <div className="text-sm font-medium text-gray-900 mb-1">{q.questionLibelle}</div>
                              <div className="text-xs text-gray-700 mb-1">
                                <span className="font-medium">Votre réponse:</span>{' '}
                                {(q.candidateAnswers || []).join(' | ') || 'N/A'}
                              </div>
                              <div className="text-xs text-gray-700">
                                <span className="font-medium">Réponse correcte:</span>{' '}
                                {(q.correctAnswers || []).join(' | ') || 'N/A'}
                              </div>
                            </div>
                          ))}
                          {selectedSession.questionsDetails.filter(q => q?.estCorrect === false).length === 0 && (
                            <div className="text-sm text-gray-600">Aucune.</div>
                          )}
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              )}
            </div>
            )}

            <div className="mt-6 flex justify-end">
              <button
                onClick={() => setShowDetails(false)}
                className="btn btn-secondary"
              >
                Fermer
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ResultsManagement;
