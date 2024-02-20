package com.yl.reservation.service.reservation;

import java.util.List;

import lombok.Data;

@Data
public class ReservationSearchResponse {
  private String message;
  private List<ReservationDetails> reservationDetailsList;

}
