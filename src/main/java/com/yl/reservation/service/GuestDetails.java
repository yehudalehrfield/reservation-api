package com.yl.reservation.service;

import com.yl.reservation.model.Guest;
import com.yl.reservation.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestDetails {
    Guest guest;
    User user;
}
