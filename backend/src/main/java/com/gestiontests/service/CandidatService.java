package com.gestiontests.service;

import com.gestiontests.entity.Candidat;
import com.gestiontests.entity.CreneauHoraire;
import com.gestiontests.entity.Inscription;
import com.gestiontests.entity.SessionTest;
import com.gestiontests.repository.CandidatRepository;
import com.gestiontests.repository.CreneauHoraireRepository;
import com.gestiontests.repository.InscriptionRepository;
import com.gestiontests.repository.SessionTestRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.security.SecureRandom;

@ApplicationScoped
public class CandidatService {
    
    @Inject
    private CandidatRepository candidatRepository;
    
    @Inject
    private CreneauHoraireRepository creneauHoraireRepository;
    
    @Inject
    private InscriptionRepository inscriptionRepository;

    @Inject
    private SessionTestRepository sessionTestRepository;
    
    @Inject
    private EmailService emailService;
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;

    private static final long OTP_VALIDITY_SECONDS = 10 * 60;
    private static final Map<String, OtpEntry> OTP_STORE = new ConcurrentHashMap<>();
    private static final SecureRandom OTP_RANDOM = new SecureRandom();

    private static class OtpEntry {
        private final String otp;
        private final LocalDateTime expiresAt;

        private OtpEntry(String otp, LocalDateTime expiresAt) {
            this.otp = otp;
            this.expiresAt = expiresAt;
        }
    }
    
    @Transactional
    public Candidat inscrireCandidat(Candidat candidat, Integer creneauId) throws Exception {
        return inscrireCandidat(candidat, creneauId, "GENERAL", null);
    }

    @Transactional
    public Candidat inscrireCandidat(Candidat candidat, Integer creneauId, String modeTest, Integer idThemeSpecialite) throws Exception {
        // Vérifier si le créneau existe et est disponible
        Optional<CreneauHoraire> creneauOpt = creneauHoraireRepository.findById(creneauId);
        if (creneauOpt.isEmpty()) {
            throw new Exception("Le créneau horaire n'existe pas");
        }

        CreneauHoraire creneau = creneauOpt.get();
        if (creneau.getEstComplet()) {
            throw new Exception("Le créneau horaire est complet");
        }

        Integer places = creneau.getPlacesDisponibles();
        if (places == null) {
            places = 0;
        }
        if (places <= 0) {
            creneau.setEstComplet(true);
            creneauHoraireRepository.update(creneau);
            throw new Exception("Le créneau horaire est complet");
        }

        String resolvedModeTest = (modeTest == null || modeTest.trim().isEmpty()) ? "GENERAL" : modeTest.trim().toUpperCase();
        if (!resolvedModeTest.equals("GENERAL") && !resolvedModeTest.equals("INFORMATIQUE")) {
            throw new Exception("Le modeTest est invalide (GENERAL ou INFORMATIQUE)");
        }
        if (resolvedModeTest.equals("INFORMATIQUE") && idThemeSpecialite == null) {
            throw new Exception("idThemeSpecialite est obligatoire si modeTest=INFORMATIQUE");
        }

        Optional<Candidat> candidatExistantOpt = candidatRepository.findByEmail(candidat.getEmail());
        Candidat savedCandidat;

        if (candidatExistantOpt.isPresent()) {
            Candidat candidatExistant = candidatExistantOpt.get();

            // Un candidat ne peut avoir qu'une inscription active (créneau futur ou en cours)
            List<Inscription> inscriptions = inscriptionRepository.findByCandidat(candidatExistant.getId());
            LocalDateTime maintenant = LocalDateTime.now();

            for (Inscription inscription : inscriptions) {
                CreneauHoraire c = inscription.getCreneau();
                if (c == null || c.getDateExam() == null || c.getHeureFin() == null) {
                    continue;
                }
                LocalDateTime finCreneau = LocalDateTime.of(c.getDateExam(), c.getHeureFin());
                if (finCreneau.isAfter(maintenant)) {
                    throw new Exception("Vous avez déjà une inscription active. Veuillez attendre la fin du créneau actuel avant de vous réinscrire.");
                }
            }

            if (inscriptionRepository.existsByCandidatAndCreneau(candidatExistant.getId(), creneauId)) {
                throw new Exception("Vous êtes déjà inscrit à ce créneau");
            }

            // Mettre à jour les infos (si l'utilisateur les ressaisit) + réinitialiser la validation
            candidatExistant.setNom(candidat.getNom());
            candidatExistant.setPrenom(candidat.getPrenom());
            candidatExistant.setEcole(candidat.getEcole());
            candidatExistant.setFiliere(candidat.getFiliere());
            candidatExistant.setGsm(candidat.getGsm());
            candidatExistant.setEstValide(false);
            candidatExistant.setCodeSession(null);

            savedCandidat = candidatRepository.update(candidatExistant);
        } else {
            // Le candidat est créé sans code session pour l'instant
            candidat.setEstValide(false);
            candidat.setCodeSession(null); // Pas de code session avant validation
            savedCandidat = candidatRepository.create(candidat);
        }

        // Créer l'inscription
        Inscription inscription = new Inscription(savedCandidat, creneau, resolvedModeTest, idThemeSpecialite);
        inscriptionRepository.create(inscription);

        // Réserver la place (décrémenter) uniquement quand l'inscription est bien créée
        Integer placesAfter = creneau.getPlacesDisponibles();
        if (placesAfter == null) {
            placesAfter = 0;
        }
        if (placesAfter <= 0) {
            creneau.setEstComplet(true);
            creneauHoraireRepository.update(creneau);
            throw new Exception("Le créneau horaire est complet");
        }
        int remaining = Math.max(0, placesAfter - 1);
        creneau.setPlacesDisponibles(remaining);
        if (remaining <= 0) {
            creneau.setEstComplet(true);
        }
        creneauHoraireRepository.update(creneau);
        
        // Envoyer l'email de confirmation d'inscription (en attente de validation)
        try {
            emailService.envoyerEmailInscription(savedCandidat, creneau, null);
        } catch (Exception e) {
            // Logger l'erreur mais ne pas bloquer l'inscription
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
        
        return savedCandidat;
    }
    
    @Transactional
    public Candidat validerInscription(Integer candidatId) throws Exception {
        Optional<Candidat> candidatOpt = candidatRepository.findById(candidatId);
        if (candidatOpt.isEmpty()) {
            throw new Exception("Candidat non trouvé");
        }
        
        Candidat candidat = candidatOpt.get();

        if (Boolean.TRUE.equals(candidat.getEstValide()) && candidat.getCodeSession() != null && !candidat.getCodeSession().trim().isEmpty()) {
            return candidat;
        }

        // Décrémenter la place du créneau au moment de la validation
        List<Inscription> inscriptions = inscriptionRepository.findByCandidat(candidat.getId());
        if (inscriptions.isEmpty()) {
            throw new Exception("Aucune inscription trouvée pour ce candidat");
        }

        Inscription inscriptionToConfirm = null;
        for (Inscription i : inscriptions) {
            if (i != null && !Boolean.TRUE.equals(i.getEstConfirme())) {
                inscriptionToConfirm = i;
                break;
            }
        }
        if (inscriptionToConfirm == null) {
            throw new Exception("Inscription déjà confirmée");
        }

        inscriptionToConfirm.setEstConfirme(true);
        inscriptionRepository.update(inscriptionToConfirm);
        
        // Générer le code session uniquement lors de la validation
        String codeSession = generateUniqueCodeSession();
        candidat.setCodeSession(codeSession);
        candidat.setEstValide(true);
        
        // Envoyer l'email de validation avec le code session
        try {
            emailService.envoyerEmailValidation(candidat);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email de validation: " + e.getMessage());
        }
        
        return candidatRepository.update(candidat);
    }

    @Transactional
    public Candidat demanderNouveauTest(Integer candidatId, Integer creneauId) throws Exception {
        return demanderNouveauTest(candidatId, creneauId, "GENERAL", null);
    }

    @Transactional
    public Candidat demanderNouveauTest(Integer candidatId, Integer creneauId, String modeTest, Integer idThemeSpecialite) throws Exception {
        if (candidatId == null) {
            throw new Exception("Le candidatId est obligatoire");
        }
        if (creneauId == null) {
            throw new Exception("Le creneauId est obligatoire");
        }

        String resolvedModeTest = (modeTest == null || modeTest.trim().isEmpty()) ? "GENERAL" : modeTest.trim().toUpperCase();
        if (!resolvedModeTest.equals("GENERAL") && !resolvedModeTest.equals("INFORMATIQUE")) {
            throw new Exception("Le modeTest est invalide (GENERAL ou INFORMATIQUE)");
        }
        if (resolvedModeTest.equals("INFORMATIQUE") && idThemeSpecialite == null) {
            throw new Exception("idThemeSpecialite est obligatoire si modeTest=INFORMATIQUE");
        }

        Optional<Candidat> candidatOpt = candidatRepository.findById(candidatId);
        if (candidatOpt.isEmpty()) {
            throw new Exception("Candidat non trouvé");
        }
        Optional<CreneauHoraire> creneauOpt = creneauHoraireRepository.findById(creneauId);
        if (creneauOpt.isEmpty()) {
            throw new Exception("Le créneau horaire n'existe pas");
        }

        Candidat candidat = candidatOpt.get();
        CreneauHoraire creneau = creneauOpt.get();

        // Un candidat ne peut avoir qu'une inscription active (créneau futur ou en cours),
        // sauf s'il a déjà terminé le test correspondant à ce créneau.
        List<Inscription> inscriptionsExistantes = inscriptionRepository.findByCandidat(candidat.getId());
        LocalDateTime maintenant = LocalDateTime.now();
        List<SessionTest> sessionsCandidat = sessionTestRepository.findByCandidat(candidat.getId());

        for (Inscription inscription : inscriptionsExistantes) {
            CreneauHoraire c = inscription.getCreneau();
            if (c == null || c.getDateExam() == null || c.getHeureDebut() == null || c.getHeureFin() == null) {
                continue;
            }

            LocalDateTime debut = LocalDateTime.of(c.getDateExam(), c.getHeureDebut());
            LocalDateTime fin = LocalDateTime.of(c.getDateExam(), c.getHeureFin());

            if (fin.isAfter(maintenant)) {
                boolean testDejaTermineDansCeCreneau = sessionsCandidat.stream().anyMatch(s -> {
                    if (s == null || !Boolean.TRUE.equals(s.getEstTermine()) || s.getDateFin() == null) {
                        return false;
                    }
                    LocalDateTime df = s.getDateFin();
                    return (!df.isBefore(debut)) && (!df.isAfter(fin));
                });

                if (!testDejaTermineDansCeCreneau) {
                    throw new Exception("Vous avez déjà une inscription active. Veuillez attendre la validation/refus, la fin du créneau, ou terminer le test.");
                }
            }
        }

        if (Boolean.TRUE.equals(creneau.getEstComplet())) {
            throw new Exception("Le créneau horaire est complet");
        }
        Integer places = creneau.getPlacesDisponibles();
        if (places == null) {
            places = 0;
        }
        if (places <= 0) {
            creneau.setEstComplet(true);
            creneauHoraireRepository.update(creneau);
            throw new Exception("Le créneau horaire est complet");
        }

        if (inscriptionRepository.existsByCandidatAndCreneau(candidat.getId(), creneauId)) {
            throw new Exception("Vous êtes déjà inscrit à ce créneau");
        }

        // Créer l'inscription (en attente)
        Inscription inscription = new Inscription(candidat, creneau, resolvedModeTest, idThemeSpecialite);
        inscriptionRepository.create(inscription);

        // Réserver la place (décrémenter) uniquement quand l'inscription est bien créée
        Integer placesAfter = creneau.getPlacesDisponibles();
        if (placesAfter == null) {
            placesAfter = 0;
        }
        if (placesAfter <= 0) {
            creneau.setEstComplet(true);
            creneauHoraireRepository.update(creneau);
            throw new Exception("Le créneau horaire est complet");
        }
        int remaining = Math.max(0, placesAfter - 1);
        creneau.setPlacesDisponibles(remaining);
        if (remaining <= 0) {
            creneau.setEstComplet(true);
        }
        creneauHoraireRepository.update(creneau);

        // Forcer une nouvelle validation admin (nouveau codeSession sera généré à la validation)
        candidat.setEstValide(false);
        candidat.setCodeSession(null);
        Candidat updated = candidatRepository.update(candidat);

        // Email inscription en attente
        try {
            emailService.envoyerEmailInscription(updated, creneau, null);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }

        return updated;
    }
    
    public Optional<Candidat> findByEmail(String email) {
        return candidatRepository.findByEmail(email);
    }

    @Transactional
    public void demanderOtpConnexion(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("L'email est obligatoire");
        }

        Optional<Candidat> candidatOpt = candidatRepository.findByEmail(email);
        if (candidatOpt.isEmpty()) {
            throw new Exception("Aucun candidat trouvé avec cet email");
        }

        Candidat candidat = candidatOpt.get();
        String otp = String.format("%06d", OTP_RANDOM.nextInt(1_000_000));
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(OTP_VALIDITY_SECONDS);
        OTP_STORE.put(email.trim().toLowerCase(), new OtpEntry(otp, expiresAt));

        try {
            emailService.envoyerOtpConnexion(candidat.getEmail(), candidat.getPrenom(), otp);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'OTP: " + e.getMessage());
            throw new Exception("Impossible d'envoyer le code de connexion. Veuillez réessayer.");
        }
    }

    @Transactional
    public Candidat verifierOtpConnexion(String email, String otp) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("L'email est obligatoire");
        }
        if (otp == null || otp.trim().isEmpty()) {
            throw new Exception("Le code est obligatoire");
        }

        String key = email.trim().toLowerCase();
        OtpEntry entry = OTP_STORE.get(key);
        if (entry == null) {
            throw new Exception("Code expiré ou invalide");
        }
        if (LocalDateTime.now().isAfter(entry.expiresAt)) {
            OTP_STORE.remove(key);
            throw new Exception("Code expiré ou invalide");
        }
        if (!entry.otp.equals(otp.trim())) {
            throw new Exception("Code incorrect");
        }

        OTP_STORE.remove(key);

        Optional<Candidat> candidatOpt = candidatRepository.findByEmail(email);
        if (candidatOpt.isEmpty()) {
            throw new Exception("Aucun candidat trouvé avec cet email");
        }
        return candidatOpt.get();
    }
    
    @Transactional
    public Optional<Candidat> findByCodeSession(String codeSession) {
        return candidatRepository.findByCodeSession(codeSession);
    }
    
    public List<Candidat> findByNomOrPrenomOrEcole(String searchTerm) {
        return candidatRepository.findByNomOrPrenomOrEcole(searchTerm);
    }
    
    public List<Candidat> findByEstValide(Boolean estValide) {
        return candidatRepository.findByEstValide(estValide);
    }
    
    public List<Candidat> findRecentCandidates(int limit) {
        return candidatRepository.findRecentCandidates(limit);
    }
    
    @Transactional
    public Candidat updateCandidat(Candidat candidat) {
        return candidatRepository.update(candidat);
    }
    
    @Transactional
    public void deleteCandidat(Integer candidatId) {
        candidatRepository.deleteById(candidatId);
    }
    
    public List<Candidat> findAll() {
        return candidatRepository.findAll();
    }
    
    public Optional<Candidat> findById(Integer id) {
        return candidatRepository.findById(id);
    }
    
    public long count() {
        return candidatRepository.count();
    }
    
    /**
     * Génère un code session unique de 8 caractères
     */
    private String generateUniqueCodeSession() {
        Random random = new Random();
        StringBuilder code;
        int attempts = 0;
        final int maxAttempts = 100;
        
        do {
            code = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            attempts++;
            
            if (attempts > maxAttempts) {
                throw new RuntimeException("Impossible de générer un code session unique après " + maxAttempts + " tentatives");
            }
        } while (candidatRepository.existsByCodeSession(code.toString()));
        
        return code.toString();
    }
    
    /**
     * Vérifie si un candidat peut passer un test maintenant
     */
    @Transactional
    public boolean peutPasserTest(String codeSession) {
        Optional<Candidat> candidatOpt = candidatRepository.findByCodeSession(codeSession);
        if (candidatOpt.isEmpty()) {
            return false;
        }
        
        Candidat candidat = candidatOpt.get();
        
        // Vérifier si le candidat est validé
        if (!candidat.getEstValide()) {
            return false;
        }
        
        // Vérifier si le candidat a une inscription à un créneau
        List<Inscription> inscriptions = inscriptionRepository.findByCandidat(candidat.getId());
        if (inscriptions.isEmpty()) {
            return false;
        }
        
        // Vérifier si le créneau horaire est valide
        LocalDateTime maintenant = LocalDateTime.now();
        System.out.println("DEBUG: Validation créneau pour candidat " + candidat.getId() + " à " + maintenant);
        boolean creneauValide = false;
        
        for (Inscription inscription : inscriptions) {
            CreneauHoraire creneau = inscription.getCreneau();
            if (creneau == null || creneau.getDateExam() == null || creneau.getHeureDebut() == null || creneau.getHeureFin() == null) {
                continue;
            }
            System.out.println("DEBUG: Créneau trouvé - Début: " + creneau.getHeureDebut() + ", Fin: " + creneau.getHeureFin());
            
            // Créer les LocalDateTime complets pour le créneau
            LocalDateTime debutCreneau = LocalDateTime.of(
                creneau.getDateExam(),
                creneau.getHeureDebut()
            );
            LocalDateTime finCreneau = LocalDateTime.of(
                creneau.getDateExam(),
                creneau.getHeureFin()
            );
            
            System.out.println("DEBUG: Début créneau complet: " + debutCreneau);
            System.out.println("DEBUG: Fin créneau complet: " + finCreneau);
            
            // Vérifier si le créneau n'est pas encore atteint (trop tôt)
            if (maintenant.isBefore(debutCreneau)) {
                System.out.println("DEBUG: Trop tôt - " + maintenant + " < " + debutCreneau);
                return false; // Trop tôt, le créneau n'a pas commencé
            }
            
            // Vérifier si la durée du test est dépassée (trop tard)
            // Utiliser directement l'heure de fin du créneau (pas +2h)
            if (maintenant.isAfter(finCreneau)) {
                System.out.println("DEBUG: Trop tard - " + maintenant + " > " + finCreneau);
                return false; // Trop tard, la durée du test est dépassée
            }
            
            System.out.println("DEBUG: Créneau valide pour cette inscription");
            creneauValide = true;
        }
        
        return creneauValide;
    }

    @Transactional
    public Optional<Map<String, Object>> getCreneauInfo(String codeSession) {
        Optional<Candidat> candidatOpt = candidatRepository.findByCodeSession(codeSession);
        if (candidatOpt.isEmpty()) {
            return Optional.empty();
        }

        Candidat candidat = candidatOpt.get();
        List<Inscription> inscriptions = inscriptionRepository.findByCandidat(candidat.getId());
        if (inscriptions.isEmpty()) {
            return Optional.empty();
        }

        LocalDateTime now = LocalDateTime.now();

        Inscription best = null;
        LocalDateTime bestStart = null;
        LocalDateTime bestEnd = null;

        for (Inscription inscription : inscriptions) {
            CreneauHoraire c = inscription.getCreneau();
            if (c == null || c.getDateExam() == null || c.getHeureDebut() == null || c.getHeureFin() == null) {
                continue;
            }

            LocalDateTime start = LocalDateTime.of(c.getDateExam(), c.getHeureDebut());
            LocalDateTime end = LocalDateTime.of(c.getDateExam(), c.getHeureFin());

            if (best == null) {
                best = inscription;
                bestStart = start;
                bestEnd = end;
                continue;
            }

            boolean currentActiveOrFuture = end.isAfter(now);
            boolean bestActiveOrFuture = bestEnd != null && bestEnd.isAfter(now);

            if (currentActiveOrFuture && !bestActiveOrFuture) {
                best = inscription;
                bestStart = start;
                bestEnd = end;
                continue;
            }

            if (currentActiveOrFuture == bestActiveOrFuture) {
                if (bestStart != null && start.isBefore(bestStart)) {
                    best = inscription;
                    bestStart = start;
                    bestEnd = end;
                }
            }
        }

        if (best == null || bestStart == null || bestEnd == null) {
            return Optional.empty();
        }

        boolean canStart = !now.isBefore(bestStart) && !now.isAfter(bestEnd);
        long secondsUntilStart = Math.max(0, Duration.between(now, bestStart).getSeconds());
        long secondsUntilEnd = Math.max(0, Duration.between(now, bestEnd).getSeconds());

        String status;
        if (now.isBefore(bestStart)) {
            status = "BEFORE";
        } else if (now.isAfter(bestEnd)) {
            status = "AFTER";
        } else {
            status = "DURING";
        }

        long nowMs = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long startMs = bestStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endMs = bestEnd.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Map<String, Object> out = new HashMap<>();
        out.put("serverTime", now.toString());
        out.put("serverTimeMs", nowMs);
        out.put("debutCreneau", bestStart.toString());
        out.put("debutCreneauMs", startMs);
        out.put("finCreneau", bestEnd.toString());
        out.put("finCreneauMs", endMs);
        out.put("canStart", canStart);
        out.put("secondsUntilStart", secondsUntilStart);
        out.put("secondsUntilEnd", secondsUntilEnd);
        out.put("status", status);
        return Optional.of(out);
    }
    
    /**
     * Vérifie si le créneau horaire est passé
     */
    @Transactional
    public boolean creneauEstPasse(String codeSession) {
        Optional<Candidat> candidatOpt = candidatRepository.findByCodeSession(codeSession);
        if (candidatOpt.isEmpty()) {
            return true; // Considérer comme passé si le candidat n'existe pas
        }
        
        List<Inscription> inscriptions = inscriptionRepository.findByCandidat(candidatOpt.get().getId());
        if (inscriptions.isEmpty()) {
            return true;
        }
        
        for (Inscription inscription : inscriptions) {
            CreneauHoraire creneau = inscription.getCreneau();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime finCreneau = LocalDateTime.of(creneau.getDateExam(), creneau.getHeureFin());
            
            if (now.isAfter(finCreneau)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Vérifie si le créneau horaire est atteint (peut commencer le test)
     */
    @Transactional
    public boolean creneauEstAtteint(String codeSession) {
        Optional<Candidat> candidatOpt = candidatRepository.findByCodeSession(codeSession);
        if (candidatOpt.isEmpty()) {
            return false;
        }
        
        List<Inscription> inscriptions = inscriptionRepository.findByCandidat(candidatOpt.get().getId());
        if (inscriptions.isEmpty()) {
            return false;
        }
        
        for (Inscription inscription : inscriptions) {
            CreneauHoraire creneau = inscription.getCreneau();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime debutCreneau = LocalDateTime.of(creneau.getDateExam(), creneau.getHeureDebut());
            
            if (now.isAfter(debutCreneau)) {
                return true;
            }
        }
        
        return false;
    }
}
