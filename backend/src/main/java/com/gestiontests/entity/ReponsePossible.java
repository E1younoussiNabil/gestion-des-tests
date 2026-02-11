package com.gestiontests.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "reponses_possibles")
public class ReponsePossible {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull(message = "La question est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_question", nullable = false, foreignKey = @ForeignKey(name = "fk_reponse_question"))
    private Question question;
    
    @NotBlank(message = "Le libellé de la réponse est obligatoire")
    @Size(max = 255, message = "Le libellé ne doit pas dépasser 255 caractères")
    @Column(name = "libelle", nullable = false, length = 255)
    private String libelle;
    
    @Column(name = "est_correct", nullable = false)
    private Boolean estCorrect = false;
    
    @OneToMany(mappedBy = "reponsePossible", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReponseCandidat> reponsesCandidat;
    
    // Constructeurs
    public ReponsePossible() {}
    
    public ReponsePossible(Question question, String libelle, Boolean estCorrect) {
        this.question = question;
        this.libelle = libelle;
        this.estCorrect = estCorrect;
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public Boolean getEstCorrect() {
        return estCorrect;
    }
    
    public void setEstCorrect(Boolean estCorrect) {
        this.estCorrect = estCorrect;
    }
    
    public List<ReponseCandidat> getReponsesCandidat() {
        return reponsesCandidat;
    }
    
    public void setReponsesCandidat(List<ReponseCandidat> reponsesCandidat) {
        this.reponsesCandidat = reponsesCandidat;
    }
    
    @Override
    public String toString() {
        return "ReponsePossible{" +
                "id=" + id +
                ", question=" + (question != null ? question.getId() : "null") +
                ", libelle='" + libelle + '\'' +
                ", estCorrect=" + estCorrect +
                '}';
    }
}
