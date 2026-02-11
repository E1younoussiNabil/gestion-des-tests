package com.gestiontests.repository;

import com.gestiontests.entity.Question;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Named
@ApplicationScoped
public class QuestionRepository extends GenericRepository<Question, Integer> {
    
    public QuestionRepository() {
        super(Question.class);
    }
    
    public List<Question> findByTheme(Integer themeId) {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE q.theme.id = :themeId ORDER BY q.createdAt DESC", 
            Question.class);
        query.setParameter("themeId", themeId);
        return query.getResultList();
    }
    
    public List<Question> findByTypeQuestion(Integer typeQuestionId) {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE q.typeQuestion.id = :typeQuestionId ORDER BY q.createdAt DESC", 
            Question.class);
        query.setParameter("typeQuestionId", typeQuestionId);
        return query.getResultList();
    }
    
    public List<Question> findByThemeAndType(Integer themeId, Integer typeQuestionId) {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE q.theme.id = :themeId AND q.typeQuestion.id = :typeQuestionId ORDER BY q.createdAt DESC", 
            Question.class);
        query.setParameter("themeId", themeId);
        query.setParameter("typeQuestionId", typeQuestionId);
        return query.getResultList();
    }
    
    public List<Question> findByLibelleContaining(String searchTerm) {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE LOWER(q.libelle) LIKE LOWER(:searchTerm) ORDER BY q.createdAt DESC", 
            Question.class);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<Question> findRandomQuestionsByTheme(Integer themeId, int limit) {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE q.theme.id = :themeId ORDER BY RAND()", 
            Question.class);
        query.setParameter("themeId", themeId);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Question> findRandomQuestions(int limit) {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q ORDER BY RAND()", 
            Question.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Question> findRecentQuestions(int limit) {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q ORDER BY q.createdAt DESC", Question.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public long countByTheme(Integer themeId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(q) FROM Question q WHERE q.theme.id = :themeId", Long.class);
        query.setParameter("themeId", themeId);
        return query.getSingleResult();
    }
    
    public long countByTypeQuestion(Integer typeQuestionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(q) FROM Question q WHERE q.typeQuestion.id = :typeQuestionId", Long.class);
        query.setParameter("typeQuestionId", typeQuestionId);
        return query.getSingleResult();
    }
    
    public long countByThemeAndType(Integer themeId, Integer typeQuestionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(q) FROM Question q WHERE q.theme.id = :themeId AND q.typeQuestion.id = :typeQuestionId", Long.class);
        query.setParameter("themeId", themeId);
        query.setParameter("typeQuestionId", typeQuestionId);
        return query.getSingleResult();
    }
    
    public List<Question> findQuestionsWithoutReponses() {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE NOT EXISTS (SELECT rp FROM ReponsePossible rp WHERE rp.question.id = q.id)", 
            Question.class);
        return query.getResultList();
    }
    
    public List<Question> findQuestionsWithReponses() {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE EXISTS (SELECT rp FROM ReponsePossible rp WHERE rp.question.id = q.id)", 
            Question.class);
        return query.getResultList();
    }
    
    public List<Question> findQuestionsWithCorrectReponses() {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE EXISTS (SELECT rp FROM ReponsePossible rp WHERE rp.question.id = q.id AND rp.estCorrect = true)", 
            Question.class);
        return query.getResultList();
    }
    
    public List<Question> findQuestionsWithoutCorrectReponses() {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE NOT EXISTS (SELECT rp FROM ReponsePossible rp WHERE rp.question.id = q.id AND rp.estCorrect = true)", 
            Question.class);
        return query.getResultList();
    }
    
    public List<Question> findQuestionsByExplicationContaining(String searchTerm) {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE LOWER(q.explication) LIKE LOWER(:searchTerm) ORDER BY q.createdAt DESC", 
            Question.class);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<Question> findQuestionsWithMinReponses(int minReponses) {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE (SELECT COUNT(rp) FROM ReponsePossible rp WHERE rp.question.id = q.id) >= :minReponses", 
            Question.class);
        query.setParameter("minReponses", minReponses);
        return query.getResultList();
    }
    
    public List<Question> findQuestionsByDateRange(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE q.createdAt BETWEEN :startDate AND :endDate ORDER BY q.createdAt DESC", 
            Question.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public Optional<Question> findRandomQuestionByTheme(Integer themeId) {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT q FROM Question q WHERE q.theme.id = :themeId ORDER BY RAND()", 
            Question.class);
        query.setParameter("themeId", themeId);
        query.setMaxResults(1);
        List<Question> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<Question> findAllWithReponses() {
        TypedQuery<Question> query = entityManager.createQuery(
            "SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.reponsesPossibles LEFT JOIN FETCH q.theme LEFT JOIN FETCH q.typeQuestion ORDER BY q.id", 
            Question.class);
        return query.getResultList();
    }
}
