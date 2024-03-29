package com.yl.reservation.service.host;

import com.yl.reservation.model.Host;
import com.yl.reservation.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostDetails {
    private Host host;
    private User user;
}
