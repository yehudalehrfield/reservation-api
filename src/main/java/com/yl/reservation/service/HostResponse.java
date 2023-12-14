package com.yl.reservation.service;

import com.yl.reservation.model.Host;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostResponse {
    private String message;
    private Host host;
}
