package com.cine.cinema.repository;

import com.cine.cinema.model.ModePaiement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ModePaiementRepository extends JpaRepository<ModePaiement, Long> {
    List<ModePaiement> findAll();
}
