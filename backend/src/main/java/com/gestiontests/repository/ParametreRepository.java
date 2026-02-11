package com.gestiontests.repository;

import com.gestiontests.entity.Parametre;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Named
@ApplicationScoped
public class ParametreRepository extends GenericRepository<Parametre, Integer> {
    
    public ParametreRepository() {
        super(Parametre.class);
    }
    
    public Optional<Parametre> findByNomParam(String nomParam) {
        TypedQuery<Parametre> query = entityManager.createQuery(
            "SELECT p FROM Parametre p WHERE p.nomParam = :nomParam", Parametre.class);
        query.setParameter("nomParam", nomParam);
        List<Parametre> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<Parametre> findAllOrderByNomParam() {
        TypedQuery<Parametre> query = entityManager.createQuery(
            "SELECT p FROM Parametre p ORDER BY p.nomParam", Parametre.class);
        return query.getResultList();
    }
    
    public boolean existsByNomParam(String nomParam) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Parametre p WHERE p.nomParam = :nomParam", Long.class);
        query.setParameter("nomParam", nomParam);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public Parametre create(Parametre parametre) {
        if (existsByNomParam(parametre.getNomParam())) {
            throw new IllegalArgumentException("Un paramètre avec ce nom existe déjà: " + parametre.getNomParam());
        }
        return super.create(parametre);
    }
    
    public Parametre updateByNomParam(String nomParam, String nouvelleValeur) {
        Optional<Parametre> paramOpt = findByNomParam(nomParam);
        if (paramOpt.isEmpty()) {
            throw new IllegalArgumentException("Paramètre non trouvé: " + nomParam);
        }
        
        Parametre parametre = paramOpt.get();
        parametre.setValeur(nouvelleValeur);
        return update(parametre);
    }
    
    public String getValeurParametre(String nomParam, String valeurDefaut) {
        Optional<Parametre> paramOpt = findByNomParam(nomParam);
        return paramOpt.map(Parametre::getValeur).orElse(valeurDefaut);
    }
    
    public Integer getValeurParametreAsInteger(String nomParam, Integer valeurDefaut) {
        Optional<Parametre> paramOpt = findByNomParam(nomParam);
        return paramOpt.map(Parametre::getValeurAsInteger).orElse(valeurDefaut);
    }
    
    public Boolean getValeurParametreAsBoolean(String nomParam, Boolean valeurDefaut) {
        Optional<Parametre> paramOpt = findByNomParam(nomParam);
        return paramOpt.map(Parametre::getValeurAsBoolean).orElse(valeurDefaut);
    }
    
    public Long getValeurParametreAsLong(String nomParam, Long valeurDefaut) {
        Optional<Parametre> paramOpt = findByNomParam(nomParam);
        return paramOpt.map(Parametre::getValeurAsLong).orElse(valeurDefaut);
    }
    
    public Double getValeurParametreAsDouble(String nomParam, Double valeurDefaut) {
        Optional<Parametre> paramOpt = findByNomParam(nomParam);
        return paramOpt.map(Parametre::getValeurAsDouble).orElse(valeurDefaut);
    }
}
