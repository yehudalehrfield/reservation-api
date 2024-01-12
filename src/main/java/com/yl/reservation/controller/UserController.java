package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.service.UserCreateUpdateRequest;
import com.yl.reservation.service.UserResponse;
import com.yl.reservation.service.UserService;
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
                .doOnSuccess(res -> logger.info("Fetched all users: {}", res.getUserList()))
                .onErrorResume(error -> {
                    logger.error("Error fetching all users");
                    return Mono.error(error);
                })
                .cache();
    }

    @QueryMapping
    Mono<UserResponse> getUserById(@Argument String userId){
        return userService.getUserById(userId)
                .switchIfEmpty(Mono.error(new ResGraphException("User " + userId + " not found", HttpStatus.NOT_FOUND)))
                .doOnSuccess(res -> logger.info("Fetched user: {}", res.getUserList()))
                .onErrorResume(error -> {
                    logger.error("Error fetching all user {}", userId);
                    return Mono.error(error);
                })
                .cache();
    }

    @MutationMapping
    Mono<UserResponse> createUser(@Argument UserCreateUpdateRequest userCreateUpdateRequest){
        String createDateTime = ResUtil.getCurrentDateTimeString();
        return userService.createNewUser(userCreateUpdateRequest.getUser(), createDateTime)
                .doOnSuccess(res -> logger.info("Created user: {}", res.getUserList().get(0).getUserId()))
                .onErrorResume(error -> {
                    logger.error("Error creating user {}", userCreateUpdateRequest.getUser());
                    return Mono.error(error);
                })
                .cache();
    }

    @MutationMapping
    Mono<UserResponse> updateUser(@Argument UserCreateUpdateRequest userCreateUpdateRequest){
        String updateDateTime = ResUtil.getCurrentDateTimeString();
        return userService.updateUser(userCreateUpdateRequest.getUser(), updateDateTime)
                .doOnSuccess(res -> logger.info("Updated user: {}", res.getUserList().get(0).getUserId()))
                .onErrorResume(error -> {
                    logger.error("Error updating user {}", userCreateUpdateRequest.getUser());
                    return Mono.error(error);
                })
                .cache();
    }

}
