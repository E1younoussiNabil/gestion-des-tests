# Mes vidéos explicatifs :Liens descriptions youtube: 

part1: https://www.youtube.com/watch?v=nqZkN-5oRd0   #### cadre générale,diagrammes cas d'utilisation et classes, étapes de déploiement et ecpace admin.


part2: https://www.youtube.com/watch?v=82Wku0DkkFU   #### création creneaux et demande de passation de tests.


part3: https://www.youtube.com/watch?v=sKIManpjJfk   #### moment final de soumission de tests et explication d'architécture choisi.

# Application de Gestion des Tests en Ligne

## Architecture
- **Backend**: Jakarta EE (WildFly/Payara)
- **Frontend**: React.js
- **Base de données**: MySQL (XAMPP)
- **Communication**: REST API

## Modules
1. **Gestion des candidats** - Inscription, connexion, choix de créneaux
2. **Gestion des tests** - Passage des tests avec timer
3. **Gestion des résultats** - Calcul et affichage des résultats
4. **Administration** - Gestion des questions, paramètres, visualisation

## Structure du projet
```
gestion/
├── backend/                 # Application Jakarta EE
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/gestiontests/
│   │   │   │       ├── entity/
│   │   │   │       ├── repository/
│   │   │   │       ├── service/
│   │   │   │       ├── rest/
│   │   │   │       └── config/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── README.md
├── frontend/               # Application React
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   └── utils/
│   ├── package.json
│   └── README.md
├── database/               # Scripts SQL
│   ├── schema.sql
│   └── data.sql
└── README.md
```

## Installation
1. Configurer XAMPP avec MySQL
2. Importer le schéma de la base de données
3. Démarrer le backend Jakarta EE
4. Démarrer l'application React

## Démarrage rapide

### Backend (Jakarta EE)
1. Assurez-vous d'avoir WildFly ou Payara installé
2. Déployez le fichier WAR généré par Maven
3. Configurez la base de données MySQL avec les scripts dans `database/`

### Frontend (React)
```bash
cd frontend
npm install
npm start
```

### Base de données
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/data.sql
```

## Fonctionnalités principales
- Inscription des candidats avec validation
- Gestion des créneaux horaires
- Tests aléatoires par thème
- Timer automatique par question
- Résultats instantanés
- Interface d'administration complète
