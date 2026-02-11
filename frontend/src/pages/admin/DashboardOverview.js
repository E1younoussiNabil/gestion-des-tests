import React, { useState, useEffect } from 'react';
import { 
  Users, 
  Calendar, 
  BookOpen, 
  BarChart3, 
  TrendingUp, 
  Clock,
  CheckCircle,
  AlertCircle
} from 'lucide-react';
import toast from 'react-hot-toast';

const DashboardOverview = () => {
  const [stats, setStats] = useState({
    totalCandidates: 0,
    activeTests: 0,
    completedTests: 0,
    averageScore: 0,
    pendingValidations: 0,
    todaySessions: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardStats();
  }, []);

  const fetchDashboardStats = async () => {
    try {
      const response = await fetch('/api/admin/dashboard/stats');
      if (response.ok) {
        const data = await response.json();
        setStats(data);
      }
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
      toast.error('Erreur lors du chargement des statistiques');
    } finally {
      setLoading(false);
    }
  };

  const statCards = [
    {
      title: 'Total Candidats',
      value: stats.totalCandidates,
      icon: Users,
      color: 'blue',
      change: '+12%',
      changeType: 'positive'
    },
    {
      title: 'Tests Actifs',
      value: stats.activeTests,
      icon: Clock,
      color: 'yellow',
      change: '+5%',
      changeType: 'positive'
    },
    {
      title: 'Tests Terminés',
      value: stats.completedTests,
      icon: CheckCircle,
      color: 'green',
      change: '+18%',
      changeType: 'positive'
    },
    {
      title: 'Score Moyen',
      value: `${stats.averageScore}%`,
      icon: BarChart3,
      color: 'purple',
      change: '+2%',
      changeType: 'positive'
    },
    {
      title: 'Validations en attente',
      value: stats.pendingValidations,
      icon: AlertCircle,
      color: 'orange',
      change: '-3%',
      changeType: 'negative'
    },
    {
      title: 'Sessions du jour',
      value: stats.todaySessions,
      icon: Calendar,
      color: 'indigo',
      change: '+8%',
      changeType: 'positive'
    }
  ];

  const getColorClasses = (color) => {
    const colors = {
      blue: 'bg-blue-100 text-blue-600',
      yellow: 'bg-yellow-100 text-yellow-600',
      green: 'bg-green-100 text-green-600',
      purple: 'bg-purple-100 text-purple-600',
      orange: 'bg-orange-100 text-orange-600',
      indigo: 'bg-indigo-100 text-indigo-600'
    };
    return colors[color] || 'bg-gray-100 text-gray-600';
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
        <h1 className="text-3xl font-bold text-gray-900">Accueil</h1>
        <p className="text-gray-600 mt-2">Vue d'ensemble de l'application</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        {statCards.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <div key={index} className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center justify-between mb-4">
                <div className={`p-3 rounded-lg ${getColorClasses(stat.color)}`}>
                  <Icon className="h-6 w-6" />
                </div>
                <div className={`flex items-center text-sm font-medium ${
                  stat.changeType === 'positive' ? 'text-green-600' : 'text-red-600'
                }`}>
                  <TrendingUp className={`h-4 w-4 mr-1 ${
                    stat.changeType === 'negative' ? 'rotate-180' : ''
                  }`} />
                  {stat.change}
                </div>
              </div>
              <div>
                <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
                <p className="text-gray-600 text-sm">{stat.title}</p>
              </div>
            </div>
          );
        })}
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Actions rapides</h2>
          <div className="grid grid-cols-2 gap-4">
            <button className="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors text-left">
              <Users className="h-8 w-8 text-blue-600 mb-2" />
              <p className="font-medium text-gray-900">Valider les candidats</p>
              <p className="text-sm text-gray-600">{stats.pendingValidations} en attente</p>
            </button>
            <button className="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors text-left">
              <Calendar className="h-8 w-8 text-green-600 mb-2" />
              <p className="font-medium text-gray-900">Créer un créneau</p>
              <p className="text-sm text-gray-600">Ajouter une session</p>
            </button>
            <button className="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors text-left">
              <BookOpen className="h-8 w-8 text-purple-600 mb-2" />
              <p className="font-medium text-gray-900">Ajouter une question</p>
              <p className="text-sm text-gray-600">Étendre la banque</p>
            </button>
            <button className="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors text-left">
              <BarChart3 className="h-8 w-8 text-orange-600 mb-2" />
              <p className="font-medium text-gray-900">Voir les rapports</p>
              <p className="text-sm text-gray-600">Analyser les résultats</p>
            </button>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Activité récente</h2>
          <div className="space-y-4">
            <div className="flex items-center justify-between py-3 border-b border-gray-100">
              <div className="flex items-center">
                <div className="w-2 h-2 bg-green-500 rounded-full mr-3"></div>
                <div>
                  <p className="font-medium text-gray-900">Nouveau candidat inscrit</p>
                  <p className="text-sm text-gray-600">Jean Dupont - Informatique</p>
                </div>
              </div>
              <span className="text-sm text-gray-500">Il y a 5 min</span>
            </div>
            <div className="flex items-center justify-between py-3 border-b border-gray-100">
              <div className="flex items-center">
                <div className="w-2 h-2 bg-blue-500 rounded-full mr-3"></div>
                <div>
                  <p className="font-medium text-gray-900">Test terminé</p>
                  <p className="text-sm text-gray-600">Marie Martin - 85%</p>
                </div>
              </div>
              <span className="text-sm text-gray-500">Il y a 15 min</span>
            </div>
            <div className="flex items-center justify-between py-3 border-b border-gray-100">
              <div className="flex items-center">
                <div className="w-2 h-2 bg-yellow-500 rounded-full mr-3"></div>
                <div>
                  <p className="font-medium text-gray-900">Créneau créé</p>
                  <p className="text-sm text-gray-600">Demain 14:00 - 25 places</p>
                </div>
              </div>
              <span className="text-sm text-gray-500">Il y a 1 heure</span>
            </div>
            <div className="flex items-center justify-between py-3">
              <div className="flex items-center">
                <div className="w-2 h-2 bg-purple-500 rounded-full mr-3"></div>
                <div>
                  <p className="font-medium text-gray-900">Question ajoutée</p>
                  <p className="text-sm text-gray-600">Mathématiques - Calcul intégral</p>
                </div>
              </div>
              <span className="text-sm text-gray-500">Il y a 2 heures</span>
            </div>
          </div>
        </div>
      </div>

      {/* Performance Chart */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-xl font-semibold text-gray-900 mb-4">Performance des 7 derniers jours</h2>
        <div className="h-64 flex items-center justify-center bg-gray-50 rounded-lg">
          <div className="text-center text-gray-500">
            <BarChart3 className="h-12 w-12 mx-auto mb-2" />
            <p>Graphique des performances</p>
            <p className="text-sm">(Intégration d'un graphique à implémenter)</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardOverview;
