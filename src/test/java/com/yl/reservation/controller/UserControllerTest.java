package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.User;
import com.yl.reservation.service.UserCreateUpdateRequest;
import com.yl.reservation.service.UserResponse;
import com.yl.reservation.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    @Test
    void getAllUsers() {
        User user1 = new User();
        user1.setUserId("userId1");
        User user2 = new User();
        user2.setUserId("userId2");

        UserResponse userResponse = new UserResponse("Retrieved all users", List.of(user1, user2));

        Mockito.when(userService.getAllUsers()).thenReturn(Mono.just(userResponse));

        StepVerifier.create(userController.getAllUsers())
                .expectNext(userResponse)
                .verifyComplete();
    }

    @Test
    void getAllUsers_Error() {
        User user1 = new User();
        user1.setUserId("userId1");
        User user2 = new User();
        user2.setUserId("userId2");
        ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);
        Mockito.when(userService.getAllUsers()).thenReturn(Mono.error(exception));

        StepVerifier.create(userController.getAllUsers())
                .expectErrorMatches(error -> error.equals(exception))
                .verify();
    }

    @Test
    void getUserById() {
        String userId = "userId1";
        User user = new User();
        user.setUserId(userId);

        UserResponse userResponse = new UserResponse("Retrieved user with id " + userId, List.of(user));

        Mockito.when(userService.getUserById(userId)).thenReturn(Mono.just(userResponse));

        StepVerifier.create(userController.getUserById(userId))
                .expectNext(userResponse)
                .verifyComplete();
    }

    @Test
    void getUserById_notFoundError() {
        String userId = "invalidUserId";
        String errorMessage = "User " + userId + " not found";

        Mockito.when(userService.getUserById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(userController.getUserById(userId))
                .expectErrorMatches(error -> error.getMessage().equals(errorMessage))
                .verify();
    }

    @Test
    void createUser() {
        UserCreateUpdateRequest request = new UserCreateUpdateRequest();
        User user = new User();
        request.setUser(user);

        UserResponse userResponse = new UserResponse("User created successfully", List.of(user));

        Mockito.when(userService.createNewUser(Mockito.any(), Mockito.anyString())).thenReturn(Mono.just(userResponse));

        StepVerifier.create(userController.createUser(request))
                .expectNext(userResponse)
                .verifyComplete();
    }

    @Test
    void createUser_Error() {
        UserCreateUpdateRequest request = new UserCreateUpdateRequest();
        User user = new User();
        request.setUser(user);

        ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(userService.createNewUser(Mockito.any(), Mockito.anyString())).thenReturn(Mono.error(exception));

        StepVerifier.create(userController.createUser(request))
                .expectErrorMatches(error -> error.equals(exception))
                .verify();
    }

    @Test
    void updateUser() {
        UserCreateUpdateRequest request = new UserCreateUpdateRequest();
        User user = new User();
        user.setUserId("userId");
        request.setUser(user);

        UserResponse userResponse = new UserResponse("User updated successfully", List.of(user));

        Mockito.when(userService.updateUser(Mockito.any(), Mockito.anyString())).thenReturn(Mono.just(userResponse));

        StepVerifier.create(userController.updateUser(request))
                .expectNext(userResponse)
                .verifyComplete();
    }

    @Test
    void updateUser_Error() {
        UserCreateUpdateRequest request = new UserCreateUpdateRequest();
        User user = new User();
        user.setUserId("userId");
        request.setUser(user);

        ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(userService.updateUser(Mockito.any(), Mockito.anyString())).thenReturn(Mono.error(exception));

        StepVerifier.create(userController.updateUser(request))
                .expectErrorMatches(error -> error.equals(exception))
                .verify();
    }

}
