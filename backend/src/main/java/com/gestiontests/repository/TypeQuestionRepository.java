package com.gestiontests.repository;

import com.gestiontests.entity.TypeQuestion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Named
@ApplicationScoped
public class TypeQuestionRepository extends GenericRepository<TypeQuestion, Integer> {
    
    public TypeQuestionRepository() {
        super(TypeQuestion.class);
    }
    
    public Optional<TypeQuestion> findByNom(String nom) {
        TypedQuery<TypeQuestion> query = entityManager.createQuery(
            "SELECT tq FROM TypeQuestion tq WHERE tq.nom = :nom", TypeQuestion.class);
        query.setParameter("nom", nom);
        List<TypeQuestion> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<TypeQuestion> findAllOrderByNom() {
        TypedQuery<TypeQuestion> query = entityManager.createQuery(
            "SELECT tq FROM TypeQuestion tq ORDER BY tq.nom", TypeQuestion.class);
        return query.getResultList();
    }
    
    public boolean existsByNom(String nom) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(tq) FROM TypeQuestion tq WHERE tq.nom = :nom", Long.class);
        query.setParameter("nom", nom);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public TypeQuestion create(TypeQuestion typeQuestion) {
        if (existsByNom(typeQuestion.getNom())) {
            throw new IllegalArgumentException("Un type de question avec ce nom existe déjà: " + typeQuestion.getNom());
        }
        return super.create(typeQuestion);
    }
    
    public long countTypesWithQuestions() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(DISTINCT tq) FROM TypeQuestion tq JOIN tq.questions q", Long.class);
        return query.getSingleResult();
    }
    
    public List<TypeQuestion> findTypesWithQuestions() {
        TypedQuery<TypeQuestion> query = entityManager.createQuery(
            "SELECT DISTINCT tq FROM TypeQuestion tq JOIN tq.questions q", TypeQuestion.class);
        return query.getResultList();
    }
    
    public List<TypeQuestion> findTypesWithoutQuestions() {
        TypedQuery<TypeQuestion> query = entityManager.createQuery(
            "SELECT tq FROM TypeQuestion tq WHERE NOT EXISTS (SELECT q FROM Question q WHERE q.typeQuestion.id = tq.id)", 
            TypeQuestion.class);
        return query.getResultList();
    }
}
