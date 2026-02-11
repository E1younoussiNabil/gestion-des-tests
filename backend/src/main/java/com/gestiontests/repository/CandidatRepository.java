package com.gestiontests.repository;

import com.gestiontests.entity.Candidat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Named
@ApplicationScoped
public class CandidatRepository extends GenericRepository<Candidat, Integer> {
    
    public CandidatRepository() {
        super(Candidat.class);
    }
    
    public Optional<Candidat> findByEmail(String email) {
        TypedQuery<Candidat> query = entityManager.createQuery(
            "SELECT c FROM Candidat c WHERE c.email = :email", Candidat.class);
        query.setParameter("email", email);
        List<Candidat> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public Optional<Candidat> findByCodeSession(String codeSession) {
        TypedQuery<Candidat> query = entityManager.createQuery(
            "SELECT c FROM Candidat c WHERE c.codeSession = :codeSession", Candidat.class);
        query.setParameter("codeSession", codeSession);
        List<Candidat> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<Candidat> findByNomOrPrenomOrEcole(String searchTerm) {
        TypedQuery<Candidat> query = entityManager.createQuery(
            "SELECT c FROM Candidat c WHERE " +
            "LOWER(c.nom) LIKE LOWER(:searchTerm) OR " +
            "LOWER(c.prenom) LIKE LOWER(:searchTerm) OR " +
            "LOWER(c.ecole) LIKE LOWER(:searchTerm)", Candidat.class);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<Candidat> findByEstValide(Boolean estValide) {
        TypedQuery<Candidat> query = entityManager.createQuery(
            "SELECT c FROM Candidat c WHERE c.estValide = :estValide", Candidat.class);
        query.setParameter("estValide", estValide);
        return query.getResultList();
    }
    
    public List<Candidat> findByEcole(String ecole) {
        TypedQuery<Candidat> query = entityManager.createQuery(
            "SELECT c FROM Candidat c WHERE LOWER(c.ecole) = LOWER(:ecole)", Candidat.class);
        query.setParameter("ecole", ecole);
        return query.getResultList();
    }
    
    public List<Candidat> findByFiliere(String filiere) {
        TypedQuery<Candidat> query = entityManager.createQuery(
            "SELECT c FROM Candidat c WHERE LOWER(c.filiere) = LOWER(:filiere)", Candidat.class);
        query.setParameter("filiere", filiere);
        return query.getResultList();
    }
    
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM Candidat c WHERE c.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
    
    public boolean existsByCodeSession(String codeSession) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM Candidat c WHERE c.codeSession = :codeSession", Long.class);
        query.setParameter("codeSession", codeSession);
        return query.getSingleResult() > 0;
    }
    
    public List<Candidat> findRecentCandidates(int limit) {
        TypedQuery<Candidat> query = entityManager.createQuery(
            "SELECT c FROM Candidat c ORDER BY c.createdAt DESC", Candidat.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}
