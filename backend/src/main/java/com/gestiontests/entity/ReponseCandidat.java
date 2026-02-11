package com.gestiontests.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "reponses_candidat")
public class ReponseCandidat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session_question", foreignKey = @ForeignKey(name = "fk_reponse_session_question"))
    private SessionQuestion sessionQuestion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reponse_possible", foreignKey = @ForeignKey(name = "fk_reponse_possible"))
    private ReponsePossible reponsePossible;
    
    @Size(max = 255, message = "La réponse textuelle ne doit pas dépasser 255 caractères")
    @Column(name = "reponse_text", length = 255)
    private String reponseText;
    
    @Column(name = "temps_reponse")
    private Integer tempsReponse; // en secondes
    
    @Column(name = "date_reponse", updatable = false)
    private LocalDateTime dateReponse;
    
    @Column(name = "est_correct", nullable = false)
    private Boolean estCorrect = false;
    
    // Constructeurs
    public ReponseCandidat() {}
    
    public ReponseCandidat(SessionQuestion sessionQuestion, ReponsePossible reponsePossible, Integer tempsReponse) {
        this.sessionQuestion = sessionQuestion;
        this.reponsePossible = reponsePossible;
        this.tempsReponse = tempsReponse;
        this.dateReponse = LocalDateTime.now();
        // Calculer si la réponse est correcte
        if (reponsePossible != null) {
            this.estCorrect = reponsePossible.getEstCorrect();
        }
    }
    
    public ReponseCandidat(SessionQuestion sessionQuestion, String reponseText, Integer tempsReponse) {
        this.sessionQuestion = sessionQuestion;
        this.reponseText = reponseText;
        this.tempsReponse = tempsReponse;
        this.dateReponse = LocalDateTime.now();
        this.estCorrect = false; // Pour les réponses textuelles, la validation se fera manuellement
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public SessionQuestion getSessionQuestion() {
        return sessionQuestion;
    }
    
    public void setSessionQuestion(SessionQuestion sessionQuestion) {
        this.sessionQuestion = sessionQuestion;
    }
    
    public ReponsePossible getReponsePossible() {
        return reponsePossible;
    }
    
    public void setReponsePossible(ReponsePossible reponsePossible) {
        this.reponsePossible = reponsePossible;
        // Recalculer si la réponse est correcte
        if (reponsePossible != null) {
            this.estCorrect = reponsePossible.getEstCorrect();
        }
    }
    
    public String getReponseText() {
        return reponseText;
    }
    
    public void setReponseText(String reponseText) {
        this.reponseText = reponseText;
    }
    
    public Integer getTempsReponse() {
        return tempsReponse;
    }
    
    public void setTempsReponse(Integer tempsReponse) {
        this.tempsReponse = tempsReponse;
    }
    
    public LocalDateTime getDateReponse() {
        return dateReponse;
    }
    
    public void setDateReponse(LocalDateTime dateReponse) {
        this.dateReponse = dateReponse;
    }
    
    public Boolean getEstCorrect() {
        return estCorrect;
    }
    
    public void setEstCorrect(Boolean estCorrect) {
        this.estCorrect = estCorrect;
    }
    
    @PrePersist
    protected void onCreate() {
        dateReponse = LocalDateTime.now();
        // Calculer automatiquement si la réponse est correcte
        if (reponsePossible != null) {
            estCorrect = reponsePossible.getEstCorrect();
        }
    }
    
    @Override
    public String toString() {
        return "ReponseCandidat{" +
                "id=" + id +
                ", sessionQuestion=" + (sessionQuestion != null ? sessionQuestion.getId() : "null") +
                ", reponsePossible=" + (reponsePossible != null ? reponsePossible.getId() : "null") +
                ", reponseText='" + reponseText + '\'' +
                ", tempsReponse=" + tempsReponse +
                ", dateReponse=" + dateReponse +
                ", estCorrect=" + estCorrect +
                '}';
    }
}
