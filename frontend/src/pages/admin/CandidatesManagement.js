import React, { useState, useEffect } from 'react';
import { 
  Users, 
  Search, 
  Filter, 
  CheckCircle, 
  XCircle, 
  Eye, 
  Mail,
  Download
} from 'lucide-react';
import toast from 'react-hot-toast';

const CandidatesManagement = () => {
  const [candidates, setCandidates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('all');
  const [selectedCandidate, setSelectedCandidate] = useState(null);
  const [showDetails, setShowDetails] = useState(false);

  const [candidateDetailsLoading, setCandidateDetailsLoading] = useState(false);

  useEffect(() => {
    fetchCandidates();
  }, []);

  const fetchCandidates = async () => {
    try {
      const response = await fetch('/api/admin/candidats');
      if (response.ok) {
        const data = await response.json();
        setCandidates(data.candidats || data); // Handle both response formats
      }
    } catch (error) {
      console.error('Error fetching candidates:', error);
      toast.error('Erreur lors du chargement des candidats');
    } finally {
      setLoading(false);
    }
  };

  const exportCandidates = async () => {
    try {
      const qs = new URLSearchParams();
      if (searchTerm && searchTerm.trim()) {
        qs.set('search', searchTerm.trim());
      }
      if (filterStatus && filterStatus !== 'all') {
        qs.set('status', filterStatus);
      }
      const url = qs.toString()
        ? `/api/admin/candidats/export/csv?${qs.toString()}`
        : '/api/admin/candidats/export/csv';

      const response = await fetch(url);
      if (!response.ok) {
        toast.error('Erreur lors de l\'export');
        return;
      }
      const blob = await response.blob();
      const dlUrl = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = dlUrl;
      a.download = `candidats_${new Date().toISOString().split('T')[0]}.csv`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(dlUrl);
      document.body.removeChild(a);
      toast.success('Candidats exportés avec succès');
    } catch (error) {
      console.error('Error exporting candidates:', error);
      toast.error('Erreur de connexion au serveur');
    }
  };

  const openCandidateDetails = async (candidate) => {
    setShowDetails(true);
    setSelectedCandidate(candidate);

    setCandidateDetailsLoading(true);
    try {
      const resp = await fetch(`/api/admin/candidats/${candidate.id}/details`);
      const data = await resp.json().catch(() => ({}));
      if (!resp.ok) {
        toast.error(data?.error || 'Erreur lors du chargement des détails');
        return;
      }
      setSelectedCandidate(data);
    } catch (e) {
      console.error('Error fetching candidate details:', e);
      toast.error('Erreur de connexion au serveur');
    } finally {
      setCandidateDetailsLoading(false);
    }
  };

  const handleValidateCandidate = async (candidateId) => {
    try {
      const response = await fetch(`/api/admin/candidats/${candidateId}/valider`, {
        method: 'POST'
      });

      if (response.ok) {
        toast.success('Candidat validé avec succès');
        fetchCandidates();
      } else {
        const error = await response.json();
        toast.error(error.error || 'Erreur lors de la validation');
      }
    } catch (error) {
      console.error('Error validating candidate:', error);
      toast.error('Erreur de connexion au serveur');
    }
  };

  const handleRejectCandidate = async (candidateId) => {
    try {
      const response = await fetch(`/api/admin/candidats/${candidateId}/rejeter`, {
        method: 'POST'
      });

      if (response.ok) {
        toast.success('Candidat rejeté');
        fetchCandidates();
      } else {
        const error = await response.json();
        toast.error(error.error || 'Erreur lors du rejet');
      }
    } catch (error) {
      console.error('Error rejecting candidate:', error);
      toast.error('Erreur de connexion au serveur');
    }
  };

  const handleSendCode = async (candidateId) => {
    try {
      const response = await fetch(`/api/admin/candidats/${candidateId}/envoyer-code`, {
        method: 'POST'
      });

      if (response.ok) {
        toast.success('Code de session envoyé par email');
      } else {
        const error = await response.json();
        toast.error(error.error || 'Erreur lors de l\'envoi du code');
      }
    } catch (error) {
      console.error('Error sending code:', error);
      toast.error('Erreur de connexion au serveur');
    }
  };

  const filteredCandidates = candidates.filter(candidate => {
    const matchesSearch = candidate.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         candidate.prenom.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         candidate.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         candidate.ecole.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesFilter = filterStatus === 'all' ||
                         (filterStatus === 'validated' && candidate.estValide) ||
                         (filterStatus === 'pending' && !candidate.estValide);
    
    return matchesSearch && matchesFilter;
  });

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  };

  if (loading) {
    return (
      <div className="p-6">
        <div className="flex items-center justify-center h-64">
          <div className="loading"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Gestion des Candidats</h1>
        <p className="text-gray-600 mt-2">Validez et gérez les inscriptions des candidats</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Candidats</p>
              <p className="text-2xl font-bold text-gray-900">{candidates.length}</p>
            </div>
            <Users className="h-8 w-8 text-blue-600" />
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">En attente</p>
              <p className="text-2xl font-bold text-yellow-600">
                {candidates.filter(c => !c.estValide).length}
              </p>
            </div>
            <XCircle className="h-8 w-8 text-yellow-600" />
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Validés</p>
              <p className="text-2xl font-bold text-green-600">
                {candidates.filter(c => c.estValide).length}
              </p>
            </div>
            <CheckCircle className="h-8 w-8 text-green-600" />
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
              <Filter className="h-5 w-5 text-gray-600" />
              <select
                className="input"
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value)}
              >
                <option value="all">Tous les candidats</option>
                <option value="pending">En attente</option>
                <option value="validated">Validés</option>
              </select>
            </div>
            <button
              onClick={exportCandidates}
              className="btn btn-secondary flex items-center"
            >
              <Download className="h-4 w-4 mr-2" />
              Exporter
            </button>
          </div>
        </div>
      </div>

      {/* Candidates Table */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Candidat
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Contact
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  École/Filière
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Statut
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Date d'inscription
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredCandidates.map((candidate) => (
                <tr key={candidate.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {candidate.prenom} {candidate.nom}
                      </div>
                      {candidate.codeSession && (
                        <div className="text-xs text-gray-500">
                          Code: {candidate.codeSession}
                        </div>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-900">{candidate.email}</div>
                    <div className="text-sm text-gray-500">{candidate.gsm}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-900">{candidate.ecole}</div>
                    <div className="text-sm text-gray-500">{candidate.filiere || '-'}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      candidate.estValide 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-yellow-100 text-yellow-800'
                    }`}>
                      {candidate.estValide ? 'Validé' : 'En attente'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {formatDate(candidate.createdAt)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex items-center space-x-2">
                      <button
                        onClick={() => {
                          openCandidateDetails(candidate);
                        }}
                        className="text-blue-600 hover:text-blue-900"
                        title="Voir les détails"
                      >
                        <Eye className="h-4 w-4" />
                      </button>
                      
                      {!candidate.estValide && (
                        <>
                          <button
                            onClick={() => handleValidateCandidate(candidate.id)}
                            className="text-green-600 hover:text-green-900"
                            title="Valider"
                          >
                            <CheckCircle className="h-4 w-4" />
                          </button>
                          <button
                            onClick={() => handleRejectCandidate(candidate.id)}
                            className="text-red-600 hover:text-red-900"
                            title="Rejeter"
                          >
                            <XCircle className="h-4 w-4" />
                          </button>
                        </>
                      )}
                      
                      {candidate.codeSession && (
                        <button
                          onClick={() => handleSendCode(candidate.id)}
                          className="text-purple-600 hover:text-purple-900"
                          title="Envoyer le code par email"
                        >
                          <Mail className="h-4 w-4" />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        {filteredCandidates.length === 0 && (
          <div className="text-center py-12">
            <Users className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">Aucun candidat trouvé</p>
          </div>
        )}
      </div>

      {/* Candidate Details Modal */}
      {showDetails && selectedCandidate && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">
              Détails du candidat
            </h3>
            {candidateDetailsLoading ? (
              <div className="flex items-center justify-center h-32">
                <div className="loading"></div>
              </div>
            ) : (
            <div className="space-y-3">
              <div>
                <p className="text-sm text-gray-600">Nom complet</p>
                <p className="font-medium">
                  {selectedCandidate.prenom} {selectedCandidate.nom}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Email</p>
                <p className="font-medium">{selectedCandidate.email}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Téléphone</p>
                <p className="font-medium">{selectedCandidate.gsm}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">École</p>
                <p className="font-medium">{selectedCandidate.ecole}</p>
              </div>
              {selectedCandidate.filiere && (
                <div>
                  <p className="text-sm text-gray-600">Filière</p>
                  <p className="font-medium">{selectedCandidate.filiere}</p>
                </div>
              )}
              {selectedCandidate.codeSession && (
                <div>
                  <p className="text-sm text-gray-600">Code de session</p>
                  <p className="font-medium font-mono">{selectedCandidate.codeSession}</p>
                </div>
              )}
              <div>
                <p className="text-sm text-gray-600">Date d'inscription</p>
                <p className="font-medium">{formatDate(selectedCandidate.createdAt)}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Statut</p>
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  selectedCandidate.estValide 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-yellow-100 text-yellow-800'
                }`}>
                  {selectedCandidate.estValide ? 'Validé' : 'En attente'}
                </span>
              </div>

              {selectedCandidate.testChoice?.modeTest && (
                <div>
                  <p className="text-sm text-gray-600">Type de test demandé</p>
                  <p className="font-medium">
                    {selectedCandidate.testChoice.modeTest === 'INFORMATIQUE'
                      ? `Informatique (spécialité)${selectedCandidate.testChoice.themeSpecialiteNom ? ` - ${selectedCandidate.testChoice.themeSpecialiteNom}` : ''}`
                      : 'Culture générale'}
                  </p>
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

export default CandidatesManagement;
