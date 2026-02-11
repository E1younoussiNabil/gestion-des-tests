package com.gestiontests.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "session_questions")
public class SessionQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session", nullable = false, foreignKey = @ForeignKey(name = "fk_session_question_session"))
    private SessionTest sessionTest;
    
    @NotNull(message = "La question est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_question", nullable = false, foreignKey = @ForeignKey(name = "fk_session_question_question"))
    private Question question;
    
    @NotNull(message = "L'ordre d'affichage est obligatoire")
    @Column(name = "ordre_affichage", nullable = false)
    private Integer ordreAffichage;
    
    @Column(name = "temps_alloue")
    private Integer tempsAlloue = 120; // 2 minutes par défaut
    
    @OneToMany(mappedBy = "sessionQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReponseCandidat> reponsesCandidat;
    
    // Constructeurs
    public SessionQuestion() {}
    
    public SessionQuestion(SessionTest sessionTest, Question question, Integer ordreAffichage, Integer tempsAlloue) {
        this.sessionTest = sessionTest;
        this.question = question;
        this.ordreAffichage = ordreAffichage;
        this.tempsAlloue = tempsAlloue != null ? tempsAlloue : 120;
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public SessionTest getSessionTest() {
        return sessionTest;
    }
    
    public void setSessionTest(SessionTest sessionTest) {
        this.sessionTest = sessionTest;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public Integer getOrdreAffichage() {
        return ordreAffichage;
    }
    
    public void setOrdreAffichage(Integer ordreAffichage) {
        this.ordreAffichage = ordreAffichage;
    }
    
    public Integer getTempsAlloue() {
        return tempsAlloue;
    }
    
    public void setTempsAlloue(Integer tempsAlloue) {
        this.tempsAlloue = tempsAlloue;
    }
    
    public List<ReponseCandidat> getReponsesCandidat() {
        return reponsesCandidat;
    }
    
    public void setReponsesCandidat(List<ReponseCandidat> reponsesCandidat) {
        this.reponsesCandidat = reponsesCandidat;
    }
    
    @Override
    public String toString() {
        return "SessionQuestion{" +
                "id=" + id +
                ", sessionTest=" + (sessionTest != null ? sessionTest.getCodeSession() : "null") +
                ", question=" + (question != null ? question.getId() : "null") +
                ", ordreAffichage=" + ordreAffichage +
                ", tempsAlloue=" + tempsAlloue +
                '}';
    }
}
