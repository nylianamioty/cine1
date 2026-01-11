package com.cine.cinema.web;

import com.cine.cinema.model.Seance;
import com.cine.cinema.repository.BilletRepository;
import com.cine.cinema.repository.SeanceRepository;
import com.cine.cinema.repository.SiegeRepository;
import com.cine.cinema.service.TicketService;
import com.cine.cinema.dto.ReserveRequest;
import com.cine.cinema.repository.PaiementBilletRepository;
import com.cine.cinema.repository.PaiementRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/seances")
public class SeanceController {
    private final SeanceRepository seanceRepository;
    private final SiegeRepository siegeRepository;
    private final BilletRepository billetRepository;
    private final TicketService ticketService;
    private final PaiementRepository paiementRepository;
    private final PaiementBilletRepository paiementBilletRepository;

    public SeanceController(SeanceRepository seanceRepository, SiegeRepository siegeRepository,
                            BilletRepository billetRepository, TicketService ticketService,
                            PaiementRepository paiementRepository, PaiementBilletRepository paiementBilletRepository) {
        this.seanceRepository = seanceRepository;
        this.siegeRepository = siegeRepository;
        this.billetRepository = billetRepository;
        this.ticketService = ticketService;
        this.paiementRepository = paiementRepository;
        this.paiementBilletRepository = paiementBilletRepository;
    }

    @GetMapping("/{id}")
    public String showSeance(@PathVariable Long id, Model model) {
        Seance seance = seanceRepository.findById(id).orElse(null);
        model.addAttribute("seance", seance);

        var takenIds = billetRepository.findBySeance_Id(seance.getId()).stream().map(b -> b.getSiege().getId()).collect(Collectors.toSet());
        var sieges = siegeRepository.findBySalle_IdOrderByRangeeAscNumeroAsc(seance.getSalle().getId());
        java.util.Map<String, java.util.List<java.util.Map<String,Object>>> rows = new java.util.LinkedHashMap<>();
        for (var s : sieges) {
            String r = s.getRangee() != null ? s.getRangee() : "";
            var seat = new java.util.HashMap<String,Object>();
            seat.put("id", s.getId());
            seat.put("numero", s.getNumero());
            seat.put("taken", takenIds.contains(s.getId()));
            rows.computeIfAbsent(r, k -> new java.util.ArrayList<>()).add(seat);
        }
        model.addAttribute("rows", rows);
        return "seance";
    }

    @PostMapping("/{id}/reserve")
    public String reserve(@PathVariable Long id, @RequestParam Long clientId, @RequestParam(required = false) List<Long> seatIds, Model model) {
        if (seatIds == null || seatIds.isEmpty()) {
            model.addAttribute("error", "Aucun siège sélectionné");
            return "redirect:/seances/" + id;
        }

        ReserveRequest req = new ReserveRequest();
        req.setSeanceId(id);
        req.setClientId(clientId);
        req.setSeatIds(seatIds);

        // redirect to purchase page to confirm number of billets and payment mode
        StringBuilder sb = new StringBuilder();
        sb.append("/purchase?seanceId=").append(id).append("&clientId=").append(clientId);
        for (Long sId : seatIds) {
            sb.append("&seatIds=").append(sId);
        }
        return "redirect:" + sb.toString();
    }
}
