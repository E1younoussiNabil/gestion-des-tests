package com.gestiontests.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "questions")
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull(message = "Le thème est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_theme", nullable = false, foreignKey = @ForeignKey(name = "fk_question_theme"))
    private Theme theme;
    
    @NotNull(message = "Le type de question est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_question", nullable = false, foreignKey = @ForeignKey(name = "fk_question_type"))
    private TypeQuestion typeQuestion;
    
    @NotBlank(message = "Le libellé de la question est obligatoire")
    @Column(name = "libelle", nullable = false, columnDefinition = "TEXT")
    private String libelle;
    
    @Column(name = "explication", columnDefinition = "TEXT")
    private String explication;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ReponsePossible> reponsesPossibles;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SessionQuestion> sessionQuestions;
    
    // Constructeurs
    public Question() {}
    
    public Question(Theme theme, TypeQuestion typeQuestion, String libelle, String explication) {
        this.theme = theme;
        this.typeQuestion = typeQuestion;
        this.libelle = libelle;
        this.explication = explication;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Theme getTheme() {
        return theme;
    }
    
    public void setTheme(Theme theme) {
        this.theme = theme;
    }
    
    public TypeQuestion getTypeQuestion() {
        return typeQuestion;
    }
    
    public void setTypeQuestion(TypeQuestion typeQuestion) {
        this.typeQuestion = typeQuestion;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public String getExplication() {
        return explication;
    }
    
    public void setExplication(String explication) {
        this.explication = explication;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<ReponsePossible> getReponsesPossibles() {
        return reponsesPossibles;
    }
    
    public void setReponsesPossibles(List<ReponsePossible> reponsesPossibles) {
        this.reponsesPossibles = reponsesPossibles;
    }
    
    public List<SessionQuestion> getSessionQuestions() {
        return sessionQuestions;
    }
    
    public void setSessionQuestions(List<SessionQuestion> sessionQuestions) {
        this.sessionQuestions = sessionQuestions;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", theme=" + (theme != null ? theme.getNom() : "null") +
                ", typeQuestion=" + (typeQuestion != null ? typeQuestion.getNom() : "null") +
                ", libelle='" + libelle + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
