package com.gestiontests.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sessions_test")
public class SessionTest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull(message = "Le candidat est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_candidat", nullable = false, foreignKey = @ForeignKey(name = "fk_session_candidat"))
    private Candidat candidat;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_creneau", foreignKey = @ForeignKey(name = "fk_session_creneau"))
    private CreneauHoraire creneau;
    
    @NotBlank(message = "Le code de session est obligatoire")
    @Column(name = "code_session", nullable = false, length = 10)
    private String codeSession;
    
    @Column(name = "date_debut")
    private LocalDateTime dateDebut;
    
    @Column(name = "date_fin")
    private LocalDateTime dateFin;
    
    @Column(name = "est_termine", nullable = false)
    private Boolean estTermine = false;
    
    @Column(name = "score_total", nullable = false)
    private Integer scoreTotal = 0;
    
    @Column(name = "score_max", nullable = false)
    private Integer scoreMax = 0;
    
    @Column(name = "pourcentage", precision = 5, scale = 2)
    private BigDecimal pourcentage = BigDecimal.ZERO;
    
    @OneToMany(mappedBy = "sessionTest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SessionQuestion> sessionQuestions;
    
    // Constructeurs
    public SessionTest() {}
    
    public SessionTest(Candidat candidat, CreneauHoraire creneau, String codeSession) {
        this.candidat = candidat;
        this.creneau = creneau;
        this.codeSession = codeSession;
        this.estTermine = false;
        this.scoreTotal = 0;
        this.scoreMax = 0;
        this.pourcentage = BigDecimal.ZERO;
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Candidat getCandidat() {
        return candidat;
    }
    
    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }
    
    public CreneauHoraire getCreneau() {
        return creneau;
    }
    
    public void setCreneau(CreneauHoraire creneau) {
        this.creneau = creneau;
    }
    
    public String getCodeSession() {
        return codeSession;
    }
    
    public void setCodeSession(String codeSession) {
        this.codeSession = codeSession;
    }
    
    public LocalDateTime getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDateTime getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }
    
    public Boolean getEstTermine() {
        return estTermine;
    }
    
    public void setEstTermine(Boolean estTermine) {
        this.estTermine = estTermine;
    }
    
    public Integer getScoreTotal() {
        return scoreTotal;
    }
    
    public void setScoreTotal(Integer scoreTotal) {
        this.scoreTotal = scoreTotal;
        // Recalculer le pourcentage automatiquement
        calculerPourcentage();
    }
    
    public Integer getScoreMax() {
        return scoreMax;
    }
    
    public void setScoreMax(Integer scoreMax) {
        this.scoreMax = scoreMax;
        // Recalculer le pourcentage automatiquement
        calculerPourcentage();
    }
    
    public BigDecimal getPourcentage() {
        return pourcentage;
    }
    
    public void setPourcentage(BigDecimal pourcentage) {
        this.pourcentage = pourcentage;
    }
    
    public List<SessionQuestion> getSessionQuestions() {
        return sessionQuestions;
    }
    
    public void setSessionQuestions(List<SessionQuestion> sessionQuestions) {
        this.sessionQuestions = sessionQuestions;
    }
    
    /**
     * Calcule le pourcentage de réussite
     */
    private void calculerPourcentage() {
        if (scoreMax != null && scoreMax > 0) {
            double pourcentageValue = (double) scoreTotal / scoreMax * 100;
            this.pourcentage = BigDecimal.valueOf(pourcentageValue).setScale(2, RoundingMode.HALF_UP);
        } else {
            this.pourcentage = BigDecimal.ZERO;
        }
    }
    
    /**
     * Démarre la session de test
     */
    public void demarrerSession() {
        this.dateDebut = LocalDateTime.now();
        this.estTermine = false;
    }
    
    /**
     * Termine la session de test
     */
    public void terminerSession() {
        this.dateFin = LocalDateTime.now();
        this.estTermine = true;
    }
    
    @Override
    public String toString() {
        return "SessionTest{" +
                "id=" + id +
                ", candidat=" + (candidat != null ? candidat.getNom() + " " + candidat.getPrenom() : "null") +
                ", codeSession='" + codeSession + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", estTermine=" + estTermine +
                ", scoreTotal=" + scoreTotal +
                ", scoreMax=" + scoreMax +
                ", pourcentage=" + pourcentage +
                '}';
    }
}
