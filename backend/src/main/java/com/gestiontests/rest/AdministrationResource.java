package com.gestiontests.rest;

import com.gestiontests.entity.*;
import com.gestiontests.service.*;
import com.gestiontests.repository.*;
import com.gestiontests.security.JwtUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AdministrationResource {
    
    @Inject
    private QuestionService questionService;
    
    @Inject
    private ParametreService parametreService;
    
    @Inject
    private CandidatService candidatService;
    
    @Inject
    private CreneauHoraireService creneauHoraireService;
    
    @Inject
    private EmailService emailService;
    
    @Inject
    private ResultatService resultatService;
    
    @Inject
    private AdministrateurRepository administrateurRepository;
    
    @Inject
    private TestService testService;

    @Inject
    private InscriptionRepository inscriptionRepository;

    @Inject
    private ThemeRepository themeRepository;
    
    // Login administrateur
    @POST
    @Path("/login")
    public Response loginAdmin(Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            System.out.println("=== loginAdmin called with username: " + username);
            
            if (username == null || password == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Nom d'utilisateur et mot de passe requis"))
                    .build();
            }
            
            Optional<Administrateur> adminOpt = administrateurRepository.findByUsername(username);
            if (adminOpt.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Nom d'utilisateur ou mot de passe incorrect"))
                    .build();
            }
            
            Administrateur admin = adminOpt.get();
            boolean motDePasseOk;
            String motDePasseStocke = admin.getPassword();
            System.out.println("=== Stored password prefix: " + (motDePasseStocke != null ? motDePasseStocke.substring(0, Math.min(10, motDePasseStocke.length())) : "null"));
            if (motDePasseStocke != null && (motDePasseStocke.startsWith("$2a$") || motDePasseStocke.startsWith("$2b$") || motDePasseStocke.startsWith("$2y$"))) {
                motDePasseOk = BCrypt.checkpw(password, motDePasseStocke);
                System.out.println("=== BCrypt check result: " + motDePasseOk);
            } else {
                motDePasseOk = password.equals(motDePasseStocke);
                System.out.println("=== Plaintext check result: " + motDePasseOk);
            }

            if (!motDePasseOk) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Nom d'utilisateur ou mot de passe incorrect"))
                    .build();
            }
            
            // Ne pas renvoyer le mot de passe
            admin.setPassword(null);

            String token = JwtUtil.generateAdminToken(admin);
            
            return Response.ok(Map.of(
                "message", "Connexion réussie",
                "admin", admin,
                "token", token
            )).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Erreur lors de la connexion"))
                .build();
        }
    }
    
    // Gestion des questions
    @POST
    @Path("/questions")
    public Response createQuestion(Map<String, Object> questionData) {
        try {
            Question question = questionService.createQuestion(questionData);
            return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                    "message", "Question créée avec succès",
                    "question", question
                ))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @PUT
    @Path("/questions/{id}")
    public Response updateQuestion(@PathParam("id") Integer id, Map<String, Object> questionData) {
        try {
            Question question = questionService.updateQuestion(id, questionData);
            return Response.ok(Map.of(
                "message", "Question mise à jour avec succès",
                "question", question
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @DELETE
    @Path("/questions/{id}")
    public Response deleteQuestion(@PathParam("id") Integer id) {
        try {
            questionService.deleteQuestion(id);
            return Response.ok(Map.of("message", "Question supprimée avec succès")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/questions")
    public Response getAllQuestions() {
        try {
            List<Question> questions = questionService.findAllWithDetails();
            
            // Créer une liste de questions simplifiées pour éviter les problèmes de sérialisation
            List<Map<String, Object>> questionsData = questions.stream().map(q -> {
                Map<String, Object> qMap = new java.util.HashMap<>();
                qMap.put("id", q.getId());
                qMap.put("libelle", q.getLibelle());
                qMap.put("explication", q.getExplication());
                // Formater la date pour éviter les problèmes de sérialisation
                if (q.getCreatedAt() != null) {
                    qMap.put("createdAt", q.getCreatedAt().toString());
                }
                
                // Ajouter les informations de thème
                if (q.getTheme() != null) {
                    Map<String, Object> themeMap = new java.util.HashMap<>();
                    themeMap.put("id", q.getTheme().getId());
                    themeMap.put("nom", q.getTheme().getNom());
                    qMap.put("theme", themeMap);
                }
                
                // Ajouter les informations de type
                if (q.getTypeQuestion() != null) {
                    Map<String, Object> typeMap = new java.util.HashMap<>();
                    typeMap.put("id", q.getTypeQuestion().getId());
                    typeMap.put("nom", q.getTypeQuestion().getNom());
                    qMap.put("typeQuestion", typeMap);
                }
                
                // Ajouter les réponses
                if (q.getReponsesPossibles() != null) {
                    List<Map<String, Object>> reponsesData = q.getReponsesPossibles().stream().map(r -> {
                        Map<String, Object> rMap = new java.util.HashMap<>();
                        rMap.put("id", r.getId());
                        rMap.put("libelle", r.getLibelle());
                        rMap.put("estCorrect", r.getEstCorrect());
                        return rMap;
                    }).collect(java.util.stream.Collectors.toList());
                    qMap.put("reponsesPossibles", reponsesData);
                }
                
                return qMap;
            }).collect(java.util.stream.Collectors.toList());
            
            return Response.ok(Map.of("questions", questionsData)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Erreur lors de la récupération des questions: " + e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/questions/theme/{themeId}")
    public Response getQuestionsByTheme(@PathParam("themeId") Integer themeId) {
        List<Question> questions = questionService.findByTheme(themeId);
        return Response.ok(Map.of("questions", questions)).build();
    }
    
    // Gestion des thèmes
    @GET
    @Path("/themes")
    public Response getAllThemes() {
        List<Theme> themes = questionService.findAllThemes();
        return Response.ok(Map.of("themes", themes)).build();
    }
    
    // Gestion des types de questions
    @GET
    @Path("/types-questions")
    public Response getAllTypesQuestions() {
        List<TypeQuestion> types = questionService.findAllTypesQuestions();
        return Response.ok(Map.of("types", types)).build();
    }

    @GET
    @Path("/dashboard/stats")
    public Response getDashboardStats() {
        try {
            List<SessionTest> sessions = testService.getAllSessions();

            long completedTests = sessions.stream().filter(s -> s.getEstTermine() != null && s.getEstTermine()).count();
            long activeTests = sessions.stream().filter(s -> s.getEstTermine() == null || !s.getEstTermine()).count();

            double averageScore = sessions.stream()
                .filter(s -> s.getEstTermine() != null && s.getEstTermine())
                .filter(s -> s.getPourcentage() != null)
                .mapToDouble(s -> s.getPourcentage().doubleValue())
                .average()
                .orElse(0.0);

            long todaySessions = sessions.stream()
                .filter(s -> s.getDateDebut() != null && s.getDateDebut().toLocalDate().equals(LocalDate.now()))
                .count();

            long totalCandidates = candidatService.count();
            long pendingValidations = candidatService.findByEstValide(false).size();

            return Response.ok(Map.of(
                "totalCandidates", totalCandidates,
                "activeTests", activeTests,
                "completedTests", completedTests,
                "averageScore", Math.round(averageScore * 10.0) / 10.0,
                "pendingValidations", pendingValidations,
                "todaySessions", todaySessions
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    // Gestion des paramètres
    @GET
    @Path("/parametres")
    public Response getAllParametres() {
        List<Parametre> parametres = parametreService.findAll();
        return Response.ok(Map.of("parametres", parametres)).build();
    }
    
    @PUT
    @Path("/parametres/{nom}")
    public Response updateParametre(@PathParam("nom") String nom, Map<String, String> data) {
        try {
            String valeur = data.get("valeur");
            Parametre parametre = parametreService.updateParametre(nom, valeur);
            return Response.ok(Map.of(
                "message", "Paramètre mis à jour avec succès",
                "parametre", parametre
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    // Validation des candidats
    @GET
    @Path("/candidats")
    public Response getAllCandidats() {
        try {
            List<Candidat> candidats = candidatService.findAll();
            
            // Créer une liste de candidats simplifiés pour éviter les problèmes de sérialisation
            List<Map<String, Object>> candidatsData = candidats.stream().map(c -> {
                Map<String, Object> cMap = new java.util.HashMap<>();
                cMap.put("id", c.getId());
                cMap.put("nom", c.getNom());
                cMap.put("prenom", c.getPrenom());
                cMap.put("email", c.getEmail());
                cMap.put("gsm", c.getGsm());
                cMap.put("ecole", c.getEcole());
                cMap.put("filiere", c.getFiliere());
                cMap.put("estValide", c.getEstValide());
                cMap.put("codeSession", c.getCodeSession());
                if (c.getCreatedAt() != null) {
                    cMap.put("createdAt", c.getCreatedAt().toString());
                }
                return cMap;
            }).collect(java.util.stream.Collectors.toList());
            
            return Response.ok(Map.of("candidats", candidatsData)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Erreur lors de la récupération des candidats"))
                .build();
        }
    }

    @GET
    @Path("/candidats/export/csv")
    @Produces("text/csv")
    public Response exportCandidatsCSV(@QueryParam("search") String search,
                                      @QueryParam("status") String status) {
        try {
            final String term = search != null ? search.trim().toLowerCase() : "";
            final String st = status != null ? status.trim().toLowerCase() : "";

            List<Candidat> candidats = candidatService.findAll();

            List<Candidat> filtered = candidats.stream().filter(c -> {
                boolean matchesSearch = true;
                if (!term.isEmpty()) {
                    String nom = c.getNom() != null ? c.getNom().toLowerCase() : "";
                    String prenom = c.getPrenom() != null ? c.getPrenom().toLowerCase() : "";
                    String email = c.getEmail() != null ? c.getEmail().toLowerCase() : "";
                    String ecole = c.getEcole() != null ? c.getEcole().toLowerCase() : "";
                    matchesSearch = nom.contains(term) || prenom.contains(term) || email.contains(term) || ecole.contains(term);
                }

                boolean matchesStatus = true;
                if ("validated".equals(st)) {
                    matchesStatus = Boolean.TRUE.equals(c.getEstValide());
                } else if ("pending".equals(st)) {
                    matchesStatus = !Boolean.TRUE.equals(c.getEstValide());
                }

                return matchesSearch && matchesStatus;
            }).toList();

            StringBuilder csv = new StringBuilder();
            csv.append("Id,Nom,Prénom,Email,GSM,École,Filière,Statut,Code Session,Date inscription\n");
            for (Candidat c : filtered) {
                String statut = Boolean.TRUE.equals(c.getEstValide()) ? "VALIDÉ" : "EN_ATTENTE";
                String createdAt = c.getCreatedAt() != null ? c.getCreatedAt().toString() : "";
                csv.append(csvEscape(String.valueOf(c.getId()))).append(',')
                   .append(csvEscape(c.getNom())).append(',')
                   .append(csvEscape(c.getPrenom())).append(',')
                   .append(csvEscape(c.getEmail())).append(',')
                   .append(csvEscape(c.getGsm())).append(',')
                   .append(csvEscape(c.getEcole())).append(',')
                   .append(csvEscape(c.getFiliere())).append(',')
                   .append(csvEscape(statut)).append(',')
                   .append(csvEscape(c.getCodeSession())).append(',')
                   .append(csvEscape(createdAt))
                   .append('\n');
            }

            String filename = "candidats_" + java.time.LocalDate.now() + ".csv";
            return Response.ok(csv.toString())
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("error," + csvEscape(e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/candidats/{id}/details")
    public Response getCandidatDetails(@PathParam("id") Integer id) {
        try {
            Optional<Candidat> candidatOpt = candidatService.findById(id);
            if (candidatOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Candidat non trouvé"))
                    .build();
            }

            Candidat c = candidatOpt.get();

            Map<String, Object> cMap = new java.util.HashMap<>();
            cMap.put("id", c.getId());
            cMap.put("nom", c.getNom());
            cMap.put("prenom", c.getPrenom());
            cMap.put("email", c.getEmail());
            cMap.put("gsm", c.getGsm());
            cMap.put("ecole", c.getEcole());
            cMap.put("filiere", c.getFiliere());
            cMap.put("estValide", c.getEstValide());
            cMap.put("codeSession", c.getCodeSession());
            if (c.getCreatedAt() != null) {
                cMap.put("createdAt", c.getCreatedAt().toString());
            }

            Map<String, Object> testChoice = new java.util.HashMap<>();
            inscriptionRepository.findLatestByCandidat(c.getId()).ifPresent(ins -> {
                testChoice.put("modeTest", ins.getModeTest());
                testChoice.put("idThemeSpecialite", ins.getIdThemeSpecialite());
                if (ins.getIdThemeSpecialite() != null) {
                    themeRepository.findById(ins.getIdThemeSpecialite()).ifPresent(t -> testChoice.put("themeSpecialiteNom", t.getNom()));
                }
            });
            cMap.put("testChoice", testChoice);

            return Response.ok(cMap).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/candidats/en-attente")
    public Response getCandidatsEnAttente() {
        List<Candidat> candidats = candidatService.findByEstValide(false);
        return Response.ok(Map.of("candidats", candidats)).build();
    }
    
    @POST
    @Path("/candidats/{id}/valider")
    public Response validerCandidat(@PathParam("id") Integer id) {
        try {
            Candidat candidat = candidatService.validerInscription(id);
            
            // Créer un objet simplifié pour éviter les problèmes de sérialisation
            Map<String, Object> candidatMap = new java.util.HashMap<>();
            candidatMap.put("id", candidat.getId());
            candidatMap.put("nom", candidat.getNom());
            candidatMap.put("prenom", candidat.getPrenom());
            candidatMap.put("email", candidat.getEmail());
            candidatMap.put("gsm", candidat.getGsm());
            candidatMap.put("ecole", candidat.getEcole());
            candidatMap.put("filiere", candidat.getFiliere());
            candidatMap.put("estValide", candidat.getEstValide());
            candidatMap.put("codeSession", candidat.getCodeSession());
            if (candidat.getCreatedAt() != null) {
                candidatMap.put("createdAt", candidat.getCreatedAt().toString());
            }
            
            return Response.ok(Map.of(
                "message", "Candidat validé avec succès",
                "candidat", candidatMap
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/candidats/{id}/rejeter")
    public Response rejeterCandidat(@PathParam("id") Integer id) {
        try {
            candidatService.deleteCandidat(id);
            return Response.ok(Map.of("message", "Candidat rejeté avec succès")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/candidats/{id}/envoyer-code")
    public Response envoyerCodeCandidat(@PathParam("id") Integer id) {
        try {
            Optional<Candidat> candidatOpt = candidatService.findById(id);
            if (candidatOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Candidat non trouvé"))
                    .build();
            }
            
            Candidat candidat = candidatOpt.get();
            if (!candidat.getEstValide() || candidat.getCodeSession() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Le candidat n'est pas encore validé"))
                    .build();
            }
            
            emailService.envoyerEmailValidation(candidat);
            return Response.ok(Map.of("message", "Code de session envoyé par email")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    // Gestion des créneaux
    @POST
    @Path("/creneaux")
    public Response createCreneau(CreneauHoraire creneau) {
        try {
            CreneauHoraire created = creneauHoraireService.createCreneau(creneau);
            
            // Créer un objet simplifié pour éviter les problèmes de sérialisation
            Map<String, Object> creneauMap = new java.util.HashMap<>();
            creneauMap.put("id", created.getId());
            if (created.getDateExam() != null) {
                creneauMap.put("dateExam", created.getDateExam().toString());
            }
            if (created.getHeureDebut() != null) {
                creneauMap.put("heureDebut", created.getHeureDebut().toString());
            }
            if (created.getHeureFin() != null) {
                creneauMap.put("heureFin", created.getHeureFin().toString());
            }
            creneauMap.put("dureeMinutes", created.getDureeMinutes());
            creneauMap.put("nombreQuestions", created.getNombreQuestions());
            creneauMap.put("placesDisponibles", created.getPlacesDisponibles());
            creneauMap.put("estComplet", created.getEstComplet());
            if (created.getCreatedAt() != null) {
                creneauMap.put("createdAt", created.getCreatedAt().toString());
            }
            
            return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                    "message", "Créneau créé avec succès",
                    "creneau", creneauMap
                ))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @PUT
    @Path("/creneaux/{id}")
    public Response updateCreneau(@PathParam("id") Integer id, CreneauHoraire creneau) {
        try {
            creneau.setId(id);
            CreneauHoraire updated = creneauHoraireService.updateCreneau(creneau);
            
            // Créer un objet simplifié pour éviter les problèmes de sérialisation
            Map<String, Object> creneauMap = new java.util.HashMap<>();
            creneauMap.put("id", updated.getId());
            if (updated.getDateExam() != null) {
                creneauMap.put("dateExam", updated.getDateExam().toString());
            }
            if (updated.getHeureDebut() != null) {
                creneauMap.put("heureDebut", updated.getHeureDebut().toString());
            }
            if (updated.getHeureFin() != null) {
                creneauMap.put("heureFin", updated.getHeureFin().toString());
            }
            creneauMap.put("dureeMinutes", updated.getDureeMinutes());
            creneauMap.put("nombreQuestions", updated.getNombreQuestions());
            creneauMap.put("placesDisponibles", updated.getPlacesDisponibles());
            creneauMap.put("estComplet", updated.getEstComplet());
            if (updated.getCreatedAt() != null) {
                creneauMap.put("createdAt", updated.getCreatedAt().toString());
            }
            
            return Response.ok(Map.of(
                "message", "Créneau mis à jour avec succès",
                "creneau", creneauMap
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @DELETE
    @Path("/creneaux/{id}")
    public Response deleteCreneau(@PathParam("id") Integer id) {
        try {
            creneauHoraireService.deleteCreneau(id);
            return Response.ok(Map.of("message", "Créneau supprimé avec succès")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/creneaux")
    public Response getAllCreneaux() {
        try {
            List<CreneauHoraire> creneaux = creneauHoraireService.findAll();
            
            // Créer une liste de créneaux simplifiés pour éviter les problèmes de sérialisation
            List<Map<String, Object>> creneauxData = creneaux.stream().map(c -> {
                Map<String, Object> cMap = new java.util.HashMap<>();
                cMap.put("id", c.getId());
                // Formater les dates pour éviter les problèmes de sérialisation
                if (c.getDateExam() != null) {
                    cMap.put("dateExam", c.getDateExam().toString());
                }
                if (c.getHeureDebut() != null) {
                    cMap.put("heureDebut", c.getHeureDebut().toString());
                }
                if (c.getHeureFin() != null) {
                    cMap.put("heureFin", c.getHeureFin().toString());
                }
                cMap.put("dureeMinutes", c.getDureeMinutes());
                cMap.put("nombreQuestions", c.getNombreQuestions());
                cMap.put("placesDisponibles", c.getPlacesDisponibles());
                cMap.put("estComplet", c.getEstComplet());
                if (c.getCreatedAt() != null) {
                    cMap.put("createdAt", c.getCreatedAt().toString());
                }
                return cMap;
            }).collect(java.util.stream.Collectors.toList());
            
            return Response.ok(Map.of("creneaux", creneauxData)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Erreur lors de la récupération des créneaux: " + e.getMessage()))
                .build();
        }
    }
    
    // Statistiques
    @GET
    @Path("/stats/overview")
    public Response getStatsOverview() {
        Map<String, Object> stats = Map.of(
            "totalCandidats", candidatService.count(),
            "candidatsValides", candidatService.findByEstValide(true).size(),
            "candidatsEnAttente", candidatService.findByEstValide(false).size(),
            "totalCreneaux", creneauHoraireService.findAll().size(),
            "creneauxDisponibles", creneauHoraireService.countAvailableCreneaux(),
            "totalQuestions", questionService.count(),
            "statsResultats", resultatService.getStatsGlobales()
        );
        
        return Response.ok(stats).build();
    }
    
    @GET
    @Path("/stats/questions")
    public Response getStatsQuestions() {
        Map<String, Object> stats = questionService.getStatsQuestions();
        return Response.ok(stats).build();
    }
    
    @GET
    @Path("/stats/candidats")
    public Response getStatsCandidats() {
        Map<String, Object> stats = Map.of(
            "total", candidatService.count(),
            "valides", candidatService.findByEstValide(true).size(),
            "enAttente", candidatService.findByEstValide(false).size(),
            "recent", candidatService.findRecentCandidates(10)
        );
        
        return Response.ok(stats).build();
    }
    
    // Gestion des administrateurs
    @GET
    @Path("/administrateurs")
    public Response getAllAdministrateurs() {
        List<Administrateur> admins = administrateurRepository.findAll();
        return Response.ok(Map.of("administrateurs", admins)).build();
    }
    
    @POST
    @Path("/administrateurs")
    @jakarta.transaction.Transactional
    public Response createAdministrateur(Map<String, String> adminData) {
        try {
            Administrateur admin = new Administrateur();
            admin.setUsername(adminData.get("username"));
            admin.setPassword(BCrypt.hashpw(adminData.get("password"), BCrypt.gensalt()));
            admin.setEmail(adminData.get("email"));
            admin.setNom(adminData.get("nom"));
            admin.setPrenom(adminData.get("prenom"));
            
            Administrateur created = administrateurRepository.create(admin);
            
            // Créer une copie sans le mot de passe pour la réponse
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("id", created.getId());
            result.put("username", created.getUsername());
            result.put("email", created.getEmail());
            result.put("nom", created.getNom());
            result.put("prenom", created.getPrenom());
            result.put("estActif", created.getEstActif());
            if (created.getCreatedAt() != null) {
                result.put("createdAt", created.getCreatedAt().toString());
            }
            
            return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                    "message", "Administrateur créé avec succès",
                    "administrateur", result
                ))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @PUT
    @Path("/administrateurs/{id}/password")
    public Response changePassword(@PathParam("id") Integer id, Map<String, String> data) {
        try {
            String oldPassword = data.get("oldPassword");
            String newPassword = data.get("newPassword");
            
            Optional<Administrateur> adminOpt = administrateurRepository.findById(id);
            if (adminOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Administrateur non trouvé"))
                    .build();
            }
            
            Administrateur admin = adminOpt.get();
            if (!BCrypt.checkpw(oldPassword, admin.getPassword())) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Ancien mot de passe incorrect"))
                    .build();
            }
            
            admin.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            administrateurRepository.update(admin);
            
            return Response.ok(Map.of("message", "Mot de passe changé avec succès")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    // Obtenir toutes les sessions de test pour l'administration
    @GET
    @Path("/resultats/sessions")
    public Response getAllSessions() {
        try {
            List<SessionTest> sessions = testService.getAllSessions();

            List<Map<String, Object>> sessionsData = sessions.stream().map(s -> {
                Map<String, Object> sMap = new java.util.HashMap<>();
                sMap.put("id", s.getId());
                sMap.put("codeSession", s.getCodeSession());
                if (s.getDateDebut() != null) {
                    sMap.put("dateDebut", s.getDateDebut().toString());
                }
                if (s.getDateFin() != null) {
                    sMap.put("dateFin", s.getDateFin().toString());
                }
                sMap.put("estTermine", s.getEstTermine());
                sMap.put("scoreTotal", s.getScoreTotal());
                sMap.put("scoreMax", s.getScoreMax());
                sMap.put("pourcentage", s.getPourcentage() != null ? s.getPourcentage().doubleValue() : 0.0);

                if (s.getDateDebut() != null && s.getDateFin() != null) {
                    Duration d = Duration.between(s.getDateDebut(), s.getDateFin());
                    long totalMinutes = Math.max(0, d.toMinutes());
                    long hours = totalMinutes / 60;
                    long minutes = totalMinutes % 60;
                    String duree = hours > 0 ? (hours + "h " + String.format("%02d", minutes) + "m") : (minutes + "m");
                    sMap.put("dureeTotale", duree);
                }

                if (s.getCandidat() != null) {
                    Map<String, Object> cMap = new java.util.HashMap<>();
                    cMap.put("id", s.getCandidat().getId());
                    cMap.put("nom", s.getCandidat().getNom());
                    cMap.put("prenom", s.getCandidat().getPrenom());
                    cMap.put("email", s.getCandidat().getEmail());
                    cMap.put("ecole", s.getCandidat().getEcole());
                    cMap.put("filiere", s.getCandidat().getFiliere());
                    sMap.put("candidat", cMap);
                }

                return sMap;
            }).collect(java.util.stream.Collectors.toList());

            return Response.ok(sessionsData).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/resultats/classement")
    public Response getClassementAdmin(@QueryParam("limit") Integer limit, @QueryParam("modeTest") String modeTest) {
        try {
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
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/resultats/session/{sessionId}/details")
    public Response getSessionResultDetails(@PathParam("sessionId") Integer sessionId) {
        try {
            Optional<SessionTest> sessionOpt = testService.getSessionById(sessionId);
            if (sessionOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Session de test non trouvée"))
                    .build();
            }

            SessionTest s = sessionOpt.get();

            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("id", s.getId());
            sessionMap.put("codeSession", s.getCodeSession());
            if (s.getDateDebut() != null) {
                sessionMap.put("dateDebut", s.getDateDebut().toString());
            }
            if (s.getDateFin() != null) {
                sessionMap.put("dateFin", s.getDateFin().toString());
            }
            sessionMap.put("estTermine", s.getEstTermine());
            sessionMap.put("scoreTotal", s.getScoreTotal());
            sessionMap.put("scoreMax", s.getScoreMax());
            sessionMap.put("pourcentage", s.getPourcentage() != null ? s.getPourcentage().doubleValue() : 0.0);

            if (s.getDateDebut() != null && s.getDateFin() != null) {
                Duration d = Duration.between(s.getDateDebut(), s.getDateFin());
                long totalMinutes = Math.max(0, d.toMinutes());
                long hours = totalMinutes / 60;
                long minutes = totalMinutes % 60;
                String duree = hours > 0 ? (hours + "h " + String.format("%02d", minutes) + "m") : (minutes + "m");
                sessionMap.put("dureeTotale", duree);
            }

            if (s.getCandidat() != null) {
                Map<String, Object> cMap = new HashMap<>();
                cMap.put("id", s.getCandidat().getId());
                cMap.put("nom", s.getCandidat().getNom());
                cMap.put("prenom", s.getCandidat().getPrenom());
                cMap.put("email", s.getCandidat().getEmail());
                cMap.put("ecole", s.getCandidat().getEcole());
                cMap.put("filiere", s.getCandidat().getFiliere());
                sessionMap.put("candidat", cMap);
            }

            Map<String, Object> details = resultatService.getDetailsSession(sessionId);
            List<Map<String, Object>> questionsDetails = resultatService.getQuestionsDetailsSession(sessionId);
            List<Map<String, Object>> resultatsParTheme = new ArrayList<>();
            if (details != null && details.get("statsParTheme") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> statsParTheme = (Map<String, Object>) details.get("statsParTheme");
                for (Map.Entry<String, Object> entry : statsParTheme.entrySet()) {
                    if (!(entry.getValue() instanceof Map)) {
                        continue;
                    }
                    @SuppressWarnings("unchecked")
                    Map<String, Object> statTheme = (Map<String, Object>) entry.getValue();

                    Map<String, Object> themeRow = new HashMap<>();
                    themeRow.put("nomTheme", entry.getKey());
                    themeRow.put("total", statTheme.getOrDefault("total", 0));
                    themeRow.put("correctes", statTheme.getOrDefault("correctes", 0));

                    Object pct = statTheme.get("pourcentage");
                    double pctDouble;
                    if (pct instanceof Number) {
                        pctDouble = ((Number) pct).doubleValue();
                    } else {
                        pctDouble = 0.0;
                    }
                    themeRow.put("pourcentage", pctDouble);
                    resultatsParTheme.add(themeRow);
                }
            }
            sessionMap.put("resultatsParTheme", resultatsParTheme);

            if (details != null) {
                Object tempsMoyen = details.get("tempsMoyen");
                if (tempsMoyen != null) {
                    sessionMap.put("tempsMoyenParQuestion", tempsMoyen.toString());
                }
            }

            sessionMap.put("questionsDetails", questionsDetails);

            return Response.ok(sessionMap).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @POST
    @Path("/resultats/export")
    @Produces("text/csv")
    public Response exportResultatsCSV(Map<String, Object> payload) {
        try {
            String searchTerm = null;
            String filterDate = null;

            if (payload != null && payload.get("filters") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> filters = (Map<String, Object>) payload.get("filters");
                Object st = filters.get("searchTerm");
                Object fd = filters.get("filterDate");
                searchTerm = st != null ? st.toString() : null;
                filterDate = fd != null ? fd.toString() : null;
            }

            final String search = searchTerm != null ? searchTerm.trim().toLowerCase() : "";
            final LocalDate filterLocalDate = (filterDate == null || filterDate.trim().isEmpty()) ? null : LocalDate.parse(filterDate.trim());

            List<SessionTest> sessions = testService.getAllSessions();

            List<SessionTest> filtered = sessions.stream().filter(s -> {
                boolean matchesSearch = true;
                if (!search.isEmpty()) {
                    String nom = s.getCandidat() != null && s.getCandidat().getNom() != null ? s.getCandidat().getNom().toLowerCase() : "";
                    String prenom = s.getCandidat() != null && s.getCandidat().getPrenom() != null ? s.getCandidat().getPrenom().toLowerCase() : "";
                    String email = s.getCandidat() != null && s.getCandidat().getEmail() != null ? s.getCandidat().getEmail().toLowerCase() : "";
                    matchesSearch = nom.contains(search) || prenom.contains(search) || email.contains(search);
                }

                boolean matchesDate = true;
                if (filterLocalDate != null) {
                    matchesDate = s.getDateDebut() != null && s.getDateDebut().toLocalDate().equals(filterLocalDate);
                }

                return matchesSearch && matchesDate;
            }).toList();

            StringBuilder csv = new StringBuilder();
            csv.append("Nom,Prénom,École,Filière,Email,Date Test,Score,Score Max,Pourcentage,Code Session\n");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (SessionTest session : filtered) {
                Candidat candidat = session.getCandidat();

                String nom = candidat != null && candidat.getNom() != null ? candidat.getNom() : "";
                String prenom = candidat != null && candidat.getPrenom() != null ? candidat.getPrenom() : "";
                String ecole = candidat != null && candidat.getEcole() != null ? candidat.getEcole() : "";
                String filiere = candidat != null && candidat.getFiliere() != null ? candidat.getFiliere() : "";
                String email = candidat != null && candidat.getEmail() != null ? candidat.getEmail() : "";

                String dateTest = session.getDateDebut() != null ? session.getDateDebut().format(dtf) : "";
                String score = session.getScoreTotal() != null ? session.getScoreTotal().toString() : "0";
                String scoreMax = session.getScoreMax() != null ? session.getScoreMax().toString() : "0";
                String pourcentage = session.getPourcentage() != null ? session.getPourcentage().toString() : "0.00";
                String codeSession = session.getCodeSession() != null ? session.getCodeSession() : "";

                csv.append(csvEscape(nom)).append(",")
                   .append(csvEscape(prenom)).append(",")
                   .append(csvEscape(ecole)).append(",")
                   .append(csvEscape(filiere)).append(",")
                   .append(csvEscape(email)).append(",")
                   .append(csvEscape(dateTest)).append(",")
                   .append(csvEscape(score)).append(",")
                   .append(csvEscape(scoreMax)).append(",")
                   .append(csvEscape(pourcentage)).append(",")
                   .append(csvEscape(codeSession)).append("\n");
            }

            String filename = "resultats_" + LocalDate.now() + ".csv";
            return Response.ok(csv.toString())
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("error," + csvEscape(e.getMessage()))
                .build();
        }
    }

    private String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        String v = value.replace("\r", " ").replace("\n", " ");
        boolean mustQuote = v.contains(",") || v.contains("\"");
        if (v.contains("\"")) {
            v = v.replace("\"", "\"\"");
            mustQuote = true;
        }
        return mustQuote ? ("\"" + v + "\"") : v;
    }
}
