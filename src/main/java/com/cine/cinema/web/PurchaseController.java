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
        List<Siege> sieges = siegeRepository.findAllById(seatIds);

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

        return "purchase";
    }

    @PostMapping("/purchase/confirm")
    public String confirmPurchase(@RequestParam Long seanceId,
                                  @RequestParam Long clientId,
                                  @RequestParam(name = "seatIds") List<Long> seatIds,
                                  @RequestParam Long paymentModeId,
                                  @RequestParam(required = false) String paymentDate,
                                  Model model) {
        java.time.LocalDate pd = null;
        if (paymentDate != null && !paymentDate.isBlank()) {
            pd = java.time.LocalDate.parse(paymentDate);
        } else {
            pd = java.time.LocalDate.now();
        }
        List<Billet> created = ticketService.reserveSeats(seanceId, clientId, seatIds, paymentModeId.intValue(), pd, paiementRepository, paiementBilletRepository);
        model.addAttribute("billets", created);
        return "confirmation";
    }
}
