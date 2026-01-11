package com.cine.cinema.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "tarif_typesiege")
public class TarifTypesiege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idtarif;

    private Long idtypesiege;

    private BigDecimal prix;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdtarif() { return idtarif; }
    public void setIdtarif(Long idtarif) { this.idtarif = idtarif; }
    public Long getIdtypesiege() { return idtypesiege; }
    public void setIdtypesiege(Long idtypesiege) { this.idtypesiege = idtypesiege; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
}
