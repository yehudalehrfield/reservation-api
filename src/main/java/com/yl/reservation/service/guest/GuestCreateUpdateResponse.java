package com.yl.reservation.service.guest;

import com.yl.reservation.model.Guest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuestCreateUpdateResponse {
    String message;
    Guest guest;
}
