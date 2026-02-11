import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { 
  Users, 
  BookOpen, 
  Clock,
  Award,
  ArrowRight
} from 'lucide-react';

const Home = () => {
  const { user } = useAuth();

  const features = [
    {
      icon: BookOpen,
      title: 'Tests Variés',
      description: 'Accédez à une large gamme de tests dans différents domaines : mathématiques, informatique, physique, chimie, français et anglais.'
    },
    {
      icon: Clock,
      title: 'Gestion du Temps',
      description: 'Chaque question est chronométrée pour simuler des conditions réelles d\'examen.'
    },
    {
      icon: Users,
      title: 'Inscription Facile',
      description: 'Inscrivez-vous en quelques clics et choisissez vos créneaux horaires.'
    },
    {
      icon: Award,
      title: 'Résultats Instantanés',
      description: 'Obtenez vos résultats immédiatement après avoir terminé le test.'
    }
  ];

  const steps = [
    { number: 1, title: 'Inscription', description: 'Créez votre compte et remplissez vos informations' },
    { number: 2, title: 'Choix du Créneau', description: 'Sélectionnez la date et l\'heure qui vous conviennent' },
    { number: 3, title: 'Passation du Test', description: 'Connectez-vous avec votre code session et passez le test' },
    { number: 4, title: 'Résultats', description: 'Consultez vos résultats et votre performance' }
  ];

  return (
    <div className="min-h-screen">
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-blue-600 to-indigo-700 text-white">
        <div className="container mx-auto px-4 py-20">
          <div className="max-w-4xl mx-auto text-center">
            <h1 className="text-4xl md:text-6xl font-bold mb-6">
              Plateforme de Tests en Ligne
            </h1>
            <p className="text-xl md:text-2xl mb-8 text-blue-100">
              Évaluez vos connaissances avec notre système de tests interactif et chronométré
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              {!user ? (
                <>
                  <Link
                    to="/inscription"
                    className="btn bg-white text-blue-600 hover:bg-gray-100 px-8 py-3 text-lg font-semibold inline-flex items-center justify-center"
                  >
                    Commencer maintenant
                    <ArrowRight className="ml-2 h-5 w-5" />
                  </Link>
                  <Link
                    to="/connexion"
                    className="btn border-2 border-white text-white hover:bg-white hover:text-blue-600 px-8 py-3 text-lg font-semibold"
                  >
                    Se connecter
                  </Link>
                  <Link
                    to="/code-session"
                    className="btn border-2 border-white text-white hover:bg-white hover:text-blue-600 px-8 py-3 text-lg font-semibold"
                  >
                    Mon code session
                  </Link>
                  <Link
                    to="/admin"
                    className="btn bg-gray-800 text-white hover:bg-gray-700 px-8 py-3 text-lg font-semibold"
                  >
                    Administration
                  </Link>
                </>
              ) : (
                user.role === 'admin' ? (
                  <Link
                    to="/admin"
                    className="btn bg-gray-800 text-white hover:bg-gray-700 px-8 py-3 text-lg font-semibold"
                  >
                    Administration
                  </Link>
                ) : (
                  <>
                    <Link
                      to="/test"
                      className="btn bg-white text-blue-600 hover:bg-gray-100 px-8 py-3 text-lg font-semibold inline-flex items-center justify-center"
                    >
                      Passer un test
                      <ArrowRight className="ml-2 h-5 w-5" />
                    </Link>
                    <Link
                      to="/resultats"
                      className="btn border-2 border-white text-white hover:bg-white hover:text-blue-600 px-8 py-3 text-lg font-semibold"
                    >
                      Mes résultats
                    </Link>
                  </>
                )
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 bg-white">
        <div className="container mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              Pourquoi choisir notre plateforme ?
            </h2>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto">
              Une solution complète pour passer des tests en ligne de manière simple et efficace
            </p>
          </div>
          
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            {features.map((feature, index) => (
              <div key={index} className="text-center group">
                <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4 group-hover:bg-blue-600 transition-colors">
                  <feature.icon className="h-8 w-8 text-blue-600 group-hover:text-white transition-colors" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-2">
                  {feature.title}
                </h3>
                <p className="text-gray-600">
                  {feature.description}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* How it works */}
      <section className="py-20 bg-gray-50">
        <div className="container mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              Comment ça fonctionne ?
            </h2>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto">
              Quatre étapes simples pour passer votre test
            </p>
          </div>
          
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            {steps.map((step, index) => (
              <div key={index} className="relative">
                <div className="bg-white rounded-lg shadow-lg p-6 h-full">
                  <div className="bg-blue-600 text-white w-10 h-10 rounded-full flex items-center justify-center font-bold mb-4">
                    {step.number}
                  </div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    {step.title}
                  </h3>
                  <p className="text-gray-600">
                    {step.description}
                  </p>
                </div>
                {index < steps.length - 1 && (
                  <div className="hidden lg:block absolute top-1/2 -right-4 transform -translate-y-1/2">
                    <ArrowRight className="h-6 w-6 text-blue-600" />
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-blue-600">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-3xl md:text-4xl font-bold text-white mb-4">
            Prêt à commencer ?
          </h2>
          <p className="text-xl text-blue-100 mb-8 max-w-2xl mx-auto">
            Rejoignez des milliers d'étudiants qui utilisent notre plateforme pour tester leurs connaissances
          </p>
          {!user && (
            <Link
              to="/inscription"
              className="btn bg-white text-blue-600 hover:bg-gray-100 px-8 py-3 text-lg font-semibold inline-flex items-center"
            >
              S'inscrire maintenant
              <ArrowRight className="ml-2 h-5 w-5" />
            </Link>
          )}
        </div>
      </section>
    </div>
  );
};

export default Home;
