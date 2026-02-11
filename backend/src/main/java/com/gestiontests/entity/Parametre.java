package com.gestiontests.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "parametres")
public class Parametre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank(message = "Le nom du paramètre est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Column(name = "nom_param", nullable = false, unique = true, length = 100)
    private String nomParam;
    
    @NotBlank(message = "La valeur du paramètre est obligatoire")
    @Size(max = 255, message = "La valeur ne doit pas dépasser 255 caractères")
    @Column(name = "valeur", nullable = false, length = 255)
    private String valeur;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public Parametre() {}
    
    public Parametre(String nomParam, String valeur, String description) {
        this.nomParam = nomParam;
        this.valeur = valeur;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNomParam() {
        return nomParam;
    }
    
    public void setNomParam(String nomParam) {
        this.nomParam = nomParam;
    }
    
    public String getValeur() {
        return valeur;
    }
    
    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Méthodes utilitaires pour convertir la valeur
    public Integer getValeurAsInteger() {
        try {
            return Integer.parseInt(valeur);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public Boolean getValeurAsBoolean() {
        return Boolean.parseBoolean(valeur);
    }
    
    public Long getValeurAsLong() {
        try {
            return Long.parseLong(valeur);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public Double getValeurAsDouble() {
        try {
            return Double.parseDouble(valeur);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return "Parametre{" +
                "id=" + id +
                ", nomParam='" + nomParam + '\'' +
                ", valeur='" + valeur + '\'' +
                ", description='" + description + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
