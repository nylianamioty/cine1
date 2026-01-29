package com.cine.cinema.web;

import com.cine.cinema.model.Billet;
import com.cine.cinema.model.Seance;
import com.cine.cinema.model.Siege;
import com.cine.cinema.model.Client;
import com.cine.cinema.model.TarifTypesiege;
import com.cine.cinema.repository.BilletRepository;
import com.cine.cinema.repository.SeanceRepository;
import com.cine.cinema.repository.SiegeRepository;
import com.cine.cinema.repository.ClientRepository;
import com.cine.cinema.repository.TarifTypesiegeRepository;
import com.cine.cinema.repository.ModePaiementRepository;
import com.cine.cinema.repository.PaiementBilletRepository;
import com.cine.cinema.repository.PaiementRepository;
import com.cine.cinema.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

@Controller
public class PurchaseController {
    private final SeanceRepository seanceRepository;
    private final SiegeRepository siegeRepository;
    private final ClientRepository clientRepository;
    private final TarifTypesiegeRepository tarifTypesiegeRepository;
    private final ModePaiementRepository modePaiementRepository;
    private final JdbcTemplate jdbcTemplate;
    private final TicketService ticketService;
    private final PaiementRepository paiementRepository;
    private final PaiementBilletRepository paiementBilletRepository;

    public PurchaseController(SeanceRepository seanceRepository, SiegeRepository siegeRepository,
                              ClientRepository clientRepository, TarifTypesiegeRepository tarifTypesiegeRepository,
                              ModePaiementRepository modePaiementRepository, TicketService ticketService,
                              PaiementRepository paiementRepository, PaiementBilletRepository paiementBilletRepository,
                              JdbcTemplate jdbcTemplate) {
        this.seanceRepository = seanceRepository;
        this.siegeRepository = siegeRepository;
        this.clientRepository = clientRepository;
        this.tarifTypesiegeRepository = tarifTypesiegeRepository;
        this.modePaiementRepository = modePaiementRepository;
        this.ticketService = ticketService;
        this.paiementRepository = paiementRepository;
        this.paiementBilletRepository = paiementBilletRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/purchase")
    public String showPurchase(@RequestParam Long seanceId,
                               @RequestParam Long clientId,
                               @RequestParam(name = "seatIds") List<Long> seatIds,
                               Model model) {
        Seance seance = seanceRepository.findById(seanceId).orElse(null);
        Client client = clientRepository.findById(clientId).orElse(null);
        // load sieges preserving the order of seatIds so category selections map to correct seats
        List<Siege> sieges = seatIds.stream()
            .map(id -> siegeRepository.findById(id).orElse(null))
            .filter(s -> s != null)
            .collect(java.util.stream.Collectors.toList());

        TarifTypesiege tarif = tarifTypesiegeRepository.findAll().stream().findFirst().orElse(null);
        java.math.BigDecimal unit = tarif != null && tarif.getPrix() != null ? tarif.getPrix() : (seance != null ? seance.getPrix() : java.math.BigDecimal.ZERO);
        java.math.BigDecimal total = unit.multiply(java.math.BigDecimal.valueOf(sieges.size()));

        model.addAttribute("seance", seance);
        model.addAttribute("client", client);
        model.addAttribute("sieges", sieges);
        model.addAttribute("seatIds", seatIds);
        model.addAttribute("count", sieges.size());
        model.addAttribute("unitPrice", unit);
        model.addAttribute("total", total);
        List<com.cine.cinema.model.ModePaiement> modes = modePaiementRepository.findAll();
        if (modes == null || modes.isEmpty()) {
            // try raw query as diagnostic
            try {
                List<Map<String,Object>> rows = jdbcTemplate.queryForList("select id, libelle from modePaiement");
                for (Map<String,Object> r : rows) {
                    System.out.println("modePaiement row: " + r);
                }
            } catch (Exception e) {
                System.out.println("jdbcTemplate query failed: " + e.getMessage());
            }
        }
        model.addAttribute("modes", modes);
        // load age categories
        List<java.util.Map<String,Object>> categories = jdbcTemplate.queryForList("select id, categ from categorie_age order by id");
        model.addAttribute("categories", categories);
        model.addAttribute("tarifTypesiegeId", tarif != null ? tarif.getId() : null);

        // compute per-seat category prices (based on existing tarif and seat type)
        java.util.Map<Long, java.util.List<java.util.Map<String,Object>>> categoriesPerSeat = new java.util.HashMap<>();
        for (Siege s : sieges) {
            Integer siegeTypeId = jdbcTemplate.queryForObject("select idTypeSiege from siege where id = ?", new Object[]{s.getId()}, Integer.class);
            java.util.List<java.util.Map<String,Object>> opts = new java.util.ArrayList<>();
            for (java.util.Map<String,Object> c : categories) {
                Long catId = ((Number)c.get("id")).longValue();
                String categ = (String)c.get("categ");
                java.math.BigDecimal price = unit;
                try {
                    // find tarif that matches category name
                    Long tarifIdForCat = jdbcTemplate.queryForObject("select id from tarif where lower(nom) like ? limit 1", new Object[]{"%" + categ.toLowerCase() + "%"}, Long.class);
                    if (tarifIdForCat != null) {
                        java.math.BigDecimal p = jdbcTemplate.queryForObject("select prix from tarif_typesiege where idtarif = ? and idtypesiege = ?", new Object[]{tarifIdForCat, siegeTypeId}, java.math.BigDecimal.class);
                        if (p != null) price = p;
                    }
                } catch (Exception ex) {
                    // ignore, fallback to unit
                }
                java.util.Map<String,Object> map = new java.util.HashMap<>();
                map.put("id", catId);
                map.put("categ", categ);
                map.put("prix", price);
                opts.add(map);
            }
            categoriesPerSeat.put(s.getId(), opts);
        }
        model.addAttribute("categoriesPerSeat", categoriesPerSeat);

        return "purchase";
    }

    @PostMapping("/purchase/confirm")
    public String confirmPurchase(@RequestParam Long seanceId,
                                  @RequestParam Long clientId,
                                  @RequestParam(name = "seatIds") List<Long> seatIds,
                                  @RequestParam(name = "categorieIds", required = false) List<Long> categorieIds,
                                  @RequestParam Long paymentModeId,
                                  @RequestParam(required = false) String paymentDate,
                                  Model model) {
        java.time.LocalDate pd = null;
        if (paymentDate != null && !paymentDate.isBlank()) {
            pd = java.time.LocalDate.parse(paymentDate);
        } else {
            pd = java.time.LocalDate.now();
        }
        List<Billet> created = ticketService.reserveSeats(seanceId, clientId, seatIds, categorieIds, paymentModeId.intValue(), pd, paiementRepository, paiementBilletRepository);
        
        java.math.BigDecimal totalBillets = java.math.BigDecimal.ZERO;
        for (Billet b : created) {
            if (b.getPrix() != null) {
                totalBillets = totalBillets.add(b.getPrix());
            }
        }
        
        model.addAttribute("billets", created);
        model.addAttribute("totalBillets", totalBillets);
        return "confirmation";
    }
}
