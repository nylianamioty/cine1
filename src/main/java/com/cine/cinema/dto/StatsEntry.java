package com.cine.cinema.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StatsEntry {
    private Long seanceId;
    private String filmTitle;
    private LocalDateTime dateHeureDebut;
    private BigDecimal ticketsRevenue;
    private BigDecimal adsRevenue;
    private BigDecimal productsRevenue;
    private BigDecimal total;

    public StatsEntry(Long seanceId, String filmTitle, LocalDateTime dateHeureDebut,
                      BigDecimal ticketsRevenue, BigDecimal adsRevenue, BigDecimal productsRevenue, BigDecimal total) {
        this.seanceId = seanceId;
        this.filmTitle = filmTitle;
        this.dateHeureDebut = dateHeureDebut;
        this.ticketsRevenue = ticketsRevenue;
        this.adsRevenue = adsRevenue;
        this.productsRevenue = productsRevenue;
        this.total = total;
    }

    public Long getSeanceId() { return seanceId; }
    public String getFilmTitle() { return filmTitle; }
    public LocalDateTime getDateHeureDebut() { return dateHeureDebut; }
    public BigDecimal getTicketsRevenue() { return ticketsRevenue; }
    public BigDecimal getAdsRevenue() { return adsRevenue; }
    public BigDecimal getProductsRevenue() { return productsRevenue; }
    public BigDecimal getTotal() { return total; }
}
