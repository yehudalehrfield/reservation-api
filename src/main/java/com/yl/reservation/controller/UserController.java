package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.service.CreateUpdateUserRequest;
import com.yl.reservation.service.UserResponse;
import com.yl.reservation.service.UserService;
import com.yl.reservation.util.ResUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

public class UserController {

    @Autowired
    UserService userService;

    @QueryMapping
    Mono<UserResponse> getAllUsers(){
        return userService.getAllUsers()
                .switchIfEmpty(Mono.error(new ResGraphException("No Users Found", HttpStatus.NOT_FOUND)))
                .cache();
    }

    @QueryMapping
    Mono<UserResponse> getUserById(@Argument String userId){
        return userService.getUserById(userId)
                .switchIfEmpty(Mono.error(new ResGraphException("User " + userId + " not found", HttpStatus.NOT_FOUND)))
                .cache();
    }

    @QueryMapping
    Mono<UserResponse> createUser(@Argument CreateUpdateUserRequest request){
        String createDateTime = ResUtil.getCurrentDateTimeString();
        return userService.createNewUser(request.getUser(), createDateTime);
    }

    @QueryMapping
    Mono<UserResponse> updateUser(@Argument CreateUpdateUserRequest request){
        String updateDateTime = ResUtil.getCurrentDateTimeString();
        return userService.updateUser(request.getUser(), updateDateTime);
    }

}
