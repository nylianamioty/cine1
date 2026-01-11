package com.cine.cinema.repository;

import com.cine.cinema.model.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeanceRepository extends JpaRepository<Seance, Long> {
    Optional<Seance> findByFilm_TitreAndDateHeureDebutAndSalle_Id(String titre, LocalDateTime dateHeureDebut, Long salleId);
    List<Seance> findByFilm_Id(Long filmId);

    @Query(value = "SELECT * FROM seance WHERE idfilm = :filmId", nativeQuery = true)
    List<Seance> findByFilmIdNative(@Param("filmId") Long filmId);
}
