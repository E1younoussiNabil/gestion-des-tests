package com.gestiontests.service;

import com.gestiontests.entity.*;
import com.gestiontests.repository.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class TestService {
    
    @Inject
    private SessionTestRepository sessionTestRepository;
    
    @Inject
    private SessionQuestionRepository sessionQuestionRepository;
    
    @Inject
    private ReponseCandidatRepository reponseCandidatRepository;
    
    @Inject
    private ReponsePossibleRepository reponsePossibleRepository;
    
    @Inject
    private QuestionRepository questionRepository;
    
    @Inject
    private ThemeRepository themeRepository;
    
    @Inject
    private CandidatService candidatService;
    
    @Inject
    private ParametreRepository parametreRepository;

    @Inject
    private InscriptionRepository inscriptionRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Inject
    private EmailService emailService;

    private static final List<String> CS_THEME_NOMS = List.of(
        "Cybersécurité",
        "Science des données",
        "Développement logiciel",
        "Réseaux et systèmes"
    );
    
    @Transactional
    public SessionTest demarrerTest(String codeSession) throws Exception {
        return demarrerTest(codeSession, null);
    }

    @Transactional
    public SessionTest demarrerTest(String codeSession, Integer themeId) throws Exception {
        // Vérifier si le candidat existe et peut passer le test
        Optional<Candidat> candidatOpt = candidatService.findByCodeSession(codeSession);
        if (candidatOpt.isEmpty()) {
            throw new Exception("Code session invalide");
        }
        
        Candidat candidat = candidatOpt.get();
        
        if (!candidatService.peutPasserTest(codeSession)) {
            throw new Exception("Vous ne pouvez pas passer le test maintenant");
        }
        
        // Vérifier si une session existe déjà pour ce codeSession
        Optional<SessionTest> sessionExistante = sessionTestRepository.findByCodeSession(codeSession);
        if (sessionExistante.isPresent() && !sessionExistante.get().getEstTermine()) {
            SessionTest session = sessionExistante.get();
            List<SessionQuestion> questions = sessionQuestionRepository.findBySession(session.getId());
            
            System.out.println("Returning existing active session: " + session.getId() + " with " + questions.size() + " questions");
            return session;
        }

        // Si une session terminée existe, on interdit de relancer un test avec le même codeSession
        if (sessionExistante.isPresent() && sessionExistante.get().getEstTermine()) {
            throw new Exception("Ce code session a déjà été utilisé. Veuillez faire une nouvelle demande et attendre la validation.");
        }

        Integer effectiveThemeId = themeId;
        String resolvedMode = "GENERAL";
        CreneauHoraire resolvedCreneau = null;
        Integer resolvedNombreQuestions = null;
        if (effectiveThemeId == null) {
            Optional<Inscription> latestInscriptionOpt = inscriptionRepository.findLatestByCandidat(candidat.getId());
            if (latestInscriptionOpt.isPresent()) {
                Inscription latest = latestInscriptionOpt.get();
                String mode = latest.getModeTest();
                if (mode != null && !mode.trim().isEmpty()) {
                    resolvedMode = mode;
                }
                if (latest.getCreneau() != null) {
                    resolvedCreneau = latest.getCreneau();
                    resolvedNombreQuestions = resolvedCreneau.getNombreQuestions();
                }
                if (resolvedMode.equalsIgnoreCase("INFORMATIQUE")) {
                    effectiveThemeId = latest.getIdThemeSpecialite();
                }
            }
        }
        
        // Générer les questions pour le test
        List<SessionQuestion> questions = genererQuestionsPourTest(resolvedMode, effectiveThemeId, resolvedNombreQuestions);
        if (questions.isEmpty()) {
            throw new Exception("Aucune question disponible pour le test");
        }
        
        // Créer la session de test
        SessionTest sessionTest = new SessionTest();
        sessionTest.setCandidat(candidat);
        sessionTest.setCreneau(resolvedCreneau);
        sessionTest.setCodeSession(codeSession);
        sessionTest.setScoreMax(questions.size());
        sessionTest.demarrerSession();
        
        SessionTest savedSession = sessionTestRepository.create(sessionTest);
        
        System.out.println("Created new session with ID: " + savedSession.getId());
        
        // Sauvegarder les questions de la session avec SQL natif
        for (int i = 0; i < questions.size(); i++) {
            SessionQuestion sessionQuestion = questions.get(i);
            
            // Utiliser SQL natif pour insérer avec le bon nom de colonne
            String sql = "INSERT INTO session_questions (ordre_affichage, id_question, id_session, temps_alloue) VALUES (?, ?, ?, ?)";
            entityManager.createNativeQuery(sql)
                .setParameter(1, i + 1)
                .setParameter(2, sessionQuestion.getQuestion().getId())
                .setParameter(3, savedSession.getId())
                .setParameter(4, sessionQuestion.getTempsAlloue())
                .executeUpdate();
        }
        
        System.out.println("Created " + questions.size() + " session questions");
        
        return savedSession;
    }
    
    @Transactional
    public ReponseCandidat enregistrerReponse(Integer sessionId, Integer questionId, Map<String, Object> reponseData) throws Exception {
        Optional<SessionTest> sessionOpt = sessionTestRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new Exception("Session de test non trouvée");
        }
        
        SessionTest session = sessionOpt.get();
        if (session.getEstTermine()) {
            throw new Exception("Le test est déjà terminé");
        }
        
        // Vérifier si le temps n'est pas écoulé
        if (session.getDateDebut() != null) {
            LocalDateTime finEstimee = session.getDateDebut().plusMinutes(20); // 20 minutes de test
            if (LocalDateTime.now().isAfter(finEstimee)) {
                terminerTest(sessionId);
                throw new Exception("Le temps du test est écoulé");
            }
        }
        
        Optional<SessionQuestion> sessionQuestionOpt = sessionQuestionRepository.findBySessionAndQuestion(sessionId, questionId);
        if (sessionQuestionOpt.isEmpty()) {
            throw new Exception("Question non trouvée dans cette session");
        }
        
        SessionQuestion sessionQuestion = sessionQuestionOpt.get();
        
        // MULTIPLE: replace all existing selections for this question
        if (reponseData.containsKey("reponsePossibleIds") && reponseData.get("reponsePossibleIds") instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> ids = (List<Object>) reponseData.get("reponsePossibleIds");

            reponseCandidatRepository.deleteBySessionQuestion(sessionQuestion.getId());

            ReponseCandidat last = null;
            for (Object idObj : ids) {
                Integer repId = null;
                if (idObj instanceof Number) {
                    repId = ((Number) idObj).intValue();
                } else if (idObj != null) {
                    try {
                        repId = Integer.parseInt(String.valueOf(idObj));
                    } catch (Exception ignored) {
                        repId = null;
                    }
                }
                if (repId == null) {
                    continue;
                }
                Map<String, Object> single = new HashMap<>(reponseData);
                single.remove("reponsePossibleIds");
                single.put("reponsePossibleId", repId);
                ReponseCandidat reponse = creerReponse(sessionQuestion, single);
                last = reponseCandidatRepository.create(reponse);
            }
            return last;
        }

        // UNIQUE/TEXTE: replace previous answer for this question
        Optional<ReponseCandidat> reponseExistanteOpt = reponseCandidatRepository.findBySessionQuestion(sessionQuestion.getId());
        if (reponseExistanteOpt.isPresent()) {
            ReponseCandidat reponse = reponseExistanteOpt.get();
            mettreAJourReponse(reponse, reponseData);
            return reponseCandidatRepository.update(reponse);
        }

        ReponseCandidat reponse = creerReponse(sessionQuestion, reponseData);
        return reponseCandidatRepository.create(reponse);
    }
    
    @Transactional
    public SessionTest terminerTest(Integer sessionId) throws Exception {
        Optional<SessionTest> sessionOpt = sessionTestRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new Exception("Session de test non trouvée");
        }
        
        SessionTest session = sessionOpt.get();
        if (session.getEstTermine()) {
            return session;
        }
        
        // Calculer le score
        calculerScore(session);
        
        // Marquer comme terminé
        session.terminerSession();
        SessionTest updatedSession = sessionTestRepository.update(session);
        
        // Envoyer les résultats par email
        try {
            emailService.envoyerEmailResultats(
                session.getCandidat(),
                session.getScoreTotal().toString(),
                session.getPourcentage().toString()
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi des résultats par email: " + e.getMessage());
        }
        
        return updatedSession;
    }
    
    public Optional<SessionTest> getSessionById(Integer sessionId) {
        return sessionTestRepository.findByIdWithCandidat(sessionId);
    }
    
    public Optional<SessionTest> getSessionByCodeSession(String codeSession) {
        return sessionTestRepository.findByCodeSession(codeSession);
    }
    
    public List<SessionQuestion> getQuestionsBySession(Integer sessionId) {
        return sessionQuestionRepository.findBySession(sessionId);
    }
    
    public Optional<SessionQuestion> getNextQuestion(Integer sessionId, Integer currentQuestionId) {
        return sessionQuestionRepository.findNextQuestion(sessionId, currentQuestionId);
    }
    
    public Optional<SessionQuestion> getPreviousQuestion(Integer sessionId, Integer currentQuestionId) {
        return sessionQuestionRepository.findPreviousQuestion(sessionId, currentQuestionId);
    }
    
    public Optional<ReponseCandidat> getReponseBySessionQuestion(Integer sessionQuestionId) {
        return reponseCandidatRepository.findBySessionQuestion(sessionQuestionId);
    }
    
    public List<SessionTest> getSessionsByCandidat(Integer candidatId) {
        return sessionTestRepository.findByCandidat(candidatId);
    }
    
    public List<SessionTest> getAllSessions() {
        return sessionTestRepository.findAllWithCandidat();
    }
    
    public List<SessionTest> getRecentSessions(int limit) {
        return sessionTestRepository.findRecentSessions(limit);
    }
    
    private List<SessionQuestion> genererQuestionsPourTest(String modeTest, Integer themeId, Integer nombreQuestionsCreneau) {
        // Récupérer les paramètres
        Integer tempsParQuestion = parametreRepository.getValeurParametreAsInteger("TEMPS_QUESTION_PAR_DEFAUT", 120);

        List<Theme> themes;
        String effectiveMode = modeTest != null ? modeTest : "GENERAL";

        if (effectiveMode.equalsIgnoreCase("INFORMATIQUE")) {
            if (themeId == null) {
                return List.of();
            }
            Optional<Theme> t = themeRepository.findById(themeId);
            themes = t.map(List::of).orElseGet(List::of);
        } else {
            // Mode "culture générale": toujours les 6 thèmes historiques
            List<String> generalThemeNoms = List.of(
                "Mathématiques",
                "Informatique",
                "Physique",
                "Chimie",
                "Français",
                "Anglais"
            );
            themes = new ArrayList<>();
            for (String nom : generalThemeNoms) {
                themeRepository.findByNom(nom).ifPresent(themes::add);
            }
        }

        List<SessionQuestion> sessionQuestions = new ArrayList<>();
        
        Random random = new Random();

        if (effectiveMode.equalsIgnoreCase("INFORMATIQUE")) {
            int targetTotal = 0;
            if (nombreQuestionsCreneau != null && nombreQuestionsCreneau > 0) {
                targetTotal = nombreQuestionsCreneau;
            } else {
                targetTotal = parametreRepository.getValeurParametreAsInteger("NOMBRE_QUESTIONS_TOTAL_INFO", 20);
            }

            Theme theme = themes.isEmpty() ? null : themes.get(0);
            if (theme == null) {
                return List.of();
            }

            List<Question> questionsTheme = questionRepository.findByTheme(theme.getId());
            Collections.shuffle(questionsTheme, random);
            int nombreAPrendre = Math.min(targetTotal, questionsTheme.size());

            for (int i = 0; i < nombreAPrendre; i++) {
                Question question = questionsTheme.get(i);
                SessionQuestion sessionQuestion = new SessionQuestion();
                sessionQuestion.setQuestion(question);
                sessionQuestion.setTempsAlloue(tempsParQuestion);
                sessionQuestions.add(sessionQuestion);
            }
        } else {
            // Culture générale: 30 questions fixes = 5 questions par chacun des 6 thèmes
            int nombreParTheme = 5;

            for (Theme theme : themes) {
                List<Question> questionsTheme = questionRepository.findByTheme(theme.getId());
                Collections.shuffle(questionsTheme, random);
                int nombreAPrendre = Math.min(nombreParTheme, questionsTheme.size());

                for (int i = 0; i < nombreAPrendre; i++) {
                    Question question = questionsTheme.get(i);
                    SessionQuestion sessionQuestion = new SessionQuestion();
                    sessionQuestion.setQuestion(question);
                    sessionQuestion.setTempsAlloue(tempsParQuestion);
                    sessionQuestions.add(sessionQuestion);
                }
            }
        }
        
        // Mélanger toutes les questions pour l'ordre aléatoire final
        Collections.shuffle(sessionQuestions, random);
        
        return sessionQuestions;
    }

    public List<Theme> getThemesInformatique() {
        List<Theme> out = new ArrayList<>();
        for (String nom : CS_THEME_NOMS) {
            themeRepository.findByNom(nom).ifPresent(out::add);
        }
        return out;
    }
    
    private void calculerScore(SessionTest session) {
        List<SessionQuestion> questions = sessionQuestionRepository.findBySession(session.getId());
        System.out.println("DEBUG: Calculating score for session " + session.getId() + " with " + questions.size() + " questions");
        
        int score = 0;
        int totalQuestionsEvaluees = 0;
        
        for (SessionQuestion sessionQuestion : questions) {
            String typeNom = sessionQuestion.getQuestion() != null && sessionQuestion.getQuestion().getTypeQuestion() != null
                ? sessionQuestion.getQuestion().getTypeQuestion().getNom()
                : null;

            List<ReponseCandidat> reps = reponseCandidatRepository.findAllBySessionQuestion(sessionQuestion.getId());
            if (reps.isEmpty()) {
                System.out.println("DEBUG: Question " + sessionQuestion.getQuestion().getId() + " - No response found");
                continue;
            }

            boolean estCorrect = false;
            if ("MULTIPLE".equalsIgnoreCase(typeNom)) {
                List<Integer> candIds = reps.stream()
                    .map(r -> r.getReponsePossible() != null ? r.getReponsePossible().getId() : null)
                    .filter(Objects::nonNull)
                    .toList();

                Integer qid = sessionQuestion.getQuestion() != null ? sessionQuestion.getQuestion().getId() : null;
                List<Integer> corrIds = qid != null
                    ? reponsePossibleRepository.findCorrectByQuestion(qid).stream().map(ReponsePossible::getId).filter(Objects::nonNull).toList()
                    : List.of();

                Set<Integer> candSet = new HashSet<>(candIds);
                Set<Integer> corrSet = new HashSet<>(corrIds);
                estCorrect = !corrSet.isEmpty() && candSet.equals(corrSet);
            } else if ("UNIQUE".equalsIgnoreCase(typeNom)) {
                estCorrect = reps.stream().anyMatch(r -> Boolean.TRUE.equals(r.getEstCorrect()));
            } else {
                estCorrect = false;
            }

            totalQuestionsEvaluees++;
            System.out.println("DEBUG: Question " + sessionQuestion.getQuestion().getId() + " - estCorrect: " + estCorrect);
            if (estCorrect) {
                score++;
            }
        }
        
        System.out.println("DEBUG: Final score - Correct: " + score + ", Total evaluated: " + totalQuestionsEvaluees + ", Total questions: " + questions.size());
        
        session.setScoreTotal(score);
        session.setScoreMax(questions.size());
    }
    
    private ReponseCandidat creerReponse(SessionQuestion sessionQuestion, Map<String, Object> reponseData) {
        ReponseCandidat reponse = new ReponseCandidat();
        reponse.setSessionQuestion(sessionQuestion);
        
        if (reponseData.containsKey("reponsePossibleId")) {
            // Réponse à choix
            Integer reponsePossibleId = (Integer) reponseData.get("reponsePossibleId");
            Optional<ReponsePossible> reponsePossibleOpt = reponsePossibleRepository.findById(reponsePossibleId);
            if (reponsePossibleOpt.isPresent()) {
                reponse.setReponsePossible(reponsePossibleOpt.get());
                reponse.setEstCorrect(reponsePossibleOpt.get().getEstCorrect());
            }
        } else if (reponseData.containsKey("reponseText")) {
            // Réponse textuelle
            reponse.setReponseText((String) reponseData.get("reponseText"));
            reponse.setEstCorrect(false); // Les réponses textuelles ne sont pas auto-évaluées
        }
        
        if (reponseData.containsKey("tempsReponse")) {
            reponse.setTempsReponse((Integer) reponseData.get("tempsReponse"));
        }
        
        return reponse;
    }
    
    private void mettreAJourReponse(ReponseCandidat reponse, Map<String, Object> reponseData) {
        if (reponseData.containsKey("reponsePossibleId")) {
            // Réponse à choix
            Integer reponsePossibleId = (Integer) reponseData.get("reponsePossibleId");
            Optional<ReponsePossible> reponsePossibleOpt = reponsePossibleRepository.findById(reponsePossibleId);
            if (reponsePossibleOpt.isPresent()) {
                reponse.setReponsePossible(reponsePossibleOpt.get());
                reponse.setEstCorrect(reponsePossibleOpt.get().getEstCorrect());
                reponse.setReponseText(null);
            }
        } else if (reponseData.containsKey("reponseText")) {
            reponse.setReponseText((String) reponseData.get("reponseText"));
            reponse.setReponsePossible(null);
            reponse.setEstCorrect(false); // Les réponses textuelles ne sont pas auto-évaluées
        }
        
        if (reponseData.containsKey("tempsReponse")) {
            reponse.setTempsReponse((Integer) reponseData.get("tempsReponse"));
        }
    }
    
    public boolean peutNaviguerVersQuestion(Integer sessionId, Integer questionId) throws Exception {
        Optional<SessionTest> sessionOpt = sessionTestRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return false;
        }
        
        SessionTest session = sessionOpt.get();
        if (session.getEstTermine()) {
            return false;
        }
        
        // Vérifier si la question appartient à cette session
        Optional<SessionQuestion> sessionQuestionOpt = sessionQuestionRepository.findBySessionAndQuestion(sessionId, questionId);
        return sessionQuestionOpt.isPresent();
    }
    
    public long getTempsRestant(Integer sessionId) {
        Optional<SessionTest> sessionOpt = sessionTestRepository.findById(sessionId);
        if (sessionOpt.isEmpty() || sessionOpt.get().getDateDebut() == null) {
            return 0;
        }
        
        SessionTest session = sessionOpt.get();
        LocalDateTime finEstimee = session.getDateDebut().plusMinutes(20); // 20 minutes de test
        LocalDateTime maintenant = LocalDateTime.now();
        
        if (maintenant.isAfter(finEstimee)) {
            return 0;
        }
        
        return java.time.Duration.between(maintenant, finEstimee).getSeconds();
    }
}
