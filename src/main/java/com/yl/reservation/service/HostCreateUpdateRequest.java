package com.yl.reservation.service;

import com.yl.reservation.model.Host;
import com.yl.reservation.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostCreateUpdateRequest {
    private Host host;
    private User user;
    // todo: can i use a primitive here? alternatively, remove this field here and
    // just use a gql field.
    private Boolean isUserUpdate;
    // todo: remove isAddressUpdate --> this will be handled with changes to the
    // host api's
    private Boolean isAddressUpdate;
}
