package com.gestiontests.service;

import com.gestiontests.entity.CreneauHoraire;
import com.gestiontests.repository.CreneauHoraireRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CreneauHoraireService {
    
    @Inject
    private CreneauHoraireRepository creneauHoraireRepository;
    
    @Transactional
    public CreneauHoraire createCreneau(CreneauHoraire creneau) {
        return creneauHoraireRepository.create(creneau);
    }
    
    @Transactional
    public CreneauHoraire updateCreneau(CreneauHoraire creneau) {
        return creneauHoraireRepository.update(creneau);
    }
    
    @Transactional
    public void deleteCreneau(Integer creneauId) {
        creneauHoraireRepository.deleteById(creneauId);
    }
    
    public Optional<CreneauHoraire> findById(Integer id) {
        return creneauHoraireRepository.findById(id);
    }
    
    public List<CreneauHoraire> findAll() {
        return creneauHoraireRepository.findAll();
    }
    
    public List<CreneauHoraire> findByDateExam(LocalDate dateExam) {
        return creneauHoraireRepository.findByDateExam(dateExam);
    }
    
    public List<CreneauHoraire> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return creneauHoraireRepository.findByDateBetween(startDate, endDate);
    }
    
    public List<CreneauHoraire> findAvailableCreneaux() {
        return creneauHoraireRepository.findAvailableCreneaux();
    }
    
    public List<CreneauHoraire> findFutureCreneaux() {
        return creneauHoraireRepository.findFutureCreneaux();
    }
    
    public List<CreneauHoraire> findPastCreneaux() {
        return creneauHoraireRepository.findPastCreneaux();
    }
    
    public List<CreneauHoraire> findCreneauxInNextDays(int days) {
        return creneauHoraireRepository.findCreneauxInNextDays(days);
    }
    
    public Optional<CreneauHoraire> findNextAvailable() {
        return creneauHoraireRepository.findNextAvailable();
    }
    
    @Transactional
    public void updateDisponibiliteCreneau(Integer creneauId) {
        Optional<CreneauHoraire> creneauOpt = creneauHoraireRepository.findById(creneauId);
        if (creneauOpt.isEmpty()) {
            throw new IllegalArgumentException("Créneau non trouvé");
        }
        
        CreneauHoraire creneau = creneauOpt.get();

        Integer places = creneau.getPlacesDisponibles();
        if (places == null) {
            places = 0;
            creneau.setPlacesDisponibles(0);
        }
        creneau.setEstComplet(places <= 0);
        
        creneauHoraireRepository.update(creneau);
    }
    
    public boolean isCreneauAvailable(Integer creneauId) {
        return creneauHoraireRepository.isCreneauAvailable(creneauId);
    }
    
    public long countAvailableCreneaux() {
        return creneauHoraireRepository.countAvailableCreneaux();
    }
    
    @Transactional
    public CreneauHoraire createCreneauWithDefaultValues(LocalDate dateExam, String heureDebut, Integer dureeMinutes, Integer placesDisponibles) {
        CreneauHoraire creneau = new CreneauHoraire();
        creneau.setDateExam(dateExam);
        creneau.setHeureDebut(java.time.LocalTime.parse(heureDebut));
        creneau.setDureeMinutes(dureeMinutes);
        creneau.setPlacesDisponibles(placesDisponibles);
        creneau.setEstComplet(false);
        
        return creneauHoraireRepository.create(creneau);
    }
    
    public List<CreneauHoraire> getCreneauxForToday() {
        return creneauHoraireRepository.findByDateExam(LocalDate.now());
    }
    
    public List<CreneauHoraire> getCreneauxForWeek() {
        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.plusDays(7);
        return creneauHoraireRepository.findByDateBetween(today, endOfWeek);
    }
    
    public boolean isCreneauInPast(Integer creneauId) {
        Optional<CreneauHoraire> creneauOpt = creneauHoraireRepository.findById(creneauId);
        if (creneauOpt.isEmpty()) {
            return true;
        }
        
        CreneauHoraire creneau = creneauOpt.get();
        LocalDateTime finCreneau = LocalDateTime.of(creneau.getDateExam(), creneau.getHeureFin());
        return LocalDateTime.now().isAfter(finCreneau);
    }
    
    public boolean isCreneauInProgress(Integer creneauId) {
        Optional<CreneauHoraire> creneauOpt = creneauHoraireRepository.findById(creneauId);
        if (creneauOpt.isEmpty()) {
            return false;
        }
        
        CreneauHoraire creneau = creneauOpt.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime debutCreneau = LocalDateTime.of(creneau.getDateExam(), creneau.getHeureDebut());
        LocalDateTime finCreneau = LocalDateTime.of(creneau.getDateExam(), creneau.getHeureFin());
        
        return now.isAfter(debutCreneau) && now.isBefore(finCreneau);
    }
    
    public boolean canStartTest(Integer creneauId) {
        Optional<CreneauHoraire> creneauOpt = creneauHoraireRepository.findById(creneauId);
        if (creneauOpt.isEmpty()) {
            return false;
        }
        
        CreneauHoraire creneau = creneauOpt.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime debutCreneau = LocalDateTime.of(creneau.getDateExam(), creneau.getHeureDebut());
        
        // Peut commencer le test si l'heure de début est atteinte ou dans la prochaine heure
        return now.isAfter(debutCreneau.minusHours(1)) && now.isBefore(debutCreneau.plusHours(1));
    }
}
