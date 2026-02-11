package com.gestiontests.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class GenericRepository<T, ID extends Serializable> {
    
    @PersistenceContext
    protected EntityManager entityManager;
    
    private Class<T> entityClass;
    
    public GenericRepository() {
        // Le type réel sera déterminé par les sous-classes
    }
    
    public GenericRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    protected Class<T> getEntityClass() {
        return entityClass;
    }
    
    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    public T create(T entity) {
        entityManager.persist(entity);
        return entity;
    }
    
    public T update(T entity) {
        return entityManager.merge(entity);
    }
    
    public void delete(T entity) {
        if (!entityManager.contains(entity)) {
            entity = entityManager.find(entityClass, entity);
        }
        entityManager.remove(entity);
    }
    
    public void deleteById(ID id) {
        T entity = findById(id).orElse(null);
        if (entity != null) {
            delete(entity);
        }
    }
    
    public Optional<T> findById(ID id) {
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }
    
    public List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);
        TypedQuery<T> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
    
    public List<T> findWithPagination(int firstResult, int maxResults) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);
        TypedQuery<T> query = entityManager.createQuery(cq);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }
    
    public long count() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);
        cq.select(cb.count(root));
        TypedQuery<Long> query = entityManager.createQuery(cq);
        return query.getSingleResult();
    }
    
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }
    
    public void flush() {
        entityManager.flush();
    }
    
    public void clear() {
        entityManager.clear();
    }
}
