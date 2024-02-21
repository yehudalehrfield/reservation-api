package com.yl.reservation.service.user;

import com.yl.reservation.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserResponse {
    String message;
    List<User> userList;
}
