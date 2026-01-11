package com.cine.cinema.repository;

import com.cine.cinema.model.Siege;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SiegeRepository extends JpaRepository<Siege, Long> {
    List<Siege> findBySalle_Id(Long salleId);
    List<Siege> findBySalle_IdOrderByRangeeAscNumeroAsc(Long salleId);
}
