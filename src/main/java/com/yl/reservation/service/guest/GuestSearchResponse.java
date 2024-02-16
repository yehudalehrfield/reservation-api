package com.yl.reservation.service.guest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestSearchResponse {
    private String message;
    private List<GuestDetails> guestDetailsList;
}
