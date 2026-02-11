@echo off
chcp 65001 >nul
set "MYSQL=C:\xampppp\mysql\bin\mysql.exe"
set "DB_USER=admin"
set "DB_PASS=admin"
if not exist "%MYSQL%" (
echo.
echo ERREUR: mysql.exe introuvable.
echo Chemin attendu: %MYSQL%
echo Corrigez la variable MYSQL dans ce fichier .bat selon votre installation XAMPP.
echo.
pause
exit /b 1
)
echo ========================================
echo Chargement complet de la base de données
echo ========================================

echo.
echo 1. Suppression et recréation de la base de données...
"%MYSQL%" --default-character-set=utf8mb4 -u %DB_USER% -p%DB_PASS% -e "DROP DATABASE IF EXISTS dbgestiontest;"
"%MYSQL%" --default-character-set=utf8mb4 -u %DB_USER% -p%DB_PASS% -e "CREATE DATABASE dbgestiontest CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo.
echo 2. Démarrez WildFly / le backend une fois pour générer le schéma depuis les entités.
echo (Hibernate hbm2ddl.auto=update)
echo.
pause

echo.
echo 3. Chargement des données (data.sql)...
"%MYSQL%" --default-character-set=utf8mb4 -u %DB_USER% -p%DB_PASS% dbgestiontest < "%~dp0data.sql"

echo.
echo ========================================
echo Base de données chargée avec succès !
echo ========================================
echo.
echo Statistiques :
"%MYSQL%" --default-character-set=utf8mb4 -u %DB_USER% -p%DB_PASS% dbgestiontest -e "SELECT 'Thèmes' as TableName, COUNT(*) as Nombre FROM themes UNION ALL SELECT 'Types de questions', COUNT(*) FROM types_question UNION ALL SELECT 'Questions', COUNT(*) FROM questions UNION ALL SELECT 'Réponses possibles', COUNT(*) FROM reponses_possibles UNION ALL SELECT 'Créneaux horaires', COUNT(*) FROM creneaux_horaires UNION ALL SELECT 'Administrateurs', COUNT(*) FROM administrateurs;"

echo.
echo Appuyez sur une touche pour quitter...
pause
