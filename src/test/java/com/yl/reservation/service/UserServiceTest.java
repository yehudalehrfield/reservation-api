package com.yl.reservation.service;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.ContactMethod;
import com.yl.reservation.model.Email;
import com.yl.reservation.model.Phone;
import com.yl.reservation.model.User;
import com.yl.reservation.repository.UserRepository;
import com.yl.reservation.service.user.UserResponse;
import com.yl.reservation.service.user.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    private String userId1 = "userId1";
    private String userId2 = "userId2";

    @Test
    void getAllUsers() {
        User user1 = new User();
        user1.setUserId(userId1);
        User user2 = new User();
        user2.setUserId(userId2);

        Mockito.when(userRepository.findAll()).thenReturn(Flux.just(user1, user2));

        UserResponse response = new UserResponse("Retrieved all users", List.of(user1, user2));

        StepVerifier.create(userService.getAllUsers())
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getAllUsers_notFound() {
        User user1 = new User();
        user1.setUserId(userId1);
        User user2 = new User();
        user2.setUserId(userId2);

        Mockito.when(userRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(userService.getAllUsers())
                .expectErrorMatches(
                        error -> error.equals(new ResGraphException("No users found", HttpStatus.NOT_FOUND)))
                .verify();
    }

    @Test
    void getUserById() {
        User user1 = new User();
        user1.setUserId(userId1);

        Mockito.when(userRepository.findByUserId(userId1)).thenReturn(Mono.just(user1));

        UserResponse userResponse = new UserResponse("Retrieved user userId1", List.of(user1));

        StepVerifier.create(userService.getUserById(userId1)).expectNext(userResponse).verifyComplete();
    }

    @Test
    void getUserById_notFound() {
        Mockito.when(userRepository.findByUserId(userId1)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUserById(userId1))
                .expectErrorMatches(
                        error -> error.equals(new ResGraphException("No user userId1", HttpStatus.NOT_FOUND)))
                .verify();
    }

    @Test
    void createNewUser_phoneAsPrimary() {
        User user = new User();
        user.setFirstName("first");
        user.setLastName("last");
        user.setPhone(List.of(new Phone(Phone.PhoneType.HOME, "1234567890", true)));
        user.setPrimaryContactMethod(ContactMethod.PHONE);

        Mockito.when(userRepository.findByLastNameAndPrimaryPhone("last", "1234567890")).thenReturn(Mono.empty());
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.createNewUser(user, "today"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void createNewUser_emailAsPrimary() {
        User user = new User();
        user.setFirstName("first");
        user.setLastName("last");
        user.setPrimaryContactMethod(ContactMethod.EMAIL);
        user.setEmail(List.of(new Email(Email.EmailType.PERSONAL, "email@email.com", true)));

        Mockito.when(userRepository.findByLastNameAndPrimaryEmail(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.empty());
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.createNewUser(user, "today"))
                .expectNextCount(1)
                .verifyComplete();
    }

    // @Test
    // void createNewUser_userAlreadyExists(){
    // User user = new User();
    // user.setFirstName("first");
    // user.setLastName("last");
    // user.setPhone(List.of(new Phone(Phone.PhoneType.HOME, "1234567890",true)));
    // user.setPrimaryContactMethod(ContactMethod.PHONE);
    //
    // Mockito.when(userRepository.findByLastNameAndPrimaryPhone("last",
    // "1234567890")).thenReturn(Mono.just(user));
    //
    // StepVerifier.create(userService.createNewUser(user,"today"))
    // .expectErrorMatches(error -> error.equals(new ResGraphException("User already
    // exists", HttpStatus.BAD_REQUEST)))
    // .verify();
    //
    // }

    @Test
    void updateUser_byUserId() {
        User user1 = new User();
        user1.setUserId(userId1);
        user1.setPrimaryContactMethod(ContactMethod.PHONE);

        User existingUser = new User();
        existingUser.setUserId(userId1);

        Mockito.when(userRepository.findByUserId(userId1)).thenReturn(Mono.just(existingUser));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(user1));

        UserResponse response = new UserResponse("Updated user userId1", List.of(user1));
        StepVerifier.create(userService.updateUser(user1, "today"))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void updateUser_byLastNameAndPhoneAsPrimary() {
        User user1 = new User();
        user1.setLastName("last");
        user1.setPrimaryContactMethod(ContactMethod.PHONE);
        user1.setPhone(List.of(new Phone(Phone.PhoneType.HOME, "1234567890", true)));
        user1.setEmail(List.of(new Email(Email.EmailType.PERSONAL, "email@email.com", true)));

        User existingUser = new User();
        existingUser.setPrimaryContactMethod(ContactMethod.PHONE);
        existingUser.setPhone(List.of(new Phone(Phone.PhoneType.HOME, "1234567890", true)));

        Mockito.when(userRepository.findByLastNameAndPrimaryPhone(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.just(existingUser));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(user1));

        UserResponse response = new UserResponse("Updated user null", List.of(user1));
        StepVerifier.create(userService.updateUser(user1, "today"))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void updateUser_byLastNameAndEmailAsPrimary() {
        User user1 = new User();
        user1.setLastName("last");
        user1.setPrimaryContactMethod(ContactMethod.EMAIL);
        user1.setPhone(List.of(new Phone(Phone.PhoneType.HOME, "1234567890", true)));
        user1.setEmail(List.of(new Email(Email.EmailType.PERSONAL, "email@email.com", true)));

        User existingUser = new User();
        existingUser.setPrimaryContactMethod(ContactMethod.PHONE);
        existingUser.setEmail(List.of(new Email(Email.EmailType.PERSONAL, "email@email.com", true)));

        Mockito.when(userRepository.findByLastNameAndPrimaryEmail(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.just(existingUser));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(user1));

        UserResponse response = new UserResponse("Updated user null", List.of(user1));
        StepVerifier.create(userService.updateUser(user1, "today"))
                .expectNext(response)
                .verifyComplete();
    }

    //
}
