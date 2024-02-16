package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.service.user.UserCreateUpdateRequest;
import com.yl.reservation.service.user.UserResponse;
import com.yl.reservation.service.user.UserService;
import com.yl.reservation.util.ResLogger;
import com.yl.reservation.util.ResUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @QueryMapping
    Mono<UserResponse> getAllUsers() {
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "getAllUsers");
        // ServletRequestAttributes servletRequestAttributes =
        // (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // HttpServletRequest request = null;
        // if (servletRequestAttributes != null) {
        // request = servletRequestAttributes.getRequest();
        // }
        resLogger.setStartTime(System.currentTimeMillis());
        resLogger.setRequestMethod(HttpMethod.POST);
        // if (request != null)
        // resLogger.getHeaders(request);
        resLogger.setQuery("getAllUsers");
        return userService.getAllUsers()
                .switchIfEmpty(returnNotFound(resLogger, "No users found"))
                .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                    return Mono.error(error);
                })
                .cache();
    }

    @QueryMapping
    Mono<UserResponse> getUserById(@Argument String userId) {
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "getUserById");
        return userService.getUserById(userId)
                .switchIfEmpty(returnNotFound(resLogger, "User " + userId + " not found"))
                .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                    return Mono.error(error);
                })
                .cache();
    }

    @MutationMapping
    Mono<UserResponse> createUser(@Argument UserCreateUpdateRequest userCreateUpdateRequest) {
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "createUser");
        String createDateTime = ResUtil.getCurrentDateTimeString();
        return userService.createNewUser(userCreateUpdateRequest.getUser(), createDateTime)
                .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                    return Mono.error(error);
                })
                .cache();
    }

    @MutationMapping
    Mono<UserResponse> updateUser(@Argument UserCreateUpdateRequest userCreateUpdateRequest) {
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "updateUser");
        String updateDateTime = ResUtil.getCurrentDateTimeString();
        return userService.updateUser(userCreateUpdateRequest.getUser(), updateDateTime)
                .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                    return Mono.error(error);
                })
                .cache();
    }

    private static Mono<UserResponse> returnNotFound(ResLogger resLogger, String message) {
        resLogger.setValuesToLogger(HttpStatus.NOT_FOUND, null);
        return Mono.error(new ResGraphException(message, HttpStatus.NOT_FOUND));
    }
}
