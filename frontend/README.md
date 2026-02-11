# Frontend React - Gestion Tests en Ligne

Application frontend React pour la plateforme de gestion des tests en ligne.

## Technologies utilisées

- **React 18** - Framework JavaScript
- **React Router** - Routage client
- **Tailwind CSS** - Framework CSS
- **Lucide React** - Icônes
- **React Hook Form** - Gestion des formulaires
- **React Hot Toast** - Notifications
- **Axios** - Client HTTP

## Structure du projet

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/          # Composants réutilisables
│   │   ├── Navbar.js
│   │   └── ProtectedRoute.js
│   ├── contexts/           # Contextes React
│   │   └── AuthContext.js
│   ├── pages/              # Pages de l'application
│   │   ├── Home.js
│   │   ├── Inscription.js
│   │   ├── Connexion.js
│   │   ├── Test.js
│   │   ├── Resultats.js
│   │   └── admin/         # Pages d'administration
│   │       ├── AdminDashboard.js
│   │       ├── DashboardOverview.js
│   │       ├── CandidatesManagement.js
│   │       ├── TestsManagement.js
│   │       ├── ResultsManagement.js
│   │       └── ScheduleManagement.js
│   ├── App.js              # Composant principal
│   ├── index.js            # Point d'entrée
│   └── index.css           # Styles globaux
├── package.json
├── tailwind.config.js
└── postcss.config.js
```

## Installation

1. Installer les dépendances :
```bash
npm install
```

2. Démarrer l'application :
```bash
npm start
```

L'application sera disponible sur `http://localhost:3000`

## Fonctionnalités

### Pour les candidats
- **Inscription** : Création de compte avec choix de créneau horaire
- **Connexion** : Accès via code de session
- **Passation de test** : Interface chronométrée avec navigation entre questions
- **Résultats** : Consultation des performances et statistiques détaillées

### Pour les administrateurs
- **Tableau de bord** : Vue d'ensemble avec statistiques
- **Gestion des candidats** : Validation, rejet, envoi de codes
- **Gestion des tests** : Création et modification de questions
- **Gestion des résultats** : Consultation et export des résultats
- **Gestion des créneaux** : Planification des sessions de test

## Configuration

### Variables d'environnement
Créer un fichier `.env` à la racine du projet :
```
REACT_APP_API_URL=http://localhost:8080/api
```

### Proxy
L'application est configurée pour utiliser un proxy vers le backend sur `http://localhost:8080`.

## Développement

### Styles
L'application utilise Tailwind CSS avec des composants personnalisés définis dans `index.css`.

### Composants
- Les composants sont organisés par fonctionnalité
- Les hooks personnalisés sont dans le dossier `hooks`
- Les utilitaires sont dans le dossier `utils`

### Routage
Le routage est géré par React Router avec des routes protégées pour l'administration.

## Build

Pour créer une version de production :
```bash
npm run build
```

Le build sera généré dans le dossier `build`.
