package com.yl.reservation.service;

import com.yl.reservation.model.Guest;
import lombok.Data;

@Data
public class GuestCreateUpdateRequest {
    private Guest guest;
}
