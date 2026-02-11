package com.gestiontests.rest;

import com.gestiontests.entity.*;
import com.gestiontests.service.*;
//import com.gestiontests.repository.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
//import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.HashMap;

@Path("/tests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TestResource {
    
    @Inject
    private TestService testService;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @POST
    @Path("/session-active")
    public Response checkSessionActive(Map<String, String> payload) {
        try {
            String codeSession = payload.get("codeSession");
            if (codeSession == null || codeSession.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Le code session est obligatoire"))
                    .build();
            }
            
            Optional<SessionTest> sessionOpt = testService.getSessionByCodeSession(codeSession);
            if (sessionOpt.isPresent()) {
                SessionTest session = sessionOpt.get();
                if (!session.getEstTermine()) {
                    List<SessionQuestion> questions = testService.getQuestionsBySession(session.getId());
                    SessionTestDTO sessionDTO = new SessionTestDTO(session);
                    return Response.ok(Map.of(
                        "session", sessionDTO,
                        "questions", questions,
                        "currentQuestionIndex", 0,
                        "answers", Map.of(),
                        "timeLeft", testService.getTempsRestant(session.getId())
                    )).build();
                }
            }
            
            return Response.ok(Map.of()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/themes-informatique")
    public Response getThemesInformatique() {
        try {
            List<Theme> themes = testService.getThemesInformatique();
            List<Map<String, Object>> out = new ArrayList<>();
            for (Theme t : themes) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", t.getId());
                row.put("nom", t.getNom());
                row.put("description", t.getDescription());
                out.add(row);
            }
            return Response.ok(Map.of("themes", out)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/demarrer")
    public Response demarrerTest(Map<String, Object> payload) {
        try {
            String codeSession = payload.get("codeSession") != null ? String.valueOf(payload.get("codeSession")) : null;
            if (codeSession == null || codeSession.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Le code session est obligatoire"))
                    .build();
            }

            Integer themeId = null;
            Object themeIdObj = payload.get("themeId");
            if (themeIdObj instanceof Number) {
                themeId = ((Number) themeIdObj).intValue();
            } else if (themeIdObj instanceof String && !((String) themeIdObj).trim().isEmpty()) {
                try {
                    themeId = Integer.parseInt(((String) themeIdObj).trim());
                } catch (NumberFormatException ignored) {
                    themeId = null;
                }
            }
            
            SessionTest session = testService.demarrerTest(codeSession, themeId);
            SessionTestDTO sessionDTO = new SessionTestDTO(session);
            
            // Utiliser une requête native pour récupérer toutes les données des questions
            String sql = "SELECT sq.id, sq.ordre_affichage, sq.temps_alloue, " +
                       "q.id as question_id, q.libelle, q.explication, " +
                       "tq.id as type_id, tq.nom as type_nom, " +
                       "rp.id as reponse_id, rp.libelle as reponse_libelle, rp.est_correct " +
                       "FROM session_questions sq " +
                       "JOIN questions q ON sq.id_question = q.id " +
                       "JOIN types_question tq ON q.id_type_question = tq.id " +
                       "LEFT JOIN reponses_possibles rp ON q.id = rp.id_question " +
                       "WHERE sq.id_session = :sessionId " +
                       "ORDER BY sq.ordre_affichage, rp.id";
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("sessionId", session.getId())
                .getResultList();
            
            // Organiser les résultats par question
            Map<Integer, SessionQuestionDTO> questionsMap = new LinkedHashMap<>();
            
            for (Object[] row : results) {
                Integer sqId = (Integer) row[0];
                Integer ordre = (Integer) row[1];
                Integer temps = (Integer) row[2];
                Integer qId = (Integer) row[3];
                String libelle = (String) row[4];
                String explication = (String) row[5];
                Integer typeId = (Integer) row[6];
                String typeNom = (String) row[7];
                Integer repId = (Integer) row[8];
                String repLibelle = (String) row[9];
                Boolean estCorrect = (Boolean) row[10];
                
                SessionQuestionDTO sqDto = questionsMap.computeIfAbsent(sqId, id -> {
                    SessionQuestionDTO dto = new SessionQuestionDTO();
                    dto.setId(id);
                    dto.setOrdreAffichage(ordre);
                    dto.setTempsAlloue(temps);
                    
                    QuestionDTO qDto = new QuestionDTO();
                    qDto.setId(qId);
                    qDto.setLibelle(libelle);
                    qDto.setExplication(explication);
                    
                    TypeQuestionDTO tqDto = new TypeQuestionDTO();
                    tqDto.setId(typeId);
                    tqDto.setNom(typeNom);
                    qDto.setTypeQuestion(tqDto);
                    
                    qDto.setReponsesPossibles(new ArrayList<>());
                    dto.setQuestion(qDto);
                    
                    return dto;
                });
                
                if (repId != null) {
                    ReponsePossibleDTO rpDto = new ReponsePossibleDTO();
                    rpDto.setId(repId);
                    rpDto.setLibelle(repLibelle);
                    rpDto.setEstCorrect(estCorrect);
                    sqDto.getQuestion().getReponsesPossibles().add(rpDto);
                }
            }
            
            List<SessionQuestionDTO> questionDTOs = new ArrayList<>(questionsMap.values());
            
            System.out.println("Session ID: " + session.getId());
            System.out.println("Session estTermine: " + session.getEstTermine());
            System.out.println("Questions count: " + questionDTOs.size());
            
            return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                    "message", "Test démarré avec succès",
                    "session", sessionDTO,
                    "questions", questionDTOs
                ))
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    // SessionTest DTO for JSON response
    public static class SessionTestDTO {
        private Integer id;
        private String codeSession;
        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private Boolean estTermine;
        private Integer scoreTotal;
        private Integer scoreMax;
        private BigDecimal pourcentage;
        
        public SessionTestDTO(SessionTest session) {
            this.id = session.getId();
            this.codeSession = session.getCodeSession();
            this.dateDebut = session.getDateDebut();
            this.dateFin = session.getDateFin();
            this.estTermine = session.getEstTermine();
            this.scoreTotal = session.getScoreTotal();
            this.scoreMax = session.getScoreMax();
            this.pourcentage = session.getPourcentage();
        }
        
        // Getters
        public Integer getId() { return id; }
        public String getCodeSession() { return codeSession; }
        public LocalDateTime getDateDebut() { return dateDebut; }
        public LocalDateTime getDateFin() { return dateFin; }
        public Boolean getEstTermine() { return estTermine; }
        public Integer getScoreTotal() { return scoreTotal; }
        public Integer getScoreMax() { return scoreMax; }
        public BigDecimal getPourcentage() { return pourcentage; }
    }
    
    // SessionQuestion DTO for JSON response
    public static class SessionQuestionDTO {
        private Integer id;
        private Integer ordreAffichage;
        private Integer tempsAlloue;
        private Integer questionId;
        private QuestionDTO question;
        
        public SessionQuestionDTO() {}
        
        public SessionQuestionDTO(SessionQuestion sessionQuestion) {
            this.id = sessionQuestion.getId();
            this.ordreAffichage = sessionQuestion.getOrdreAffichage();
            this.tempsAlloue = sessionQuestion.getTempsAlloue();
            // Récupérer l'ID sans déclencher le lazy loading
            this.questionId = sessionQuestion.getQuestion() != null ? sessionQuestion.getQuestion().getId() : null;
        }
        
        // Getters and Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public Integer getOrdreAffichage() { return ordreAffichage; }
        public void setOrdreAffichage(Integer ordreAffichage) { this.ordreAffichage = ordreAffichage; }
        public Integer getTempsAlloue() { return tempsAlloue; }
        public void setTempsAlloue(Integer tempsAlloue) { this.tempsAlloue = tempsAlloue; }
        public Integer getQuestionId() { return questionId; }
        public void setQuestionId(Integer questionId) { this.questionId = questionId; }
        public QuestionDTO getQuestion() { return question; }
        public void setQuestion(QuestionDTO question) { this.question = question; }
    }
    
    // Question DTO for JSON response
    public static class QuestionDTO {
        private Integer id;
        private String libelle;
        private String explication;
        private TypeQuestionDTO typeQuestion;
        private List<ReponsePossibleDTO> reponsesPossibles;
        
        public QuestionDTO() {}
        
        public QuestionDTO(Question question) {
            this.id = question.getId();
            this.libelle = question.getLibelle();
            this.explication = question.getExplication();
            this.typeQuestion = new TypeQuestionDTO(question.getTypeQuestion());
            this.reponsesPossibles = question.getReponsesPossibles().stream()
                .map(ReponsePossibleDTO::new)
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Getters and Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getLibelle() { return libelle; }
        public void setLibelle(String libelle) { this.libelle = libelle; }
        public String getExplication() { return explication; }
        public void setExplication(String explication) { this.explication = explication; }
        public TypeQuestionDTO getTypeQuestion() { return typeQuestion; }
        public void setTypeQuestion(TypeQuestionDTO typeQuestion) { this.typeQuestion = typeQuestion; }
        public List<ReponsePossibleDTO> getReponsesPossibles() { return reponsesPossibles; }
        public void setReponsesPossibles(List<ReponsePossibleDTO> reponsesPossibles) { this.reponsesPossibles = reponsesPossibles; }
    }
    
    // TypeQuestion DTO for JSON response
    public static class TypeQuestionDTO {
        private Integer id;
        private String nom;
        
        public TypeQuestionDTO() {}
        
        public TypeQuestionDTO(TypeQuestion typeQuestion) {
            this.id = typeQuestion.getId();
            this.nom = typeQuestion.getNom();
        }
        
        // Getters and Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
    }
    
    // ReponsePossible DTO for JSON response
    public static class ReponsePossibleDTO {
        private Integer id;
        private String libelle;
        private Boolean estCorrect;
        
        public ReponsePossibleDTO() {}
        
        public ReponsePossibleDTO(ReponsePossible reponsePossible) {
            this.id = reponsePossible.getId();
            this.libelle = reponsePossible.getLibelle();
            this.estCorrect = reponsePossible.getEstCorrect();
        }
        
        // Getters and Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getLibelle() { return libelle; }
        public void setLibelle(String libelle) { this.libelle = libelle; }
        public Boolean getEstCorrect() { return estCorrect; }
        public void setEstCorrect(Boolean estCorrect) { this.estCorrect = estCorrect; }
    }

    @GET
    @Path("/questions/{questionId}")
    public Response getQuestionById(@PathParam("questionId") Integer questionId) {
        try {
            // Utiliser une requête native pour éviter le lazy loading
            String sql = "SELECT q.id, q.libelle, q.explication, " +
                       "tq.id as type_id, tq.nom as type_nom, " +
                       "rp.id as reponse_id, rp.libelle as reponse_libelle, rp.est_correct " +
                       "FROM questions q " +
                       "JOIN types_question tq ON q.id_type_question = tq.id " +
                       "LEFT JOIN reponses_possibles rp ON q.id = rp.id_question " +
                       "WHERE q.id = :questionId " +
                       "ORDER BY rp.id";
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("questionId", questionId)
                .getResultList();
            
            if (results.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Question non trouvée"))
                    .build();
            }
            
            // Créer le DTO à partir des résultats
            Object[] firstRow = results.get(0);
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setId((Integer) firstRow[0]);
            questionDTO.setLibelle((String) firstRow[1]);
            questionDTO.setExplication((String) firstRow[2]);
            
            TypeQuestionDTO typeDTO = new TypeQuestionDTO();
            typeDTO.setId((Integer) firstRow[3]);
            typeDTO.setNom((String) firstRow[4]);
            questionDTO.setTypeQuestion(typeDTO);
            
            List<ReponsePossibleDTO> reponses = new ArrayList<>();
            for (Object[] row : results) {
                if (row[5] != null) { // reponse_id
                    ReponsePossibleDTO reponseDTO = new ReponsePossibleDTO();
                    reponseDTO.setId((Integer) row[5]);
                    reponseDTO.setLibelle((String) row[6]);
                    reponseDTO.setEstCorrect((Boolean) row[7]);
                    reponses.add(reponseDTO);
                }
            }
            questionDTO.setReponsesPossibles(reponses);
            
            return Response.ok(questionDTO).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    @POST
    @Path("/soumettre")
    public Response soumettreTest(Map<String, Object> payload) {
        try {
            Integer sessionId = (Integer) payload.get("sessionId");
            @SuppressWarnings("unchecked")
            Map<String, Object> answers = (Map<String, Object>) payload.get("answers");

            @SuppressWarnings("unchecked")
            Map<String, Object> responseTimes = payload.get("responseTimes") instanceof Map
                ? (Map<String, Object>) payload.get("responseTimes")
                : null;
            
            if (sessionId == null || answers == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "sessionId et answers sont obligatoires"))
                    .build();
            }
            
            // Enregistrer toutes les réponses avant de terminer le test
            System.out.println("DEBUG: Processing " + answers.size() + " answers for session " + sessionId);
            for (Map.Entry<String, Object> entry : answers.entrySet()) {
                String questionIdStr = entry.getKey();
                Object answer = entry.getValue();
                
                System.out.println("DEBUG: Processing answer for question " + questionIdStr + ": " + answer);
                
                // Convertir l'ID de question de String à Integer
                Integer questionId;
                try {
                    questionId = Integer.parseInt(questionIdStr);
                } catch (NumberFormatException e) {
                    System.err.println("ID de question invalide: " + questionIdStr);
                    continue;
                }
                
                Map<String, Object> reponseData = new HashMap<>();

                Integer tempsReponse = null;
                if (responseTimes != null && responseTimes.containsKey(questionIdStr)) {
                    Object t = responseTimes.get(questionIdStr);
                    if (t instanceof Number) {
                        tempsReponse = ((Number) t).intValue();
                    } else if (t != null) {
                        try {
                            tempsReponse = Integer.parseInt(String.valueOf(t));
                        } catch (Exception ignored) {
                            tempsReponse = null;
                        }
                    }
                }
                if (tempsReponse != null) {
                    reponseData.put("tempsReponse", tempsReponse);
                }
                
                if (answer instanceof Integer) {
                    // Réponse à choix (ID de la réponse possible)
                    reponseData.put("reponsePossibleId", answer);
                    System.out.println("DEBUG: Setting reponsePossibleId = " + answer);
                } else if (answer instanceof String) {
                    // Réponse textuelle
                    reponseData.put("reponseText", answer);
                    System.out.println("DEBUG: Setting reponseText = " + answer);
                } else if (answer instanceof List) {
                    // Réponses multiples (liste d'IDs)
                    @SuppressWarnings("unchecked")
                    List<Integer> reponseIds = (List<Integer>) answer;
                    System.out.println("DEBUG: Processing multiple answers: " + reponseIds);
                    if (!reponseIds.isEmpty()) {
                        // Pour les questions multiples, on envoie la liste complète en une seule fois
                        reponseData.put("reponsePossibleIds", reponseIds);
                    }
                }
                
                try {
                    testService.enregistrerReponse(sessionId, questionId, reponseData);
                    System.out.println("DEBUG: Successfully recorded answer for question " + questionId);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'enregistrement de la réponse pour la question " + questionId + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            SessionTest session = testService.terminerTest(sessionId);
            SessionTestDTO sessionDTO = new SessionTestDTO(session);
            
            return Response.ok(Map.of(
                "message", "Test terminé avec succès",
                "session", sessionDTO,
                "score", Map.of(
                    "total", session.getScoreTotal(),
                    "max", session.getScoreMax(),
                    "pourcentage", session.getPourcentage()
                )
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/{sessionId}/reponses")
    public Response enregistrerReponse(@PathParam("sessionId") Integer sessionId, Map<String, Object> reponseData) {
        try {
            Integer questionId = (Integer) reponseData.get("questionId");
            if (questionId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "L'ID de la question est obligatoire"))
                    .build();
            }
            
            ReponseCandidat reponse = testService.enregistrerReponse(sessionId, questionId, reponseData);
            
            return Response.ok(Map.of(
                "message", "Réponse enregistrée avec succès",
                "reponse", reponse
            )).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/{sessionId}/terminer")
    public Response terminerTest(@PathParam("sessionId") Integer sessionId) {
        try {
            SessionTest session = testService.terminerTest(sessionId);
            SessionTestDTO sessionDTO = new SessionTestDTO(session);
            
            return Response.ok(Map.of(
                "message", "Test terminé avec succès",
                "session", sessionDTO,
                "score", Map.of(
                    "total", session.getScoreTotal(),
                    "max", session.getScoreMax(),
                    "pourcentage", session.getPourcentage()
                )
            )).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/{sessionId}")
    public Response getSession(@PathParam("sessionId") Integer sessionId) {
        Optional<SessionTest> sessionOpt = testService.getSessionById(sessionId);
        if (sessionOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Session de test non trouvée"))
                .build();
        }
        
        SessionTest session = sessionOpt.get();
            List<SessionQuestion> questions = testService.getQuestionsBySession(sessionId);
            SessionTestDTO sessionDTO = new SessionTestDTO(session);
        
        return Response.ok(Map.of(
            "session", sessionDTO,
            "questions", questions,
            "tempsRestant", testService.getTempsRestant(sessionId)
        )).build();
    }
    
    @GET
    @Path("/code/{codeSession}")
    public Response getSessionByCodeSession(@PathParam("codeSession") String codeSession) {
        Optional<SessionTest> sessionOpt = testService.getSessionByCodeSession(codeSession);
        if (sessionOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Aucune session active pour ce code"))
                .build();
        }
        
        SessionTest session = sessionOpt.get();
        List<SessionQuestion> questions = testService.getQuestionsBySession(session.getId());
        SessionTestDTO sessionDTO = new SessionTestDTO(session);
        
        return Response.ok(Map.of(
            "session", sessionDTO,
            "questions", questions,
            "tempsRestant", testService.getTempsRestant(session.getId())
        )).build();
    }
    
    @GET
    @Path("/{sessionId}/questions")
    public Response getQuestionsBySession(@PathParam("sessionId") Integer sessionId) {
        List<SessionQuestion> questions = testService.getQuestionsBySession(sessionId);
        return Response.ok(Map.of("questions", questions)).build();
    }
    
    @GET
    @Path("/{sessionId}/questions/{questionId}")
    public Response getQuestion(@PathParam("sessionId") Integer sessionId, @PathParam("questionId") Integer questionId) {
        try {
            if (!testService.peutNaviguerVersQuestion(sessionId, questionId)) {
                return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("error", "Accès non autorisé à cette question"))
                    .build();
            }
            
            List<SessionQuestion> questions = testService.getQuestionsBySession(sessionId);
            Optional<SessionQuestion> questionOpt = questions.stream()
                .filter(q -> q.getQuestion().getId().equals(questionId))
                .findFirst();
            
            if (questionOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Question non trouvée"))
                    .build();
            }
            
            SessionQuestion sessionQuestion = questionOpt.get();
            Optional<ReponseCandidat> reponseOpt = testService.getReponseBySessionQuestion(sessionQuestion.getId());
            
            return Response.ok(Map.of(
                "question", sessionQuestion,
                "reponse", reponseOpt.orElse(null)
            )).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/{sessionId}/questions/{questionId}/next")
    public Response getNextQuestion(@PathParam("sessionId") Integer sessionId, @PathParam("questionId") Integer questionId) {
        Optional<SessionQuestion> nextQuestionOpt = testService.getNextQuestion(sessionId, questionId);
        if (nextQuestionOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Aucune question suivante"))
                .build();
        }
        
        SessionQuestion nextQuestion = nextQuestionOpt.get();
        Optional<ReponseCandidat> reponseOpt = testService.getReponseBySessionQuestion(nextQuestion.getId());
        
        return Response.ok(Map.of(
            "question", nextQuestion,
            "reponse", reponseOpt.orElse(null)
        )).build();
    }
    
    @GET
    @Path("/{sessionId}/questions/{questionId}/previous")
    public Response getPreviousQuestion(@PathParam("sessionId") Integer sessionId, @PathParam("questionId") Integer questionId) {
        Optional<SessionQuestion> prevQuestionOpt = testService.getPreviousQuestion(sessionId, questionId);
        if (prevQuestionOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Aucune question précédente"))
                .build();
        }
        
        SessionQuestion prevQuestion = prevQuestionOpt.get();
        Optional<ReponseCandidat> reponseOpt = testService.getReponseBySessionQuestion(prevQuestion.getId());
        
        return Response.ok(Map.of(
            "question", prevQuestion,
            "reponse", reponseOpt.orElse(null)
        )).build();
    }
    
    @GET
    @Path("/{sessionId}/temps-restant")
    public Response getTempsRestant(@PathParam("sessionId") Integer sessionId) {
        long tempsRestant = testService.getTempsRestant(sessionId);
        return Response.ok(Map.of("tempsRestant", tempsRestant)).build();
    }
    
    @GET
    @Path("/candidat/{candidatId}")
    public Response getSessionsByCandidat(@PathParam("candidatId") Integer candidatId) {
        List<SessionTest> sessions = testService.getSessionsByCandidat(candidatId);
        List<SessionTestDTO> sessionDTOs = sessions.stream().map(SessionTestDTO::new).toList();
        return Response.ok(Map.of("sessions", sessionDTOs)).build();
    }
    
    @GET
    @Path("/recent/{limit}")
    public Response getRecentSessions(@PathParam("limit") Integer limit) {
        List<SessionTest> sessions = testService.getRecentSessions(limit);
        List<SessionTestDTO> sessionDTOs = sessions.stream().map(SessionTestDTO::new).toList();
        return Response.ok(Map.of("sessions", sessionDTOs)).build();
    }
    
    @GET
    @Path("/stats")
    public Response getStats() {
        List<SessionTest> recentSessions = testService.getRecentSessions(100);
        
        long totalSessions = recentSessions.size();
        long sessionsTerminees = recentSessions.stream().mapToLong(s -> s.getEstTermine() ? 1 : 0).sum();
        double scoreMoyen = recentSessions.stream()
            .filter(s -> s.getEstTermine())
            .mapToDouble(s -> s.getPourcentage().doubleValue())
            .average()
            .orElse(0.0);
        
        return Response.ok(Map.of(
            "totalSessions", totalSessions,
            "sessionsTerminees", sessionsTerminees,
            "scoreMoyen", Math.round(scoreMoyen * 100.0) / 100.0
        )).build();
    }
}
