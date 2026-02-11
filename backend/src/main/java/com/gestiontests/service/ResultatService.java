package com.gestiontests.service;

import com.gestiontests.entity.*;
import com.gestiontests.repository.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
//import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ResultatService {
    
    @Inject
    private SessionTestRepository sessionTestRepository;
    
    @Inject
    private ReponseCandidatRepository reponseCandidatRepository;
    
    @Inject
    private CandidatRepository candidatRepository;
    
    @Inject
    private QuestionRepository questionRepository;
    
    @Inject
    private ThemeRepository themeRepository;
    
    @Inject
    private ReponsePossibleRepository reponsePossibleRepository;
    
    public List<SessionTest> getResultatsByCandidat(Integer candidatId) {
        return sessionTestRepository.findByCandidat(candidatId);
    }
    
    public List<SessionTest> getResultatsTermineesByCandidat(Integer candidatId) {
        return sessionTestRepository.findByCandidat(candidatId).stream()
            .filter(SessionTest::getEstTermine)
            .collect(Collectors.toList());
    }
    
    public List<SessionTest> rechercherSessionsParCandidat(String terme) {
        List<Candidat> candidats = candidatRepository.findByNomOrPrenomOrEcole(terme);
        List<SessionTest> sessions = new ArrayList<>();
        
        for (Candidat candidat : candidats) {
            sessions.addAll(sessionTestRepository.findByCandidat(candidat.getId()));
        }
        
        return sessions.stream()
            .sorted((s1, s2) -> s2.getDateDebut().compareTo(s1.getDateDebut()))
            .collect(Collectors.toList());
    }
    
    public List<SessionTest> rechercheAvanceeSessions(String nom, String prenom, String ecole, String codeExam) {
        List<SessionTest> sessions = new ArrayList<>();
        
        if (codeExam != null && !codeExam.trim().isEmpty()) {
            Optional<SessionTest> sessionOpt = sessionTestRepository.findByCodeSession(codeExam);
            if (sessionOpt.isPresent()) {
                sessions.add(sessionOpt.get());
            }
        } else {
            List<Candidat> candidats = new ArrayList<>();
            
            if (nom != null && !nom.trim().isEmpty()) {
                candidats.addAll(candidatRepository.findByNomOrPrenomOrEcole(nom));
            }
            if (prenom != null && !prenom.trim().isEmpty()) {
                candidats.addAll(candidatRepository.findByNomOrPrenomOrEcole(prenom));
            }
            if (ecole != null && !ecole.trim().isEmpty()) {
                candidats.addAll(candidatRepository.findByEcole(ecole));
            }
            
            // Éviter les doublons
            Set<Integer> candidatIds = candidats.stream()
                .map(Candidat::getId)
                .collect(Collectors.toSet());
            
            for (Integer candidatId : candidatIds) {
                sessions.addAll(sessionTestRepository.findByCandidat(candidatId));
            }
        }
        
        return sessions.stream()
            .sorted((s1, s2) -> s2.getDateDebut().compareTo(s1.getDateDebut()))
            .distinct()
            .collect(Collectors.toList());
    }
    
    public List<Map<String, Object>> getStatsParEcole() {
        List<Candidat> candidats = candidatRepository.findAll();
        Map<String, List<Candidat>> candidatsParEcole = candidats.stream()
            .filter(c -> c.getEcole() != null && !c.getEcole().trim().isEmpty())
            .collect(Collectors.groupingBy(Candidat::getEcole));
        
        List<Map<String, Object>> stats = new ArrayList<>();
        
        for (Map.Entry<String, List<Candidat>> entry : candidatsParEcole.entrySet()) {
            String ecole = entry.getKey();
            List<Candidat> candidatsEcole = entry.getValue();
            
            List<SessionTest> sessionsEcole = new ArrayList<>();
            for (Candidat candidat : candidatsEcole) {
                sessionsEcole.addAll(sessionTestRepository.findByCandidat(candidat.getId()));
            }
            
            List<SessionTest> sessionsTerminees = sessionsEcole.stream()
                .filter(SessionTest::getEstTermine)
                .collect(Collectors.toList());
            
            double scoreMoyen = sessionsTerminees.stream()
                .mapToDouble(s -> s.getPourcentage().doubleValue())
                .average()
                .orElse(0.0);
            
            Map<String, Object> statEcole = new HashMap<>();
            statEcole.put("ecole", ecole);
            statEcole.put("nombreCandidats", candidatsEcole.size());
            statEcole.put("nombreSessions", sessionsEcole.size());
            statEcole.put("sessionsTerminees", sessionsTerminees.size());
            statEcole.put("scoreMoyen", Math.round(scoreMoyen * 100.0) / 100.0);
            
            stats.add(statEcole);
        }
        
        // Trier par score moyen décroissant
        stats.sort((s1, s2) -> Double.compare(
            (Double) s2.get("scoreMoyen"), 
            (Double) s1.get("scoreMoyen")
        ));
        
        return stats;
    }
    
    public List<Map<String, Object>> getStatsParDate(Integer jours) {
        LocalDate dateFin = LocalDate.now();
        LocalDate dateDebut = dateFin.minusDays(jours);
        
        List<SessionTest> sessions = sessionTestRepository.findByDateRange(
            dateDebut.atStartOfDay(), 
            dateFin.atTime(23, 59, 59)
        );
        
        Map<LocalDate, List<SessionTest>> sessionsParDate = sessions.stream()
            .filter(s -> s.getDateDebut() != null)
            .collect(Collectors.groupingBy(s -> s.getDateDebut().toLocalDate()));
        
        List<Map<String, Object>> stats = new ArrayList<>();
        
        for (LocalDate date = dateDebut; !date.isAfter(dateFin); date = date.plusDays(1)) {
            List<SessionTest> sessionsDate = sessionsParDate.getOrDefault(date, new ArrayList<>());
            List<SessionTest> sessionsTerminees = sessionsDate.stream()
                .filter(SessionTest::getEstTermine)
                .collect(Collectors.toList());
            
            double scoreMoyen = sessionsTerminees.stream()
                .mapToDouble(s -> s.getPourcentage().doubleValue())
                .average()
                .orElse(0.0);
            
            Map<String, Object> statDate = new HashMap<>();
            statDate.put("date", date.format(DateTimeFormatter.ISO_LOCAL_DATE));
            statDate.put("nombreSessions", sessionsDate.size());
            statDate.put("sessionsTerminees", sessionsTerminees.size());
            statDate.put("scoreMoyen", Math.round(scoreMoyen * 100.0) / 100.0);
            
            stats.add(statDate);
        }
        
        return stats;
    }
    
    public List<SessionTest> getTopScores(Integer limit) {
        return sessionTestRepository.findTopScorers(limit);
    }

    public List<Map<String, Object>> getClassementGlobal(Integer limit) {
        return getClassementGlobal(limit, null);
    }

    public List<Map<String, Object>> getClassementGlobal(Integer limit, String modeTest) {
        String filterMode = modeTest != null && !modeTest.trim().isEmpty() ? modeTest.trim().toUpperCase() : null;
        if (filterMode != null && !filterMode.equals("GENERAL") && !filterMode.equals("INFORMATIQUE")) {
            filterMode = null;
        }

        Set<String> csThemeNames = Set.of(
            "Cybersécurité",
            "Science des données",
            "Développement logiciel",
            "Réseaux et systèmes"
        );

        List<SessionTest> sessions = sessionTestRepository.findAllWithCandidat().stream()
            .filter(s -> Boolean.TRUE.equals(s.getEstTermine()))
            .collect(Collectors.toList());

        Map<String, SessionTest> bestSessionByKey = new HashMap<>();
        for (SessionTest s : sessions) {
            if (s.getCandidat() == null || s.getCandidat().getId() == null) {
                continue;
            }

            String detectedMode = detectModeForSession(s, csThemeNames);
            if (filterMode != null && !filterMode.equals(detectedMode)) {
                continue;
            }

            Integer candidatId = s.getCandidat().getId();
            String specialiteKey = null;
            if ("INFORMATIQUE".equals(detectedMode)) {
                specialiteKey = detectSpecialiteForSession(s, csThemeNames);
            }

            String key = candidatId + "|" + detectedMode + "|" + (specialiteKey != null ? specialiteKey : "");
            SessionTest currentBest = bestSessionByKey.get(key);
            if (currentBest == null) {
                bestSessionByKey.put(key, s);
                continue;
            }

            int scoreCmp = Integer.compare(
                s.getScoreTotal() != null ? s.getScoreTotal() : 0,
                currentBest.getScoreTotal() != null ? currentBest.getScoreTotal() : 0
            );
            if (scoreCmp > 0) {
                bestSessionByKey.put(key, s);
                continue;
            }
            if (scoreCmp < 0) {
                continue;
            }

            long durS = computeSessionDurationSeconds(s);
            long durBest = computeSessionDurationSeconds(currentBest);
            if (durS < durBest) {
                bestSessionByKey.put(key, s);
                continue;
            }
            if (durS > durBest) {
                continue;
            }

            // Stable fallback: most recent first
            if (s.getDateDebut() != null && currentBest.getDateDebut() != null) {
                if (s.getDateDebut().isAfter(currentBest.getDateDebut())) {
                    bestSessionByKey.put(key, s);
                }
            }
        }

        List<SessionTest> bestSessions = new ArrayList<>(bestSessionByKey.values());
        bestSessions.sort((a, b) -> {
            int scoreA = a.getScoreTotal() != null ? a.getScoreTotal() : 0;
            int scoreB = b.getScoreTotal() != null ? b.getScoreTotal() : 0;
            int cmpScore = Integer.compare(scoreB, scoreA);
            if (cmpScore != 0) {
                return cmpScore;
            }
            long durA = computeSessionDurationSeconds(a);
            long durB = computeSessionDurationSeconds(b);
            int cmpDur = Long.compare(durA, durB);
            if (cmpDur != 0) {
                return cmpDur;
            }
            LocalDateTime da = a.getDateDebut();
            LocalDateTime db = b.getDateDebut();
            if (da == null && db == null) {
                return 0;
            }
            if (da == null) {
                return 1;
            }
            if (db == null) {
                return -1;
            }
            return db.compareTo(da);
        });

        if (limit != null && limit > 0 && bestSessions.size() > limit) {
            bestSessions = bestSessions.subList(0, limit);
        }

        List<Map<String, Object>> classement = new ArrayList<>();
        int rank = 1;
        for (SessionTest s : bestSessions) {
            String detectedMode = detectModeForSession(s, csThemeNames);
            String specialite = "INFORMATIQUE".equals(detectedMode)
                ? detectSpecialiteForSession(s, csThemeNames)
                : null;

            Map<String, Object> row = new HashMap<>();
            row.put("rang", rank);
            row.put("candidatId", s.getCandidat().getId());
            row.put("nom", s.getCandidat().getNom());
            row.put("prenom", s.getCandidat().getPrenom());
            row.put("email", s.getCandidat().getEmail());
            row.put("ecole", s.getCandidat().getEcole());
            row.put("filiere", s.getCandidat().getFiliere());
            row.put("sessionId", s.getId());
            row.put("modeTest", detectedMode);
            if (specialite != null) {
                row.put("specialite", specialite);
            }
            row.put("scoreTotal", s.getScoreTotal() != null ? s.getScoreTotal() : 0);
            row.put("scoreMax", s.getScoreMax() != null ? s.getScoreMax() : 0);
            row.put("pourcentage", s.getPourcentage() != null ? s.getPourcentage() : 0);
            row.put("dureeSecondes", computeSessionDurationSeconds(s));
            if (s.getDateDebut() != null) {
                row.put("dateDebut", s.getDateDebut().toString());
            }
            if (s.getDateFin() != null) {
                row.put("dateFin", s.getDateFin().toString());
            }
            classement.add(row);
            rank++;
        }

        return classement;
    }

    private String detectModeForSession(SessionTest session, Set<String> csThemeNames) {
        if (session == null || session.getSessionQuestions() == null || session.getSessionQuestions().isEmpty()) {
            return "GENERAL";
        }

        boolean sawCs = false;
        for (SessionQuestion sq : session.getSessionQuestions()) {
            if (sq == null || sq.getQuestion() == null || sq.getQuestion().getTheme() == null) {
                continue;
            }
            String themeName = sq.getQuestion().getTheme().getNom();
            if (themeName == null) {
                continue;
            }
            if (csThemeNames.contains(themeName)) {
                sawCs = true;
            }
        }
        return sawCs ? "INFORMATIQUE" : "GENERAL";
    }

    private String detectSpecialiteForSession(SessionTest session, Set<String> csThemeNames) {
        if (session == null || session.getSessionQuestions() == null) {
            return null;
        }
        Map<String, Integer> counts = new HashMap<>();
        for (SessionQuestion sq : session.getSessionQuestions()) {
            if (sq == null || sq.getQuestion() == null || sq.getQuestion().getTheme() == null) {
                continue;
            }
            String themeName = sq.getQuestion().getTheme().getNom();
            if (themeName == null || !csThemeNames.contains(themeName)) {
                continue;
            }
            counts.put(themeName, counts.getOrDefault(themeName, 0) + 1);
        }
        String best = null;
        int bestCount = -1;
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            if (e.getValue() > bestCount) {
                best = e.getKey();
                bestCount = e.getValue();
            }
        }
        return best;
    }

    public Map<String, Object> getClassementForCandidat(Integer candidatId) {
        List<Map<String, Object>> classement = getClassementGlobal(null);
        List<Map<String, Object>> entries = new ArrayList<>();
        for (Map<String, Object> row : classement) {
            Object rowCid = row.get("candidatId");
            if (rowCid instanceof Integer && ((Integer) rowCid).equals(candidatId)) {
                entries.add(row);
            }
        }

        Map<String, Object> best = null;
        Integer bestRank = null;
        for (Map<String, Object> e : entries) {
            Object r = e.get("rang");
            Integer rank = r instanceof Integer ? (Integer) r : null;
            if (rank == null) {
                continue;
            }
            if (bestRank == null || rank < bestRank) {
                bestRank = rank;
                best = e;
            }
        }

        if (best == null) {
            return Map.of(
                "rang", null,
                "total", classement.size(),
                "entry", null,
                "entries", entries
            );
        }

        return Map.of(
            "rang", best.get("rang"),
            "total", classement.size(),
            "entry", best,
            "entries", entries
        );
    }

    @Transactional
    public List<Map<String, Object>> getQuestionsDetailsSession(Integer sessionId) {
        Optional<SessionTest> sessionOpt = sessionTestRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return List.of();
        }

        SessionTest session = sessionOpt.get();
        List<ReponseCandidat> reponses = reponseCandidatRepository.findBySession(sessionId);

        Map<Integer, List<ReponseCandidat>> reponsesByQuestionId = new HashMap<>();
        for (ReponseCandidat rc : reponses) {
            SessionQuestion sq = rc.getSessionQuestion();
            if (sq == null || sq.getQuestion() == null || sq.getQuestion().getId() == null) {
                continue;
            }
            Integer qid = sq.getQuestion().getId();
            reponsesByQuestionId.computeIfAbsent(qid, k -> new ArrayList<>()).add(rc);
        }

        List<Map<String, Object>> questionsDetails = new ArrayList<>();
        if (session.getSessionQuestions() == null) {
            return questionsDetails;
        }

        for (SessionQuestion sq : session.getSessionQuestions()) {
            if (sq == null || sq.getQuestion() == null || sq.getQuestion().getId() == null) {
                continue;
            }

            Question q = sq.getQuestion();
            Integer qid = q.getId();
            String typeNom = q.getTypeQuestion() != null ? q.getTypeQuestion().getNom() : null;

            List<ReponseCandidat> reps = reponsesByQuestionId.getOrDefault(qid, List.of());
            List<Integer> candidateAnswerIds = new ArrayList<>();
            List<String> candidateAnswers = new ArrayList<>();
            Integer tempsReponse = null;

            for (ReponseCandidat rc : reps) {
                if (tempsReponse == null && rc.getTempsReponse() != null) {
                    tempsReponse = rc.getTempsReponse();
                }
                if (rc.getReponsePossible() != null && rc.getReponsePossible().getId() != null) {
                    candidateAnswerIds.add(rc.getReponsePossible().getId());
                    if (rc.getReponsePossible().getLibelle() != null) {
                        candidateAnswers.add(rc.getReponsePossible().getLibelle());
                    }
                } else if (rc.getReponseText() != null) {
                    candidateAnswers.add(rc.getReponseText());
                }
            }

            List<ReponsePossible> correctRps = reponsePossibleRepository.findCorrectByQuestion(qid);
            List<Integer> correctAnswerIds = correctRps.stream()
                .filter(rp -> rp.getId() != null)
                .map(ReponsePossible::getId)
                .toList();
            List<String> correctAnswers = correctRps.stream()
                .map(ReponsePossible::getLibelle)
                .filter(Objects::nonNull)
                .toList();

            boolean estCorrect = false;
            if ("MULTIPLE".equalsIgnoreCase(typeNom)) {
                Set<Integer> candSet = new HashSet<>(candidateAnswerIds);
                Set<Integer> corrSet = new HashSet<>(correctAnswerIds);
                estCorrect = !corrSet.isEmpty() && candSet.equals(corrSet);
            } else if ("UNIQUE".equalsIgnoreCase(typeNom)) {
                estCorrect = reps.stream().anyMatch(r -> Boolean.TRUE.equals(r.getEstCorrect()));
            } else {
                // TEXTE or unknown: not auto-corrected
                estCorrect = false;
            }

            Map<String, Object> row = new HashMap<>();
            row.put("sessionQuestionId", sq.getId());
            row.put("questionId", qid);
            row.put("questionLibelle", q.getLibelle());
            row.put("typeQuestion", typeNom);
            if (q.getTheme() != null) {
                row.put("themeId", q.getTheme().getId());
                row.put("themeNom", q.getTheme().getNom());
            }
            row.put("candidateAnswerIds", candidateAnswerIds);
            row.put("candidateAnswers", candidateAnswers);
            row.put("correctAnswerIds", correctAnswerIds);
            row.put("correctAnswers", correctAnswers);
            row.put("tempsReponse", tempsReponse);
            row.put("estCorrect", estCorrect);
            questionsDetails.add(row);
        }

        questionsDetails.sort(Comparator.comparingInt(m -> {
            Object sid = m.get("sessionQuestionId");
            return sid instanceof Integer ? (Integer) sid : Integer.MAX_VALUE;
        }));

        return questionsDetails;
    }

    private long computeSessionDurationSeconds(SessionTest session) {
        if (session == null) {
            return 0;
        }
        if (session.getDateDebut() != null && session.getDateFin() != null) {
            return Math.max(0, Duration.between(session.getDateDebut(), session.getDateFin()).getSeconds());
        }
        // fallback: sum response times
        List<ReponseCandidat> reponses = reponseCandidatRepository.findBySession(session.getId());
        return reponses.stream()
            .filter(r -> r.getTempsReponse() != null)
            .mapToLong(ReponseCandidat::getTempsReponse)
            .sum();
    }
    
    public String exporterResultatsCSV(String dateDebutStr, String dateFinStr) throws Exception {
        LocalDateTime dateDebut = dateDebutStr != null ? 
            LocalDate.parse(dateDebutStr).atStartOfDay() : 
            LocalDateTime.now().minusDays(30);
        LocalDateTime dateFin = dateFinStr != null ? 
            LocalDate.parse(dateFinStr).atTime(23, 59, 59) : 
            LocalDateTime.now();
        
        List<SessionTest> sessions = sessionTestRepository.findByDateRange(dateDebut, dateFin);
        List<SessionTest> sessionsTerminees = sessions.stream()
            .filter(SessionTest::getEstTermine)
            .sorted((s1, s2) -> s2.getDateDebut().compareTo(s1.getDateDebut()))
            .collect(Collectors.toList());
        
        StringBuilder csv = new StringBuilder();
        csv.append("Nom,Prénom,École,Filière,Email,Date Test,Score,Score Max,Pourcentage,Code Session\n");
        
        for (SessionTest session : sessionsTerminees) {
            Candidat candidat = session.getCandidat();
            csv.append(candidat.getNom()).append(",");
            csv.append(candidat.getPrenom()).append(",");
            csv.append(candidat.getEcole()).append(",");
            csv.append(candidat.getFiliere() != null ? candidat.getFiliere() : "").append(",");
            csv.append(candidat.getEmail()).append(",");
            csv.append(session.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append(",");
            csv.append(session.getScoreTotal()).append(",");
            csv.append(session.getScoreMax()).append(",");
            csv.append(session.getPourcentage()).append(",");
            csv.append(session.getCodeSession()).append("\n");
        }
        
        return csv.toString();
    }
    
    @Transactional
    public Map<String, Object> getDetailsSession(Integer sessionId) {
        Optional<SessionTest> sessionOpt = sessionTestRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return null;
        }
        
        SessionTest session = sessionOpt.get();
        List<ReponseCandidat> reponses = reponseCandidatRepository.findBySession(sessionId);
        List<Map<String, Object>> questionsDetails = getQuestionsDetailsSession(sessionId);
        
        // Statistiques par thème
        Map<String, Object> statsParTheme = new HashMap<>();

        Set<Integer> themeIdsInSession = new HashSet<>();
        if (session.getSessionQuestions() != null) {
            for (SessionQuestion sq : session.getSessionQuestions()) {
                if (sq == null || sq.getQuestion() == null || sq.getQuestion().getTheme() == null) {
                    continue;
                }
                Integer tid = sq.getQuestion().getTheme().getId();
                if (tid != null) {
                    themeIdsInSession.add(tid);
                }
            }
        }

        for (Integer themeId : themeIdsInSession) {
            Optional<Theme> themeOpt = themeRepository.findById(themeId);
            if (themeOpt.isEmpty()) {
                continue;
            }

            Theme theme = themeOpt.get();

            List<Map<String, Object>> questionsTheme = questionsDetails.stream()
                .filter(m -> {
                    Object tid = m.get("themeId");
                    if (tid instanceof Integer) {
                        return themeId.equals(tid);
                    }
                    if (tid instanceof Number) {
                        return themeId.equals(((Number) tid).intValue());
                    }
                    return false;
                })
                .toList();

            if (questionsTheme.isEmpty()) {
                continue;
            }

            long totalQuestions = questionsTheme.size();
            long correctes = questionsTheme.stream()
                .filter(m -> Boolean.TRUE.equals(m.get("estCorrect")))
                .count();

            Map<String, Object> statTheme = new HashMap<>();
            statTheme.put("total", totalQuestions);
            statTheme.put("correctes", correctes);
            statTheme.put("pourcentage", totalQuestions > 0 ?
                Math.round((double) correctes / totalQuestions * 10000.0) / 100.0 : 0.0);

            statsParTheme.put(theme.getNom(), statTheme);
        }
        
        // Temps moyen par question
        double tempsMoyen = reponses.stream()
            .filter(r -> r.getTempsReponse() != null)
            .mapToInt(ReponseCandidat::getTempsReponse)
            .average()
            .orElse(0.0);
        
        // Questions sans réponse
        long questionsSansReponse = session.getSessionQuestions().stream()
            .mapToLong(sq -> reponses.stream()
                .anyMatch(r -> r.getSessionQuestion().getId().equals(sq.getId())) ? 0 : 1)
            .sum();
        
        Map<String, Object> details = new HashMap<>();
        details.put("statsParTheme", statsParTheme);
        details.put("tempsMoyen", Math.round(tempsMoyen * 100.0) / 100.0);
        details.put("questionsSansReponse", questionsSansReponse);
        details.put("totalQuestions", session.getSessionQuestions().size());
        details.put("reponsesDonnees", reponses.size());
        
        return details;
    }
    
    public Map<String, Object> getStatsGlobales() {
        List<SessionTest> sessions = sessionTestRepository.findRecentSessions(1000);
        
        long total = sessions.size();
        long terminees = sessions.stream().mapToLong(s -> s.getEstTermine() ? 1 : 0).sum();
        double scoreMoyen = sessions.stream()
            .filter(SessionTest::getEstTermine)
            .mapToDouble(s -> s.getPourcentage().doubleValue())
            .average()
            .orElse(0.0);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", total);
        stats.put("sessionsTerminees", terminees);
        stats.put("tauxCompletion", total > 0 ? (double) terminees / total * 100 : 0);
        stats.put("scoreMoyen", Math.round(scoreMoyen * 100.0) / 100.0);
        
        return stats;
    }
}
