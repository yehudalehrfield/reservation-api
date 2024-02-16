package com.yl.reservation.service.host;

import com.yl.reservation.model.Host;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostCreateUpdateRequest {
    private Host host;
    private Boolean isAddressUpdate;
}
