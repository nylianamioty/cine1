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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billet")
public class Billet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idseance")
    private Seance seance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idsiege")
    private Siege siege;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idclient")
    private Client client;

    @Column(name = "idtarif_typesiege")
    private Long tarifTypesiegeId;

    @Column(name = "idcategorie_age")
    private Long categorieAgeId;

    private BigDecimal prix;

    private String statut = "EN_ATTENTE";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Seance getSeance() { return seance; }
    public void setSeance(Seance seance) { this.seance = seance; }
    public Siege getSiege() { return siege; }
    public void setSiege(Siege siege) { this.siege = siege; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public Long getTarifTypesiegeId() { return tarifTypesiegeId; }
    public void setTarifTypesiegeId(Long tarifTypesiegeId) { this.tarifTypesiegeId = tarifTypesiegeId; }
    public Long getCategorieAgeId() { return categorieAgeId; }
    public void setCategorieAgeId(Long categorieAgeId) { this.categorieAgeId = categorieAgeId; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
