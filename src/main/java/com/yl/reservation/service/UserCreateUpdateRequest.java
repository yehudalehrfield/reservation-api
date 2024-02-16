package com.yl.reservation.service;

import com.yl.reservation.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCreateUpdateRequest {
    private User user;
}
