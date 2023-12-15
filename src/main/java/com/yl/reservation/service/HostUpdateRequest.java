package com.yl.reservation.service;

import com.yl.reservation.model.Host;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostUpdateRequest {
    private Host host;
    private Boolean isAddressUpdate;
}
