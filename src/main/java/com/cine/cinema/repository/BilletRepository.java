package com.cine.cinema.repository;

import com.cine.cinema.model.Billet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BilletRepository extends JpaRepository<Billet, Long> {
    long countBySeance_Id(Long seanceId);
    List<Billet> findBySeance_Id(Long seanceId);
    List<Billet> findBySeance_IdAndSiege_IdIn(Long seanceId, List<Long> siegeIds);
}
