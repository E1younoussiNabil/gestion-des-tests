package com.gestiontests.repository;

import com.gestiontests.entity.Theme;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Named
@ApplicationScoped
public class ThemeRepository extends GenericRepository<Theme, Integer> {
    
    public ThemeRepository() {
        super(Theme.class);
    }
    
    public Optional<Theme> findByNom(String nom) {
        TypedQuery<Theme> query = entityManager.createQuery(
            "SELECT t FROM Theme t WHERE t.nom = :nom", Theme.class);
        query.setParameter("nom", nom);
        List<Theme> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<Theme> findByNomContaining(String searchTerm) {
        TypedQuery<Theme> query = entityManager.createQuery(
            "SELECT t FROM Theme t WHERE LOWER(t.nom) LIKE LOWER(:searchTerm) ORDER BY t.nom", 
            Theme.class);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<Theme> findByDescriptionContaining(String searchTerm) {
        TypedQuery<Theme> query = entityManager.createQuery(
            "SELECT t FROM Theme t WHERE LOWER(t.description) LIKE LOWER(:searchTerm) ORDER BY t.nom", 
            Theme.class);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<Theme> findAllOrderByNom() {
        TypedQuery<Theme> query = entityManager.createQuery(
            "SELECT t FROM Theme t ORDER BY t.nom", Theme.class);
        return query.getResultList();
    }
    
    public List<Theme> findAllOrderByQuestionCount() {
        TypedQuery<Theme> query = entityManager.createQuery(
            "SELECT t FROM Theme t LEFT JOIN FETCH t.questions ORDER BY SIZE(t.questions) DESC", 
            Theme.class);
        return query.getResultList();
    }
    
    public List<Theme> findThemesWithQuestions() {
        TypedQuery<Theme> query = entityManager.createQuery(
            "SELECT DISTINCT t FROM Theme t JOIN t.questions q", Theme.class);
        return query.getResultList();
    }
    
    public List<Theme> findThemesWithoutQuestions() {
        TypedQuery<Theme> query = entityManager.createQuery(
            "SELECT t FROM Theme t WHERE NOT EXISTS (SELECT q FROM Question q WHERE q.theme.id = t.id)", 
            Theme.class);
        return query.getResultList();
    }
    
    public boolean existsByNom(String nom) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(t) FROM Theme t WHERE t.nom = :nom", Long.class);
        query.setParameter("nom", nom);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public Theme create(Theme theme) {
        if (existsByNom(theme.getNom())) {
            throw new IllegalArgumentException("Un thème avec ce nom existe déjà: " + theme.getNom());
        }
        return super.create(theme);
    }
    
    public long countThemesWithQuestions() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(DISTINCT t) FROM Theme t JOIN t.questions q", Long.class);
        return query.getSingleResult();
    }
    
    public long countThemesWithoutQuestions() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(t) FROM Theme t WHERE NOT EXISTS (SELECT q FROM Question q WHERE q.theme.id = t.id)", 
            Long.class);
        return query.getSingleResult();
    }
    
    public Theme findThemeWithMostQuestions() {
        TypedQuery<Theme> query = entityManager.createQuery(
            "SELECT t FROM Theme t LEFT JOIN t.questions q GROUP BY t.id ORDER BY COUNT(q) DESC", 
            Theme.class);
        query.setMaxResults(1);
        List<Theme> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public Theme findThemeWithFewestQuestions() {
        TypedQuery<Theme> query = entityManager.createQuery(
            "SELECT t FROM Theme t LEFT JOIN t.questions q GROUP BY t.id ORDER BY COUNT(q) ASC", 
            Theme.class);
        query.setMaxResults(1);
        List<Theme> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public List<Theme> findThemesByDateRange(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        TypedQuery<Theme> query = entityManager.createQuery(
            "SELECT t FROM Theme t WHERE t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC", 
            Theme.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Theme> findRecentThemes(int limit) {
        TypedQuery<Theme> query = entityManager.createQuery(
            "SELECT t FROM Theme t ORDER BY t.createdAt DESC", Theme.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}
