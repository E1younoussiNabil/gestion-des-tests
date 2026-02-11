package com.gestiontests.repository;

import com.gestiontests.entity.Administrateur;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Named
@ApplicationScoped
public class AdministrateurRepository extends GenericRepository<Administrateur, Integer> {
    
    public AdministrateurRepository() {
        super(Administrateur.class);
    }
    
    public Optional<Administrateur> findByUsername(String username) {
        TypedQuery<Administrateur> query = entityManager.createQuery(
            "SELECT a FROM Administrateur a WHERE a.username = :username", Administrateur.class);
        query.setParameter("username", username);
        List<Administrateur> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public Optional<Administrateur> findByEmail(String email) {
        TypedQuery<Administrateur> query = entityManager.createQuery(
            "SELECT a FROM Administrateur a WHERE a.email = :email", Administrateur.class);
        query.setParameter("email", email);
        List<Administrateur> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<Administrateur> findByEstActif(Boolean estActif) {
        TypedQuery<Administrateur> query = entityManager.createQuery(
            "SELECT a FROM Administrateur a WHERE a.estActif = :estActif ORDER BY a.createdAt DESC", 
            Administrateur.class);
        query.setParameter("estActif", estActif);
        return query.getResultList();
    }
    
    public List<Administrateur> findAllOrderByUsername() {
        TypedQuery<Administrateur> query = entityManager.createQuery(
            "SELECT a FROM Administrateur a ORDER BY a.username", Administrateur.class);
        return query.getResultList();
    }
    
    public List<Administrateur> findAllOrderByCreatedAt() {
        TypedQuery<Administrateur> query = entityManager.createQuery(
            "SELECT a FROM Administrateur a ORDER BY a.createdAt DESC", Administrateur.class);
        return query.getResultList();
    }
    
    public boolean existsByUsername(String username) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(a) FROM Administrateur a WHERE a.username = :username", Long.class);
        query.setParameter("username", username);
        return query.getSingleResult() > 0;
    }
    
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(a) FROM Administrateur a WHERE a.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public Administrateur create(Administrateur administrateur) {
        if (existsByUsername(administrateur.getUsername())) {
            throw new IllegalArgumentException("Un administrateur avec ce nom d'utilisateur existe déjà: " + administrateur.getUsername());
        }
        if (existsByEmail(administrateur.getEmail())) {
            throw new IllegalArgumentException("Un administrateur avec cet email existe déjà: " + administrateur.getEmail());
        }
        return super.create(administrateur);
    }
    
    public long countByEstActif(Boolean estActif) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(a) FROM Administrateur a WHERE a.estActif = :estActif", Long.class);
        query.setParameter("estActif", estActif);
        return query.getSingleResult();
    }
    
    public List<Administrateur> findRecentAdministrateurs(int limit) {
        TypedQuery<Administrateur> query = entityManager.createQuery(
            "SELECT a FROM Administrateur a ORDER BY a.createdAt DESC", Administrateur.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}
