import React, { useState, useEffect } from 'react';
import { 
  Calendar, 
  Plus, 
  Edit, 
  Trash2, 
  Search, 
  Clock,
  Users,
  CheckCircle,
  AlertCircle
} from 'lucide-react';
import toast from 'react-hot-toast';

const ScheduleManagement = () => {
  const [creneaux, setCreneaux] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingCreneau, setEditingCreneau] = useState(null);
  const [formData, setFormData] = useState({
    dateExam: '',
    heureDebut: '',
    dureeMinutes: 120,
    nombreQuestions: 20,
    placesDisponibles: 30
  });

  useEffect(() => {
    fetchCreneaux();
  }, []);

  const fetchCreneaux = async () => {
    try {
      const response = await fetch('/api/admin/creneaux');
      if (response.ok) {
        const data = await response.json();
        setCreneaux(data.creneaux);
      }
    } catch (error) {
      console.error('Error fetching creneaux:', error);
      toast.error('Erreur lors du chargement des créneaux');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const url = editingCreneau 
        ? `/api/admin/creneaux/${editingCreneau.id}`
        : '/api/admin/creneaux';
      const method = editingCreneau ? 'PUT' : 'POST';

      // Convertir la date en format simple
      const payload = {
        ...formData,
        nombreQuestions: formData.nombreQuestions === '' ? null : formData.nombreQuestions,
        dateExam: formData.dateExam // Format YYYY-MM-DD
      };

      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        toast.success(editingCreneau ? 'Créneau modifié' : 'Créneau ajouté');
        setShowForm(false);
        setEditingCreneau(null);
        resetForm();
        fetchCreneaux();
      } else {
        const error = await response.json();
        toast.error(error.error || 'Erreur lors de l\'opération');
      }
    } catch (error) {
      console.error('Error saving creneau:', error);
      toast.error('Erreur de connexion au serveur');
    }
  };

  const handleDelete = async (creneauId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce créneau ?')) {
      return;
    }

    try {
      const response = await fetch(`/api/admin/creneaux/${creneauId}`, {
        method: 'DELETE'
      });

      if (response.ok) {
        toast.success('Créneau supprimé');
        fetchCreneaux();
      } else {
        const error = await response.json();
        toast.error(error.error || 'Erreur lors de la suppression');
      }
    } catch (error) {
      console.error('Error deleting creneau:', error);
      toast.error('Erreur de connexion au serveur');
    }
  };

  const handleEdit = (creneau) => {
    setEditingCreneau(creneau);
    setFormData({
      dateExam: creneau.dateExam,
      heureDebut: creneau.heureDebut.substring(0, 5),
      dureeMinutes: creneau.dureeMinutes,
      nombreQuestions: creneau.nombreQuestions ?? 20,
      placesDisponibles: creneau.placesDisponibles
    });
    setShowForm(true);
  };

  const resetForm = () => {
    setFormData({
      dateExam: '',
      heureDebut: '',
      dureeMinutes: 120,
      nombreQuestions: 20,
      placesDisponibles: 30
    });
  };

  const calculateHeureFin = (heureDebut, dureeMinutes) => {
    if (!heureDebut) return '';
    const [hours, minutes] = heureDebut.split(':').map(Number);
    const totalMinutes = hours * 60 + minutes + dureeMinutes;
    const endHours = Math.floor(totalMinutes / 60) % 24;
    const endMinutes = totalMinutes % 60;
    return `${endHours.toString().padStart(2, '0')}:${endMinutes.toString().padStart(2, '0')}`;
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const getStatusColor = (creneau) => {
    if (creneau.estComplet) return 'text-red-600 bg-red-100';
    if (new Date(creneau.dateExam) < new Date().setHours(0,0,0,0)) return 'text-gray-600 bg-gray-100';
    return 'text-green-600 bg-green-100';
  };

  const getStatusText = (creneau) => {
    if (creneau.estComplet) return 'Complet';
    if (new Date(creneau.dateExam) < new Date().setHours(0,0,0,0)) return 'Passé';
    return 'Disponible';
  };

  const calculateStats = () => {
    const today = new Date().setHours(0,0,0,0);
    const upcoming = creneaux.filter(c => new Date(c.dateExam) >= today);
    const available = upcoming.filter(c => !c.estComplet);
    const todayCreneaux = creneaux.filter(c => 
      new Date(c.dateExam).toDateString() === new Date().toDateString()
    );

    return {
      total: creneaux.length,
      upcoming: upcoming.length,
      available: available.length,
      today: todayCreneaux.length
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

  return (
    <div className="p-6">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Gestion des Créneaux</h1>
        <p className="text-gray-600 mt-2">Créez et gérez les créneaux horaires pour les tests</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Créneaux</p>
              <p className="text-2xl font-bold text-gray-900">{stats.total}</p>
            </div>
            <Calendar className="h-8 w-8 text-blue-600" />
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">À venir</p>
              <p className="text-2xl font-bold text-yellow-600">{stats.upcoming}</p>
            </div>
            <Clock className="h-8 w-8 text-yellow-600" />
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Disponibles</p>
              <p className="text-2xl font-bold text-green-600">{stats.available}</p>
            </div>
            <CheckCircle className="h-8 w-8 text-green-600" />
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Aujourd'hui</p>
              <p className="text-2xl font-bold text-purple-600">{stats.today}</p>
            </div>
            <AlertCircle className="h-8 w-8 text-purple-600" />
          </div>
        </div>
      </div>

      {/* Actions */}
      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <div className="flex items-center justify-between">
          <div className="flex-1 max-w-md">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
              <input
                type="text"
                placeholder="Rechercher un créneau..."
                className="input pl-10"
              />
            </div>
          </div>
          <button
            onClick={() => {
              resetForm();
              setEditingCreneau(null);
              setShowForm(true);
            }}
            className="btn btn-primary flex items-center"
          >
            <Plus className="h-4 w-4 mr-2" />
            Ajouter un créneau
          </button>
        </div>
      </div>

      {/* Creneaux Table */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Date
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Horaires
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Durée
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Places
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
              {creneaux.map((creneau) => (
                <tr key={creneau.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {formatDate(creneau.dateExam)}
                      </div>
                      <div className="text-xs text-gray-500">
                        {new Date(creneau.dateExam).toLocaleDateString('fr-FR')}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center text-sm text-gray-900">
                      <Clock className="h-4 w-4 mr-2 text-gray-400" />
                      {creneau.heureDebut.substring(0, 5)} - {creneau.heureFin.substring(0, 5)}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {creneau.dureeMinutes} min
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <Users className="h-4 w-4 mr-2 text-gray-400" />
                      <span className="text-sm text-gray-900">
                        {creneau.placesDisponibles}
                      </span>
                      {creneau.placesDisponibles <= 5 && (
                        <span className="ml-2 text-xs text-yellow-600">
                          (Presque complet)
                        </span>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(creneau)}`}>
                      {getStatusText(creneau)}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex items-center space-x-2">
                      <button
                        onClick={() => handleEdit(creneau)}
                        className="text-blue-600 hover:text-blue-900"
                        title="Modifier"
                      >
                        <Edit className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(creneau.id)}
                        className="text-red-600 hover:text-red-900"
                        title="Supprimer"
                      >
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        {creneaux.length === 0 && (
          <div className="text-center py-12">
            <Calendar className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">Aucun créneau trouvé</p>
          </div>
        )}
      </div>

      {/* Creneau Form Modal */}
      {showForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">
              {editingCreneau ? 'Modifier le créneau' : 'Ajouter un créneau'}
            </h3>
            
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="label">Date de l'examen *</label>
                <input
                  type="date"
                  className="input"
                  value={formData.dateExam}
                  onChange={(e) => setFormData(prev => ({ ...prev, dateExam: e.target.value }))}
                  required
                  min={new Date().toISOString().split('T')[0]}
                />
              </div>

              <div>
                <label className="label">Nombre de questions</label>
                <input
                  type="number"
                  className="input"
                  value={formData.nombreQuestions}
                  onChange={(e) => {
                    const raw = e.target.value;
                    if (raw === '') {
                      setFormData(prev => ({ ...prev, nombreQuestions: '' }));
                      return;
                    }
                    const v = Number(raw);
                    if (!Number.isNaN(v)) {
                      setFormData(prev => ({ ...prev, nombreQuestions: v }));
                    }
                  }}
                  min="1"
                  max="200"
                />
              </div>

              <div>
                <label className="label">Heure de début *</label>
                <input
                  type="time"
                  className="input"
                  value={formData.heureDebut}
                  onChange={(e) => setFormData(prev => ({ ...prev, heureDebut: e.target.value }))}
                  required
                />
                {formData.heureDebut && (
                  <p className="text-sm text-gray-600 mt-1">
                    Heure de fin: {calculateHeureFin(formData.heureDebut, formData.dureeMinutes)}
                  </p>
                )}
              </div>

              <div>
                <label className="label">Durée (minutes) *</label>
                <input
                  type="number"
                  className="input"
                  value={formData.dureeMinutes}
                  onChange={(e) => {
                    const raw = e.target.value;
                    if (raw === '') {
                      setFormData(prev => ({ ...prev, dureeMinutes: '' }));
                      return;
                    }
                    const v = Number(raw);
                    if (!Number.isNaN(v)) {
                      setFormData(prev => ({ ...prev, dureeMinutes: v }));
                    }
                  }}
                  min="30"
                  max="480"
                  required
                />
              </div>

              <div>
                <label className="label">Places disponibles *</label>
                <input
                  type="number"
                  className="input"
                  value={formData.placesDisponibles}
                  onChange={(e) => {
                    const raw = e.target.value;
                    if (raw === '') {
                      setFormData(prev => ({ ...prev, placesDisponibles: '' }));
                      return;
                    }
                    const v = Number(raw);
                    if (!Number.isNaN(v)) {
                      setFormData(prev => ({ ...prev, placesDisponibles: v }));
                    }
                  }}
                  min="1"
                  max="100"
                  required
                />
              </div>

              <div className="flex justify-end space-x-4 pt-4">
                <button
                  type="button"
                  onClick={() => {
                    setShowForm(false);
                    setEditingCreneau(null);
                    resetForm();
                  }}
                  className="btn btn-secondary"
                >
                  Annuler
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                >
                  {editingCreneau ? 'Modifier' : 'Ajouter'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ScheduleManagement;
