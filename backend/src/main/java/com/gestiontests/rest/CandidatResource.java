package com.gestiontests.rest;

import com.gestiontests.entity.Candidat;
import com.gestiontests.entity.CreneauHoraire;
import com.gestiontests.service.CandidatService;
import com.gestiontests.service.CreneauHoraireService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/candidats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CandidatResource {
    
    @Inject
    private CandidatService candidatService;
    
    @Inject
    private CreneauHoraireService creneauHoraireService;
    
    @POST
    @Path("/inscription")
    public Response inscrireCandidat(Map<String, Object> payload) {
        try {
            Candidat candidat = new Candidat();
            candidat.setNom((String) payload.get("nom"));
            candidat.setPrenom((String) payload.get("prenom"));
            candidat.setEcole((String) payload.get("ecole"));
            candidat.setFiliere((String) payload.get("filiere"));
            candidat.setEmail((String) payload.get("email"));
            candidat.setGsm((String) payload.get("gsm"));
            
            Integer creneauId = (Integer) payload.get("creneauId");
            if (creneauId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Le creneauId est obligatoire"))
                    .build();
            }

            String modeTest = payload.get("modeTest") != null ? String.valueOf(payload.get("modeTest")).trim() : "GENERAL";
            if (modeTest.isEmpty()) {
                modeTest = "GENERAL";
            }
            if (!modeTest.equalsIgnoreCase("GENERAL") && !modeTest.equalsIgnoreCase("INFORMATIQUE")) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Le modeTest est invalide (GENERAL ou INFORMATIQUE)"))
                    .build();
            }

            Integer idThemeSpecialite = null;
            Object idThemeSpecialiteObj = payload.get("idThemeSpecialite");
            if (idThemeSpecialiteObj instanceof Number) {
                idThemeSpecialite = ((Number) idThemeSpecialiteObj).intValue();
            } else if (idThemeSpecialiteObj instanceof String && !((String) idThemeSpecialiteObj).trim().isEmpty()) {
                try {
                    idThemeSpecialite = Integer.parseInt(((String) idThemeSpecialiteObj).trim());
                } catch (NumberFormatException ignored) {
                    idThemeSpecialite = null;
                }
            }

            if (modeTest.equalsIgnoreCase("INFORMATIQUE") && idThemeSpecialite == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "idThemeSpecialite est obligatoire si modeTest=INFORMATIQUE"))
                    .build();
            }
            
            Candidat inscrit = candidatService.inscrireCandidat(candidat, creneauId, modeTest.toUpperCase(), idThemeSpecialite);
            
            return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                    "message", "Inscription réussie",
                    "candidat", inscrit,
                    "validationRequise", !inscrit.getEstValide()
                ))
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @POST
    @Path("/demander-nouveau-test")
    public Response demanderNouveauTest(Map<String, Object> payload) {
        try {
            Integer candidatId = payload.get("candidatId") instanceof Number ? ((Number) payload.get("candidatId")).intValue() : null;
            Integer creneauId = payload.get("creneauId") instanceof Number ? ((Number) payload.get("creneauId")).intValue() : null;
            if (candidatId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Le candidatId est obligatoire"))
                    .build();
            }
            if (creneauId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Le creneauId est obligatoire"))
                    .build();
            }

            String modeTest = payload.get("modeTest") != null ? String.valueOf(payload.get("modeTest")).trim() : "GENERAL";
            if (modeTest.isEmpty()) {
                modeTest = "GENERAL";
            }
            if (!modeTest.equalsIgnoreCase("GENERAL") && !modeTest.equalsIgnoreCase("INFORMATIQUE")) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Le modeTest est invalide (GENERAL ou INFORMATIQUE)"))
                    .build();
            }

            Integer idThemeSpecialite = null;
            Object idThemeSpecialiteObj = payload.get("idThemeSpecialite");
            if (idThemeSpecialiteObj instanceof Number) {
                idThemeSpecialite = ((Number) idThemeSpecialiteObj).intValue();
            } else if (idThemeSpecialiteObj instanceof String && !((String) idThemeSpecialiteObj).trim().isEmpty()) {
                try {
                    idThemeSpecialite = Integer.parseInt(((String) idThemeSpecialiteObj).trim());
                } catch (NumberFormatException ignored) {
                    idThemeSpecialite = null;
                }
            }

            if (modeTest.equalsIgnoreCase("INFORMATIQUE") && idThemeSpecialite == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "idThemeSpecialite est obligatoire si modeTest=INFORMATIQUE"))
                    .build();
            }

            Candidat candidat = candidatService.demanderNouveauTest(candidatId, creneauId, modeTest.toUpperCase(), idThemeSpecialite);
            return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                    "message", "Demande enregistrée. Votre demande est en attente de validation par l'administrateur.",
                    "candidat", candidat,
                    "validationRequise", true
                ))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/validation/{candidatId}")
    public Response validerInscription(@PathParam("candidatId") Integer candidatId) {
        try {
            Candidat candidat = candidatService.validerInscription(candidatId);
            return Response.ok(Map.of(
                "message", "Inscription validée avec succès",
                "candidat", candidat
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @POST
    @Path("/connexion")
    public Response connecterCandidat(Map<String, Object> payload) {
        Object codeSessionObj = payload.get("codeSession");
        if (codeSessionObj == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Le codeSession est obligatoire"))
                .build();
        }

        String codeSession = String.valueOf(codeSessionObj);
        if (codeSession.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Le codeSession est obligatoire"))
                .build();
        }

        Optional<Candidat> candidatOpt = candidatService.findByCodeSession(codeSession);
        if (candidatOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Code session non trouvé"))
                .build();
        }

        Candidat candidat = candidatOpt.get();
        return Response.ok(Map.of(
            "candidat", candidat,
            "peutPasserTest", candidatService.peutPasserTest(codeSession),
            "creneauEstPasse", candidatService.creneauEstPasse(codeSession),
            "creneauEstAtteint", candidatService.creneauEstAtteint(codeSession)
        )).build();
    }

    @POST
    @Path("/otp/request")
    public Response demanderOtpConnexion(Map<String, Object> payload) {
        try {
            Object emailObj = payload.get("email");
            if (emailObj == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "L'email est obligatoire"))
                    .build();
            }
            String email = String.valueOf(emailObj);
            candidatService.demanderOtpConnexion(email);
            return Response.ok(Map.of("message", "Code envoyé"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @POST
    @Path("/otp/verify")
    public Response verifierOtpConnexion(Map<String, Object> payload) {
        try {
            Object emailObj = payload.get("email");
            Object otpObj = payload.get("otp");
            if (emailObj == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "L'email est obligatoire"))
                    .build();
            }
            if (otpObj == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Le code est obligatoire"))
                    .build();
            }

            String email = String.valueOf(emailObj);
            String otp = String.valueOf(otpObj);
            Candidat candidat = candidatService.verifierOtpConnexion(email, otp);
            return Response.ok(Map.of(
                "message", "Connexion réussie",
                "candidat", candidat
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/creneau-info/{codeSession}")
    public Response getCreneauInfo(@PathParam("codeSession") String codeSession) {
        Optional<Map<String, Object>> infoOpt = candidatService.getCreneauInfo(codeSession);
        if (infoOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Créneau non trouvé"))
                .build();
        }
        return Response.ok(infoOpt.get()).build();
    }
    
    @GET
    @Path("/code/{codeSession}")
    public Response getCandidatByCodeSession(@PathParam("codeSession") String codeSession) {
        Optional<Candidat> candidatOpt = candidatService.findByCodeSession(codeSession);
        if (candidatOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Code session non trouvé"))
                .build();
        }
        
        Candidat candidat = candidatOpt.get();
        return Response.ok(Map.of(
            "candidat", candidat,
            "peutPasserTest", candidatService.peutPasserTest(codeSession),
            "creneauEstPasse", candidatService.creneauEstPasse(codeSession),
            "creneauEstAtteint", candidatService.creneauEstAtteint(codeSession)
        )).build();
    }
    
    @GET
    @Path("/search")
    public Response searchCandidats(@QueryParam("term") String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Le terme de recherche est obligatoire"))
                .build();
        }
        
        List<Candidat> candidats = candidatService.findByNomOrPrenomOrEcole(searchTerm);
        return Response.ok(Map.of("candidats", candidats)).build();
    }
    
    @GET
    @Path("/valide/{estValide}")
    public Response getCandidatsByValidation(@PathParam("estValide") Boolean estValide) {
        List<Candidat> candidats = candidatService.findByEstValide(estValide);
        return Response.ok(Map.of("candidats", candidats)).build();
    }
    
    @GET
    @Path("/recent/{limit}")
    public Response getRecentCandidats(@PathParam("limit") Integer limit) {
        List<Candidat> candidats = candidatService.findRecentCandidates(limit);
        return Response.ok(Map.of("candidats", candidats)).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getCandidatById(@PathParam("id") Integer id) {
        Optional<Candidat> candidatOpt = candidatService.findById(id);
        if (candidatOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Candidat non trouvé"))
                .build();
        }
        
        return Response.ok(Map.of("candidat", candidatOpt.get())).build();
    }
    
    @GET
    public Response getAllCandidats() {
        List<Candidat> candidats = candidatService.findAll();
        return Response.ok(Map.of(
            "candidats", candidats,
            "total", candidats.size()
        )).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateCandidat(@PathParam("id") Integer id, Candidat candidat) {
        try {
            candidat.setId(id);
            Candidat updated = candidatService.updateCandidat(candidat);
            return Response.ok(Map.of(
                "message", "Candidat mis à jour avec succès",
                "candidat", updated
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteCandidat(@PathParam("id") Integer id) {
        try {
            candidatService.deleteCandidat(id);
            return Response.ok(Map.of("message", "Candidat supprimé avec succès")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/stats")
    public Response getStats() {
        long total = candidatService.count();
        long valides = candidatService.findByEstValide(true).size();
        long enAttente = candidatService.findByEstValide(false).size();
        
        return Response.ok(Map.of(
            "total", total,
            "valides", valides,
            "enAttente", enAttente
        )).build();
    }
    
    @GET
    @Path("/creneaux-disponibles")
    public Response getCreneauxDisponibles() {
        List<CreneauHoraire> creneaux = creneauHoraireService.findAvailableCreneaux();
        return Response.ok(Map.of("creneaux", creneaux)).build();
    }
}
