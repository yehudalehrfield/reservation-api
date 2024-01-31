package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.Guest;
import com.yl.reservation.model.User;
import com.yl.reservation.service.*;
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
public class GuestControllerTest {

    @InjectMocks
    GuestController guestController;

    @Mock
    GuestService guestService;

    private final String GUEST_ID1 = "guestId1";
    private final String GUEST_ID2 = "guestId2";
    private final String USER_ID1 = "userId1";
    private final String USER_ID2 = "userId2";

    @Test
    void getAllGuests(){
        Guest guest1 = new Guest();
        guest1.setGuestId(GUEST_ID1);
        Guest guest2 = new Guest();
        guest2.setGuestId(GUEST_ID2);

        User user1 = new User();
        user1.setUserId(USER_ID1);
        User user2 = new User();
        user2.setUserId(USER_ID2);

        GuestDetails guestDetails1 = new GuestDetails(guest1, null);
        GuestDetails guestDetails2 = new GuestDetails(guest2, null);
        GuestDetails guestDetails3 = new GuestDetails(guest1, user1);
        GuestDetails guestDetails4 = new GuestDetails(guest2, user2);

        GuestSearchResponse guestSearchResponseNoUserDetails = new GuestSearchResponse("Retrieved all guests", List.of(guestDetails1, guestDetails2));
        GuestSearchResponse guestSearchResponseWithUserGDetails = new GuestSearchResponse("Retrieved all guests", List.of(guestDetails3, guestDetails4));

        Mockito.when(guestService.getAllGuests(false)).thenReturn(Mono.just(guestSearchResponseNoUserDetails));
        Mockito.when(guestService.getAllGuests(true)).thenReturn(Mono.just(guestSearchResponseWithUserGDetails));

        StepVerifier.create(guestController.getAllGuests(false))
                .expectNext(guestSearchResponseNoUserDetails)
                .verifyComplete();

        StepVerifier.create(guestController.getAllGuests(true))
                .expectNext(guestSearchResponseWithUserGDetails)
                .verifyComplete();
    }

    @Test
    void getAllGuestsError(){
        ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(guestService.getAllGuests(false)).thenReturn(Mono.error(exception));

        StepVerifier.create(guestController.getAllGuests(false))
                .expectErrorMatches(error -> error.equals(exception))
                .verify();
    }
    @Test
    void getGuestById(){
        Guest guest1 = new Guest();
        guest1.setGuestId(GUEST_ID1);

        User user1 = new User();
        user1.setUserId(USER_ID1);

        GuestDetails guestDetails1 = new GuestDetails(guest1, null);
        GuestDetails guestDetails2 = new GuestDetails(guest1, user1);


        GuestSearchResponse guestSearchResponseNoUserDetails = new GuestSearchResponse("Retrieved all guests", List.of(guestDetails1));
        GuestSearchResponse guestSearchResponseWithUserGDetails = new GuestSearchResponse("Retrieved all guests", List.of(guestDetails2));

        Mockito.when(guestService.getGuestById(GUEST_ID1, false)).thenReturn(Mono.just(guestSearchResponseNoUserDetails));
        Mockito.when(guestService.getGuestById(GUEST_ID1, true)).thenReturn(Mono.just(guestSearchResponseWithUserGDetails));

        StepVerifier.create(guestController.getGuestById(GUEST_ID1, false))
                .expectNext(guestSearchResponseNoUserDetails)
                .verifyComplete();

        StepVerifier.create(guestController.getGuestById(GUEST_ID1, true))
                .expectNext(guestSearchResponseWithUserGDetails)
                .verifyComplete();
    }

    @Test
    void getGuestByIdError(){
        ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(guestService.getGuestById(GUEST_ID1, false)).thenReturn(Mono.error(exception));

        StepVerifier.create(guestController.getGuestById(GUEST_ID1, false))
                .expectErrorMatches(error -> error.equals(exception))
                .verify();
    }

    @Test
    void createGuest(){
        Guest guest = new Guest();

        GuestCreateUpdateRequest request = new GuestCreateUpdateRequest();
        request.setGuest(guest);

        GuestCreateUpdateResponse response = new GuestCreateUpdateResponse("Guest created successfully", guest);

        Mockito.when(guestService.createNewGuest(Mockito.any(), Mockito.anyString())).thenReturn(Mono.just(response));

        StepVerifier.create(guestController.createGuest(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void createGuestError(){
        Guest guest = new Guest();

        GuestCreateUpdateRequest request = new GuestCreateUpdateRequest();
        request.setGuest(guest);

        ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(guestService.createNewGuest(Mockito.any(), Mockito.anyString())).thenReturn(Mono.error(exception));

        StepVerifier.create(guestController.createGuest(request))
                .expectErrorMatches(error -> error.equals(exception))
                .verify();
    }

    @Test
    void updateGuest(){
        Guest guest = new Guest();

        GuestCreateUpdateRequest request = new GuestCreateUpdateRequest();
        request.setGuest(guest);

        GuestCreateUpdateResponse response = new GuestCreateUpdateResponse("Guest created successfully", guest);

        Mockito.when(guestService.updateGuest(Mockito.any(), Mockito.anyString())).thenReturn(Mono.just(response));

        StepVerifier.create(guestController.updateGuest(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void updateGuestError() {
        Guest guest = new Guest();

        GuestCreateUpdateRequest request = new GuestCreateUpdateRequest();
        request.setGuest(guest);

        ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(guestService.updateGuest(Mockito.any(), Mockito.anyString())).thenReturn(Mono.error(exception));

        StepVerifier.create(guestController.updateGuest(request))
                .expectErrorMatches(error -> error.equals(exception))
                .verify();
    }
}

