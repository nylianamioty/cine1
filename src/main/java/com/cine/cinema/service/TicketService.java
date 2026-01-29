package com.cine.cinema.service;

import com.cine.cinema.dto.PurchaseRequest;
import com.cine.cinema.exception.ResourceNotFoundException;
import com.cine.cinema.model.Billet;
import com.cine.cinema.model.Siege;
import com.cine.cinema.model.TarifTypesiege;
import com.cine.cinema.repository.BilletRepository;
import com.cine.cinema.repository.ClientRepository;
import com.cine.cinema.repository.SeanceRepository;
import com.cine.cinema.repository.SiegeRepository;
import com.cine.cinema.repository.TarifTypesiegeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TicketService {
    private final BilletRepository billetRepository;
    private final SeanceRepository seanceRepository;
    private final SiegeRepository siegeRepository;
    private final ClientRepository clientRepository;
        private final TarifTypesiegeRepository tarifTypesiegeRepository;
        private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

        public TicketService(BilletRepository billetRepository, SeanceRepository seanceRepository,
                                                 SiegeRepository siegeRepository, ClientRepository clientRepository,
                                                 TarifTypesiegeRepository tarifTypesiegeRepository,
                                                 org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
                this.billetRepository = billetRepository;
                this.seanceRepository = seanceRepository;
                this.siegeRepository = siegeRepository;
                this.clientRepository = clientRepository;
                this.tarifTypesiegeRepository = tarifTypesiegeRepository;
                this.jdbcTemplate = jdbcTemplate;
        }

    @Transactional
    public Billet purchase(PurchaseRequest req) {
        String dt = req.getStartDateTime();
        if (dt == null) throw new IllegalArgumentException("startDateTime requis");
        // Accept 'yyyy-MM-dd HH:mm' or ISO with 'T'
        String normalized = dt.contains("T") ? dt : dt.replace(' ', 'T');
        LocalDateTime start = LocalDateTime.parse(normalized, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        var seance = seanceRepository.findByFilm_TitreAndDateHeureDebutAndSalle_Id(req.getFilmTitle(), start, req.getSalleId())
                .orElseThrow(() -> new ResourceNotFoundException("Seance introuvable"));

        var client = clientRepository.findById(req.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

        List<Siege> sieges = siegeRepository.findBySalle_Id(req.getSalleId());
        if (sieges.isEmpty()) throw new IllegalStateException("Aucun siège défini pour la salle");

        Set<Long> usedSiegeIds = billetRepository.findBySeance_Id(seance.getId()).stream()
                .map(b -> b.getSiege().getId())
                .collect(Collectors.toSet());

        Siege available = sieges.stream()
                .filter(s -> !usedSiegeIds.contains(s.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Plus de sièges disponibles"));

        TarifTypesiege tarif = tarifTypesiegeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucun tarif disponible"));

        Billet billet = new Billet();
        billet.setSeance(seance);
        billet.setSiege(available);
        billet.setClient(client);
        billet.setTarifTypesiegeId(tarif.getId());
        billet.setPrix(tarif.getPrix() != null ? tarif.getPrix() : seance.getPrix());
        billet.setStatut("PAYE");

        return billetRepository.save(billet);
    }

        @Transactional
        public java.util.List<Billet> reserveSeats(Long seanceId, Long clientId, java.util.List<Long> seatIds, java.util.List<Long> categorieIds, Integer paymentModeId,
                                                                                           java.time.LocalDate paymentDate,
                                                                                           com.cine.cinema.repository.PaiementRepository paiementRepo,
                                                                                           com.cine.cinema.repository.PaiementBilletRepository paiementBilletRepo) {
                // verify seance
                var seance = seanceRepository.findById(seanceId)
                                .orElseThrow(() -> new ResourceNotFoundException("Seance introuvable"));

                var client = clientRepository.findById(clientId)
                                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

                // check already reserved
                var already = billetRepository.findBySeance_IdAndSiege_IdIn(seanceId, seatIds);
                if (!already.isEmpty()) throw new IllegalStateException("Un ou plusieurs sièges sont déjà réservés");

                // fetch sieges preserving seatIds order
                var sieges = new java.util.ArrayList<Siege>();
                for (var id : seatIds) {
                        var sOpt = siegeRepository.findById(id);
                        if (sOpt.isEmpty()) throw new IllegalStateException("Sièges invalides");
                        sieges.add(sOpt.get());
                }

                java.math.BigDecimal total = java.math.BigDecimal.ZERO;
                java.util.ArrayList<Billet> created = new java.util.ArrayList<>();

                var tarifOpt = tarifTypesiegeRepository.findAll().stream().findFirst();
                if (tarifOpt.isEmpty()) throw new IllegalStateException("Aucun tarif disponible");
                TarifTypesiege tarif = tarifOpt.get();

                for (int i = 0; i < sieges.size(); i++) {
                        var s = sieges.get(i);
                        Billet b = new Billet();
                        b.setSeance(seance);
                        b.setSiege(s);
                        b.setClient(client);
                        java.math.BigDecimal price = tarif.getPrix() != null ? tarif.getPrix() : seance.getPrix();
                        // if a category is selected, prefer the category-specific price if available
                        if (categorieIds != null && categorieIds.size() == sieges.size()) {
                                Long catId = categorieIds.get(i);
                                try {
                                        // find category name
                                        String categ = jdbcTemplate.queryForObject("select categ from categorie_age where id = ?", new Object[]{catId}, String.class);
                                        if (categ != null) {
                                                // find tarif that matches category name (e.g., 'Tarif enfant')
                                                Long tarifIdForCat = jdbcTemplate.queryForObject("select id from tarif where lower(nom) like ? limit 1", new Object[]{"%" + categ.toLowerCase() + "%"}, Long.class);
                                                if (tarifIdForCat != null) {
                                                        Integer siegeTypeId = jdbcTemplate.queryForObject("select idTypeSiege from siege where id = ?", new Object[]{s.getId()}, Integer.class);
                                                        java.math.BigDecimal p = jdbcTemplate.queryForObject(
                                                                "select prix from tarif_typesiege where idtarif = ? and idtypesiege = ?",
                                                                new Object[]{tarifIdForCat, siegeTypeId}, java.math.BigDecimal.class);
                                                        if (p != null) price = p;
                                                }
                                        }
                                } catch (Exception ex) {
                                        // fallback to tarif/seance price if no specific category price
                                }
                        }
                        b.setPrix(price);
                        b.setTarifTypesiegeId(tarif.getId());
                        // assign category if provided (parallel to seatIds)
                        if (categorieIds != null && categorieIds.size() == sieges.size()) {
                                Long catId = categorieIds.get(i);
                                b.setCategorieAgeId(catId);
                        }
                        b.setStatut("PAYE");
                        created.add(b);
                        total = total.add(price != null ? price : java.math.BigDecimal.ZERO);
                }

                // save billets
                created = (java.util.ArrayList<Billet>) billetRepository.saveAll(created);

                // create paiement
                if (paymentModeId == null) throw new IllegalArgumentException("mode de paiement requis");
                com.cine.cinema.model.Paiement paiement = new com.cine.cinema.model.Paiement();
                paiement.setClient(client);
                paiement.setModePaiementId(paymentModeId.longValue());
                paiement.setMontant(total);
                paiement.setDatePaiement(paymentDate != null ? paymentDate : java.time.LocalDate.now());
                paiement = paiementRepo.save(paiement);

                // link paiement_billet
                for (var b : created) {
                        com.cine.cinema.model.PaiementBillet pb = new com.cine.cinema.model.PaiementBillet();
                        pb.setPaiement(paiement);
                        pb.setBillet(b);
                        paiementBilletRepo.save(pb);
                }

                return created;
        }
}
