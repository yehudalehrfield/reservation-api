package com.yl.reservation.service;

import com.yl.reservation.model.Guest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestResponse {
    String message;
    List<Guest> guestList;
}
