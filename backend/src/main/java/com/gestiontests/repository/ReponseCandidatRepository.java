package com.gestiontests.repository;

import com.gestiontests.entity.ReponseCandidat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Named
@ApplicationScoped
public class ReponseCandidatRepository extends GenericRepository<ReponseCandidat, Integer> {
    
    public ReponseCandidatRepository() {
        super(ReponseCandidat.class);
    }
    
    public Optional<ReponseCandidat> findBySessionQuestion(Integer sessionQuestionId) {
        TypedQuery<ReponseCandidat> query = entityManager.createQuery(
            "SELECT rc FROM ReponseCandidat rc WHERE rc.sessionQuestion.id = :sessionQuestionId", 
            ReponseCandidat.class);
        query.setParameter("sessionQuestionId", sessionQuestionId);
        List<ReponseCandidat> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<ReponseCandidat> findAllBySessionQuestion(Integer sessionQuestionId) {
        TypedQuery<ReponseCandidat> query = entityManager.createQuery(
            "SELECT rc FROM ReponseCandidat rc WHERE rc.sessionQuestion.id = :sessionQuestionId ORDER BY rc.dateReponse",
            ReponseCandidat.class);
        query.setParameter("sessionQuestionId", sessionQuestionId);
        return query.getResultList();
    }

    public void deleteBySessionQuestion(Integer sessionQuestionId) {
        entityManager.createQuery(
            "DELETE FROM ReponseCandidat rc WHERE rc.sessionQuestion.id = :sessionQuestionId")
            .setParameter("sessionQuestionId", sessionQuestionId)
            .executeUpdate();
    }
    
    public List<ReponseCandidat> findBySession(Integer sessionId) {
        TypedQuery<ReponseCandidat> query = entityManager.createQuery(
            "SELECT rc FROM ReponseCandidat rc WHERE rc.sessionQuestion.sessionTest.id = :sessionId ORDER BY rc.dateReponse", 
            ReponseCandidat.class);
        query.setParameter("sessionId", sessionId);
        return query.getResultList();
    }
    
    public List<ReponseCandidat> findByCandidat(Integer candidatId) {
        TypedQuery<ReponseCandidat> query = entityManager.createQuery(
            "SELECT rc FROM ReponseCandidat rc WHERE rc.sessionQuestion.sessionTest.candidat.id = :candidatId ORDER BY rc.dateReponse DESC", 
            ReponseCandidat.class);
        query.setParameter("candidatId", candidatId);
        return query.getResultList();
    }
    
    public List<ReponseCandidat> findByEstCorrect(Boolean estCorrect) {
        TypedQuery<ReponseCandidat> query = entityManager.createQuery(
            "SELECT rc FROM ReponseCandidat rc WHERE rc.estCorrect = :estCorrect ORDER BY rc.dateReponse DESC", 
            ReponseCandidat.class);
        query.setParameter("estCorrect", estCorrect);
        return query.getResultList();
    }
    
    public List<ReponseCandidat> findByQuestion(Integer questionId) {
        TypedQuery<ReponseCandidat> query = entityManager.createQuery(
            "SELECT rc FROM ReponseCandidat rc WHERE rc.sessionQuestion.question.id = :questionId ORDER BY rc.dateReponse DESC", 
            ReponseCandidat.class);
        query.setParameter("questionId", questionId);
        return query.getResultList();
    }
    
    public List<ReponseCandidat> findByReponsePossible(Integer reponsePossibleId) {
        TypedQuery<ReponseCandidat> query = entityManager.createQuery(
            "SELECT rc FROM ReponseCandidat rc WHERE rc.reponsePossible.id = :reponsePossibleId ORDER BY rc.dateReponse DESC", 
            ReponseCandidat.class);
        query.setParameter("reponsePossibleId", reponsePossibleId);
        return query.getResultList();
    }
    
    public List<ReponseCandidat> findRecentReponses(int limit) {
        TypedQuery<ReponseCandidat> query = entityManager.createQuery(
            "SELECT rc FROM ReponseCandidat rc ORDER BY rc.dateReponse DESC", ReponseCandidat.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public long countBySession(Integer sessionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rc) FROM ReponseCandidat rc WHERE rc.sessionQuestion.sessionTest.id = :sessionId", Long.class);
        query.setParameter("sessionId", sessionId);
        return query.getSingleResult();
    }
    
    public long countByCandidat(Integer candidatId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rc) FROM ReponseCandidat rc WHERE rc.sessionQuestion.sessionTest.candidat.id = :candidatId", Long.class);
        query.setParameter("candidatId", candidatId);
        return query.getSingleResult();
    }
    
    public long countByEstCorrect(Boolean estCorrect) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rc) FROM ReponseCandidat rc WHERE rc.estCorrect = :estCorrect", Long.class);
        query.setParameter("estCorrect", estCorrect);
        return query.getSingleResult();
    }
    
    public long countCorrectBySession(Integer sessionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rc) FROM ReponseCandidat rc WHERE rc.sessionQuestion.sessionTest.id = :sessionId AND rc.estCorrect = true", Long.class);
        query.setParameter("sessionId", sessionId);
        return query.getSingleResult();
    }
    
    public long countIncorrectBySession(Integer sessionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rc) FROM ReponseCandidat rc WHERE rc.sessionQuestion.sessionTest.id = :sessionId AND rc.estCorrect = false", Long.class);
        query.setParameter("sessionId", sessionId);
        return query.getSingleResult();
    }
    
    public long countByQuestion(Integer questionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rc) FROM ReponseCandidat rc WHERE rc.sessionQuestion.question.id = :questionId", Long.class);
        query.setParameter("questionId", questionId);
        return query.getSingleResult();
    }
    
    public long countCorrectByQuestion(Integer questionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rc) FROM ReponseCandidat rc WHERE rc.sessionQuestion.question.id = :questionId AND rc.estCorrect = true", Long.class);
        query.setParameter("questionId", questionId);
        return query.getSingleResult();
    }
    
    public Double getAverageTempsReponse(Integer sessionId) {
        TypedQuery<Double> query = entityManager.createQuery(
            "SELECT AVG(rc.tempsReponse) FROM ReponseCandidat rc WHERE rc.sessionQuestion.sessionTest.id = :sessionId AND rc.tempsReponse IS NOT NULL", 
            Double.class);
        query.setParameter("sessionId", sessionId);
        return query.getSingleResult();
    }
    
    public Double getAverageTempsReponseByQuestion(Integer questionId) {
        TypedQuery<Double> query = entityManager.createQuery(
            "SELECT AVG(rc.tempsReponse) FROM ReponseCandidat rc WHERE rc.sessionQuestion.question.id = :questionId AND rc.tempsReponse IS NOT NULL", 
            Double.class);
        query.setParameter("questionId", questionId);
        return query.getSingleResult();
    }
    
    public List<ReponseCandidat> findByTempsReponseRange(Integer minTemps, Integer maxTemps) {
        TypedQuery<ReponseCandidat> query = entityManager.createQuery(
            "SELECT rc FROM ReponseCandidat rc WHERE rc.tempsReponse BETWEEN :minTemps AND :maxTemps ORDER BY rc.tempsReponse", 
            ReponseCandidat.class);
        query.setParameter("minTemps", minTemps);
        query.setParameter("maxTemps", maxTemps);
        return query.getResultList();
    }
    
    public List<ReponseCandidat> findTextResponses() {
        TypedQuery<ReponseCandidat> query = entityManager.createQuery(
            "SELECT rc FROM ReponseCandidat rc WHERE rc.reponseText IS NOT NULL ORDER BY rc.dateReponse DESC", 
            ReponseCandidat.class);
        return query.getResultList();
    }
    
    public List<ReponseCandidat> findChoiceResponses() {
        TypedQuery<ReponseCandidat> query = entityManager.createQuery(
            "SELECT rc FROM ReponseCandidat rc WHERE rc.reponsePossible IS NOT NULL ORDER BY rc.dateReponse DESC", 
            ReponseCandidat.class);
        return query.getResultList();
    }
    
    public boolean existsBySessionQuestion(Integer sessionQuestionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rc) FROM ReponseCandidat rc WHERE rc.sessionQuestion.id = :sessionQuestionId", Long.class);
        query.setParameter("sessionQuestionId", sessionQuestionId);
        return query.getSingleResult() > 0;
    }
}
