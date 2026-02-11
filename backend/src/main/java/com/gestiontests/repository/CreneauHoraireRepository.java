package com.gestiontests.repository;

import com.gestiontests.entity.CreneauHoraire;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Named
@ApplicationScoped
public class CreneauHoraireRepository extends GenericRepository<CreneauHoraire, Integer> {
    
    public CreneauHoraireRepository() {
        super(CreneauHoraire.class);
    }
    
    public List<CreneauHoraire> findByDateExam(LocalDate dateExam) {
        TypedQuery<CreneauHoraire> query = entityManager.createQuery(
            "SELECT c FROM CreneauHoraire c WHERE c.dateExam = :dateExam ORDER BY c.heureDebut", 
            CreneauHoraire.class);
        query.setParameter("dateExam", dateExam);
        return query.getResultList();
    }
    
    public List<CreneauHoraire> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        TypedQuery<CreneauHoraire> query = entityManager.createQuery(
            "SELECT c FROM CreneauHoraire c WHERE c.dateExam BETWEEN :startDate AND :endDate ORDER BY c.dateExam, c.heureDebut", 
            CreneauHoraire.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<CreneauHoraire> findAvailableCreneaux() {
        TypedQuery<CreneauHoraire> query = entityManager.createQuery(
            "SELECT c FROM CreneauHoraire c WHERE (c.estComplet = false OR c.estComplet IS NULL) AND (c.placesDisponibles IS NULL OR c.placesDisponibles > 0) AND c.dateExam >= :today ORDER BY c.dateExam, c.heureDebut", 
            CreneauHoraire.class);
        query.setParameter("today", LocalDate.now());
        return query.getResultList();
    }
    
    public List<CreneauHoraire> findFutureCreneaux() {
        TypedQuery<CreneauHoraire> query = entityManager.createQuery(
            "SELECT c FROM CreneauHoraire c WHERE c.dateExam >= :today ORDER BY c.dateExam, c.heureDebut", 
            CreneauHoraire.class);
        query.setParameter("today", LocalDate.now());
        return query.getResultList();
    }
    
    public List<CreneauHoraire> findPastCreneaux() {
        TypedQuery<CreneauHoraire> query = entityManager.createQuery(
            "SELECT c FROM CreneauHoraire c WHERE c.dateExam < :today ORDER BY c.dateExam DESC, c.heureDebut DESC", 
            CreneauHoraire.class);
        query.setParameter("today", LocalDate.now());
        return query.getResultList();
    }
    
    public Optional<CreneauHoraire> findNextAvailable() {
        TypedQuery<CreneauHoraire> query = entityManager.createQuery(
            "SELECT c FROM CreneauHoraire c WHERE (c.estComplet = false OR c.estComplet IS NULL) AND (c.placesDisponibles IS NULL OR c.placesDisponibles > 0) AND c.dateExam >= :today ORDER BY c.dateExam, c.heureDebut", 
            CreneauHoraire.class);
        query.setParameter("today", LocalDate.now());
        query.setMaxResults(1);
        List<CreneauHoraire> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<CreneauHoraire> findByEstComplet(Boolean estComplet) {
        TypedQuery<CreneauHoraire> query = entityManager.createQuery(
            "SELECT c FROM CreneauHoraire c WHERE c.estComplet = :estComplet ORDER BY c.dateExam, c.heureDebut", 
            CreneauHoraire.class);
        query.setParameter("estComplet", estComplet);
        return query.getResultList();
    }
    
    public List<CreneauHoraire> findCreneauxInNextDays(int days) {
        LocalDate endDate = LocalDate.now().plusDays(days);
        TypedQuery<CreneauHoraire> query = entityManager.createQuery(
            "SELECT c FROM CreneauHoraire c WHERE c.dateExam BETWEEN :today AND :endDate AND (c.estComplet = false OR c.estComplet IS NULL) AND (c.placesDisponibles IS NULL OR c.placesDisponibles > 0) ORDER BY c.dateExam, c.heureDebut", 
            CreneauHoraire.class);
        query.setParameter("today", LocalDate.now());
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public boolean isCreneauAvailable(Integer creneauId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM CreneauHoraire c WHERE c.id = :id AND (c.estComplet = false OR c.estComplet IS NULL) AND (c.placesDisponibles IS NULL OR c.placesDisponibles > 0)", Long.class);
        query.setParameter("id", creneauId);
        return query.getSingleResult() > 0;
    }
    
    public long countAvailableCreneaux() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM CreneauHoraire c WHERE (c.estComplet = false OR c.estComplet IS NULL) AND (c.placesDisponibles IS NULL OR c.placesDisponibles > 0) AND c.dateExam >= :today", Long.class);
        query.setParameter("today", LocalDate.now());
        return query.getSingleResult();
    }
    
    public List<CreneauHoraire> findCreneauxByHeure(LocalDate date, String heure) {
        TypedQuery<CreneauHoraire> query = entityManager.createQuery(
            "SELECT c FROM CreneauHoraire c WHERE c.dateExam = :date AND c.heureDebut = :heure ORDER BY c.heureDebut", 
            CreneauHoraire.class);
        query.setParameter("date", date);
        query.setParameter("heure", heure);
        return query.getResultList();
    }
}
