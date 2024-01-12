package com.yl.reservation.service;

import com.yl.reservation.model.Guest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestCreateUpdateResponse {
    String message;
    Guest guest;
}
