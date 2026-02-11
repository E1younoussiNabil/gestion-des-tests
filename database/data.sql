-- Données initiales pour l'application de gestion des tests en ligne

USE dbgestiontest;

SET NAMES utf8mb4;

-- Insertion des thèmes
INSERT INTO themes (nom, description) VALUES
('Mathématiques', 'Questions sur les mathématiques fondamentales et appliquées'),
('Informatique', 'Questions sur la programmation et les technologies de l''information'),
('Physique', 'Questions sur les principes physiques et applications'),
('Chimie', 'Questions sur la chimie organique et inorganique'),
('Français', 'Questions sur la langue et la littérature française'),
('Anglais', 'Questions sur la langue et la culture anglaise');

-- Insertion des types de questions
INSERT INTO types_question (nom, description) VALUES
('UNIQUE', 'Question à réponse unique - une seule réponse possible'),
('MULTIPLE', 'Question à réponses multiples - plusieurs réponses possibles'),
('TEXTE', 'Question à réponse textuelle libre');

-- Insertion des administrateurs (mot de passe: admin123)
INSERT INTO administrateurs (username, password, email, nom, prenom) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@gestiontests.com', 'Administrateur', 'Système');

-- Insertion des paramètres par défaut
INSERT INTO parametres (nom_param, valeur, description) VALUES
('NOMBRE_QUESTIONS_PAR_THEME', '5', 'Nombre de questions à tirer par thème pour chaque test'),
('TEMPS_QUESTION_PAR_DEFAUT', '120', 'Temps par défaut par question en secondes'),
('TEMPS_AVANT_DEMARRAGE', '5', 'Temps d''attente avant de pouvoir démarrer le test en minutes'),
('VALIDATION_INSCRIPTION', 'true', 'Nécessite une validation admin pour les inscriptions'),
('EMAIL_HOST', 'smtp.gmail.com', 'Serveur SMTP pour l''envoi d''emails'),
('EMAIL_PORT', '587', 'Port du serveur SMTP'),
('EMAIL_USERNAME', '', 'Nom d''utilisateur pour le serveur SMTP'),
('EMAIL_PASSWORD', '', 'Mot de passe pour le serveur SMTP');

-- Insertion des questions exemples
-- Mathématiques
INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(1, 1, 'Quelle est la valeur de 2 + 2 ?', 'Addition simple'),
(1, 1, 'Combien font 7 × 8 ?', 'Multiplication'),
(1, 2, 'Quels sont les nombres premiers suivants : 2, 3, 5, 7, 11 ?', 'Identification des nombres premiers'),
(1, 1, 'Quelle est la racine carrée de 64 ?', 'Racine carrée'),
(1, 1, 'Combien font 15% de 200 ?', 'Pourcentage'),
(1, 2, 'Quelles opérations sont correctes : 10+5=15, 8×3=24, 20-5=18, 12÷4=3 ?', 'Vérification d''opérations');

-- Informatique
INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(2, 1, 'Quel langage est principalement utilisé pour le développement web frontend ?', 'Langages web'),
(2, 2, 'Quels sont des systèmes de gestion de bases de données : MySQL, PostgreSQL, Excel, MongoDB ?', 'SGBD'),
(2, 1, 'Que signifie l''acronyme API ?', 'Définition technique'),
(2, 1, 'Quel protocole est utilisé pour la communication web sécurisée ?', 'Protocoles réseau'),
(2, 2, 'Quels sont des paradigmes de programmation : Orienté objet, Fonctionnel, Impératif, Visuel ?', 'Paradigmes'),
(2, 1, 'Quel est le rôle d''un compilateur ?', 'Compilation');

-- Physique
INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(3, 1, 'Quelle est l''unité SI de la force ?', 'Unités de mesure'),
(3, 1, 'Qui a formulé la loi de la gravitation universelle ?', 'Histoire des sciences'),
(3, 2, 'Quels sont des états de la matière : Solide, Liquide, Gazeux, Plasmique ?', 'États de la matière'),
(3, 1, 'Quelle est la vitesse de la lumière dans le vide ?', 'Constantes physiques'),
(3, 1, 'Quel est le principe d''Archimède ?', 'Principes physiques'),
(3, 2, 'Quelles formes d''énergie existent : Cinétique, Potentielle, Thermique, Magnétique ?', 'Types d''énergie');

-- Chimie
INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(4, 1, 'Quel est le symbole chimique de l''or ?', 'Éléments chimiques'),
(4, 1, 'Combien d''électrons peut contenir la couche K d''un atome ?', 'Structure atomique'),
(4, 2, 'Quelles sont des réactions chimiques : Oxydation, Réduction, Neutralisation, Évaporation ?', 'Types de réactions'),
(4, 1, 'Quel est le pH de l''eau pure ?', 'Acidité'),
(4, 1, 'Quelle est la formule chimique de l''eau ?', 'Molécules'),
(4, 2, 'Quels sont des gaz nobles : Hélium, Néon, Argon, Oxygène ?', 'Classification périodique');

-- Français
INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(5, 1, 'Quel est le pluriel de "cheval" ?', 'Grammaire'),
(5, 2, 'Quels sont des temps de l''indicatif : Présent, Imparfait, Futur simple, Conditionnel ?', 'Conjugaison'),
(5, 1, 'Qui a écrit "Les Misérables" ?', 'Littérature'),
(5, 1, 'Quel est le synonyme de "rapidement" ?', 'Vocabulaire'),
(5, 1, 'Combien de temps comporte le passé composé ?', 'Grammaire'),
(5, 2, 'Quels sont des figures de style : Métaphore, Comparaison, Allitération, Définition ?', 'Figures de style');

-- Anglais
INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(6, 1, 'Comment dit-on "bonjour" en anglais ?', 'Vocabulaire de base'),
(6, 2, 'Quels sont des temps verbaux anglais : Present Simple, Past Continuous, Future Perfect, Subjonctif ?', 'Temps anglais'),
(6, 1, 'Quel est le superlatif de "good" ?', 'Comparatifs et superlatifs'),
(6, 1, 'Qui a écrit "Romeo and Juliet" ?', 'Littérature anglaise'),
(6, 1, 'Comment se conjugue "to be" au présent à la première personne du singulier ?', 'Conjugaison'),
(6, 2, 'Quels sont des pays anglophones : Canada, Australie, France, Nouvelle-Zélande ?', 'Géographie linguistique');

-- Insertion des réponses possibles
-- Réponses pour les questions de mathématiques
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(1, '3', FALSE),
(1, '4', TRUE),
(1, '5', FALSE),
(1, '6', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(2, '54', FALSE),
(2, '56', TRUE),
(2, '58', FALSE),
(2, '60', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(3, '2', TRUE),
(3, '3', TRUE),
(3, '5', TRUE),
(3, '7', TRUE),
(3, '9', FALSE),
(3, '11', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(4, '6', FALSE),
(4, '7', FALSE),
(4, '8', TRUE),
(4, '9', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(5, '25', FALSE),
(5, '30', TRUE),
(5, '35', FALSE),
(5, '40', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(6, '10+5=15', TRUE),
(6, '8×3=24', TRUE),
(6, '20-5=18', FALSE),
(6, '12÷4=3', TRUE);

-- Réponses pour les questions d'informatique
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(7, 'Java', FALSE),
(7, 'JavaScript', TRUE),
(7, 'Python', FALSE),
(7, 'C++', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(8, 'MySQL', TRUE),
(8, 'PostgreSQL', TRUE),
(8, 'Excel', FALSE),
(8, 'MongoDB', TRUE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(9, 'Application Programming Interface', TRUE),
(9, 'Advanced Programming Integration', FALSE),
(9, 'Automated Process Interface', FALSE),
(9, 'Application Process Integration', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(10, 'HTTP', FALSE),
(10, 'HTTPS', TRUE),
(10, 'FTP', FALSE),
(10, 'SSH', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(11, 'Orienté objet', TRUE),
(11, 'Fonctionnel', TRUE),
(11, 'Impératif', TRUE),
(11, 'Visuel', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(12, 'Exécuter le code directement', FALSE),
(12, 'Traduire le code en langage machine', TRUE),
(12, 'Déboguer le code', FALSE),
(12, 'Optimiser le code', FALSE);

-- Réponses pour les questions de physique
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(13, 'Newton', TRUE),
(13, 'Pascal', FALSE),
(13, 'Einstein', FALSE),
(13, 'Coulomb', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(14, 'Isaac Newton', TRUE),
(14, 'Albert Einstein', FALSE),
(14, 'Galileo Galilei', FALSE),
(14, 'Niels Bohr', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(15, 'Solide', TRUE),
(15, 'Liquide', TRUE),
(15, 'Gazeux', TRUE),
(15, 'Plasmique', TRUE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(16, '299 792 458 m/s', TRUE),
(16, '300 000 km/h', FALSE),
(16, '299 792 km/s', FALSE),
(16, '3×10⁸ m/s', TRUE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(17, 'Un corps plongé dans un fluide subit une poussée verticale vers le haut', TRUE),
(17, 'La somme des forces est nulle à l''équilibre', FALSE),
(17, 'L''énergie se conserve', FALSE),
(17, 'L''action est égale à la réaction', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(18, 'Cinétique', TRUE),
(18, 'Potentielle', TRUE),
(18, 'Thermique', TRUE),
(18, 'Magnétique', TRUE);

-- Réponses pour les questions de chimie
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(19, 'Ag', FALSE),
(19, 'Au', TRUE),
(19, 'Go', FALSE),
(19, 'Gd', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(20, '2', TRUE),
(20, '4', FALSE),
(20, '6', FALSE),
(20, '8', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(21, 'Oxydation', TRUE),
(21, 'Réduction', TRUE),
(21, 'Neutralisation', TRUE),
(21, 'Évaporation', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(22, '6', FALSE),
(22, '7', TRUE),
(22, '8', FALSE),
(22, '9', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(23, 'H₂O', TRUE),
(23, 'HO', FALSE),
(23, 'H₂O₂', FALSE),
(23, 'OH', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(24, 'Hélium', TRUE),
(24, 'Néon', TRUE),
(24, 'Argon', TRUE),
(24, 'Oxygène', FALSE);

-- Réponses pour les questions de français
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(25, 'Chevals', FALSE),
(25, 'Chevaux', TRUE),
(25, 'Chevalx', FALSE),
(25, 'Cheveaux', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(26, 'Présent', TRUE),
(26, 'Imparfait', TRUE),
(26, 'Futur simple', TRUE),
(26, 'Conditionnel', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(27, 'Victor Hugo', TRUE),
(27, 'Alexandre Dumas', FALSE),
(27, 'Émile Zola', FALSE),
(27, 'Marcel Proust', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(28, 'Lentement', FALSE),
(28, 'Vite', FALSE),
(28, 'Rapidement', TRUE),
(28, 'Doucement', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(29, '1', FALSE),
(29, '2', TRUE),
(29, '3', FALSE),
(29, '4', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(30, 'Métaphore', TRUE),
(30, 'Comparaison', TRUE),
(30, 'Allitération', TRUE),
(30, 'Définition', FALSE);

-- Réponses pour les questions d'anglais
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(31, 'Hello', TRUE),
(31, 'Hi', FALSE),
(31, 'Good morning', FALSE),
(31, 'Hey', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(32, 'Present Simple', TRUE),
(32, 'Past Continuous', TRUE),
(32, 'Future Perfect', TRUE),
(32, 'Subjonctif', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(33, 'Gooder', FALSE),
(33, 'Better', FALSE),
(33, 'Best', TRUE),
(33, 'Goodest', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(34, 'William Shakespeare', TRUE),
(34, 'Charles Dickens', FALSE),
(34, 'Jane Austen', FALSE),
(34, 'George Orwell', FALSE);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(35, 'I be', FALSE),
(35, 'I am', TRUE),
(35, 'I is', FALSE),
(35, 'I are', FALSE);

SET @q_anglo_pays := (
    SELECT id
    FROM questions
    WHERE libelle = 'Quels sont des pays anglophones : Canada, Australie, France, Nouvelle-Zélande ?'
    ORDER BY id DESC
    LIMIT 1
);

INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(COALESCE(@q_anglo_pays, 36), 'Canada', TRUE),
(COALESCE(@q_anglo_pays, 36), 'Australie', TRUE),
(COALESCE(@q_anglo_pays, 36), 'France', FALSE),
(COALESCE(@q_anglo_pays, 36), 'Nouvelle-Zélande', TRUE);

-- Insertion de quelques créneaux horaires pour les tests
-- INSERT INTO creneaux_horaires (date_exam, heure_debut, heure_fin, duree_minutes, places_disponibles) VALUES
-- (CURDATE() + INTERVAL 1 DAY, '09:00:00', '11:00:00', 120, 30),
-- (CURDATE() + INTERVAL 1 DAY, '14:00:00', '16:00:00', 120, 30),
-- (CURDATE() + INTERVAL 2 DAY, '10:00:00', '12:00:00', 120, 25),
-- (CURDATE() + INTERVAL 2 DAY, '15:00:00', '17:00:00', 120, 25),
-- (CURDATE() + INTERVAL 3 DAY, '09:00:00', '11:00:00', 120, 30),
-- (CURDATE() + INTERVAL 3 DAY, '14:00:00', '16:00:00', 120, 30);

-- Créneaux de 10 minutes (dev/test) : à partir de maintenant, toutes les 10 minutes pendant 24h
-- INSERT INTO creneaux_horaires (date_exam, heure_debut, heure_fin, duree_minutes, places_disponibles)
-- SELECT
--     DATE(TIMESTAMPADD(MINUTE, n * 10, start_dt)) AS date_exam,
--     TIME(TIMESTAMPADD(MINUTE, n * 10, start_dt)) AS heure_debut,
--     TIME(TIMESTAMPADD(MINUTE, (n + 1) * 10, start_dt)) AS heure_fin,
--     10 AS duree_minutes,
--     5 AS places_disponibles
-- FROM (
--     SELECT NOW() AS start_dt
-- ) t
-- JOIN (
--     SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
--     UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19
--     UNION ALL SELECT 20 UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29
--     UNION ALL SELECT 30 UNION ALL SELECT 31 UNION ALL SELECT 32 UNION ALL SELECT 33 UNION ALL SELECT 34 UNION ALL SELECT 35 UNION ALL SELECT 36 UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39
--     UNION ALL SELECT 40 UNION ALL SELECT 41 UNION ALL SELECT 42 UNION ALL SELECT 43 UNION ALL SELECT 44 UNION ALL SELECT 45 UNION ALL SELECT 46 UNION ALL SELECT 47 UNION ALL SELECT 48 UNION ALL SELECT 49
--     UNION ALL SELECT 50 UNION ALL SELECT 51 UNION ALL SELECT 52 UNION ALL SELECT 53 UNION ALL SELECT 54 UNION ALL SELECT 55 UNION ALL SELECT 56 UNION ALL SELECT 57 UNION ALL SELECT 58 UNION ALL SELECT 59
--     UNION ALL SELECT 60 UNION ALL SELECT 61 UNION ALL SELECT 62 UNION ALL SELECT 63 UNION ALL SELECT 64 UNION ALL SELECT 65 UNION ALL SELECT 66 UNION ALL SELECT 67 UNION ALL SELECT 68 UNION ALL SELECT 69
--     UNION ALL SELECT 70 UNION ALL SELECT 71 UNION ALL SELECT 72 UNION ALL SELECT 73 UNION ALL SELECT 74 UNION ALL SELECT 75 UNION ALL SELECT 76 UNION ALL SELECT 77 UNION ALL SELECT 78 UNION ALL SELECT 79
--     UNION ALL SELECT 80 UNION ALL SELECT 81 UNION ALL SELECT 82 UNION ALL SELECT 83 UNION ALL SELECT 84 UNION ALL SELECT 85 UNION ALL SELECT 86 UNION ALL SELECT 87 UNION ALL SELECT 88 UNION ALL SELECT 89
--     UNION ALL SELECT 90 UNION ALL SELECT 91 UNION ALL SELECT 92 UNION ALL SELECT 93 UNION ALL SELECT 94 UNION ALL SELECT 95 UNION ALL SELECT 96 UNION ALL SELECT 97 UNION ALL SELECT 98 UNION ALL SELECT 99
--     UNION ALL SELECT 100 UNION ALL SELECT 101 UNION ALL SELECT 102 UNION ALL SELECT 103 UNION ALL SELECT 104 UNION ALL SELECT 105 UNION ALL SELECT 106 UNION ALL SELECT 107 UNION ALL SELECT 108 UNION ALL SELECT 109
--     UNION ALL SELECT 110 UNION ALL SELECT 111 UNION ALL SELECT 112 UNION ALL SELECT 113 UNION ALL SELECT 114 UNION ALL SELECT 115 UNION ALL SELECT 116 UNION ALL SELECT 117 UNION ALL SELECT 118 UNION ALL SELECT 119
--     UNION ALL SELECT 120 UNION ALL SELECT 121 UNION ALL SELECT 122 UNION ALL SELECT 123 UNION ALL SELECT 124 UNION ALL SELECT 125 UNION ALL SELECT 126 UNION ALL SELECT 127 UNION ALL SELECT 128 UNION ALL SELECT 129
--     UNION ALL SELECT 130 UNION ALL SELECT 131 UNION ALL SELECT 132 UNION ALL SELECT 133 UNION ALL SELECT 134 UNION ALL SELECT 135 UNION ALL SELECT 136 UNION ALL SELECT 137 UNION ALL SELECT 138 UNION ALL SELECT 139
--     UNION ALL SELECT 140 UNION ALL SELECT 141 UNION ALL SELECT 142 UNION ALL SELECT 143 UNION ALL SELECT 144 UNION ALL SELECT 145 UNION ALL SELECT 146 UNION ALL SELECT 147 UNION ALL SELECT 148 UNION ALL SELECT 149
--     UNION ALL SELECT 150 UNION ALL SELECT 151 UNION ALL SELECT 152 UNION ALL SELECT 153 UNION ALL SELECT 154 UNION ALL SELECT 155 UNION ALL SELECT 156 UNION ALL SELECT 157 UNION ALL SELECT 158 UNION ALL SELECT 159
--     UNION ALL SELECT 160 UNION ALL SELECT 161 UNION ALL SELECT 162 UNION ALL SELECT 163 UNION ALL SELECT 164 UNION ALL SELECT 165 UNION ALL SELECT 166 UNION ALL SELECT 167 UNION ALL SELECT 168 UNION ALL SELECT 169
--     UNION ALL SELECT 170 UNION ALL SELECT 171 UNION ALL SELECT 172 UNION ALL SELECT 173 UNION ALL SELECT 174 UNION ALL SELECT 175 UNION ALL SELECT 176 UNION ALL SELECT 177 UNION ALL SELECT 178 UNION ALL SELECT 179
--     UNION ALL SELECT 180 UNION ALL SELECT 181 UNION ALL SELECT 182 UNION ALL SELECT 183 UNION ALL SELECT 184 UNION ALL SELECT 185 UNION ALL SELECT 186 UNION ALL SELECT 187 UNION ALL SELECT 188 UNION ALL SELECT 189
--     UNION ALL SELECT 190 UNION ALL SELECT 191
-- ) seq
-- WHERE TIMESTAMPADD(MINUTE, n * 10, start_dt) >= start_dt
--   AND TIMESTAMPADD(MINUTE, (n + 1) * 10, start_dt) <= TIMESTAMPADD(DAY, 1, start_dt);

INSERT INTO themes (nom, description) VALUES
('Cybersécurité', 'Questions sur la sécurité informatique, la protection des données et les menaces'),
('Science des données', 'Questions sur l\'analyse de données, les statistiques et l\'apprentissage automatique'),
('Développement logiciel', 'Questions sur la programmation, les architectures et les bonnes pratiques'),
('Réseaux et systèmes', 'Questions sur les réseaux, les systèmes d\'exploitation et l\'infrastructure');

SET @theme_cyber := (SELECT id FROM themes WHERE nom = 'Cybersécurité' ORDER BY id DESC LIMIT 1);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quel est l\'objectif principal du chiffrement ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Augmenter la vitesse de transmission des données', FALSE),
(@q, 'Compresser les données pour économiser de l\'espace de stockage', FALSE),
(@q, 'Protéger la confidentialité des données', TRUE),
(@q, 'Améliorer la bande passante du réseau', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Parmi les éléments suivants, lesquels sont des types de logiciels malveillants ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Rançongiciel (ransomware)', TRUE),
(@q, 'Logiciel espion (spyware)', TRUE),
(@q, 'Pare-feu', FALSE),
(@q, 'Cheval de Troie', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Le "phishing" (hameçonnage) consiste généralement en quoi ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Le vol physique de matériel', FALSE),
(@q, 'Des e-mails trompeurs pour voler des identifiants', TRUE),
(@q, 'Des attaques par force brute sur les mots de passe', FALSE),
(@q, 'L\'écoute du trafic réseau (sniffing)', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quel protocole permet une navigation Web sécurisée ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'HTTP', FALSE),
(@q, 'HTTPS', TRUE),
(@q, 'FTP', FALSE),
(@q, 'SMTP', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce que l\'authentification à deux facteurs (2FA) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Utiliser deux mots de passe différents', FALSE),
(@q, 'Une authentification utilisant deux facteurs différents (connaissance, possession, biométrie)', TRUE),
(@q, 'Avoir deux comptes utilisateur', FALSE),
(@q, 'Utiliser le même mot de passe sur deux appareils', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Parmi les algorithmes suivants, lesquels sont des algorithmes de chiffrement symétrique ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'AES', TRUE),
(@q, 'RSA', FALSE),
(@q, 'DES', TRUE),
(@q, 'Blowfish', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'une vulnérabilité zero-day ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une vulnérabilité qui survient à minuit', FALSE),
(@q, 'Une faille de sécurité inconnue de l\'éditeur', FALSE),
(@q, 'Une vulnérabilité pour laquelle aucun correctif n\'est disponible', FALSE),
(@q, 'Les réponses B et C', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Que signifie l\'acronyme SIEM ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Security Information and Event Management', TRUE),
(@q, 'System Integrity and Encryption Method', FALSE),
(@q, 'Secure Internet Enterprise Management', FALSE),
(@q, 'Security Intelligence and Event Monitoring', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Quels sont les types de tests d\'intrusion (pentest) suivants ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Test en boîte noire (black box)', TRUE),
(@q, 'Test en boîte blanche (white box)', TRUE),
(@q, 'Test en boîte grise (gray box)', TRUE),
(@q, 'Test en boîte rouge (red box)', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quel est le principe du moindre privilège ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Les utilisateurs doivent avoir le minimum de permissions nécessaires', TRUE),
(@q, 'Tous les utilisateurs doivent avoir des droits administrateur', FALSE),
(@q, 'Les privilèges doivent être revus une fois par an', FALSE),
(@q, 'Utiliser le mot de passe le plus simple possible', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quel port est couramment utilisé pour SSH ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, '22', TRUE),
(@q, '80', FALSE),
(@q, '443', FALSE),
(@q, '21', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce que l\'ingénierie sociale ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Concevoir des plateformes de réseaux sociaux', FALSE),
(@q, 'Manipuler des personnes pour qu\'elles divulguent des informations', TRUE),
(@q, 'Sécuriser les réseaux sociaux', FALSE),
(@q, 'Corriger les failles via une communauté', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Quels éléments font partie d\'une stratégie de défense en profondeur ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Pare-feu', TRUE),
(@q, 'Systèmes de détection d\'intrusion (IDS)', TRUE),
(@q, 'Antivirus', TRUE),
(@q, 'Formation des employés', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'une attaque de type "man-in-the-middle" (homme du milieu) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Attaquer depuis l\'intérieur de l\'organisation', FALSE),
(@q, 'Intercepter la communication entre deux parties', TRUE),
(@q, 'Attaquer pendant les fenêtres de maintenance', FALSE),
(@q, 'Attaquer simultanément depuis plusieurs lieux', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Que signifie DDoS ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Distributed Denial of Service', TRUE),
(@q, 'Direct Denial of Security', FALSE),
(@q, 'Data Destruction on System', FALSE),
(@q, 'Digital Defense of Servers', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Parmi les éléments suivants, lesquels sont des algorithmes de hachage ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'MD5', TRUE),
(@q, 'SHA-256', TRUE),
(@q, 'AES', FALSE),
(@q, 'bcrypt', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quel est l\'objectif d\'un VPN ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Créer un réseau privé au-dessus d\'un réseau public', TRUE),
(@q, 'Augmenter la vitesse d\'Internet', FALSE),
(@q, 'Empêcher tous les logiciels malveillants', FALSE),
(@q, 'Gérer des machines virtuelles', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce que les "données au repos" (data at rest) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Des données en cours de traitement par le CPU', FALSE),
(@q, 'Des données stockées sur des supports de stockage', TRUE),
(@q, 'Des données transmises sur le réseau', FALSE),
(@q, 'Des données affichées à l\'écran', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Quels cadres (frameworks) sont utilisés pour la gestion de la cybersécurité ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'NIST Cybersecurity Framework', TRUE),
(@q, 'ISO 27001', TRUE),
(@q, 'RGPD (GDPR)', FALSE),
(@q, 'COBIT', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'un rançongiciel (ransomware) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un logiciel qui chiffre les données pour demander une rançon', TRUE),
(@q, 'Un logiciel qui vole des données', FALSE),
(@q, 'Un logiciel qui surveille l\'activité', FALSE),
(@q, 'Un logiciel qui supprime des données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce que le salage (salting) dans le stockage des mots de passe ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Ajouter des données aléatoires aux mots de passe avant le hachage', TRUE),
(@q, 'Chiffrer les mots de passe avec un algorithme de salage', FALSE),
(@q, 'Stocker les mots de passe sur des serveurs salés', FALSE),
(@q, 'Utiliser le sel comme clé de chiffrement', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Quels sont des protocoles de sécurité Wi‑Fi ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'WEP', TRUE),
(@q, 'WPA2', TRUE),
(@q, 'WPA3', TRUE),
(@q, 'HTTP', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'un IDS ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Système de détection d\'intrusion (Intrusion Detection System)', TRUE),
(@q, 'Internet Defense System', FALSE),
(@q, 'Internal Data Security', FALSE),
(@q, 'Integrated Defense Solution', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quelle est la différence entre authentification et autorisation ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'L\'authentification vérifie l\'identité, l\'autorisation attribue des droits', TRUE),
(@q, 'L\'autorisation vérifie l\'identité, l\'authentification attribue des droits', FALSE),
(@q, 'Ce sont la même chose', FALSE),
(@q, 'L\'authentification concerne les utilisateurs, l\'autorisation concerne les systèmes', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Quels sont des vecteurs d\'attaque courants ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Pièces jointes d\'e-mail', TRUE),
(@q, 'Mots de passe faibles', TRUE),
(@q, 'Logiciels non corrigés (non patchés)', TRUE),
(@q, 'Réseaux sociaux', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'un honeypot en cybersécurité ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un piège mis en place pour détecter ou détourner des accès non autorisés', TRUE),
(@q, 'Une friandise pour les pirates', FALSE),
(@q, 'Un type de chiffrement', FALSE),
(@q, 'Une méthode de stockage des mots de passe', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Quels sont des algorithmes de chiffrement asymétrique ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'RSA', TRUE),
(@q, 'ECC (cryptographie à courbe elliptique)', TRUE),
(@q, 'AES', FALSE),
(@q, 'Diffie-Hellman', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quel est l\'objectif principal d\'un certificat numérique ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Vérifier la propriété d\'une clé publique', TRUE),
(@q, 'Chiffrer les e-mails', FALSE),
(@q, 'Stocker les mots de passe de manière sécurisée', FALSE),
(@q, 'Créer uniquement des signatures numériques', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'une attaque CSRF ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Cross-Site Request Forgery', TRUE),
(@q, 'Cross-Site Remote File', FALSE),
(@q, 'Client-Side Request Forgery', FALSE),
(@q, 'Cross-Site Resource Fetching', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Quels sont des facteurs d\'authentification courants ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Quelque chose que vous savez (mot de passe)', TRUE),
(@q, 'Quelque chose que vous possédez (jeton)', TRUE),
(@q, 'Quelque chose que vous êtes (biométrie)', TRUE),
(@q, 'Quelque chose que vous pensez', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce que l\'injection SQL ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Injecter du code SQL malveillant via une entrée utilisateur', TRUE),
(@q, 'Injecter des bases SQL avec des données', FALSE),
(@q, 'Un type de sauvegarde de base de données', FALSE),
(@q, 'Une technique d\'optimisation de requêtes SQL', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Que signifie le triade CIA en sécurité ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Confidentialité, Intégrité, Disponibilité', TRUE),
(@q, 'Central Intelligence Agency', FALSE),
(@q, 'Cybersécurité, Intégrité, Authentification', FALSE),
(@q, 'Confidentialité, Identification, Autorisation', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quels sont des risques de sécurité des applications Web ? (Choisissez la meilleure réponse)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Cross-Site Scripting (XSS)', FALSE),
(@q, 'Références directes à des objets non sécurisées', FALSE),
(@q, 'Mauvaise configuration de sécurité', FALSE),
(@q, 'Toutes les réponses ci-dessus', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quel est l\'objectif d\'un audit de sécurité ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Évaluer les politiques et contrôles de sécurité', TRUE),
(@q, 'Auditer les transactions financières', FALSE),
(@q, 'Auditer la performance des employés', FALSE),
(@q, 'Vérifier l\'inventaire matériel', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'une attaque par force brute ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Essayer toutes les combinaisons possibles pour casser des mots de passe', TRUE),
(@q, 'Forcer une entrée par des moyens physiques', FALSE),
(@q, 'Attaquer avec une force excessive', FALSE),
(@q, 'Un type d\'attaque DDoS', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Quelles sont les phases de réponse à incident ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Préparation', TRUE),
(@q, 'Détection et analyse', TRUE),
(@q, 'Confinement, éradication et rétablissement', TRUE),
(@q, 'Activités post-incident', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quelle est la différence entre un hacker black hat et white hat ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Le black hat attaque de façon malveillante, le white hat teste de manière éthique', TRUE),
(@q, 'Le black hat travaille la nuit, le white hat le jour', FALSE),
(@q, 'Ils utilisent des chapeaux de couleurs différentes', FALSE),
(@q, 'Le black hat est gouvernemental, le white hat est privé', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'un jeton de sécurité (security token) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un dispositif physique ou numérique utilisé pour l\'authentification', TRUE),
(@q, 'Un token utilisé en programmation', FALSE),
(@q, 'Un type de cryptomonnaie', FALSE),
(@q, 'Un certificat de sécurité', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Quelles sont des méthodes d\'authentification biométrique ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Lecture d\'empreinte digitale', TRUE),
(@q, 'Reconnaissance faciale', TRUE),
(@q, 'Scan de l\'iris', TRUE),
(@q, 'Reconnaissance vocale', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'une politique de sécurité ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un document formel définissant les règles d\'accès aux actifs de l\'organisation', TRUE),
(@q, 'Une police d\'assurance contre les cyberattaques', FALSE),
(@q, 'Une réglementation gouvernementale de cybersécurité', FALSE),
(@q, 'Les paramètres de sécurité d\'un logiciel', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quel est l\'objectif d\'un bac à sable (sandbox) en sécurité ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Environnement isolé pour tester des programmes suspects', TRUE),
(@q, 'Stockage des outils de sécurité', FALSE),
(@q, 'Environnement de sauvegarde', FALSE),
(@q, 'Algorithme de chiffrement', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Quels sont des types de pare-feu ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Pare-feu à filtrage de paquets', TRUE),
(@q, 'Pare-feu à inspection avec état (stateful)', TRUE),
(@q, 'Pare-feu proxy', TRUE),
(@q, 'Pare-feu de nouvelle génération (NGFW)', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'un tunnel VPN ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Connexion chiffrée entre le client VPN et le serveur', TRUE),
(@q, 'Tunnel physique pour des câbles', FALSE),
(@q, 'Chemin réseau', FALSE),
(@q, 'Méthode de compression de données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quelle est la différence entre vulnérabilité et menace ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une vulnérabilité est une faiblesse, une menace est un potentiel d\'exploitation', TRUE),
(@q, 'Ce sont la même chose', FALSE),
(@q, 'La vulnérabilité est externe, la menace est interne', FALSE),
(@q, 'La menace est une faiblesse, la vulnérabilité est un exploit', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 2, 'Quels sont des protocoles de sécurité des e-mails ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'S/MIME', TRUE),
(@q, 'PGP', TRUE),
(@q, 'SMTP', FALSE),
(@q, 'POP3', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'un référentiel (baseline) de sécurité ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un ensemble minimal de standards de sécurité pour les systèmes', TRUE),
(@q, 'Un chiffrement de base', FALSE),
(@q, 'Un plan d\'étage de sécurité', FALSE),
(@q, 'Une formation de sécurité basique', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quel est l\'objectif d\'un programme de sensibilisation à la sécurité ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Former les employés aux risques et bonnes pratiques de sécurité', TRUE),
(@q, 'Rendre les employés conscients des caméras de sécurité', FALSE),
(@q, 'Programmer des logiciels de sécurité', FALSE),
(@q, 'Créer des alertes de sécurité', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quels sont des risques de sécurité liés aux appareils mobiles ? (Choisissez la meilleure réponse)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Perte ou vol des appareils', FALSE),
(@q, 'Connexions Wi‑Fi non sécurisées', FALSE),
(@q, 'Applications malveillantes', FALSE),
(@q, 'Toutes les réponses ci-dessus', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Qu\'est-ce qu\'un contrôle de sécurité ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une mesure de protection visant à réduire les risques de sécurité', TRUE),
(@q, 'Un panneau de contrôle pour un logiciel de sécurité', FALSE),
(@q, 'Un ordre donné par l\'administrateur de sécurité', FALSE),
(@q, 'Un contrôleur de trafic réseau', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_cyber, 1, 'Quelle est la différence entre le chiffrement des données et la tokenisation ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Le chiffrement transforme les données, la tokenisation les remplace par des jetons', TRUE),
(@q, 'Le chiffrement est toujours plus fort que la tokenisation', FALSE),
(@q, 'La tokenisation est un type de chiffrement', FALSE),
(@q, 'Il n\'y a aucune différence', FALSE);

SET @theme_ds := (SELECT id FROM themes WHERE nom = 'Science des données' ORDER BY id DESC LIMIT 1);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce que le surapprentissage (overfitting) en apprentissage automatique ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un modèle trop simple pour capturer des motifs', FALSE),
(@q, 'Un modèle performant sur les données d\'entraînement mais mauvais sur de nouvelles données', TRUE),
(@q, 'Un modèle qui généralise trop', FALSE),
(@q, 'Des données d\'entraînement insuffisantes', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quels sont des algorithmes d\'apprentissage supervisé ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Régression linéaire', TRUE),
(@q, 'Forêt aléatoire (Random Forest)', TRUE),
(@q, 'Clustering K-means', FALSE),
(@q, 'Machines à vecteurs de support (SVM)', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Que représente la valeur p (p-value) en test d\'hypothèse ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'La probabilité que l\'hypothèse nulle soit vraie', FALSE),
(@q, 'La probabilité d\'observer les résultats en supposant l\'hypothèse nulle vraie', TRUE),
(@q, 'La largeur de l\'intervalle de confiance', FALSE),
(@q, 'Une mesure de la taille d\'effet', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quel est l\'objectif de la mise à l\'échelle des caractéristiques (feature scaling) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Normaliser les plages des variables pour les algorithmes sensibles à l\'échelle', TRUE),
(@q, 'Augmenter la taille du jeu de données', FALSE),
(@q, 'Supprimer toutes les variables catégorielles', FALSE),
(@q, 'Réduire la complexité de calcul', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quelle métrique est la plus adaptée aux problèmes de classification déséquilibrés ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Exactitude (accuracy)', FALSE),
(@q, 'Score F1', TRUE),
(@q, 'R-carré (R²)', FALSE),
(@q, 'Erreur absolue moyenne (MAE)', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'À quoi sert la validation croisée (cross-validation) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Augmenter la taille des données d\'entraînement', FALSE),
(@q, 'Évaluer les performances du modèle sur des données non vues', TRUE),
(@q, 'Réduire la complexité du modèle', FALSE),
(@q, 'Sélectionner des caractéristiques (features)', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quelles sont des techniques de réduction de dimension ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'ACP (PCA - Analyse en Composantes Principales)', TRUE),
(@q, 't-SNE', TRUE),
(@q, 'Régression linéaire', FALSE),
(@q, 'Forêt aléatoire (Random Forest)', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce que la malédiction de la dimension (curse of dimensionality) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Les données deviennent clairsemées en haute dimension', TRUE),
(@q, 'Les modèles deviennent trop simples', FALSE),
(@q, 'Le coût de calcul diminue', FALSE),
(@q, 'La précision augmente toujours avec plus de variables', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Que signifie SQL ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Structured Query Language', TRUE),
(@q, 'Simple Query Logic', FALSE),
(@q, 'System Query Language', FALSE),
(@q, 'Standard Question Language', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quels sont des types de bases NoSQL ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Bases orientées documents', TRUE),
(@q, 'Magasins clé-valeur', TRUE),
(@q, 'Bases en colonnes (column-family)', TRUE),
(@q, 'Bases orientées graphes', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce qu\'une matrice de confusion ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un tableau décrivant les performances d\'un modèle de classification', TRUE),
(@q, 'Une matrice de points de données confus', FALSE),
(@q, 'Un outil de visualisation de données', FALSE),
(@q, 'Un algorithme de clustering', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quelle est la différence entre classification et régression ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'La classification prédit des catégories, la régression prédit des valeurs continues', TRUE),
(@q, 'La classification est non supervisée, la régression est supervisée', FALSE),
(@q, 'La classification utilise des réseaux de neurones, la régression utilise des arbres', FALSE),
(@q, 'C\'est la même chose', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quelles sont des méthodes d\'ensemble (ensemble methods) ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Forêt aléatoire (Random Forest)', TRUE),
(@q, 'Gradient Boosting', TRUE),
(@q, 'AdaBoost', TRUE),
(@q, 'Arbre de décision (seul)', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'À quoi sert un test A/B ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Comparer deux versions pour déterminer laquelle performe le mieux', TRUE),
(@q, 'Tester les algorithmes A et B', FALSE),
(@q, 'Alterner entre deux jeux de données', FALSE),
(@q, 'Tester une classification binaire', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quel est l\'objectif d\'un processus ETL ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Extraire, transformer et charger des données', TRUE),
(@q, 'Évaluer, tester, apprendre des modèles', FALSE),
(@q, 'Encoder, transmettre, journaliser des données', FALSE),
(@q, 'Extraire, entraîner, étiqueter des données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quelles sont des bibliothèques Python de visualisation de données ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Matplotlib', TRUE),
(@q, 'Seaborn', TRUE),
(@q, 'Plotly', TRUE),
(@q, 'Pandas', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce que le feature engineering ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Créer de nouvelles variables (features) à partir de données existantes', TRUE),
(@q, 'Concevoir des fonctionnalités logicielles', FALSE),
(@q, 'Construire des modèles de machine learning', FALSE),
(@q, 'Un processus de collecte de données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce que le compromis biais-variance (bias-variance tradeoff) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'L\'équilibre entre sous-apprentissage (underfitting) et surapprentissage (overfitting)', TRUE),
(@q, 'Un compromis entre précision et vitesse', FALSE),
(@q, 'Choisir entre différents algorithmes', FALSE),
(@q, 'Une technique d\'échantillonnage', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quelles sont des étapes courantes de prétraitement des données ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Gérer les valeurs manquantes', TRUE),
(@q, 'Mise à l\'échelle des variables (feature scaling)', TRUE),
(@q, 'Encodage one-hot', TRUE),
(@q, 'Entraînement du modèle', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce que le deep learning ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une branche du machine learning utilisant des réseaux de neurones à plusieurs couches', TRUE),
(@q, 'Apprendre des algorithmes complexes', FALSE),
(@q, 'Une technique de data mining', FALSE),
(@q, 'Une méthode d\'analyse statistique', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce qu\'une fonction d\'activation dans un réseau de neurones ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une fonction qui détermine la sortie d\'un neurone', TRUE),
(@q, 'Une fonction qui initialise les poids', FALSE),
(@q, 'Une fonction qui calcule la perte (loss)', FALSE),
(@q, 'Une fonction qui optimise le taux d\'apprentissage', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quels sont des types de réseaux de neurones ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'CNN (réseau convolutionnel)', TRUE),
(@q, 'RNN (réseau récurrent)', TRUE),
(@q, 'MLP (perceptron multicouche)', TRUE),
(@q, 'SQL (Structured Query Language)', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quel est l\'objectif de la régularisation ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Éviter le surapprentissage', TRUE),
(@q, 'Augmenter la complexité du modèle', FALSE),
(@q, 'Accélérer l\'entraînement', FALSE),
(@q, 'Améliorer la qualité des données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce qu\'un hyperparamètre ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un paramètre fixé avant l\'entraînement', TRUE),
(@q, 'Un paramètre appris pendant l\'entraînement', FALSE),
(@q, 'Un paramètre des données', FALSE),
(@q, 'Un paramètre de sortie', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quelles sont des technologies Big Data ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Hadoop', TRUE),
(@q, 'Spark', TRUE),
(@q, 'Kafka', TRUE),
(@q, 'Excel', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce qu\'un arbre de décision ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un modèle en forme d\'arbre décrivant des décisions et leurs conséquences', TRUE),
(@q, 'Une structure d\'arbre de base de données', FALSE),
(@q, 'Un logiciel d\'aide à la décision', FALSE),
(@q, 'Une organisation du système de fichiers', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quels sont des algorithmes de clustering ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'K-means', TRUE),
(@q, 'DBSCAN', TRUE),
(@q, 'Clustering hiérarchique', TRUE),
(@q, 'Régression linéaire', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce que le traitement du langage naturel (NLP) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un domaine de l\'IA permettant aux ordinateurs de comprendre le langage humain', TRUE),
(@q, 'Le traitement des nombres naturels', FALSE),
(@q, 'Uniquement la traduction de langues', FALSE),
(@q, 'Uniquement la reconnaissance vocale', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'À quoi sert un notebook Jupyter ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un environnement interactif pour l\'analyse de données', TRUE),
(@q, 'Un carnet de notes pour data scientists', FALSE),
(@q, 'Un système de base de données Jupyter', FALSE),
(@q, 'Un compilateur Python', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quelles sont des structures de données pandas ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Series', TRUE),
(@q, 'DataFrame', TRUE),
(@q, 'Array', FALSE),
(@q, 'Tensor', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce qu\'un coefficient de corrélation ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une mesure de la relation linéaire entre deux variables', TRUE),
(@q, 'Le coefficient de variation', FALSE),
(@q, 'Un coefficient de régression', FALSE),
(@q, 'Une mesure de significativité statistique', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quel est l\'objectif du découpage train/test ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Évaluer les performances du modèle sur des données non vues', TRUE),
(@q, 'Entraîner plus vite', FALSE),
(@q, 'Réduire la taille des données', FALSE),
(@q, 'Créer deux jeux de données identiques', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quelles sont des tâches de préparation des données (data wrangling) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Nettoyer des données désordonnées', FALSE),
(@q, 'Transformer des formats de données', FALSE),
(@q, 'Enrichir les données avec des sources additionnelles', FALSE),
(@q, 'Toutes les réponses ci-dessus', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce qu\'un optimiseur de réseau de neurones ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un algorithme qui minimise la fonction de perte (loss)', TRUE),
(@q, 'Un outil pour optimiser l\'architecture du réseau', FALSE),
(@q, 'Un logiciel pour accélérer l\'entraînement', FALSE),
(@q, 'Une optimisation matérielle', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quelle est la différence entre précision (precision) et rappel (recall) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'La précision mesure les faux positifs, le rappel mesure les faux négatifs', TRUE),
(@q, 'Ce sont la même métrique', FALSE),
(@q, 'La précision concerne la régression, le rappel la classification', FALSE),
(@q, 'Le rappel est toujours meilleur que la précision', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quelles sont des techniques d\'analyse de séries temporelles ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'ARIMA', TRUE),
(@q, 'Lissage exponentiel', TRUE),
(@q, 'Réseaux LSTM', TRUE),
(@q, 'Clustering K-means', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce qu\'un pipeline de données ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un processus automatisé pour déplacer et transformer des données', TRUE),
(@q, 'Un réseau pour transmettre des données', FALSE),
(@q, 'Une connexion à la base de données', FALSE),
(@q, 'Un flux de visualisation de données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quel est l\'objectif de la sélection de variables (feature selection) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Choisir les variables les plus pertinentes pour la modélisation', TRUE),
(@q, 'Sélectionner les variables à afficher', FALSE),
(@q, 'Choisir des fonctionnalités du langage', FALSE),
(@q, 'Sélectionner des features de programmation', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quels sont des problèmes courants de qualité des données ? (Choisissez la meilleure réponse)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Valeurs manquantes', FALSE),
(@q, 'Formats incohérents', FALSE),
(@q, 'Doublons', FALSE),
(@q, 'Toutes les réponses ci-dessus', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce que l\'apprentissage par transfert (transfer learning) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Utiliser des modèles pré-entraînés pour de nouvelles tâches', TRUE),
(@q, 'Transférer des données entre des systèmes', FALSE),
(@q, 'Apprendre à transférer des fichiers', FALSE),
(@q, 'Transférer un modèle entre des serveurs', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quelle est la différence entre bagging et boosting ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Le bagging entraîne des modèles en parallèle, le boosting de manière séquentielle', FALSE),
(@q, 'Le bagging réduit la variance, le boosting réduit le biais', FALSE),
(@q, 'Ce sont deux méthodes d\'ensemble', FALSE),
(@q, 'Toutes les réponses ci-dessus', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quels sont des formats de stockage de données ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'CSV', TRUE),
(@q, 'JSON', TRUE),
(@q, 'Parquet', TRUE),
(@q, 'Avro', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce qu\'un entrepôt de données (data warehouse) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un référentiel central de données intégrées provenant de plusieurs sources', TRUE),
(@q, 'Un entrepôt pour stocker des données physiques', FALSE),
(@q, 'Une installation de stockage Big Data', FALSE),
(@q, 'Un emplacement de sauvegarde de base de données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quel est l\'objectif de la normalisation des données ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Mettre les données à l\'échelle vers une plage standard', TRUE),
(@q, 'Normaliser les tables d\'une base de données', FALSE),
(@q, 'Rendre les données distribuées normalement', FALSE),
(@q, 'Supprimer toutes les valeurs aberrantes', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quels sont les composants de l\'apprentissage par renforcement ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Agent', TRUE),
(@q, 'Environnement', TRUE),
(@q, 'Récompense', TRUE),
(@q, 'Action', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce qu\'un lac de données (data lake) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un référentiel de stockage contenant des données brutes dans leur format natif', TRUE),
(@q, 'Un lac de visualisation de données', FALSE),
(@q, 'Un système de sauvegarde de données', FALSE),
(@q, 'Un lac de traitement des données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quelle est la différence entre apprentissage supervisé et non supervisé ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Le supervisé utilise des données étiquetées, le non supervisé utilise des données non étiquetées', TRUE),
(@q, 'Le supervisé est toujours meilleur', FALSE),
(@q, 'Le non supervisé est plus rapide', FALSE),
(@q, 'Il n\'y a aucune différence', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 2, 'Quelles sont des techniques de data mining ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Apprentissage de règles d\'association', TRUE),
(@q, 'Détection d\'anomalies', TRUE),
(@q, 'Classification', TRUE),
(@q, 'Clustering', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Qu\'est-ce qu\'un système de recommandation (recommender system) ? (Choisissez la meilleure réponse)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un système qui suggère des éléments aux utilisateurs', FALSE),
(@q, 'Un système qui recommande des actions', FALSE),
(@q, 'Un système qui recommande des emplois', FALSE),
(@q, 'Toutes les réponses ci-dessus', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_ds, 1, 'Quel est l\'objectif de la gouvernance des données (data governance) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Gérer la disponibilité, l\'utilisabilité, l\'intégrité et la sécurité des données', TRUE),
(@q, 'Contrôler les données par le gouvernement', FALSE),
(@q, 'Gouverner le stockage des données', FALSE),
(@q, 'Administrer la base de données', FALSE);

SET @theme_dev := (SELECT id FROM themes WHERE nom = 'Développement logiciel' ORDER BY id DESC LIMIT 1);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Quelle est la complexité temporelle d\'une recherche binaire ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'O(n)', FALSE),
(@q, 'O(log n)', TRUE),
(@q, 'O(n²)', FALSE),
(@q, 'O(1)', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels principes font partie de SOLID ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Principe de responsabilité unique (SRP)', TRUE),
(@q, 'Principe ouvert/fermé (OCP)', TRUE),
(@q, 'Principe de développement rapide', FALSE),
(@q, 'Principe de ségrégation des interfaces (ISP)', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Sur quoi repose la conception d\'une API RESTful ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Les méthodes HTTP et des URLs basées sur des ressources', TRUE),
(@q, 'Les standards du protocole SOAP', FALSE),
(@q, 'Le RPC (Remote Procedure Call)', FALSE),
(@q, 'Le langage de requête GraphQL', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Que signifie CI/CD ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Continuous Inspection / Continuous Delivery', FALSE),
(@q, 'Continuous Integration / Continuous Deployment', TRUE),
(@q, 'Code Integration / Code Deployment', FALSE),
(@q, 'Continuous Improvement / Continuous Development', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels sont des frameworks JavaScript ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'React', TRUE),
(@q, 'Angular', TRUE),
(@q, 'Django', FALSE),
(@q, 'Vue.js', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'À quoi sert le contrôle de version (version control) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Suivre les changements dans le code source', FALSE),
(@q, 'Contrôler les versions d\'un logiciel', FALSE),
(@q, 'Gérer les versions de bases de données', FALSE),
(@q, 'Toutes les réponses ci-dessus', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quelles sont des commandes Git ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'git commit', TRUE),
(@q, 'git push', TRUE),
(@q, 'git merge', TRUE),
(@q, 'git compile', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce que la programmation orientée objet (POO) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Programmer en se basant sur des objets contenant des données et des méthodes', TRUE),
(@q, 'Programmer en étant orienté vers des objectifs', FALSE),
(@q, 'Programmer uniquement avec des objets', FALSE),
(@q, 'Une méthodologie de programmation de bases de données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Quels sont les quatre piliers de la POO ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Encapsulation, abstraction, héritage, polymorphisme', TRUE),
(@q, 'Classes, objets, méthodes, propriétés', FALSE),
(@q, 'Public, privé, protégé, statique', FALSE),
(@q, 'Variables, fonctions, boucles, conditions', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels sont des types de tests logiciels ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Tests unitaires', TRUE),
(@q, 'Tests d\'intégration', TRUE),
(@q, 'Tests end-to-end (E2E)', TRUE),
(@q, 'Tests de performance', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'À quoi sert Docker ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Conteneuriser des applications', TRUE),
(@q, 'Gérer des machines virtuelles', FALSE),
(@q, 'Compiler du code', FALSE),
(@q, 'Gérer une base de données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Quelle est la différence entre les bases SQL et NoSQL ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'SQL est relationnel, NoSQL est non relationnel', TRUE),
(@q, 'SQL est plus rapide, NoSQL est plus lent', FALSE),
(@q, 'SQL est pour les petites données, NoSQL pour le Big Data', FALSE),
(@q, 'Il n\'y a aucune différence', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels sont des langages de programmation backend ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Python', TRUE),
(@q, 'Java', TRUE),
(@q, 'Node.js', TRUE),
(@q, 'C#', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce que la méthodologie Agile ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une approche itérative de la gestion de projet', TRUE),
(@q, 'Une alternative au modèle Waterfall', FALSE),
(@q, 'Un langage de programmation', FALSE),
(@q, 'Un framework de test', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce qu\'une architecture microservices ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Construire une application comme un ensemble de petits services', TRUE),
(@q, 'Utiliser des micro-ordinateurs pour développer', FALSE),
(@q, 'Une approche de code minimaliste', FALSE),
(@q, 'Une conception de petite base de données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quelles sont des technologies frontend ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'HTML', TRUE),
(@q, 'CSS', TRUE),
(@q, 'JavaScript', TRUE),
(@q, 'TypeScript', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce que le pattern MVC ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un pattern d\'architecture Model-View-Controller', TRUE),
(@q, 'Le pattern Module-View-Component', FALSE),
(@q, 'Un design Main-View-Control', FALSE),
(@q, 'Model-Validation-Controller', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Quel est l\'objectif d\'un ORM ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Faire le mapping objet-relationnel entre base de données et langage orienté objet', TRUE),
(@q, 'Organiser la gestion des ressources', FALSE),
(@q, 'Optimiser des méthodes de réponse', FALSE),
(@q, 'Surveiller le runtime des objets', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels sont des modèles de services cloud ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'IaaS (Infrastructure as a Service)', TRUE),
(@q, 'PaaS (Platform as a Service)', TRUE),
(@q, 'SaaS (Software as a Service)', TRUE),
(@q, 'FaaS (Functions as a Service)', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce que le responsive web design ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Des sites qui s\'adaptent à différentes tailles d\'écran', TRUE),
(@q, 'Des sites qui chargent très rapidement', FALSE),
(@q, 'Des sites qui répondent aux entrées utilisateur', FALSE),
(@q, 'Un design web sécurisé', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce qu\'une closure en JavaScript ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une fonction qui a accès à la portée (scope) de sa fonction englobante', TRUE),
(@q, 'Fermer correctement des balises HTML', FALSE),
(@q, 'Terminer un programme', FALSE),
(@q, 'Fermer une connexion à la base de données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels sont des types de données JavaScript ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'String', TRUE),
(@q, 'Number', TRUE),
(@q, 'Boolean', TRUE),
(@q, 'Object', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Quelle est la différence entre var, let et const en JavaScript ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Des règles différentes de portée (scope) et de réassignation', TRUE),
(@q, 'Ils sont identiques', FALSE),
(@q, 'var est pour les variables, let pour les fonctions, const pour les constantes', FALSE),
(@q, 'Uniquement des différences de performance', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce qu\'une API ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Application Programming Interface', TRUE),
(@q, 'Advanced Programming Interface', FALSE),
(@q, 'Application Process Integration', FALSE),
(@q, 'Automated Programming Interface', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels sont des frameworks Web Python ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Django', TRUE),
(@q, 'Flask', TRUE),
(@q, 'FastAPI', TRUE),
(@q, 'Spring', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce qu\'un design pattern ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une solution réutilisable à des problèmes de conception logicielle courants', TRUE),
(@q, 'Un modèle de design UI', FALSE),
(@q, 'Un pattern de formatage de code', FALSE),
(@q, 'Un pattern de tests logiciels', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels sont des design patterns de création (creational) ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Singleton', TRUE),
(@q, 'Factory Method', TRUE),
(@q, 'Observer', FALSE),
(@q, 'Builder', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce que le TDD (Test-Driven Development) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Écrire les tests avant d\'écrire le code d\'implémentation', TRUE),
(@q, 'Tester après le développement', FALSE),
(@q, 'Tester selon les exigences', FALSE),
(@q, 'Des tests automatiques', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'À quoi sert un gestionnaire de paquets (package manager) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Gérer les dépendances et bibliothèques d\'un projet', TRUE),
(@q, 'Emballer des applications', FALSE),
(@q, 'Gérer les fichiers du projet', FALSE),
(@q, 'Compresser des fichiers', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels sont des gestionnaires de paquets ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'npm (Node.js)', TRUE),
(@q, 'pip (Python)', TRUE),
(@q, 'Maven (Java)', TRUE),
(@q, 'NuGet (.NET)', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce qu\'un WebSocket ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un protocole de communication full-duplex sur une seule connexion TCP', TRUE),
(@q, 'Une prise Web pour les connexions', FALSE),
(@q, 'Une connexion Web sécurisée', FALSE),
(@q, 'De la programmation de sockets Web', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Quelle est la différence entre programmation synchrone et asynchrone ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'La synchrone s\'exécute séquentiellement, l\'asynchrone n\'attend pas les tâches', TRUE),
(@q, 'La synchrone est plus rapide', FALSE),
(@q, 'L\'asynchrone est plus simple', FALSE),
(@q, 'Il n\'y a aucune différence', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quelles sont des technologies de serveurs Web ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Apache', TRUE),
(@q, 'Nginx', TRUE),
(@q, 'IIS', TRUE),
(@q, 'Tomcat', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce qu\'une fonction de rappel (callback) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une fonction passée en argument pour être exécutée plus tard', TRUE),
(@q, 'Une fonction qui rappelle', FALSE),
(@q, 'Une fonction de rappel téléphonique', FALSE),
(@q, 'Une fonction de gestion d\'erreurs', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'À quoi sert le middleware ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un logiciel qui connecte différentes applications ou composants', TRUE),
(@q, 'La couche du milieu d\'une application', FALSE),
(@q, 'Un middleware de base de données', FALSE),
(@q, 'Un middleware réseau', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels sont des environnements d\'exécution JavaScript (runtimes) ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Node.js', TRUE),
(@q, 'Deno', TRUE),
(@q, 'Bun', TRUE),
(@q, 'Chrome V8', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce qu\'une promesse (promise) en JavaScript ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un objet représentant l\'achèvement futur d\'une opération asynchrone', TRUE),
(@q, 'Une promesse d\'écrire un meilleur code', FALSE),
(@q, 'Un gestionnaire d\'opération synchrone', FALSE),
(@q, 'Un objet de gestion d\'erreurs', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Quelle est la différence entre langages compilés et interprétés ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Les compilés sont traduits en code machine avant exécution, les interprétés s\'exécutent ligne par ligne', TRUE),
(@q, 'Les compilés sont plus rapides à écrire', FALSE),
(@q, 'Les interprétés sont toujours plus rapides', FALSE),
(@q, 'Il n\'y a aucune différence', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels sont des langages compilés ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'C++', TRUE),
(@q, 'Java', FALSE),
(@q, 'Go', TRUE),
(@q, 'Rust', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce qu\'une base NoSQL ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une base non relationnelle pour des données non structurées', TRUE),
(@q, 'Une base sans support SQL', FALSE),
(@q, 'Une base simple', FALSE),
(@q, 'Un ancien système de base de données', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Quel est l\'objectif d\'un CDN ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Distribuer du contenu géographiquement (Content Delivery Network)', TRUE),
(@q, 'Code Delivery Network', FALSE),
(@q, 'Central Data Network', FALSE),
(@q, 'Content Development Network', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quels sont des types de bases de données ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Bases relationnelles', TRUE),
(@q, 'Bases orientées documents', TRUE),
(@q, 'Bases orientées graphes', TRUE),
(@q, 'Magasins clé-valeur', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce qu\'un framework ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une structure préconstruite pour développer des applications', TRUE),
(@q, 'Un cadre photo pour le code', FALSE),
(@q, 'Un framework matériel', FALSE),
(@q, 'Un outil de gestion de projet', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Quelle est la différence entre une bibliothèque et un framework ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Votre code appelle la bibliothèque, le framework appelle votre code', TRUE),
(@q, 'Une bibliothèque est plus grande', FALSE),
(@q, 'Un framework est plus simple', FALSE),
(@q, 'C\'est la même chose', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quelles sont des approches de développement mobile ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Développement natif', TRUE),
(@q, 'Développement multiplateforme', TRUE),
(@q, 'Développement hybride', TRUE),
(@q, 'Progressive Web Apps (PWA)', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce qu\'une PWA (Progressive Web App) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une application Web qui se comporte comme une application native', TRUE),
(@q, 'Une page Web qui charge progressivement', FALSE),
(@q, 'Une application Web professionnelle', FALSE),
(@q, 'Une application Web programmable', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'À quoi sert un linter ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Analyser le code pour détecter des erreurs potentielles et des problèmes de style', TRUE),
(@q, 'Lier des bibliothèques', FALSE),
(@q, 'Vérifier les commentaires', FALSE),
(@q, 'Compresser du code', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 2, 'Quelles sont des plateformes d\'hébergement de contrôle de version ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'GitHub', TRUE),
(@q, 'GitLab', TRUE),
(@q, 'Bitbucket', TRUE),
(@q, 'Azure DevOps', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Qu\'est-ce qu\'un monorepo ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un dépôt unique contenant plusieurs projets', TRUE),
(@q, 'Un dépôt mono pour un seul projet', FALSE),
(@q, 'Un dépôt contenant des fichiers mono', FALSE),
(@q, 'Un dépôt simple', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_dev, 1, 'Quelle est la différence entre programmation fonctionnelle et orientée objet ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'La fonctionnelle utilise des fonctions pures, la POO utilise des objets avec état', TRUE),
(@q, 'La fonctionnelle est plus récente', FALSE),
(@q, 'La POO est toujours meilleure', FALSE),
(@q, 'Il n\'y a pas de différence significative', FALSE);

SET @theme_net := (SELECT id FROM themes WHERE nom = 'Réseaux et systèmes' ORDER BY id DESC LIMIT 1);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Quel est le port par défaut pour HTTPS ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, '80', FALSE),
(@q, '443', TRUE),
(@q, '21', FALSE),
(@q, '25', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quels sont des protocoles de routage ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'OSPF', TRUE),
(@q, 'BGP', TRUE),
(@q, 'HTTP', FALSE),
(@q, 'EIGRP', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'À quoi sert la résolution DNS ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Traduire des noms de domaine en adresses IP', TRUE),
(@q, 'Chiffrer le trafic réseau', FALSE),
(@q, 'Gérer la bande passante réseau', FALSE),
(@q, 'Attribuer dynamiquement des adresses IP', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'À quoi sert un masque de sous-réseau (subnet mask) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Cacher le réseau au public', FALSE),
(@q, 'Diviser une adresse IP en partie réseau et partie hôte', TRUE),
(@q, 'Chiffrer les paquets réseau', FALSE),
(@q, 'Prioriser le trafic réseau', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quelles sont des technologies de virtualisation ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'VMware', TRUE),
(@q, 'Hyper-V', TRUE),
(@q, 'KVM', TRUE),
(@q, 'Apache', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce que le modèle OSI ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un modèle conceptuel en 7 couches pour la communication réseau', TRUE),
(@q, 'Operating System Interface', FALSE),
(@q, 'Open Systems Integration', FALSE),
(@q, 'Un modèle de sécurité réseau', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quelles sont des couches du modèle OSI ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Couche physique', TRUE),
(@q, 'Couche liaison de données', TRUE),
(@q, 'Couche réseau', TRUE),
(@q, 'Couche transport', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'À quoi sert le DHCP ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Attribuer automatiquement des adresses IP aux appareils', TRUE),
(@q, 'Générer du HTML dynamique', FALSE),
(@q, 'Mettre en pool des connexions à la base de données', FALSE),
(@q, 'Vérifier l\'état des disques', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce qu\'un VLAN ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Virtual Local Area Network', TRUE),
(@q, 'Very Large Area Network', FALSE),
(@q, 'Virtual LAN Adapter', FALSE),
(@q, 'Volume Licensing Agreement Network', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quels sont des modèles de déploiement cloud ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Cloud public', TRUE),
(@q, 'Cloud privé', TRUE),
(@q, 'Cloud hybride', TRUE),
(@q, 'Cloud communautaire', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce que le RAID ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Redundant Array of Independent Disks (matrice redondante de disques indépendants)', TRUE),
(@q, 'Random Access Interface Device', FALSE),
(@q, 'Remote Administration Interface Dashboard', FALSE),
(@q, 'Rapid Application Integration Deployment', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Quelle est la différence entre TCP et UDP ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'TCP est orienté connexion, UDP est sans connexion', TRUE),
(@q, 'TCP est plus rapide, UDP est plus lent', FALSE),
(@q, 'TCP est pour la vidéo, UDP pour les données', FALSE),
(@q, 'Ils sont identiques', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quelles sont des distributions Linux ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Ubuntu', TRUE),
(@q, 'CentOS', TRUE),
(@q, 'Debian', TRUE),
(@q, 'Fedora', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce que l\'équilibrage de charge (load balancing) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Répartir le trafic réseau sur plusieurs serveurs', TRUE),
(@q, 'Équilibrer la consommation électrique', FALSE),
(@q, 'Équilibrer l\'utilisation du stockage', FALSE),
(@q, 'Gérer uniquement la charge CPU', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce qu\'un serveur proxy ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un serveur intermédiaire entre le client et la destination', TRUE),
(@q, 'Un serveur qui proxifie des requêtes de base de données', FALSE),
(@q, 'Un serveur de sauvegarde', FALSE),
(@q, 'Une instance de serveur virtuelle', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quels sont des outils de supervision (monitoring) ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Nagios', TRUE),
(@q, 'Zabbix', TRUE),
(@q, 'Prometheus', TRUE),
(@q, 'Grafana', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce que le NAT ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Network Address Translation (traduction d\'adresses réseau)', TRUE),
(@q, 'Network Access Terminal', FALSE),
(@q, 'Native Address Translation', FALSE),
(@q, 'Network Authentication Token', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Quel est l\'objectif d\'un reverse proxy ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Se placer devant des serveurs Web et relayer les requêtes des clients', TRUE),
(@q, 'Inverser le sens du trafic réseau', FALSE),
(@q, 'Servir de proxy pour des recherches DNS inversées', FALSE),
(@q, 'Un proxy rétrocompatible', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quelles sont des plateformes d\'orchestration de conteneurs ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Kubernetes', TRUE),
(@q, 'Docker Swarm', TRUE),
(@q, 'Apache Mesos', TRUE),
(@q, 'VMware vSphere', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce qu\'un SAN ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Storage Area Network', TRUE),
(@q, 'System Area Network', FALSE),
(@q, 'Secure Access Network', FALSE),
(@q, 'Server Area Network', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce qu\'une adresse MAC ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un identifiant unique attribué aux interfaces réseau', TRUE),
(@q, 'L\'adresse d\'un ordinateur Mac', FALSE),
(@q, 'Une adresse d\'accès mémoire', FALSE),
(@q, 'Un contrôle d\'accès principal', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quelles sont des topologies réseau ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Étoile', TRUE),
(@q, 'Bus', TRUE),
(@q, 'Anneau', TRUE),
(@q, 'Maillée (mesh)', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce qu\'un switch (commutateur) en réseau ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un appareil qui connecte des équipements sur un réseau via la commutation de paquets', TRUE),
(@q, 'Un interrupteur marche/arrêt du réseau', FALSE),
(@q, 'Un outil pour basculer entre réseaux', FALSE),
(@q, 'Un outil logiciel de commutation', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Quel est le rôle d\'un routeur ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Transférer des paquets de données entre des réseaux informatiques', TRUE),
(@q, 'Acheminer des câbles', FALSE),
(@q, 'Rooter des appareils', FALSE),
(@q, 'Acheminer l\'accès Internet (uniquement)', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quels sont des types de stockage ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'DAS (Direct Attached Storage)', TRUE),
(@q, 'NAS (Network Attached Storage)', TRUE),
(@q, 'SAN (Storage Area Network)', TRUE),
(@q, 'Stockage cloud', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce qu\'un hyperviseur ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un logiciel qui crée et exécute des machines virtuelles', TRUE),
(@q, 'Un hyperviseur de réseaux', FALSE),
(@q, 'Un superviseur pour les réseaux', FALSE),
(@q, 'Une virtualisation matérielle', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Quelle est la différence entre IPv4 et IPv6 ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'IPv4 utilise des adresses 32 bits, IPv6 utilise des adresses 128 bits', TRUE),
(@q, 'IPv4 est plus récent', FALSE),
(@q, 'IPv6 est plus simple', FALSE),
(@q, 'Aucune différence fonctionnelle', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quels sont des fournisseurs de cloud computing ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Amazon Web Services (AWS)', TRUE),
(@q, 'Microsoft Azure', TRUE),
(@q, 'Google Cloud Platform', TRUE),
(@q, 'IBM Cloud', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce qu\'un conteneur ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un package logiciel léger et autonome exécutable', TRUE),
(@q, 'Un conteneur de stockage', FALSE),
(@q, 'Un conteneur réseau', FALSE),
(@q, 'Une boîte logicielle', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Quel est l\'objectif d\'un bastion host ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une passerelle sécurisée entre réseaux internes et externes', TRUE),
(@q, 'Un hôte pour des jeux "bastion"', FALSE),
(@q, 'Le serveur hôte principal', FALSE),
(@q, 'Un hôte de secours', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quels sont des systèmes d\'exploitation serveur ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Windows Server', TRUE),
(@q, 'Distributions Linux', TRUE),
(@q, 'Unix', TRUE),
(@q, 'macOS Server', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce qu\'une DMZ ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Une zone démilitarisée : sous-réseau isolé entre réseaux interne et externe', TRUE),
(@q, 'Une zone réseau démilitarisée', FALSE),
(@q, 'Une zone de gestion de base de données', FALSE),
(@q, 'Une zone de gestion directe', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Quelle est la différence entre scaling up et scaling out ? (Choisissez la meilleure réponse)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Scaling up ajoute des ressources à des serveurs existants, scaling out ajoute des serveurs', FALSE),
(@q, 'Scaling up est vertical, scaling out est horizontal', FALSE),
(@q, 'Ce sont deux stratégies de mise à l\'échelle', FALSE),
(@q, 'Toutes les réponses ci-dessus', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quels sont des protocoles réseau ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'TCP/IP', TRUE),
(@q, 'HTTP/HTTPS', TRUE),
(@q, 'FTP', TRUE),
(@q, 'SSH', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce qu\'un serveur de fichiers (file server) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un serveur qui fournit du stockage de fichiers et l\'accès aux clients', TRUE),
(@q, 'Un serveur uniquement pour les fichiers', FALSE),
(@q, 'Un serveur de gestion de fichiers', FALSE),
(@q, 'Un serveur de fichiers de sauvegarde', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Quel est l\'objectif d\'un serveur mail ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Envoyer, recevoir et stocker des e-mails', TRUE),
(@q, 'Servir des applications de messagerie', FALSE),
(@q, 'Envoyer des données au serveur mail', FALSE),
(@q, 'Sauvegarder les e-mails', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 2, 'Quels sont des types de serveurs ? (Choisissez toutes les réponses correctes)', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Serveur Web', TRUE),
(@q, 'Serveur de base de données', TRUE),
(@q, 'Serveur applicatif', TRUE),
(@q, 'Serveur d\'impression', TRUE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Qu\'est-ce qu\'un panneau de brassage (patch panel) ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Un dispositif avec des ports pour connecter et gérer des câbles réseau', TRUE),
(@q, 'Un panneau pour des correctifs logiciels', FALSE),
(@q, 'Un panneau de gestion des patches', FALSE),
(@q, 'Un outil de patch réseau', FALSE);

INSERT INTO questions (id_theme, id_type_question, libelle, explication) VALUES
(@theme_net, 1, 'Quelle est la différence entre des câbles cat5e et cat6 ?', '');
SET @q := LAST_INSERT_ID();
INSERT INTO reponses_possibles (id_question, libelle, est_correct) VALUES
(@q, 'Le cat6 supporte une bande passante et des débits supérieurs au cat5e', TRUE),
(@q, 'Le cat5e est plus récent', FALSE),
(@q, 'Le cat6 est moins cher', FALSE),
(@q, 'Il n\'y a aucune différence', FALSE);
