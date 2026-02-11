package com.gestiontests.repository;

import com.gestiontests.entity.ReponsePossible;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Named
@ApplicationScoped
public class ReponsePossibleRepository extends GenericRepository<ReponsePossible, Integer> {
    
    public ReponsePossibleRepository() {
        super(ReponsePossible.class);
    }
    
    public List<ReponsePossible> findByQuestion(Integer questionId) {
        TypedQuery<ReponsePossible> query = entityManager.createQuery(
            "SELECT rp FROM ReponsePossible rp WHERE rp.question.id = :questionId ORDER BY rp.id", 
            ReponsePossible.class);
        query.setParameter("questionId", questionId);
        return query.getResultList();
    }
    
    public List<ReponsePossible> findCorrectByQuestion(Integer questionId) {
        TypedQuery<ReponsePossible> query = entityManager.createQuery(
            "SELECT rp FROM ReponsePossible rp WHERE rp.question.id = :questionId AND rp.estCorrect = true ORDER BY rp.id", 
            ReponsePossible.class);
        query.setParameter("questionId", questionId);
        return query.getResultList();
    }
    
    public List<ReponsePossible> findIncorrectByQuestion(Integer questionId) {
        TypedQuery<ReponsePossible> query = entityManager.createQuery(
            "SELECT rp FROM ReponsePossible rp WHERE rp.question.id = :questionId AND rp.estCorrect = false ORDER BY rp.id", 
            ReponsePossible.class);
        query.setParameter("questionId", questionId);
        return query.getResultList();
    }
    
    public Optional<ReponsePossible> findById(Integer id) {
        return super.findById(id);
    }
    
    public long countByQuestion(Integer questionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rp) FROM ReponsePossible rp WHERE rp.question.id = :questionId", Long.class);
        query.setParameter("questionId", questionId);
        return query.getSingleResult();
    }
    
    public long countCorrectByQuestion(Integer questionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rp) FROM ReponsePossible rp WHERE rp.question.id = :questionId AND rp.estCorrect = true", Long.class);
        query.setParameter("questionId", questionId);
        return query.getSingleResult();
    }
    
    public long countIncorrectByQuestion(Integer questionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rp) FROM ReponsePossible rp WHERE rp.question.id = :questionId AND rp.estCorrect = false", Long.class);
        query.setParameter("questionId", questionId);
        return query.getSingleResult();
    }
    
    public List<ReponsePossible> findByLibelleContaining(String searchTerm) {
        TypedQuery<ReponsePossible> query = entityManager.createQuery(
            "SELECT rp FROM ReponsePossible rp WHERE LOWER(rp.libelle) LIKE LOWER(:searchTerm) ORDER BY rp.libelle", 
            ReponsePossible.class);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<ReponsePossible> findCorrectResponses() {
        TypedQuery<ReponsePossible> query = entityManager.createQuery(
            "SELECT rp FROM ReponsePossible rp WHERE rp.estCorrect = true ORDER BY rp.question.id", 
            ReponsePossible.class);
        return query.getResultList();
    }
    
    public List<ReponsePossible> findIncorrectResponses() {
        TypedQuery<ReponsePossible> query = entityManager.createQuery(
            "SELECT rp FROM ReponsePossible rp WHERE rp.estCorrect = false ORDER BY rp.question.id", 
            ReponsePossible.class);
        return query.getResultList();
    }
    
    public boolean hasCorrectResponse(Integer questionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rp) FROM ReponsePossible rp WHERE rp.question.id = :questionId AND rp.estCorrect = true", Long.class);
        query.setParameter("questionId", questionId);
        return query.getSingleResult() > 0;
    }
    
    public List<ReponsePossible> findRandomResponses(int limit) {
        TypedQuery<ReponsePossible> query = entityManager.createQuery(
            "SELECT rp FROM ReponsePossible rp ORDER BY RAND()", 
            ReponsePossible.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<ReponsePossible> findMostUsedResponses(int limit) {
        TypedQuery<ReponsePossible> query = entityManager.createQuery(
            "SELECT rp FROM ReponsePossible rp JOIN rp.reponsesCandidat rc GROUP BY rp.id ORDER BY COUNT(rc) DESC", 
            ReponsePossible.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<ReponsePossible> findLeastUsedResponses(int limit) {
        TypedQuery<ReponsePossible> query = entityManager.createQuery(
            "SELECT rp FROM ReponsePossible rp LEFT JOIN rp.reponsesCandidat rc GROUP BY rp.id ORDER BY COUNT(rc) ASC", 
            ReponsePossible.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<ReponsePossible> findUnusedResponses() {
        TypedQuery<ReponsePossible> query = entityManager.createQuery(
            "SELECT rp FROM ReponsePossible rp WHERE NOT EXISTS (SELECT rc FROM ReponseCandidat rc WHERE rc.reponsePossible.id = rp.id)", 
            ReponsePossible.class);
        return query.getResultList();
    }
    
    public List<ReponsePossible> findResponsesByTheme(Integer themeId) {
        TypedQuery<ReponsePossible> query = entityManager.createQuery(
            "SELECT rp FROM ReponsePossible rp WHERE rp.question.theme.id = :themeId ORDER BY rp.question.id, rp.id", 
            ReponsePossible.class);
        query.setParameter("themeId", themeId);
        return query.getResultList();
    }
}
