package com.gestiontests.repository;

import com.gestiontests.entity.SessionQuestion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Named
@ApplicationScoped
public class SessionQuestionRepository extends GenericRepository<SessionQuestion, Integer> {
    
    public SessionQuestionRepository() {
        super(SessionQuestion.class);
    }
    
    public List<SessionQuestion> findBySession(Integer sessionId) {
        TypedQuery<SessionQuestion> query = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId ORDER BY sq.ordreAffichage", 
            SessionQuestion.class);
        query.setParameter("sessionId", sessionId);
        return query.getResultList();
    }
    
    public Optional<SessionQuestion> findBySessionAndQuestion(Integer sessionId, Integer questionId) {
        TypedQuery<SessionQuestion> query = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId AND sq.question.id = :questionId", 
            SessionQuestion.class);
        query.setParameter("sessionId", sessionId);
        query.setParameter("questionId", questionId);
        List<SessionQuestion> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public Optional<SessionQuestion> findBySessionAndOrdre(Integer sessionId, Integer ordre) {
        TypedQuery<SessionQuestion> query = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId AND sq.ordreAffichage = :ordre", 
            SessionQuestion.class);
        query.setParameter("sessionId", sessionId);
        query.setParameter("ordre", ordre);
        List<SessionQuestion> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public Optional<SessionQuestion> findNextQuestion(Integer sessionId, Integer currentQuestionId) {
        TypedQuery<SessionQuestion> query = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId AND sq.question.id = :currentQuestionId", 
            SessionQuestion.class);
        query.setParameter("sessionId", sessionId);
        query.setParameter("currentQuestionId", currentQuestionId);
        List<SessionQuestion> results = query.getResultList();
        
        if (results.isEmpty()) {
            return Optional.empty();
        }
        
        SessionQuestion current = results.get(0);
        TypedQuery<SessionQuestion> nextQuery = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId AND sq.ordreAffichage > :ordre ORDER BY sq.ordreAffichage", 
            SessionQuestion.class);
        nextQuery.setParameter("sessionId", sessionId);
        nextQuery.setParameter("ordre", current.getOrdreAffichage());
        nextQuery.setMaxResults(1);
        
        List<SessionQuestion> nextResults = nextQuery.getResultList();
        return nextResults.isEmpty() ? Optional.empty() : Optional.of(nextResults.get(0));
    }
    
    public Optional<SessionQuestion> findPreviousQuestion(Integer sessionId, Integer currentQuestionId) {
        TypedQuery<SessionQuestion> query = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId AND sq.question.id = :currentQuestionId", 
            SessionQuestion.class);
        query.setParameter("sessionId", sessionId);
        query.setParameter("currentQuestionId", currentQuestionId);
        List<SessionQuestion> results = query.getResultList();
        
        if (results.isEmpty()) {
            return Optional.empty();
        }
        
        SessionQuestion current = results.get(0);
        TypedQuery<SessionQuestion> prevQuery = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId AND sq.ordreAffichage < :ordre ORDER BY sq.ordreAffichage DESC", 
            SessionQuestion.class);
        prevQuery.setParameter("sessionId", sessionId);
        prevQuery.setParameter("ordre", current.getOrdreAffichage());
        prevQuery.setMaxResults(1);
        
        List<SessionQuestion> prevResults = prevQuery.getResultList();
        return prevResults.isEmpty() ? Optional.empty() : Optional.of(prevResults.get(0));
    }
    
    public SessionQuestion getFirstQuestion(Integer sessionId) {
        TypedQuery<SessionQuestion> query = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId ORDER BY sq.ordreAffichage", 
            SessionQuestion.class);
        query.setParameter("sessionId", sessionId);
        query.setMaxResults(1);
        List<SessionQuestion> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public SessionQuestion getLastQuestion(Integer sessionId) {
        TypedQuery<SessionQuestion> query = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId ORDER BY sq.ordreAffichage DESC", 
            SessionQuestion.class);
        query.setParameter("sessionId", sessionId);
        query.setMaxResults(1);
        List<SessionQuestion> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public long countBySession(Integer sessionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(sq) FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId", Long.class);
        query.setParameter("sessionId", sessionId);
        return query.getSingleResult();
    }
    
    public List<SessionQuestion> findByQuestion(Integer questionId) {
        TypedQuery<SessionQuestion> query = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.question.id = :questionId ORDER BY sq.sessionTest.dateDebut DESC", 
            SessionQuestion.class);
        query.setParameter("questionId", questionId);
        return query.getResultList();
    }
    
    public List<SessionQuestion> findByTempsAlloue(Integer tempsMin, Integer tempsMax) {
        TypedQuery<SessionQuestion> query = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.tempsAlloue BETWEEN :tempsMin AND :tempsMax ORDER BY sq.tempsAlloue", 
            SessionQuestion.class);
        query.setParameter("tempsMin", tempsMin);
        query.setParameter("tempsMax", tempsMax);
        return query.getResultList();
    }
    
    public Integer getMaxOrdreBySession(Integer sessionId) {
        TypedQuery<Integer> query = entityManager.createQuery(
            "SELECT MAX(sq.ordreAffichage) FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId", 
            Integer.class);
        query.setParameter("sessionId", sessionId);
        Integer result = query.getSingleResult();
        return result != null ? result : 0;
    }
    
    public List<SessionQuestion> findUnansweredQuestions(Integer sessionId) {
        TypedQuery<SessionQuestion> query = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId AND NOT EXISTS (" +
            "SELECT rc FROM ReponseCandidat rc WHERE rc.sessionQuestion.id = sq.id) ORDER BY sq.ordreAffichage", 
            SessionQuestion.class);
        query.setParameter("sessionId", sessionId);
        return query.getResultList();
    }
    
    public List<SessionQuestion> findAnsweredQuestions(Integer sessionId) {
        TypedQuery<SessionQuestion> query = entityManager.createQuery(
            "SELECT sq FROM SessionQuestion sq WHERE sq.sessionTest.id = :sessionId AND EXISTS (" +
            "SELECT rc FROM ReponseCandidat rc WHERE rc.sessionQuestion.id = sq.id) ORDER BY sq.ordreAffichage", 
            SessionQuestion.class);
        query.setParameter("sessionId", sessionId);
        return query.getResultList();
    }
}
