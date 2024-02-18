package com.yl.reservation.service.reservation;

import com.yl.reservation.model.Reservation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationCreateUpdateResponse {
  private String message;
  private Reservation reservation;
}
