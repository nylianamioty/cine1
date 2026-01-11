package com.cine.cinema.repository;

import com.cine.cinema.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FilmRepository extends JpaRepository<Film, Long> {
    Optional<Film> findByTitre(String titre);
}
