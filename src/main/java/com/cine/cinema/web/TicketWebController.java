package com.cine.cinema.web;

import com.cine.cinema.dto.PurchaseRequest;
import com.cine.cinema.model.Billet;
import com.cine.cinema.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tickets")
public class TicketWebController {
    private final TicketService ticketService;

    public TicketWebController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/purchase")
    public String showPurchaseForm(Model model) {
        model.addAttribute("purchaseRequest", new PurchaseRequest());
        return "purchase";
    }

    @PostMapping("/purchase")
    public String submitPurchase(PurchaseRequest purchaseRequest, Model model) {
        Billet billet = ticketService.purchase(purchaseRequest);
        model.addAttribute("billetId", billet.getId());
        return "confirmation";
    }

    // Show seance (redirect convenience) - not used directly
}

