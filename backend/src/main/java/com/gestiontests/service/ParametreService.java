package com.gestiontests.service;

import com.gestiontests.entity.Parametre;
import com.gestiontests.repository.ParametreRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ParametreService {
    
    @Inject
    private ParametreRepository parametreRepository;
    
    @Transactional
    public Parametre createParametre(Parametre parametre) {
        return parametreRepository.create(parametre);
    }
    
    @Transactional
    public Parametre updateParametre(Parametre parametre) {
        return parametreRepository.update(parametre);
    }
    
    @Transactional
    public Parametre updateParametre(String nomParam, String valeur) throws Exception {
        return parametreRepository.updateByNomParam(nomParam, valeur);
    }
    
    @Transactional
    public void deleteParametre(Integer id) {
        parametreRepository.deleteById(id);
    }
    
    public Optional<Parametre> findById(Integer id) {
        return parametreRepository.findById(id);
    }
    
    public Optional<Parametre> findByNomParam(String nomParam) {
        return parametreRepository.findByNomParam(nomParam);
    }
    
    public List<Parametre> findAll() {
        return parametreRepository.findAllOrderByNomParam();
    }
    
    public String getValeurParametre(String nomParam, String valeurDefaut) {
        return parametreRepository.getValeurParametre(nomParam, valeurDefaut);
    }
    
    public Integer getValeurParametreAsInteger(String nomParam, Integer valeurDefaut) {
        return parametreRepository.getValeurParametreAsInteger(nomParam, valeurDefaut);
    }
    
    public Boolean getValeurParametreAsBoolean(String nomParam, Boolean valeurDefaut) {
        return parametreRepository.getValeurParametreAsBoolean(nomParam, valeurDefaut);
    }
    
    public Long getValeurParametreAsLong(String nomParam, Long valeurDefaut) {
        return parametreRepository.getValeurParametreAsLong(nomParam, valeurDefaut);
    }
    
    public Double getValeurParametreAsDouble(String nomParam, Double valeurDefaut) {
        return parametreRepository.getValeurParametreAsDouble(nomParam, valeurDefaut);
    }
    
    public long count() {
        return parametreRepository.count();
    }
}
