package com.cine.cinema.web;

import com.cine.cinema.dto.StatsEntry;
import com.cine.cinema.model.Seance;
import com.cine.cinema.repository.SeanceRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class StatsController {

    private final SeanceRepository seanceRepository;
    private final JdbcTemplate jdbcTemplate;

    public StatsController(SeanceRepository seanceRepository, JdbcTemplate jdbcTemplate) {
        this.seanceRepository = seanceRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/stats")
    public String stats(@RequestParam(required = false) String dateDebut,
                       @RequestParam(required = false) String dateFin,
                       Model model) {
        // Parse dates
        LocalDate start = null;
        LocalDate end = null;
        
        if (dateDebut != null && !dateDebut.isBlank()) {
            start = LocalDate.parse(dateDebut);
        }
        if (dateFin != null && !dateFin.isBlank()) {
            end = LocalDate.parse(dateFin);
        }
        
        List<Seance> seances = seanceRepository.findAll();
        List<StatsEntry> rows = new ArrayList<>();

        BigDecimal totalTickets = BigDecimal.ZERO;
        BigDecimal totalAds = BigDecimal.ZERO;
        BigDecimal totalProducts = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Seance s : seances) {
            // Filter by date range if provided
            LocalDate seanceDate = s.getDateHeureDebut().toLocalDate();
            if (start != null && seanceDate.isBefore(start)) continue;
            if (end != null && seanceDate.isAfter(end)) continue;
            
            Long sid = s.getId();
            BigDecimal tickets = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(prix),0) FROM billet WHERE idSeance = ?", new Object[]{sid}, BigDecimal.class);
            BigDecimal ads = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(prix),0) FROM diffusion_publicite WHERE idSeance = ?", new Object[]{sid}, BigDecimal.class);
            BigDecimal products = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(p.prix * v.quantite),0) FROM vente_produit v JOIN produit p ON v.idproduit = p.id WHERE v.dateVente = ?", new Object[]{seanceDate}, BigDecimal.class);
            
            if (tickets == null) tickets = BigDecimal.ZERO;
            if (ads == null) ads = BigDecimal.ZERO;
            if (products == null) products = BigDecimal.ZERO;
            BigDecimal total = tickets.add(ads).add(products);

            rows.add(new StatsEntry(sid, s.getFilm().getTitre(), s.getDateHeureDebut(), tickets, ads, products, total));
            
            totalTickets = totalTickets.add(tickets);
            totalAds = totalAds.add(ads);
            totalProducts = totalProducts.add(products);
            grandTotal = grandTotal.add(total);
        }

        model.addAttribute("rows", rows);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("totalAds", totalAds);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);
        return "statistics";
    }
}
