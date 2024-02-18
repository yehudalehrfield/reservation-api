package com.yl.reservation.service.reservation;

import com.yl.reservation.model.Reservation;

import lombok.Data;

@Data
public class ReservationCreateUpdateRequest {
  private Reservation reservation;
}
