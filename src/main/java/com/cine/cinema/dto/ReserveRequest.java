package com.cine.cinema.dto;

import java.util.List;

public class ReserveRequest {
    private Long seanceId;
    private Long clientId;
    private List<Long> seatIds;
    private Integer paymentModeId;

    public Long getSeanceId() { return seanceId; }
    public void setSeanceId(Long seanceId) { this.seanceId = seanceId; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public List<Long> getSeatIds() { return seatIds; }
    public void setSeatIds(List<Long> seatIds) { this.seatIds = seatIds; }
    public Integer getPaymentModeId() { return paymentModeId; }
    public void setPaymentModeId(Integer paymentModeId) { this.paymentModeId = paymentModeId; }
}
