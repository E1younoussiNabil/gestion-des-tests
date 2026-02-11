package com.gestiontests.rest;

import com.gestiontests.entity.SessionTest;
import com.gestiontests.entity.ReponseCandidat;
import com.gestiontests.entity.SessionQuestion;
import com.gestiontests.entity.Question;
import com.gestiontests.entity.Theme;
import com.gestiontests.entity.ReponsePossible;
import com.gestiontests.service.TestService;
import com.gestiontests.service.ResultatService;
import com.gestiontests.repository.ReponseCandidatRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import jakarta.transaction.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/resultats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ResultatResource {
    
    // DTO pour éviter les problèmes de lazy loading
    public static class SessionTestDTO {
        private Integer id;
        private String codeSession;
        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private Boolean estTermine;
        private Integer scoreTotal;
        private Integer scoreMax;
        private String pourcentage;
        
        public SessionTestDTO(SessionTest session) {
            this.id = session.getId();
            this.codeSession = session.getCodeSession();
            this.dateDebut = session.getDateDebut();
            this.dateFin = session.getDateFin();
            this.estTermine = session.getEstTermine();
            this.scoreTotal = session.getScoreTotal();
            this.scoreMax = session.getScoreMax();
            this.pourcentage = session.getPourcentage() != null ? session.getPourcentage().toString() : "0.00";
        }
        
        // Getters
        public Integer getId() { return id; }
        public String getCodeSession() { return codeSession; }
        public LocalDateTime getDateDebut() { return dateDebut; }
        public LocalDateTime getDateFin() { return dateFin; }
        public Boolean getEstTermine() { return estTermine; }
        public Integer getScoreTotal() { return scoreTotal; }
        public Integer getScoreMax() { return scoreMax; }
        public String getPourcentage() { return pourcentage; }
    }
    
    @Inject
    private ReponseCandidatRepository reponseCandidatRepository;
    
    @Inject
    private TestService testService;
    
    @Inject
    private ResultatService resultatService;
    
    @GET
    @Path("/session/{sessionId}")
    @Transactional
    public Response getResultatsBySession(@PathParam("sessionId") Integer sessionId) {
        Optional<SessionTest> sessionOpt = testService.getSessionById(sessionId);
        if (sessionOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Session de test non trouvée"))
                .build();
        }
        
        SessionTest session = sessionOpt.get();
        SessionTestDTO sessionDTO = new SessionTestDTO(session);
        List<ReponseCandidat> reponses = reponseCandidatRepository.findBySession(sessionId);

        List<Map<String, Object>> reponsesData = reponses.stream().map(rc -> {
            Map<String, Object> rcMap = new java.util.HashMap<>();
            rcMap.put("id", rc.getId());
            rcMap.put("estCorrect", rc.getEstCorrect());
            rcMap.put("tempsReponse", rc.getTempsReponse());
            if (rc.getDateReponse() != null) {
                rcMap.put("dateReponse", rc.getDateReponse().toString());
            }
            if (rc.getReponseText() != null) {
                rcMap.put("reponseText", rc.getReponseText());
            }

            SessionQuestion sq = rc.getSessionQuestion();
            if (sq != null) {
                rcMap.put("sessionQuestionId", sq.getId());
                Question q = sq.getQuestion();
                if (q != null) {
                    rcMap.put("questionId", q.getId());
                    rcMap.put("questionLibelle", q.getLibelle());
                    Theme t = q.getTheme();
                    if (t != null) {
                        rcMap.put("themeId", t.getId());
                        rcMap.put("themeNom", t.getNom());
                    }
                }
            }

            ReponsePossible rp = rc.getReponsePossible();
            if (rp != null) {
                rcMap.put("reponsePossibleId", rp.getId());
                rcMap.put("reponsePossibleLibelle", rp.getLibelle());
            }

            return rcMap;
        }).collect(Collectors.toList());

        Integer scoreTotal = session.getScoreTotal() != null ? session.getScoreTotal() : 0;
        Integer scoreMax = session.getScoreMax() != null ? session.getScoreMax() : 0;
        Object pourcentage = session.getPourcentage() != null ? session.getPourcentage() : 0;
        
        return Response.ok(Map.of(
            "session", sessionDTO,
            "reponses", reponsesData,
            "score", Map.of(
                "total", scoreTotal,
                "max", scoreMax,
                "pourcentage", pourcentage
            )
        )).build();
    }
    
    @GET
    @Path("/candidat/{candidatId}")
    public Response getResultatsByCandidat(@PathParam("candidatId") Integer candidatId) {
        List<SessionTest> sessions = testService.getSessionsByCandidat(candidatId);
        List<SessionTestDTO> sessionDTOs = sessions.stream()
            .map(SessionTestDTO::new)
            .collect(Collectors.toList());
        
        return Response.ok(Map.of(
            "candidatId", candidatId,
            "sessions", sessionDTOs,
            "totalSessions", sessionDTOs.size(),
            "sessionsTerminees", sessionDTOs.stream().mapToLong(s -> s.getEstTermine() ? 1 : 0).sum()
        )).build();
    }
    
    @GET
    @Path("/candidat/{candidatId}/terminees")
    public Response getResultatsTermineesByCandidat(@PathParam("candidatId") Integer candidatId) {
        List<SessionTest> sessions = testService.getSessionsByCandidat(candidatId);
        List<SessionTestDTO> sessionsTerminees = sessions.stream()
            .filter(SessionTest::getEstTermine)
            .map(SessionTestDTO::new)
            .toList();
        
        return Response.ok(Map.of(
            "candidatId", candidatId,
            "sessions", sessionsTerminees,
            "total", sessionsTerminees.size()
        )).build();
    }
    
    @GET
    @Path("/recherche")
    public Response rechercherResultats(@QueryParam("terme") String terme) {
        if (terme == null || terme.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Le terme de recherche est obligatoire"))
                .build();
        }
        
        List<SessionTest> sessions = resultatService.rechercherSessionsParCandidat(terme);
        List<SessionTestDTO> sessionDTOs = sessions.stream().map(SessionTestDTO::new).toList();
        
        return Response.ok(Map.of(
            "terme", terme,
            "sessions", sessionDTOs,
            "total", sessionDTOs.size()
        )).build();
    }
    
    @GET
    @Path("/recherche/avancee")
    public Response rechercheAvancee(@QueryParam("nom") String nom,
                                   @QueryParam("prenom") String prenom,
                                   @QueryParam("ecole") String ecole,
                                   @QueryParam("codeExam") String codeExam) {
        List<SessionTest> sessions = resultatService.rechercheAvanceeSessions(nom, prenom, ecole, codeExam);
        List<SessionTestDTO> sessionDTOs = sessions.stream().map(SessionTestDTO::new).toList();
        
        return Response.ok(Map.of(
            "critères", Map.of(
                "nom", nom,
                "prenom", prenom,
                "ecole", ecole,
                "codeExam", codeExam
            ),
            "sessions", sessionDTOs,
            "total", sessionDTOs.size()
        )).build();
    }
    
    @GET
    @Path("/stats/globales")
    public Response getStatsGlobales() {
        List<SessionTest> recentSessions = testService.getRecentSessions(1000);
        
        long totalSessions = recentSessions.size();
        long sessionsTerminees = recentSessions.stream().mapToLong(s -> s.getEstTermine() ? 1 : 0).sum();
        double scoreMoyen = recentSessions.stream()
            .filter(SessionTest::getEstTermine)
            .mapToDouble(s -> s.getPourcentage().doubleValue())
            .average()
            .orElse(0.0);
        
        double scoreMax = recentSessions.stream()
            .filter(SessionTest::getEstTermine)
            .mapToDouble(s -> s.getPourcentage().doubleValue())
            .max()
            .orElse(0.0);
        
        double scoreMin = recentSessions.stream()
            .filter(SessionTest::getEstTermine)
            .mapToDouble(s -> s.getPourcentage().doubleValue())
            .min()
            .orElse(0.0);
        
        return Response.ok(Map.of(
            "totalSessions", totalSessions,
            "sessionsTerminees", sessionsTerminees,
            "tauxCompletion", totalSessions > 0 ? (double) sessionsTerminees / totalSessions * 100 : 0,
            "scoreMoyen", Math.round(scoreMoyen * 100.0) / 100.0,
            "scoreMax", Math.round(scoreMax * 100.0) / 100.0,
            "scoreMin", Math.round(scoreMin * 100.0) / 100.0
        )).build();
    }
    
    @GET
    @Path("/stats/par-ecole")
    public Response getStatsParEcole() {
        List<Map<String, Object>> statsEcole = resultatService.getStatsParEcole();
        
        return Response.ok(Map.of("statsEcole", statsEcole)).build();
    }
    
    @GET
    @Path("/stats/par-date")
    public Response getStatsParDate(@QueryParam("jours") Integer jours) {
        if (jours == null) {
            jours = 30; // Par défaut 30 jours
        }
        
        List<Map<String, Object>> statsDate = resultatService.getStatsParDate(jours);
        
        return Response.ok(Map.of(
            "periode", jours + " jours",
            "statsDate", statsDate
        )).build();
    }
    
    @GET
    @Path("/top-scores/{limit}")
    public Response getTopScores(@PathParam("limit") Integer limit) {
        List<SessionTest> topScores = resultatService.getTopScores(limit);
        
        return Response.ok(Map.of(
            "topScores", topScores,
            "limit", limit
        )).build();
    }

    @GET
    @Path("/classement")
    public Response getClassement(@QueryParam("limit") Integer limit, @QueryParam("modeTest") String modeTest) {
        List<Map<String, Object>> classement = resultatService.getClassementGlobal(limit, modeTest);
        Map<String, Object> out = new java.util.HashMap<>();
        out.put("classement", classement);
        out.put("total", classement.size());
        if (limit != null) {
            out.put("limit", limit);
        }
        if (modeTest != null && !modeTest.trim().isEmpty()) {
            out.put("modeTest", modeTest);
        }
        return Response.ok(out).build();
    }

    @GET
    @Path("/classement/candidat/{candidatId}")
    public Response getClassementCandidat(@PathParam("candidatId") Integer candidatId) {
        Map<String, Object> data = resultatService.getClassementForCandidat(candidatId);
        return Response.ok(data).build();
    }
    
    @GET
    @Path("/recent/{limit}")
    public Response getResultatsRecents(@PathParam("limit") Integer limit) {
        List<SessionTest> sessions = testService.getRecentSessions(limit);
        
        return Response.ok(Map.of(
            "sessions", sessions,
            "limit", limit
        )).build();
    }
    
    @GET
    @Path("/export/csv")
    @Produces("text/csv")
    public Response exportResultatsCSV(@QueryParam("dateDebut") String dateDebut,
                                      @QueryParam("dateFin") String dateFin) {
        try {
            String csv = resultatService.exporterResultatsCSV(dateDebut, dateFin);
            
            return Response.ok(csv)
                .header("Content-Disposition", "attachment; filename=\"resultats.csv\"")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/session/{sessionId}/details")
    @Transactional
    public Response getDetailsSession(@PathParam("sessionId") Integer sessionId) {
        Optional<SessionTest> sessionOpt = testService.getSessionById(sessionId);
        if (sessionOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Session de test non trouvée"))
                .build();
        }
        
        SessionTest session = sessionOpt.get();
        SessionTestDTO sessionDTO = new SessionTestDTO(session);
        Map<String, Object> details = resultatService.getDetailsSession(sessionId);
        List<Map<String, Object>> questionsDetails = resultatService.getQuestionsDetailsSession(sessionId);
        
        return Response.ok(Map.of(
            "session", sessionDTO,
            "details", details,
            "questionsDetails", questionsDetails
        )).build();
    }

    @GET
    @Path("/session/{sessionId}/rapport")
    @Produces("application/pdf")
    @Transactional
    public Response downloadSessionReport(@PathParam("sessionId") Integer sessionId) {
        try {
            Optional<SessionTest> sessionOpt = testService.getSessionById(sessionId);
            if (sessionOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", "Session de test non trouvée"))
                    .build();
            }

            SessionTest session = sessionOpt.get();
            Map<String, Object> details = resultatService.getDetailsSession(sessionId);

            byte[] pdfBytes = generateSessionPdf(session, details);
            String filename = "rapport_session_" + sessionId + ".pdf";
            return Response.ok(pdfBytes)
                .type("application/pdf")
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    private byte[] generateSessionPdf(SessionTest session, Map<String, Object> details) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;
                float leading = 16;

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 18);
                content.newLineAtOffset(margin, y);
                content.showText("Rapport de test");
                content.endText();
                y -= (leading * 2);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String dateDebut = session.getDateDebut() != null ? session.getDateDebut().format(dtf) : "";
                String dateFin = session.getDateFin() != null ? session.getDateFin().format(dtf) : "";

                String nomPrenom = "";
                String email = "";
                String ecole = "";
                String filiere = "";
                if (session.getCandidat() != null) {
                    nomPrenom = (session.getCandidat().getPrenom() != null ? session.getCandidat().getPrenom() : "") +
                        " " + (session.getCandidat().getNom() != null ? session.getCandidat().getNom() : "");
                    email = session.getCandidat().getEmail() != null ? session.getCandidat().getEmail() : "";
                    ecole = session.getCandidat().getEcole() != null ? session.getCandidat().getEcole() : "";
                    filiere = session.getCandidat().getFiliere() != null ? session.getCandidat().getFiliere() : "";
                }

                y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA_BOLD, 12, "Candidat: " + nomPrenom);
                y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA, 12, "Email: " + email);
                y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA, 12, "École: " + ecole);
                y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA, 12, "Filière: " + filiere);
                y -= 8;

                String scoreTotal = session.getScoreTotal() != null ? session.getScoreTotal().toString() : "0";
                String scoreMax = session.getScoreMax() != null ? session.getScoreMax().toString() : "0";
                String pourcentage = session.getPourcentage() != null ? session.getPourcentage().toString() : "0.00";

                y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA_BOLD, 12, "Résumé de session");
                y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA, 12, "Code session: " + (session.getCodeSession() != null ? session.getCodeSession() : ""));
                y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA, 12, "Début: " + dateDebut);
                y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA, 12, "Fin: " + dateFin);
                y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA, 12, "Score: " + scoreTotal + "/" + scoreMax + " (" + pourcentage + "%)");
                y -= 8;

                y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA_BOLD, 12, "Performance par thème");

                if (details != null && details.get("statsParTheme") instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> statsParTheme = (Map<String, Object>) details.get("statsParTheme");
                    for (Map.Entry<String, Object> entry : statsParTheme.entrySet()) {
                        String themeName = entry.getKey();
                        if (entry.getValue() instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> stat = (Map<String, Object>) entry.getValue();
                            Object total = stat.get("total");
                            Object correctes = stat.get("correctes");
                            Object pct = stat.get("pourcentage");
                            String line = themeName + ": " + (correctes != null ? correctes : 0) + "/" + (total != null ? total : 0) + " (" + (pct != null ? pct : 0) + "%)";
                            y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA, 12, line);
                        }
                    }
                } else {
                    y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA, 12, "Aucune statistique disponible.");
                }

                y -= 8;
                if (details != null) {
                    Object tempsMoyen = details.get("tempsMoyen");
                    Object reponsesDonnees = details.get("reponsesDonnees");
                    Object totalQuestions = details.get("totalQuestions");
                    y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA_BOLD, 12, "Autres informations");
                    y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA, 12, "Temps moyen (sec): " + (tempsMoyen != null ? tempsMoyen : "N/A"));
                    y = writeLine(content, margin, y, leading, PDType1Font.HELVETICA, 12, "Réponses données: " + (reponsesDonnees != null ? reponsesDonnees : "N/A") + "/" + (totalQuestions != null ? totalQuestions : "N/A"));
                }
            }

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                document.save(out);
                return out.toByteArray();
            }
        }
    }

    private float writeLine(PDPageContentStream content, float x, float y, float leading,
                            org.apache.pdfbox.pdmodel.font.PDFont font, int fontSize, String text) throws Exception {
        if (y < 60) {
            // Simple overflow protection: stop writing when reaching bottom.
            return y;
        }
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text != null ? text : "");
        content.endText();
        return y - leading;
    }
    
    @GET
    @Path("/candidat/{candidatId}/progression")
    public Response getProgressionCandidat(@PathParam("candidatId") Integer candidatId) {
        List<SessionTest> sessions = testService.getSessionsByCandidat(candidatId);
        List<SessionTest> sessionsTerminees = sessions.stream()
            .filter(SessionTest::getEstTermine)
            .sorted((s1, s2) -> s1.getDateDebut().compareTo(s2.getDateDebut()))
            .toList();
        
        return Response.ok(Map.of(
            "candidatId", candidatId,
            "progression", sessionsTerminees,
            "total", sessionsTerminees.size(),
            "amelioration", calculerAmelioration(sessionsTerminees)
        )).build();
    }
    
    private Map<String, Object> calculerAmelioration(List<SessionTest> sessions) {
        if (sessions.size() < 2) {
            return Map.of("amelioration", 0.0, "tendance", "insuffisant_de_donnees");
        }
        
        double premierScore = sessions.get(0).getPourcentage().doubleValue();
        double dernierScore = sessions.get(sessions.size() - 1).getPourcentage().doubleValue();
        double amelioration = dernierScore - premierScore;
        
        String tendance;
        if (amelioration > 5) {
            tendance = "amelioration";
        } else if (amelioration < -5) {
            tendance = "regression";
        } else {
            tendance = "stable";
        }
        
        return Map.of(
            "amelioration", Math.round(amelioration * 100.0) / 100.0,
            "tendance", tendance,
            "premierScore", premierScore,
            "dernierScore", dernierScore
        );
    }
}
