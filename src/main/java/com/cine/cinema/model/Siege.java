package com.cine.cinema.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "siege")
public class Siege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idsalle")
    private Salle salle;

    private String rangee;

    private int numero;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Salle getSalle() { return salle; }
    public void setSalle(Salle salle) { this.salle = salle; }
    public String getRangee() { return rangee; }
    public void setRangee(String rangee) { this.rangee = rangee; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
}
