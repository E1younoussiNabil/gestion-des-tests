package com.gestiontests.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "candidats")
public class Candidat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;
    
    @NotBlank(message = "L'école est obligatoire")
    @Size(max = 100, message = "Le nom de l'école ne doit pas dépasser 100 caractères")
    @Column(name = "ecole", nullable = false, length = 100)
    private String ecole;
    
    @Size(max = 100, message = "La filière ne doit pas dépasser 100 caractères")
    @Column(name = "filiere", length = 100)
    private String filiere;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;
    
    @NotBlank(message = "Le GSM est obligatoire")
    @Size(max = 20, message = "Le GSM ne doit pas dépasser 20 caractères")
    @Column(name = "gsm", nullable = false, length = 20)
    private String gsm;
    
    @Column(name = "code_session", unique = true, length = 10)
    private String codeSession;
    
    @Column(name = "est_valide", nullable = false)
    private Boolean estValide = false;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Inscription> inscriptions;
    
    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SessionTest> sessionsTest;
    
    // Constructeurs
    public Candidat() {}
    
    public Candidat(String nom, String prenom, String ecole, String filiere, String email, String gsm) {
        this.nom = nom;
        this.prenom = prenom;
        this.ecole = ecole;
        this.filiere = filiere;
        this.email = email;
        this.gsm = gsm;
        this.createdAt = LocalDateTime.now();
        this.estValide = false;
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getEcole() {
        return ecole;
    }
    
    public void setEcole(String ecole) {
        this.ecole = ecole;
    }
    
    public String getFiliere() {
        return filiere;
    }
    
    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getGsm() {
        return gsm;
    }
    
    public void setGsm(String gsm) {
        this.gsm = gsm;
    }
    
    public String getCodeSession() {
        return codeSession;
    }
    
    public void setCodeSession(String codeSession) {
        this.codeSession = codeSession;
    }
    
    public Boolean getEstValide() {
        return estValide;
    }
    
    public void setEstValide(Boolean estValide) {
        this.estValide = estValide;
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
    }
    
    @Override
    public String toString() {
        return "Candidat{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", ecole='" + ecole + '\'' +
                ", filiere='" + filiere + '\'' +
                ", email='" + email + '\'' +
                ", gsm='" + gsm + '\'' +
                ", codeSession='" + codeSession + '\'' +
                ", estValide=" + estValide +
                ", createdAt=" + createdAt +
                '}';
    }
}
