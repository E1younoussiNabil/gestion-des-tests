package com.gestiontests.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "creneaux_horaires")
public class CreneauHoraire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull(message = "La date d'examen est obligatoire")
    @Column(name = "date_exam", nullable = false)
    private LocalDate dateExam;
    
    @NotNull(message = "L'heure de début est obligatoire")
    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;
    
    @NotNull(message = "L'heure de fin est obligatoire")
    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;
    
    @NotNull(message = "La durée est obligatoire")
    @Column(name = "duree_minutes", nullable = false)
    private Integer dureeMinutes;

    @Column(name = "nombre_questions")
    private Integer nombreQuestions;
    
    @Column(name = "places_disponibles", nullable = false)
    private Integer placesDisponibles = 1;
    
    @Column(name = "est_complet", nullable = false)
    private Boolean estComplet = false;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "creneau", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Inscription> inscriptions;
    
    @OneToMany(mappedBy = "creneau", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SessionTest> sessionsTest;
    
    // Constructeurs
    public CreneauHoraire() {}
    
    public CreneauHoraire(LocalDate dateExam, LocalTime heureDebut, Integer dureeMinutes, Integer placesDisponibles) {
        this.dateExam = dateExam;
        this.heureDebut = heureDebut;
        this.dureeMinutes = dureeMinutes;
        this.placesDisponibles = placesDisponibles;
        this.heureFin = heureDebut.plusMinutes(dureeMinutes);
        this.estComplet = false;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public LocalDate getDateExam() {
        return dateExam;
    }
    
    public void setDateExam(LocalDate dateExam) {
        this.dateExam = dateExam;
    }
    
    public LocalTime getHeureDebut() {
        return heureDebut;
    }
    
    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
        // Recalculer l'heure de fin automatiquement
        if (dureeMinutes != null) {
            this.heureFin = heureDebut.plusMinutes(dureeMinutes);
        }
    }
    
    public LocalTime getHeureFin() {
        return heureFin;
    }
    
    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
        // Recalculer la durée automatiquement
        if (heureDebut != null) {
            this.dureeMinutes = (int) java.time.Duration.between(heureDebut, heureFin).toMinutes();
        }
    }
    
    public Integer getDureeMinutes() {
        return dureeMinutes;
    }
    
    public void setDureeMinutes(Integer dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
        // Recalculer l'heure de fin automatiquement
        if (heureDebut != null) {
            this.heureFin = heureDebut.plusMinutes(dureeMinutes);
        }
    }

    public Integer getNombreQuestions() {
        return nombreQuestions;
    }

    public void setNombreQuestions(Integer nombreQuestions) {
        this.nombreQuestions = nombreQuestions;
    }
    
    public Integer getPlacesDisponibles() {
        return placesDisponibles;
    }
    
    public void setPlacesDisponibles(Integer placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }
    
    public Boolean getEstComplet() {
        return estComplet;
    }
    
    public void setEstComplet(Boolean estComplet) {
        this.estComplet = estComplet;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<Inscription> getInscriptions() {
        return inscriptions;
    }
    
    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }
    
    public List<SessionTest> getSessionsTest() {
        return sessionsTest;
    }
    
    public void setSessionsTest(List<SessionTest> sessionsTest) {
        this.sessionsTest = sessionsTest;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // S'assurer que l'heure de fin est correcte
        if (heureDebut != null && dureeMinutes != null) {
            heureFin = heureDebut.plusMinutes(dureeMinutes);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        // S'assurer que l'heure de fin est correcte lors de la mise à jour
        if (heureDebut != null && dureeMinutes != null) {
            heureFin = heureDebut.plusMinutes(dureeMinutes);
        }
    }
    
    @Override
    public String toString() {
        return "CreneauHoraire{" +
                "id=" + id +
                ", dateExam=" + dateExam +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                ", dureeMinutes=" + dureeMinutes +
                ", nombreQuestions=" + nombreQuestions +
                ", placesDisponibles=" + placesDisponibles +
                ", estComplet=" + estComplet +
                ", createdAt=" + createdAt +
                '}';
    }
}
