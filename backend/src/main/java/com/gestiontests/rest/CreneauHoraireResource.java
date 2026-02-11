package com.gestiontests.rest;

import com.gestiontests.entity.CreneauHoraire;
import com.gestiontests.service.CreneauHoraireService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/creneaux")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CreneauHoraireResource {
    
    @Inject
    private CreneauHoraireService creneauHoraireService;
    
    @POST
    public Response createCreneau(CreneauHoraire creneau) {
        try {
            CreneauHoraire created = creneauHoraireService.createCreneau(creneau);
            return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                    "message", "Créneau créé avec succès",
                    "creneau", created
                ))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateCreneau(@PathParam("id") Integer id, CreneauHoraire creneau) {
        try {
            creneau.setId(id);
            CreneauHoraire updated = creneauHoraireService.updateCreneau(creneau);
            return Response.ok(Map.of(
                "message", "Créneau mis à jour avec succès",
                "creneau", updated
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
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
    @Path("/{id}")
    public Response getCreneauById(@PathParam("id") Integer id) {
        Optional<CreneauHoraire> creneauOpt = creneauHoraireService.findById(id);
        if (creneauOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Créneau non trouvé"))
                .build();
        }
        
        return Response.ok(Map.of("creneau", creneauOpt.get())).build();
    }
    
    @GET
    public Response getAllCreneaux() {
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
    }
    
    @GET
    @Path("/date/{date}")
    public Response getCreneauxByDate(@PathParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            List<CreneauHoraire> creneaux = creneauHoraireService.findByDateExam(date);
            return Response.ok(Map.of("creneaux", creneaux)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Format de date invalide. Utilisez YYYY-MM-DD"))
                .build();
        }
    }
    
    @GET
    @Path("/between/{startDate}/{endDate}")
    public Response getCreneauxBetweenDates(@PathParam("startDate") String startDateStr, 
                                           @PathParam("endDate") String endDateStr) {
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            List<CreneauHoraire> creneaux = creneauHoraireService.findByDateBetween(startDate, endDate);
            return Response.ok(Map.of("creneaux", creneaux)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Format de date invalide. Utilisez YYYY-MM-DD"))
                .build();
        }
    }
    
    @GET
    @Path("/disponibles")
    public Response getCreneauxDisponibles() {
        List<CreneauHoraire> creneaux = creneauHoraireService.findAvailableCreneaux();
        
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
    }
    
    @GET
    @Path("/futurs")
    public Response getCreneauxFuturs() {
        List<CreneauHoraire> creneaux = creneauHoraireService.findFutureCreneaux();
        return Response.ok(Map.of("creneaux", creneaux)).build();
    }
    
    @GET
    @Path("/passes")
    public Response getCreneauxPasses() {
        List<CreneauHoraire> creneaux = creneauHoraireService.findPastCreneaux();
        return Response.ok(Map.of("creneaux", creneaux)).build();
    }
    
    @GET
    @Path("/prochains-jours/{jours}")
    public Response getCreneauxProchainsJours(@PathParam("jours") Integer jours) {
        List<CreneauHoraire> creneaux = creneauHoraireService.findCreneauxInNextDays(jours);
        return Response.ok(Map.of("creneaux", creneaux)).build();
    }
    
    @GET
    @Path("/prochain-disponible")
    public Response getProchainCreneauDisponible() {
        Optional<CreneauHoraire> creneauOpt = creneauHoraireService.findNextAvailable();
        if (creneauOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Aucun créneau disponible"))
                .build();
        }
        
        return Response.ok(Map.of("creneau", creneauOpt.get())).build();
    }
    
    @GET
    @Path("/{id}/disponibilite")
    public Response getDisponibiliteCreneau(@PathParam("id") Integer id) {
        boolean disponible = creneauHoraireService.isCreneauAvailable(id);
        return Response.ok(Map.of(
            "creneauId", id,
            "disponible", disponible
        )).build();
    }
    
    @POST
    @Path("/{id}/mettre-a-jour-disponibilite")
    public Response mettreAJourDisponibilite(@PathParam("id") Integer id) {
        try {
            creneauHoraireService.updateDisponibiliteCreneau(id);
            return Response.ok(Map.of("message", "Disponibilité mise à jour avec succès")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/aujourd-hui")
    public Response getCreneauxAujourdHui() {
        List<CreneauHoraire> creneaux = creneauHoraireService.getCreneauxForToday();
        return Response.ok(Map.of("creneaux", creneaux)).build();
    }
    
    @GET
    @Path("/semaine")
    public Response getCreneauxSemaine() {
        List<CreneauHoraire> creneaux = creneauHoraireService.getCreneauxForWeek();
        return Response.ok(Map.of("creneaux", creneaux)).build();
    }
    
    @GET
    @Path("/{id}/statut")
    public Response getStatutCreneau(@PathParam("id") Integer id) {
        Optional<CreneauHoraire> creneauOpt = creneauHoraireService.findById(id);
        if (creneauOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Créneau non trouvé"))
                .build();
        }
        
        CreneauHoraire creneau = creneauOpt.get();
        
        return Response.ok(Map.of(
            "creneauId", id,
            "estPasse", creneauHoraireService.isCreneauInPast(id),
            "enCours", creneauHoraireService.isCreneauInProgress(id),
            "peutCommencer", creneauHoraireService.canStartTest(id),
            "estComplet", creneau.getEstComplet()
        )).build();
    }
    
    @GET
    @Path("/stats")
    public Response getStats() {
        long total = creneauHoraireService.findAll().size();
        long disponibles = creneauHoraireService.countAvailableCreneaux();
        
        return Response.ok(Map.of(
            "total", total,
            "disponibles", disponibles,
            "complets", total - disponibles
        )).build();
    }
}
