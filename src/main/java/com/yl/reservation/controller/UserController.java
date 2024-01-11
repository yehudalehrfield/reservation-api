package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.service.CreateUpdateUserRequest;
import com.yl.reservation.service.UserResponse;
import com.yl.reservation.service.UserService;
import com.yl.reservation.service.CreateUpdateMapper;
import com.yl.reservation.util.ResUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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

    @MutationMapping
    Mono<UserResponse> createUser(@Argument CreateUpdateUserRequest createUpdateUserRequest){
        String createDateTime = ResUtil.getCurrentDateTimeString();
        return userService.createNewUser(createUpdateUserRequest.getUser(), createDateTime)
        .doOnNext(res -> logger.info(String.valueOf(res)))
        .doFinally(res -> logger.info("Created User: {}",res));
    }

    @MutationMapping
    Mono<UserResponse> updateUser(@Argument CreateUpdateUserRequest createUpdateUserRequest){
        String updateDateTime = ResUtil.getCurrentDateTimeString();
        return userService.updateUser(createUpdateUserRequest.getUser(), updateDateTime);
    }

}
