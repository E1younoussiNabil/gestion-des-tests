package com.gestiontests.repository;

import com.gestiontests.entity.Inscription;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Named
@ApplicationScoped
public class InscriptionRepository extends GenericRepository<Inscription, Integer> {
    
    public InscriptionRepository() {
        super(Inscription.class);
    }
    
    public List<Inscription> findByCandidat(Integer candidatId) {
        TypedQuery<Inscription> query = entityManager.createQuery(
            "SELECT i FROM Inscription i WHERE i.candidat.id = :candidatId ORDER BY i.dateInscription DESC", 
            Inscription.class);
        query.setParameter("candidatId", candidatId);
        return query.getResultList();
    }

    public Optional<Inscription> findLatestByCandidat(Integer candidatId) {
        TypedQuery<Inscription> query = entityManager.createQuery(
            "SELECT i FROM Inscription i WHERE i.candidat.id = :candidatId ORDER BY i.dateInscription DESC",
            Inscription.class);
        query.setParameter("candidatId", candidatId);
        query.setMaxResults(1);
        List<Inscription> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<Inscription> findByCreneau(Integer creneauId) {
        TypedQuery<Inscription> query = entityManager.createQuery(
            "SELECT i FROM Inscription i WHERE i.creneau.id = :creneauId ORDER BY i.dateInscription", 
            Inscription.class);
        query.setParameter("creneauId", creneauId);
        return query.getResultList();
    }
    
    public Optional<Inscription> findByCandidatAndCreneau(Integer candidatId, Integer creneauId) {
        TypedQuery<Inscription> query = entityManager.createQuery(
            "SELECT i FROM Inscription i WHERE i.candidat.id = :candidatId AND i.creneau.id = :creneauId", 
            Inscription.class);
        query.setParameter("candidatId", candidatId);
        query.setParameter("creneauId", creneauId);
        List<Inscription> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<Inscription> findByEstConfirme(Boolean estConfirme) {
        TypedQuery<Inscription> query = entityManager.createQuery(
            "SELECT i FROM Inscription i WHERE i.estConfirme = :estConfirme ORDER BY i.dateInscription DESC", 
            Inscription.class);
        query.setParameter("estConfirme", estConfirme);
        return query.getResultList();
    }
    
    public List<Inscription> findRecentInscriptions(int limit) {
        TypedQuery<Inscription> query = entityManager.createQuery(
            "SELECT i FROM Inscription i ORDER BY i.dateInscription DESC", Inscription.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public long countByCreneau(Integer creneauId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(i) FROM Inscription i WHERE i.creneau.id = :creneauId", Long.class);
        query.setParameter("creneauId", creneauId);
        return query.getSingleResult();
    }
    
    public long countByCandidat(Integer candidatId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(i) FROM Inscription i WHERE i.candidat.id = :candidatId", Long.class);
        query.setParameter("candidatId", candidatId);
        return query.getSingleResult();
    }
    
    public boolean existsByCandidatAndCreneau(Integer candidatId, Integer creneauId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(i) FROM Inscription i WHERE i.candidat.id = :candidatId AND i.creneau.id = :creneauId", Long.class);
        query.setParameter("candidatId", candidatId);
        query.setParameter("creneauId", creneauId);
        return query.getSingleResult() > 0;
    }
    
    public List<Inscription> findInscriptionsByDateRange(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        TypedQuery<Inscription> query = entityManager.createQuery(
            "SELECT i FROM Inscription i WHERE i.dateInscription BETWEEN :startDate AND :endDate ORDER BY i.dateInscription DESC", 
            Inscription.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Inscription> findByEcole(String ecole) {
        TypedQuery<Inscription> query = entityManager.createQuery(
            "SELECT i FROM Inscription i WHERE i.candidat.ecole = :ecole ORDER BY i.dateInscription DESC", 
            Inscription.class);
        query.setParameter("ecole", ecole);
        return query.getResultList();
    }
}
