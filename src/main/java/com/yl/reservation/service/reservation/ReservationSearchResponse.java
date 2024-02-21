package com.yl.reservation.service.reservation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReservationSearchResponse {
  private String message;
  private List<ReservationDetails> reservationDetailsList;

}
