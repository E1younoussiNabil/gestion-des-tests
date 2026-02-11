import React, { useState, useEffect } from 'react';
import { 
  BookOpen, 
  Plus, 
  Eye,
  Edit, 
  Trash2, 
  Search, 
  CheckCircle,
  XCircle
} from 'lucide-react';
import toast from 'react-hot-toast';

const TestsManagement = () => {
  const [questions, setQuestions] = useState([]);
  const [themes, setThemes] = useState([]);
  const [types, setTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterTheme, setFilterTheme] = useState('all');
  const [filterType, setFilterType] = useState('all');
  const [showForm, setShowForm] = useState(false);
  const [editingQuestion, setEditingQuestion] = useState(null);
  const [selectedQuestion, setSelectedQuestion] = useState(null);
  const [showDetails, setShowDetails] = useState(false);
  const [formData, setFormData] = useState({
    libelle: '',
    explication: '',
    themeId: '',
    typeQuestionId: '',
    reponses: []
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [questionsRes, themesRes, typesRes] = await Promise.all([
        fetch('/api/admin/questions'),
        fetch('/api/admin/themes'),
        fetch('/api/admin/types-questions')
      ]);

      if (questionsRes.ok) {
        const questionsData = await questionsRes.json();
        setQuestions(questionsData.questions || []);
      }
      if (themesRes.ok) {
        const themesData = await themesRes.json();
        setThemes(themesData.themes || []);
      }
      if (typesRes.ok) {
        const typesData = await typesRes.json();
        setTypes(typesData.types || []);
      }
    } catch (error) {
      console.error('Error fetching data:', error);
      toast.error('Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (formData.reponses.length === 0) {
      toast.error('Veuillez ajouter au moins une réponse');
      return;
    }

    const hasCorrectAnswer = formData.reponses.some(r => r.estCorrect);
    if (!hasCorrectAnswer) {
      toast.error('Veuillez marquer au moins une réponse comme correcte');
      return;
    }

    try {
      const url = editingQuestion 
        ? `/api/admin/questions/${editingQuestion.id}`
        : '/api/admin/questions';
      const method = editingQuestion ? 'PUT' : 'POST';

      // Convert IDs to numbers before sending
      const payload = {
        themeId: parseInt(formData.themeId, 10) || null,
        typeQuestionId: parseInt(formData.typeQuestionId, 10) || null,
        libelle: formData.libelle,
        explication: formData.explication,
        reponses: formData.reponses
      };

      console.log('Payload being sent:', payload);

      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        toast.success(editingQuestion ? 'Question modifiée' : 'Question ajoutée');
        setShowForm(false);
        setEditingQuestion(null);
        resetForm();
        fetchData();
      } else {
        const error = await response.json();
        toast.error(error.error || 'Erreur lors de l\'opération');
      }
    } catch (error) {
      console.error('Error saving question:', error);
      toast.error('Erreur de connexion au serveur');
    }
  };

  const handleDelete = async (questionId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer cette question ?')) {
      return;
    }

    try {
      const response = await fetch(`/api/admin/questions/${questionId}`, {
        method: 'DELETE'
      });

      if (response.ok) {
        toast.success('Question supprimée');
        fetchData();
      } else {
        const error = await response.json();
        toast.error(error.error || 'Erreur lors de la suppression');
      }
    } catch (error) {
      console.error('Error deleting question:', error);
      toast.error('Erreur de connexion au serveur');
    }
  };

  const handleEdit = (question) => {
    setEditingQuestion(question);
    setFormData({
      libelle: question.libelle,
      explication: question.explication || '',
      themeId: question.theme?.id || '',
      typeQuestionId: question.typeQuestion?.id || '',
      reponses: question.reponsesPossibles || []
    });
    setShowForm(true);
  };

  const openQuestionDetails = (question) => {
    setSelectedQuestion(question);
    setShowDetails(true);
  };

  const resetForm = () => {
    setFormData({
      libelle: '',
      explication: '',
      themeId: '',
      typeQuestionId: '',
      reponses: []
    });
  };

  const addReponse = () => {
    setFormData(prev => ({
      ...prev,
      reponses: [...prev.reponses, { libelle: '', estCorrect: false }]
    }));
  };

  const updateReponse = (index, field, value) => {
    setFormData(prev => ({
      ...prev,
      reponses: prev.reponses.map((r, i) => 
        i === index ? { ...r, [field]: value } : r
      )
    }));
  };

  const removeReponse = (index) => {
    setFormData(prev => ({
      ...prev,
      reponses: prev.reponses.filter((_, i) => i !== index)
    }));
  };

  const filteredQuestions = questions.filter(question => {
    const term = (searchTerm || '').toLowerCase().trim();
    const haystack = [
      question.libelle,
      question.explication,
      question.theme?.nom,
      question.typeQuestion?.nom
    ]
      .filter(Boolean)
      .join(' ')
      .toLowerCase();
    const matchesSearch = !term || haystack.includes(term);
    const matchesTheme = filterTheme === 'all' || String(question.theme?.id) === String(filterTheme);
    const matchesType = filterType === 'all' || String(question.typeQuestion?.id) === String(filterType);
    
    return matchesSearch && matchesTheme && matchesType;
  });

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
        <h1 className="text-3xl font-bold text-gray-900">Gestion des Questions</h1>
        <p className="text-gray-600 mt-2">Créez et gérez les questions et les thèmes</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Questions</p>
              <p className="text-2xl font-bold text-gray-900">{questions.length}</p>
            </div>
            <BookOpen className="h-8 w-8 text-blue-600" />
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Thèmes</p>
              <p className="text-2xl font-bold text-gray-900">{themes.length}</p>
            </div>
            <div className="h-8 w-8 bg-purple-100 rounded-full flex items-center justify-center">
              <span className="text-purple-600 font-bold">T</span>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Types de questions</p>
              <p className="text-2xl font-bold text-gray-900">{types.length}</p>
            </div>
            <div className="h-8 w-8 bg-green-100 rounded-full flex items-center justify-center">
              <span className="text-green-600 font-bold">?</span>
            </div>
          </div>
        </div>
      </div>

      {/* Actions */}
      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div className="flex-1 max-w-md">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
              <input
                type="text"
                placeholder="Rechercher une question..."
                className="input pl-10"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
          <div className="flex items-center space-x-4">
            <select
              className="input"
              value={filterTheme}
              onChange={(e) => setFilterTheme(e.target.value)}
            >
              <option value="all">Tous les thèmes</option>
              {themes.map(theme => (
                <option key={theme.id} value={theme.id}>{theme.nom}</option>
              ))}
            </select>
            <select
              className="input"
              value={filterType}
              onChange={(e) => setFilterType(e.target.value)}
            >
              <option value="all">Tous les types</option>
              {types.map(type => (
                <option key={type.id} value={type.id}>{type.nom}</option>
              ))}
            </select>
            <button
              onClick={() => {
                resetForm();
                setEditingQuestion(null);
                setShowForm(true);
              }}
              className="btn btn-primary flex items-center"
            >
              <Plus className="h-4 w-4 mr-2" />
              Ajouter une question
            </button>
          </div>
        </div>
      </div>

      {/* Questions Table */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Question
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Thème
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Type
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Réponses
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredQuestions.map((question) => (
                <tr key={question.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4">
                    <div className="max-w-xs">
                      <p className="text-sm font-medium text-gray-900 line-clamp-2">
                        {question.libelle}
                      </p>
                      {question.explication && (
                        <p className="text-xs text-gray-500 mt-1 line-clamp-1">
                          {question.explication}
                        </p>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                      {question.theme?.nom}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
                      {question.typeQuestion?.nom}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center space-x-1">
                      <span className="text-sm text-gray-900">
                        {question.reponsesPossibles?.length || 0}
                      </span>
                      <span className="text-xs text-green-600">
                        ({question.reponsesPossibles?.filter(r => r.estCorrect).length || 0} correctes)
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex items-center space-x-2">
                      <button
                        onClick={() => openQuestionDetails(question)}
                        className="text-blue-600 hover:text-blue-900"
                        title="Voir les détails"
                      >
                        <Eye className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => handleEdit(question)}
                        className="text-blue-600 hover:text-blue-900"
                        title="Modifier"
                      >
                        <Edit className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(question.id)}
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
        
        {filteredQuestions.length === 0 && (
          <div className="text-center py-12">
            <BookOpen className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">Aucune question trouvée</p>
          </div>
        )}
      </div>

      {/* Question Form Modal */}
      {showForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">
              {editingQuestion ? 'Modifier la question' : 'Ajouter une question'}
            </h3>
            
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="label">Question *</label>
                <textarea
                  className="input"
                  rows={3}
                  value={formData.libelle}
                  onChange={(e) => setFormData(prev => ({ ...prev, libelle: e.target.value }))}
                  required
                />
              </div>

              <div>
                <label className="label">Explication</label>
                <textarea
                  className="input"
                  rows={2}
                  value={formData.explication}
                  onChange={(e) => setFormData(prev => ({ ...prev, explication: e.target.value }))}
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="label">Thème *</label>
                  <select
                    className="input"
                    value={formData.themeId}
                    onChange={(e) => setFormData(prev => ({ ...prev, themeId: e.target.value }))}
                    required
                  >
                    <option value="">Sélectionner un thème</option>
                    {themes.map(theme => (
                      <option key={theme.id} value={theme.id}>{theme.nom}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="label">Type de question *</label>
                  <select
                    className="input"
                    value={formData.typeQuestionId}
                    onChange={(e) => setFormData(prev => ({ ...prev, typeQuestionId: e.target.value }))}
                    required
                  >
                    <option value="">Sélectionner un type</option>
                    {types.map(type => (
                      <option key={type.id} value={type.id}>{type.nom}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <div className="flex items-center justify-between mb-2">
                  <label className="label">Réponses possibles</label>
                  <button
                    type="button"
                    onClick={addReponse}
                    className="btn btn-secondary text-sm"
                  >
                    <Plus className="h-4 w-4 mr-1" />
                    Ajouter une réponse
                  </button>
                </div>
                
                <div className="space-y-2">
                  {formData.reponses.map((reponse, index) => (
                    <div key={index} className="flex items-center space-x-2">
                      <input
                        type="text"
                        className="input flex-1"
                        placeholder="Réponse..."
                        value={reponse.libelle}
                        onChange={(e) => updateReponse(index, 'libelle', e.target.value)}
                        required
                      />
                      <label className="flex items-center cursor-pointer">
                        <input
                          type="checkbox"
                          className="mr-2"
                          checked={reponse.estCorrect}
                          onChange={(e) => updateReponse(index, 'estCorrect', e.target.checked)}
                        />
                        <CheckCircle className={`h-5 w-5 ${reponse.estCorrect ? 'text-green-600' : 'text-gray-400'}`} />
                      </label>
                      {formData.reponses.length > 1 && (
                        <button
                          type="button"
                          onClick={() => removeReponse(index)}
                          className="text-red-600 hover:text-red-900"
                        >
                          <XCircle className="h-5 w-5" />
                        </button>
                      )}
                    </div>
                  ))}
                </div>
              </div>

              <div className="flex justify-end space-x-4 pt-4">
                <button
                  type="button"
                  onClick={() => {
                    setShowForm(false);
                    setEditingQuestion(null);
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
                  {editingQuestion ? 'Modifier' : 'Ajouter'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showDetails && selectedQuestion && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-start mb-4">
              <h3 className="text-lg font-semibold text-gray-900">Détails de la question</h3>
              <button
                onClick={() => {
                  setShowDetails(false);
                  setSelectedQuestion(null);
                }}
                className="text-gray-400 hover:text-gray-600"
              >
                ×
              </button>
            </div>

            <div className="space-y-4">
              <div className="bg-gray-50 rounded-lg p-4">
                <div className="text-sm text-gray-600 mb-1">Question</div>
                <div className="text-base font-medium text-gray-900">{selectedQuestion.libelle}</div>
                {selectedQuestion.explication && (
                  <div className="mt-3">
                    <div className="text-sm text-gray-600 mb-1">Explication</div>
                    <div className="text-sm text-gray-800 whitespace-pre-wrap">{selectedQuestion.explication}</div>
                  </div>
                )}
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="bg-blue-50 rounded-lg p-4">
                  <div className="text-sm text-gray-600 mb-1">Thème</div>
                  <div className="text-sm font-medium text-gray-900">{selectedQuestion.theme?.nom || '-'}</div>
                </div>
                <div className="bg-purple-50 rounded-lg p-4">
                  <div className="text-sm text-gray-600 mb-1">Type</div>
                  <div className="text-sm font-medium text-gray-900">{selectedQuestion.typeQuestion?.nom || '-'}</div>
                </div>
              </div>

              <div>
                <div className="text-sm font-medium text-gray-900 mb-2">Réponses possibles</div>
                {Array.isArray(selectedQuestion.reponsesPossibles) && selectedQuestion.reponsesPossibles.length > 0 ? (
                  <div className="space-y-2">
                    {selectedQuestion.reponsesPossibles.map((r, idx) => (
                      <div key={r.id || idx} className="flex items-start justify-between gap-3 p-3 rounded border">
                        <div className="text-sm text-gray-900 whitespace-pre-wrap">{r.libelle}</div>
                        <div className="flex items-center gap-2 flex-shrink-0">
                          {r.estCorrect ? (
                            <CheckCircle className="h-5 w-5 text-green-600" />
                          ) : (
                            <XCircle className="h-5 w-5 text-gray-400" />
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-sm text-gray-600">Aucune réponse.</div>
                )}
              </div>
            </div>

            <div className="mt-6 flex justify-end">
              <button
                onClick={() => {
                  setShowDetails(false);
                  setSelectedQuestion(null);
                }}
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

export default TestsManagement;
