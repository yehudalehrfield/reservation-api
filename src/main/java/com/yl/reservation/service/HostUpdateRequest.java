package com.yl.reservation.service;

import com.yl.reservation.model.Host;
import com.yl.reservation.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostUpdateRequest {
    private Host host;
    private User user;
    //todo: can i use a primitive here?
    private Boolean isUserUpdate;
    //todo: remove isAddressUpdate
    private Boolean isAddressUpdate;
}
