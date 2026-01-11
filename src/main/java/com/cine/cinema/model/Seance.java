package com.cine.cinema.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "seance")
public class Seance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfilm")
    private Film film;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idsalle")
    private Salle salle;

    @Column(name = "date_heure_debut")
    private LocalDateTime dateHeureDebut;

    private String version;

    private java.math.BigDecimal prix;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Film getFilm() { return film; }
    public void setFilm(Film film) { this.film = film; }
    public Salle getSalle() { return salle; }
    public void setSalle(Salle salle) { this.salle = salle; }
    public LocalDateTime getDateHeureDebut() { return dateHeureDebut; }
    public void setDateHeureDebut(LocalDateTime dateHeureDebut) { this.dateHeureDebut = dateHeureDebut; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public java.math.BigDecimal getPrix() { return prix; }
    public void setPrix(java.math.BigDecimal prix) { this.prix = prix; }

    @Override
    public String toString() {
        String salleInfo = (salle != null) ? ("Salle{" + "id=" + salle.getId() + ", num=" + salle.getNum() + "}") : "Salle{null}";
        return "Seance{" + "id=" + id + ", dateHeureDebut=" + dateHeureDebut + ", " + salleInfo + ", prix=" + prix + '}';
    }
}
