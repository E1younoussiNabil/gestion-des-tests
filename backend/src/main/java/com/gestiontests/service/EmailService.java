package com.gestiontests.service;


import java.time.format.DateTimeFormatter;
import java.util.Properties;

import com.gestiontests.entity.Candidat;
import com.gestiontests.entity.CreneauHoraire;


import jakarta.enterprise.context.ApplicationScoped;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@ApplicationScoped
public class EmailService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Identifiants SMTP (à sécuriser via variables d'environnement dans un vrai projet)
    private final String username = "nabilelyounoussi.ny@gmail.com";
    private final String password = "dohp wbhm xnnj felv"; // mot de passe d'application Gmail
    
    // Configuration alternative pour les réseaux bloqués
    private final boolean USE_ALTERNATIVE_SMTP = true; // Mettre à false si Gmail fonctionne
    private final boolean SIMULATION_MODE = true; // Mode simulation pour contourner le blocage réseau
    
    // Configuration SMTP alternative (ex: SendGrid, Mailgun, ou autre)
    private final String altUsername = "apikey"; // Pour SendGrid
    private final String altPassword = "VOTRE_API_KEY_SENDGRID"; // À configurer
    private final String altHost = "smtp.sendgrid.net";
    private final String altPort = "587";

    /** Envoi email après inscription */
    public void envoyerEmailInscription(Candidat candidat, CreneauHoraire creneau, String messageInfo) throws Exception {
        String sujet = "Confirmation d'inscription - Test en ligne";
        String contenu = construireContenuInscription(candidat, creneau, messageInfo);

        envoyerEmail(candidat.getEmail(), sujet, contenu);
    }

    /** Envoi email après validation par admin */
    public void envoyerEmailValidation(Candidat candidat) throws Exception {
        String sujet = "Validation de votre inscription - Code de session";
        String contenu = construireContenuValidation(candidat);

        envoyerEmail(candidat.getEmail(), sujet, contenu);
    }

    /** Envoi email avec les résultats du test */
    public void envoyerEmailResultats(Candidat candidat, String score, String pourcentage) throws Exception {
        String sujet = "Résultats de votre test en ligne";
        String contenu = construireContenuResultats(candidat, score, pourcentage);

        envoyerEmail(candidat.getEmail(), sujet, contenu);
    }
    
    /** Envoi email avec le code de session */
    public void envoyerCodeSession(String email, String prenom, String codeSession) throws Exception {
        String sujet = "Votre code de session pour le test en ligne";
        String contenu = construireContenuCodeSession(prenom, codeSession);
        
        envoyerEmail(email, sujet, contenu);
    }

    /** Envoi email avec un OTP de connexion (portail candidat) */
    public void envoyerOtpConnexion(String email, String prenom, String otp) throws Exception {
        String sujet = "Code de connexion - Portail candidat";
        String contenu = construireContenuOtpConnexion(prenom, otp);
        envoyerEmail(email, sujet, contenu);
    }

    /** Test de connexion SMTP */
    public boolean testerConnexionSMTP() {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.timeout", "10000"); // 10 secondes timeout
            props.put("mail.smtp.connectiontimeout", "10000"); // 10 secondes timeout connexion

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Tenter de se connecter sans envoyer d'email
            session.getTransport().connect();
            System.out.println("✅ Connexion SMTP réussie");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur de connexion SMTP: " + e.getMessage());
            e.printStackTrace();
            
            // Essayer avec le port 465 (SSL)
            try {
                System.out.println("🔄 Tentative avec port 465 (SSL)...");
                return testerConnexionSMTPSSL();
            } catch (Exception e2) {
                System.err.println("❌ Échec aussi avec port 465: " + e2.getMessage());
            }
            return false;
        }
    }
    
    /** Test avec port 465 (SSL) */
    private boolean testerConnexionSMTPSSL() {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.connectiontimeout", "10000");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            session.getTransport().connect();
            System.out.println("✅ Connexion SMTP SSL réussie (port 465)");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur connexion SMTP SSL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /** Méthode interne pour envoyer un email via SMTP Gmail */
    private void envoyerEmail(String destinataire, String sujet, String contenu) throws Exception {
        if (username == null || password == null) {
            throw new Exception("EMAIL_USERNAME et EMAIL_PASSWORD non définis !");
        }

        System.out.println("🔍 Tentative d'envoi d'email à: " + destinataire);
        System.out.println("🔍 Utilisateur SMTP: " + username);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.debug", "true"); // Activer le debug SMTP
        props.put("mail.smtp.timeout", "10000"); // 10 secondes timeout
        props.put("mail.smtp.connectiontimeout", "10000"); // 10 secondes timeout connexion

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
        message.setSubject(sujet);
        
        // Détecter si le contenu est HTML
        if (contenu.trim().startsWith("<!DOCTYPE html>") || contenu.trim().startsWith("<html>")) {
            message.setContent(contenu, "text/html; charset=UTF-8");
        } else {
            message.setText(contenu);
        }

        Transport.send(message);

        System.out.println("✅ Email envoyé avec succès à " + destinataire);
    }

    /** Contenu email pour l'inscription */
    private String construireContenuInscription(Candidat candidat, CreneauHoraire creneau, String messageInfo) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }");
        html.append(".content { background-color: #f9f9f9; padding: 20px; border-radius: 0 0 5px 5px; }");
        html.append(".info-box { background-color: white; padding: 15px; margin: 15px 0; border-left: 4px solid #4CAF50; border-radius: 4px; }");
        html.append(".info-box h3 { margin-top: 0; color: #4CAF50; }");
        html.append(".info-row { margin: 8px 0; }");
        html.append(".info-label { font-weight: bold; display: inline-block; width: 120px; }");
        html.append(".instructions { background-color: #e3f2fd; padding: 15px; margin: 15px 0; border-radius: 4px; }");
        html.append(".instructions ol { margin: 10px 0; padding-left: 20px; }");
        html.append(".footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }");
        html.append(".alert { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 15px 0; border-radius: 4px; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<div class='header'><h1>Confirmation d'inscription</h1></div>");
        html.append("<div class='content'>");
        html.append("<p>Cher/Chère <strong>").append(candidat.getPrenom()).append(" ").append(candidat.getNom()).append("</strong>,</p>");
        html.append("<p>Votre inscription au test en ligne a bien été enregistrée.</p>");
        
        html.append("<div class='info-box'>");
        html.append("<h3>Récapitulatif de vos informations</h3>");
        html.append("<div class='info-row'><span class='info-label'>Nom:</span>").append(candidat.getNom()).append("</div>");
        html.append("<div class='info-row'><span class='info-label'>Prénom:</span>").append(candidat.getPrenom()).append("</div>");
        html.append("<div class='info-row'><span class='info-label'>École:</span>").append(candidat.getEcole()).append("</div>");
        if (candidat.getFiliere() != null && !candidat.getFiliere().isEmpty()) {
            html.append("<div class='info-row'><span class='info-label'>Filière:</span>").append(candidat.getFiliere()).append("</div>");
        }
        html.append("<div class='info-row'><span class='info-label'>Email:</span>").append(candidat.getEmail()).append("</div>");
        html.append("<div class='info-row'><span class='info-label'>GSM:</span>").append(candidat.getGsm()).append("</div>");
        html.append("</div>");

        html.append("<div class='info-box'>");
        html.append("<h3>Créneau horaire choisi</h3>");
        html.append("<div class='info-row'><span class='info-label'>Date:</span>").append(creneau.getDateExam().format(DATE_FORMATTER)).append("</div>");
        html.append("<div class='info-row'><span class='info-label'>Heure de début:</span>").append(creneau.getHeureDebut().format(TIME_FORMATTER)).append("</div>");
        html.append("<div class='info-row'><span class='info-label'>Heure de fin:</span>").append(creneau.getHeureFin().format(TIME_FORMATTER)).append("</div>");
        html.append("<div class='info-row'><span class='info-label'>Durée:</span>").append(creneau.getDureeMinutes()).append(" minutes</div>");
        html.append("</div>");

        if (messageInfo != null && !messageInfo.trim().isEmpty()) {
            html.append("<div class='alert'><strong>").append(messageInfo).append("</strong></div>");
        }

        html.append("<div class='instructions'>");
        html.append("<h3>Instructions pour le test</h3>");
        html.append("<ol>");
        html.append("<li>Connectez-vous à l'application le jour du test</li>");
        html.append("<li>Saisissez votre code de session (vous le recevrez après validation)</li>");
        html.append("<li>Attendez l'heure de début du créneau</li>");
        html.append("<li>Le bouton de démarrage sera activé automatiquement</li>");
        html.append("<li>Chaque question a un temps limité (2 minutes par défaut)</li>");
        html.append("</ol>");
        html.append("</div>");

        html.append("<div class='footer'>");
        html.append("<p>Cordialement,<br>L'équipe de gestion des tests en ligne</p>");
        html.append("</div>");
        html.append("</div></div></body></html>");
        
        return html.toString();
    }

    /** Contenu email pour la validation */
    private String construireContenuValidation(Candidat candidat) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #2196F3; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }");
        html.append(".content { background-color: #f9f9f9; padding: 20px; border-radius: 0 0 5px 5px; }");
        html.append(".code-box { background-color: white; padding: 25px; margin: 20px 0; border-radius: 8px; text-align: center; border: 3px solid #2196F3; }");
        html.append(".code-label { font-size: 14px; color: #666; margin-bottom: 10px; }");
        html.append(".code-value { font-size: 36px; font-weight: bold; color: #2196F3; letter-spacing: 5px; font-family: 'Courier New', monospace; }");
        html.append(".instructions { background-color: #e3f2fd; padding: 15px; margin: 15px 0; border-radius: 4px; }");
        html.append(".instructions ol { margin: 10px 0; padding-left: 20px; }");
        html.append(".warning-box { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 15px 0; border-radius: 4px; }");
        html.append(".footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<div class='header'><h1>Validation de votre inscription</h1></div>");
        html.append("<div class='content'>");
        html.append("<p>Cher/Chère <strong>").append(candidat.getPrenom()).append(" ").append(candidat.getNom()).append("</strong>,</p>");
        html.append("<p>Votre inscription a été validée par l'administrateur.</p>");
        
        html.append("<div class='code-box'>");
        html.append("<div class='code-label'>Votre code de session</div>");
        html.append("<div class='code-value'>").append(candidat.getCodeSession()).append("</div>");
        html.append("</div>");

        html.append("<div class='warning-box'>");
        html.append("<p><strong>⚠️ Important:</strong> Conservez ce code précieusement, il vous sera demandé pour accéder au test le jour de l'examen.</p>");
        html.append("</div>");

        html.append("<div class='instructions'>");
        html.append("<h3>Instructions pour le test</h3>");
        html.append("<ol>");
        html.append("<li>Connectez-vous à l'application le jour du test</li>");
        html.append("<li>Saisissez votre code de session ci-dessus</li>");
        html.append("<li>Attendez l'heure de début du créneau</li>");
        html.append("<li>Le bouton de démarrage sera activé automatiquement</li>");
        html.append("<li>Chaque question a un temps limité (2 minutes par défaut)</li>");
        html.append("</ol>");
        html.append("</div>");

        html.append("<div class='footer'>");
        html.append("<p>Cordialement,<br>L'équipe de gestion des tests en ligne</p>");
        html.append("</div>");
        html.append("</div></div></body></html>");
        
        return html.toString();
    }

    /** Contenu email pour les résultats */
    private String construireContenuResultats(Candidat candidat, String score, String pourcentage) {
        double pourcentageNum = Double.parseDouble(pourcentage);
        String messageMotivation = "";
        String couleurBarre = "";
        String niveau = "";
        
        if (pourcentageNum >= 80) {
            messageMotivation = "EXCELLENT ! Félicitations pour votre performance remarquable !";
            couleurBarre = "#4CAF50";
            niveau = "Excellent";
        } else if (pourcentageNum >= 60) {
            messageMotivation = "BON travail ! Vous avez bien réussi votre test.";
            couleurBarre = "#2196F3";
            niveau = "Bon";
        } else if (pourcentageNum >= 40) {
            messageMotivation = "Résultat correct. Continuez à travailler pour vous améliorer.";
            couleurBarre = "#FF9800";
            niveau = "Correct";
        } else {
            messageMotivation = "Nous vous encourageons à continuer vos efforts et à vous préparer davantage.";
            couleurBarre = "#F44336";
            niveau = "À améliorer";
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: ").append(couleurBarre).append("; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }");
        html.append(".content { background-color: #f9f9f9; padding: 20px; border-radius: 0 0 5px 5px; }");
        html.append(".result-box { background-color: white; padding: 25px; margin: 20px 0; border-radius: 8px; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        html.append(".score-large { font-size: 48px; font-weight: bold; color: ").append(couleurBarre).append("; margin: 10px 0; }");
        html.append(".score-label { font-size: 18px; color: #666; margin-bottom: 10px; }");
        html.append(".score-details { display: flex; justify-content: space-around; margin: 20px 0; padding: 15px; background-color: #f5f5f5; border-radius: 5px; }");
        html.append(".score-item { text-align: center; }");
        html.append(".score-item-value { font-size: 24px; font-weight: bold; color: ").append(couleurBarre).append("; }");
        html.append(".score-item-label { font-size: 12px; color: #666; margin-top: 5px; }");
        html.append(".progress-bar { width: 100%; height: 30px; background-color: #e0e0e0; border-radius: 15px; margin: 20px 0; overflow: hidden; }");
        html.append(".progress-fill { height: 100%; background-color: ").append(couleurBarre).append("; display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; transition: width 0.3s ease; }");
        html.append(".message-box { background-color: ").append(couleurBarre).append("15; border-left: 4px solid ").append(couleurBarre).append("; padding: 15px; margin: 20px 0; border-radius: 4px; }");
        html.append(".message-box p { margin: 0; color: #333; font-size: 16px; }");
        html.append(".footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }");
        html.append(".info-section { background-color: white; padding: 15px; margin: 15px 0; border-radius: 4px; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<div class='header'><h1>Résultats de votre test</h1></div>");
        html.append("<div class='content'>");
        html.append("<p>Cher/Chère <strong>").append(candidat.getPrenom()).append(" ").append(candidat.getNom()).append("</strong>,</p>");
        html.append("<p>Voici les résultats de votre test en ligne passé le <strong>").append(java.time.LocalDate.now().format(DATE_FORMATTER)).append("</strong>.</p>");
        
        html.append("<div class='result-box'>");
        html.append("<div class='score-label'>Niveau: ").append(niveau).append("</div>");
        html.append("<div class='score-large'>").append(pourcentage).append("%</div>");
        html.append("<div class='progress-bar'>");
        html.append("<div class='progress-fill' style='width: ").append(pourcentageNum).append("%;'>").append(pourcentageNum).append("%</div>");
        html.append("</div>");
        html.append("<div class='score-details'>");
        html.append("<div class='score-item'>");
        html.append("<div class='score-item-value'>").append(score).append("</div>");
        html.append("<div class='score-item-label'>Score obtenu</div>");
        html.append("</div>");
        html.append("<div class='score-item'>");
        html.append("<div class='score-item-value'>").append(pourcentage).append("%</div>");
        html.append("<div class='score-item-label'>Pourcentage</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");

        html.append("<div class='message-box'>");
        html.append("<p><strong>").append(messageMotivation).append("</strong></p>");
        html.append("</div>");

        html.append("<div class='info-section'>");
        html.append("<p><strong>Informations importantes:</strong></p>");
        html.append("<ul style='margin: 10px 0; padding-left: 20px;'>");
        html.append("<li>Ces résultats sont définitifs et ont été enregistrés dans notre système</li>");
        html.append("<li>Vous pouvez consulter vos résultats détaillés en vous connectant à l'application</li>");
        html.append("<li>Pour toute question, n'hésitez pas à nous contacter</li>");
        html.append("</ul>");
        html.append("</div>");

        html.append("<div class='footer'>");
        html.append("<p>Cordialement,<br>L'équipe de gestion des tests en ligne</p>");
        html.append("</div>");
        html.append("</div></div></body></html>");
        
        return html.toString();
    }
    
    /** Construit le contenu de l'email pour l'envoi du code de session */
    private String construireContenuCodeSession(String prenom, String codeSession) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Code de session</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background-color: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append(".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }");
        html.append(".content { padding: 20px; }");
        html.append(".code-box { background-color: #f8f9fa; border: 2px dashed #4CAF50; padding: 20px; text-align: center; margin: 20px 0; border-radius: 8px; }");
        html.append(".code { font-size: 32px; font-weight: bold; color: #4CAF50; letter-spacing: 5px; font-family: monospace; }");
        html.append(".instructions { background-color: #e8f5e8; padding: 15px; border-radius: 5px; margin: 20px 0; }");
        html.append(".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; background-color: #f8f9fa; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<div class='header'><h1>🎯 Votre Code de Session</h1></div>");
        html.append("<div class='content'>");
        html.append("<p>Bonjour <strong>").append(prenom).append("</strong>,</p>");
        html.append("<p>Votre inscription a été validée ! Voici votre code de session pour passer le test en ligne :</p>");
        
        html.append("<div class='code-box'>");
        html.append("<p class='code'>").append(codeSession).append("</p>");
        html.append("<p><strong>Conservez ce code précieusement</strong></p>");
        html.append("</div>");
        
        html.append("<div class='instructions'>");
        html.append("<h3>📋 Instructions :</h3>");
        html.append("<ul>");
        html.append("<li>Rendez-vous sur la plateforme de test à la date et heure prévues</li>");
        html.append("<li>Utilisez ce code pour vous connecter et commencer votre test</li>");
        html.append("<li>Assurez-vous d'avoir une connexion internet stable</li>");
        html.append("<li>Prévoyez environ 30-45 minutes pour compléter le test</li>");
        html.append("</ul>");
        html.append("</div>");
        
        html.append("<p><strong>⚠️ Important :</strong></p>");
        html.append("<ul>");
        html.append("<li>Ce code est personnel et non transférable</li>");
        html.append("<li>Il ne peut être utilisé qu'une seule fois</li>");
        html.append("<li>En cas de problème, contactez-nous immédiatement</li>");
        html.append("</ul>");
        html.append("</div>");
        html.append("<div class='footer'>");
        html.append("<p>Bonne chance pour votre test ! 🍀</p>");
        html.append("<p>&copy; 2024 - Système de Gestion des Tests en Ligne</p>");
        html.append("</div>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    private String construireContenuOtpConnexion(String prenom, String otp) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Code de connexion</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background-color: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append(".header { background-color: #2563EB; color: white; padding: 20px; text-align: center; }");
        html.append(".content { padding: 20px; }");
        html.append(".code-box { background-color: #f8f9fa; border: 2px dashed #2563EB; padding: 20px; text-align: center; margin: 20px 0; border-radius: 8px; }");
        html.append(".code { font-size: 32px; font-weight: bold; color: #2563EB; letter-spacing: 5px; font-family: monospace; }");
        html.append(".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; background-color: #f8f9fa; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<div class='header'><h1>🔐 Code de connexion</h1></div>");
        html.append("<div class='content'>");
        html.append("<p>Bonjour <strong>").append(prenom != null ? prenom : "").append("</strong>,</p>");
        html.append("<p>Voici votre code à usage unique pour accéder à votre espace candidat :</p>");
        html.append("<div class='code-box'><p class='code'>").append(otp).append("</p></div>");
        html.append("<p>Si vous n'êtes pas à l'origine de cette demande, vous pouvez ignorer cet email.</p>");
        html.append("</div>");
        html.append("<div class='footer'>");
        html.append("<p>&copy; 2024 - Système de Gestion des Tests en Ligne</p>");
        html.append("</div>");
        html.append("</div></body></html>");
        return html.toString();
    }
}
