package com.yl.reservation.service.reservation;

import com.yl.reservation.model.Reservation;
import com.yl.reservation.service.guest.GuestDetails;
import com.yl.reservation.service.host.HostDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetails {
  Reservation reservation;
  HostDetails hostDetails;
  GuestDetails guestDetails;
}
