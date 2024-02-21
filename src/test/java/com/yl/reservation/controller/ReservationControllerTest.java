package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.Guest;
import com.yl.reservation.model.Host;
import com.yl.reservation.model.Reservation;
import com.yl.reservation.model.User;
import com.yl.reservation.service.guest.GuestDetails;
import com.yl.reservation.service.host.HostDetails;
import com.yl.reservation.service.reservation.*;
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
public class ReservationControllerTest {

    @InjectMocks
    ReservationController reservationController;

    @Mock
    ReservationService reservationService;

    private final String RES_ID1 = "resId1";
    private final String RES_ID2 = "resId2";
    private final String GUEST_ID = "guestId";
    private final String HOST_ID = "hostId";
    private final String USER_ID1 = "userId1";
    private final String USER_ID2 = "userId2";
    @Test
    void getAllReservations(){
        Reservation res1 = new Reservation();
        res1.setReservationId(RES_ID1);
        res1.setStartDate("today");
        Reservation res2 = new Reservation();
        res2.setReservationId(RES_ID2);
        res2.setStartDate("yesterday");

        Guest guest = new Guest();
        guest.setGuestId(GUEST_ID);
        guest.setUserId(USER_ID1);

        User user1 = new User();
        user1.setUserId(USER_ID1);

        GuestDetails guestDetails = new GuestDetails(guest,user1);

        Host host = new Host();
        host.setHostId(HOST_ID);
        host.setUserId(USER_ID2);

        User user2 = new User();
        user2.setUserId(USER_ID2);

        HostDetails hostDetails = new HostDetails(host,user1);

        ReservationDetails resDetails1 = new ReservationDetails(res1, hostDetails, guestDetails);
        ReservationDetails resDetails2 = new ReservationDetails(res2, hostDetails, guestDetails);

        ReservationSearchResponse reservationSearchResponse = new ReservationSearchResponse("Retrieved all reservations", List.of(resDetails1, resDetails2));

        Mockito.when(reservationService.getAllReservations()).thenReturn(Mono.just(reservationSearchResponse));

        StepVerifier.create(reservationController.getAllReservations())
                .expectNext(reservationSearchResponse)
                .verifyComplete();
    }

    @Test
    void getAllReservationsError(){
        ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(reservationService.getAllReservations()).thenReturn(Mono.error(exception));

        StepVerifier.create(reservationController.getAllReservations())
                .expectErrorMatches(error -> error.equals(exception))
                .verify();
    }

    @Test
    void getReservationById(){
        Reservation res = new Reservation();
        res.setReservationId(RES_ID1);
        res.setStartDate("today");

        Guest guest = new Guest();
        guest.setGuestId(GUEST_ID);
        guest.setUserId(USER_ID1);

        User user1 = new User();
        user1.setUserId(USER_ID1);

        GuestDetails guestDetails = new GuestDetails(guest,user1);

        Host host = new Host();
        host.setHostId(HOST_ID);
        host.setUserId(USER_ID2);

        User user2 = new User();
        user2.setUserId(USER_ID2);

        HostDetails hostDetails = new HostDetails(host,user1);

        ReservationDetails resDetails = new ReservationDetails(res, hostDetails, guestDetails);

        ReservationSearchResponse reservationSearchResponse = new ReservationSearchResponse("Retrieved reservation resId1", List.of(resDetails));

        Mockito.when(reservationService.getReservationById(RES_ID1)).thenReturn(Mono.just(reservationSearchResponse));

        StepVerifier.create(reservationController.getReservationById(RES_ID1))
                .expectNext(reservationSearchResponse)
                .verifyComplete();
    }

    @Test
    void getReservationByIdError(){
        ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(reservationService.getReservationById(Mockito.any())).thenReturn(Mono.error(exception));

        StepVerifier.create(reservationController.getReservationById(RES_ID1))
                .expectErrorMatches(error -> error.equals(exception))
                .verify();
    }

    @Test
    void createReservation(){
        Reservation res = new Reservation();
        ReservationCreateUpdateRequest request = new ReservationCreateUpdateRequest();
        request.setReservation(res);

        ReservationCreateUpdateResponse response = new ReservationCreateUpdateResponse("Successfully created reservation", res);

        Mockito.when(reservationService.createReservation(Mockito.any(), Mockito.anyString())).thenReturn(Mono.just(response));

        StepVerifier.create(reservationController.createReservation(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void createReservationError(){
        ReservationCreateUpdateRequest request = new ReservationCreateUpdateRequest();

        ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(reservationService.createReservation(Mockito.any(), Mockito.anyString())).thenReturn(Mono.error(exception));

        StepVerifier.create(reservationController.createReservation(request))
                .expectErrorMatches(error -> error.equals(exception))
                .verify();
    }

    @Test
    void updateReservation(){
        Reservation res = new Reservation();
        ReservationCreateUpdateRequest request = new ReservationCreateUpdateRequest();
        request.setReservation(res);

        ReservationCreateUpdateResponse response = new ReservationCreateUpdateResponse("Successfully created reservation", res);

        Mockito.when(reservationService.updateReservation(Mockito.any(), Mockito.anyString())).thenReturn(Mono.just(response));

        StepVerifier.create(reservationController.updateReservation(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void updateReservationError(){
        ReservationCreateUpdateRequest request = new ReservationCreateUpdateRequest();

        ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(reservationService.updateReservation(Mockito.any(), Mockito.anyString())).thenReturn(Mono.error(exception));

        StepVerifier.create(reservationController.updateReservation(request))
                .expectErrorMatches(error -> error.equals(exception))
                .verify();
    }

}
