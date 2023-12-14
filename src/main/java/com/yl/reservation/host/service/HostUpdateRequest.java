package com.yl.reservation.host.service;

import com.yl.reservation.host.model.Host;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostUpdateRequest {
    private Host host;
}
