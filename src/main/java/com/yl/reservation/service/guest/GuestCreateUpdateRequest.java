package com.yl.reservation.service.guest;

import com.yl.reservation.model.Guest;
import lombok.Data;

@Data
public class GuestCreateUpdateRequest {
    private Guest guest;
}
