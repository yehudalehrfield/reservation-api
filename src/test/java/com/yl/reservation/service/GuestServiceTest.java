package com.yl.reservation.service;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.Guest;
import com.yl.reservation.model.User;
import com.yl.reservation.repository.GuestRepository;
import com.yl.reservation.repository.UserRepository;
import com.yl.reservation.service.guest.GuestCreateUpdateResponse;
import com.yl.reservation.service.guest.GuestDetails;
import com.yl.reservation.service.guest.GuestSearchResponse;
import com.yl.reservation.service.guest.GuestService;
import com.yl.reservation.util.ResConstants;
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
public class GuestServiceTest {

    @InjectMocks
    GuestService guestService;

    @Mock
    GuestRepository guestRepository;

    @Mock
    UserRepository userRepository;

    private final String GUEST1_ID = "guest1Id";
    private final String GUEST2_ID = "guest2Id";
    private final String USER1_ID = "user1Id";
    private final String USER2_ID = "user2Id";

    @Test
    void getAllUsers() {
        Guest guest1 = new Guest();
        guest1.setGuestId(GUEST1_ID);
        guest1.setUserId(USER1_ID);
        Guest guest2 = new Guest();
        guest2.setGuestId(GUEST2_ID);
        guest2.setUserId(USER2_ID);

        User user1 = new User();
        user1.setUserId(USER1_ID);
        User user2 = new User();
        user2.setUserId(USER2_ID);

        GuestDetails guestDetailsWithUserInfo1 = new GuestDetails(guest1, user1);
        GuestDetails guestDetailsWithUserInfo2 = new GuestDetails(guest2, user2);

        GuestDetails guestDetailsWithoutUserInfo1 = new GuestDetails(guest1, null);
        GuestDetails guestDetailsWithoutUserInfo2 = new GuestDetails(guest2, null);

        GuestSearchResponse guestSearchResponseWithUserInfo = new GuestSearchResponse(
                ResConstants.GUEST_FIND_ALL_USER_INFO, List.of(guestDetailsWithUserInfo1, guestDetailsWithUserInfo2));
        GuestSearchResponse guestSearchResponseWithoutUserInfo = new GuestSearchResponse(
                ResConstants.GUEST_FIND_ALL_NO_USER_INFO,
                List.of(guestDetailsWithoutUserInfo1, guestDetailsWithoutUserInfo2));

        Mockito.when(guestRepository.findAll()).thenReturn(Flux.just(guest1, guest2));
        Mockito.when(userRepository.findByUserId(Mockito.anyString())).thenReturn(Mono.just(user1), Mono.just(user2));

        StepVerifier.create(guestService.getAllGuests(true))
                .expectNext(guestSearchResponseWithUserInfo)
                .verifyComplete();

        StepVerifier.create(guestService.getAllGuests(false))
                .expectNext(guestSearchResponseWithoutUserInfo)
                .verifyComplete();
    }

    @Test
    void getGuestById() {
        Guest guest = new Guest();
        guest.setGuestId(GUEST1_ID);
        guest.setUserId(USER1_ID);

        User user = new User();
        user.setUserId(USER1_ID);

        GuestDetails guestDetailsWithUserInfo = new GuestDetails(guest, user);
        ;

        GuestDetails guestDetailsWithoutUserInfo = new GuestDetails(guest, null);

        GuestSearchResponse guestSearchResponseWithUserInfo = new GuestSearchResponse(
                ResConstants.GUEST_FIND + GUEST1_ID + " with user info...", List.of(guestDetailsWithUserInfo));
        GuestSearchResponse guestSearchResponseWithoutUserInfo = new GuestSearchResponse(
                ResConstants.GUEST_FIND + GUEST1_ID, List.of(guestDetailsWithoutUserInfo));

        Mockito.when(guestRepository.findByGuestId(Mockito.anyString())).thenReturn(Mono.just(guest));
        Mockito.when(userRepository.findByUserId(Mockito.anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(guestService.getGuestById(GUEST1_ID, true))
                .expectNext(guestSearchResponseWithUserInfo)
                .verifyComplete();

        StepVerifier.create(guestService.getGuestById(GUEST1_ID, false))
                .expectNext(guestSearchResponseWithoutUserInfo)
                .verifyComplete();
    }

    @Test
    void createNewGuest() {
        Guest requestGuest = new Guest();
        requestGuest.setUserId(USER1_ID);
        requestGuest.setNickName("guestie");

        User user = new User();
        user.setUserId(USER1_ID);

        Mockito.when(userRepository.findByUserId(USER1_ID)).thenReturn(Mono.just(user));
        Mockito.when(guestRepository.findByUserIdAndNickName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.empty());
        Mockito.when(guestRepository.save(Mockito.any())).thenReturn(Mono.just(requestGuest));

        StepVerifier.create(guestService.createGuest(requestGuest, "today")).expectNextCount(1).verifyComplete();
    }

    @Test
    void createNewGuest_existingGuest() {
        Guest requestGuest = new Guest();
        requestGuest.setUserId(USER1_ID);
        requestGuest.setNickName("guestie");

        Guest existingGuest = new Guest();
        existingGuest.setNickName("guestie");
        existingGuest.setUserId(USER1_ID);
        existingGuest.setGuestId(GUEST1_ID);

        User user = new User();
        user.setUserId(USER1_ID);

        Mockito.when(guestRepository.findByUserIdAndNickName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.just(existingGuest));

        ResGraphException error = new ResGraphException("Guest already exists", HttpStatus.BAD_REQUEST);

        StepVerifier.create(guestService.createGuest(requestGuest, "today"))
                .expectErrorMatches(errorResponse -> errorResponse.equals(error))
                .verify();
    }

    @Test
    void createNewGuest_noSuchUser() {
        Guest requestGuest = new Guest();
        requestGuest.setUserId(USER1_ID);
        requestGuest.setNickName("guestie");

        Mockito.when(guestRepository.findByUserIdAndNickName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.empty());
        Mockito.when(userRepository.findByUserId(USER1_ID)).thenReturn(Mono.empty());

        ResGraphException error = new ResGraphException("No user with id: " + USER1_ID, HttpStatus.BAD_REQUEST);

        StepVerifier.create(guestService.createGuest(requestGuest, "today"))
                .expectErrorMatches(errorResponse -> errorResponse.equals(error))
                .verify();
    }

    @Test
    void updateGuest_guestId() {
        Guest requestGuest = new Guest();
        requestGuest.setGuestId(GUEST1_ID);
        requestGuest.setNickName("guestette");

        Guest existingGuest = new Guest();
        existingGuest.setGuestId(GUEST1_ID);
        existingGuest.setNickName("guestie");

        GuestCreateUpdateResponse response = new GuestCreateUpdateResponse(ResConstants.GUEST_UPDATE + GUEST1_ID,
                requestGuest);

        Mockito.when(guestRepository.findByGuestId(Mockito.any())).thenReturn(Mono.just(existingGuest));
        Mockito.when(guestRepository.save(Mockito.any())).thenReturn(Mono.just(requestGuest));

        StepVerifier.create(guestService.updateGuest(requestGuest, "today"))
                .expectNext(response)
                .verifyComplete();

    }

    @Test
    void updateGuest_guestIdNotFound() {
        Guest requestGuest = new Guest();
        requestGuest.setGuestId(GUEST1_ID);
        requestGuest.setNickName("guestette");

        ResGraphException error = new ResGraphException(ResConstants.GUEST_NOT_FOUND_WITH_ID + GUEST1_ID,
                HttpStatus.NOT_FOUND);

        Mockito.when(guestRepository.findByGuestId(Mockito.any())).thenReturn(Mono.empty());

        StepVerifier.create(guestService.updateGuest(requestGuest, "today"))
                .expectErrorMatches(errorResponse -> errorResponse.equals(error))
                .verify();
    }

    @Test
    void updateGuest_userIdNickName() {
        Guest requestGuest = new Guest();
        requestGuest.setUserId(USER1_ID);
        requestGuest.setNickName("guestie");
        requestGuest.setNotes("notes");

        Guest existingGuest = new Guest();
        existingGuest.setGuestId(GUEST1_ID);
        requestGuest.setUserId(USER1_ID);
        existingGuest.setNickName("guestie");

        GuestCreateUpdateResponse response = new GuestCreateUpdateResponse(ResConstants.GUEST_UPDATE + "null",
                requestGuest);

        Mockito.when(guestRepository.findByUserIdAndNickName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.just(existingGuest));
        Mockito.when(guestRepository.save(Mockito.any())).thenReturn(Mono.just(requestGuest));

        StepVerifier.create(guestService.updateGuest(requestGuest, "today"))
                .expectNext(response)
                .verifyComplete();

    }

    @Test
    void updateGuest_userIdNickNameNotFound() {
        Guest requestGuest = new Guest();
        requestGuest.setUserId(USER1_ID);
        requestGuest.setNickName("guestie");
        requestGuest.setNotes("notes");

        Mockito.when(guestRepository.findByUserIdAndNickName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.empty());

        ResGraphException error = new ResGraphException(ResConstants.GUEST_NOT_FOUND_WITH_ID + GUEST1_ID,
                HttpStatus.NOT_FOUND);

        StepVerifier.create(guestService.updateGuest(requestGuest, "today"))
                .expectErrorMatches(errorResponse -> errorResponse.equals(error))
                .verify();

    }

    @Test
    void updateGuest_noGuestIdentifying() {
        Guest requestGuest = new Guest();
        requestGuest.setNickName("guestie");
        requestGuest.setNotes("notes");

        ResGraphException expectedError = new ResGraphException(ResConstants.GUEST_NO_IDENTIFYING_ERROR,
                HttpStatus.BAD_REQUEST);

        StepVerifier.create(guestService.updateGuest(requestGuest, "today"))
                .expectErrorMatches(error -> error.equals(expectedError))
                .verify();

    }
}
