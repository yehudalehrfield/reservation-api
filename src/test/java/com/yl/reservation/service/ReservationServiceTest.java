package com.yl.reservation.service;

import com.yl.reservation.model.Guest;
import com.yl.reservation.model.Host;
import com.yl.reservation.model.Reservation;
import com.yl.reservation.model.User;
import com.yl.reservation.repository.GuestRepository;
import com.yl.reservation.repository.HostRepository;
import com.yl.reservation.repository.ReservationRepository;
import com.yl.reservation.repository.UserRepository;
import com.yl.reservation.service.guest.GuestDetails;
import com.yl.reservation.service.host.HostDetails;
import com.yl.reservation.service.reservation.*;
import com.yl.reservation.util.ResConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @InjectMocks
    ReservationService reservationService;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    HostRepository hostRepository;

    @Mock
    GuestRepository guestRepository;

    @Mock
    UserRepository userRepository;

    private final String RES_ID1 = "resId1";
    private final String RES_ID2 = "resId2";
    private final String GUEST_ID1 = "guestId1";
    private final String GUEST_ID2 = "guestId2";
    private final String HOST_ID1 = "hostId1";
    private final String HOST_ID2 = "hostId2";
    private final String USER_ID1 = "userId1";
    private final String USER_ID2 = "userId2";

    @Test
    void getAllReservations() {
        Reservation res1 = new Reservation();
        res1.setReservationId(RES_ID1);
        res1.setHostId(HOST_ID1);
        res1.setGuestId(GUEST_ID1);
        res1.setStartDate("today");
        Reservation res2 = new Reservation();
        res2.setReservationId(RES_ID2);
        res2.setStartDate("yesterday");
        res2.setHostId(HOST_ID2);
        res2.setGuestId(GUEST_ID2);

        User user1 = new User();
        user1.setUserId(USER_ID1);

        User user2 = new User();
        user2.setUserId(USER_ID2);

        Host host1 = new Host();
        host1.setHostId(HOST_ID1);
        host1.setUserId(USER_ID1);

        Host host2 = new Host();
        host2.setHostId(HOST_ID2);
        host2.setUserId(USER_ID2);

        Guest guest1 = new Guest();
        guest1.setGuestId(GUEST_ID1);
        guest1.setUserId(USER_ID2);

        Guest guest2 = new Guest();
        guest2.setGuestId(GUEST_ID2);
        guest2.setUserId(USER_ID1);

        HostDetails hostDetails1 = new HostDetails(host1, user1);
        GuestDetails guestDetails1 = new GuestDetails(guest1, user2);

        HostDetails hostDetails2 = new HostDetails(host2, user2);
        GuestDetails guestDetails2 = new GuestDetails(guest2, user1);

        ReservationDetails resDetails1 = new ReservationDetails(res1, hostDetails1, guestDetails1);
        ReservationDetails resDetails2 = new ReservationDetails(res2, hostDetails2, guestDetails2);

        ReservationSearchResponse reservationSearchResponse = new ReservationSearchResponse("Retrieved all " +
                "reservations", List.of(resDetails1, resDetails2));

        Mockito.when(reservationRepository.findAll()).thenReturn(Flux.just(res1, res2));
        Mockito.when(hostRepository.findByHostId(HOST_ID1)).thenReturn(Mono.just(host1));
        Mockito.when(hostRepository.findByHostId(HOST_ID2)).thenReturn(Mono.just(host2));
        Mockito.when(guestRepository.findByGuestId(GUEST_ID1)).thenReturn(Mono.just(guest1));
        Mockito.when(guestRepository.findByGuestId(GUEST_ID2)).thenReturn(Mono.just(guest2));
        Mockito.when(userRepository.findByUserId(USER_ID1)).thenReturn(Mono.just(user1));
        Mockito.when(userRepository.findByUserId(USER_ID2)).thenReturn(Mono.just(user2));

        StepVerifier.create(reservationService.getAllReservations()).expectNext(reservationSearchResponse).verifyComplete();
    }

    @Test
    void getReservationById() {
        Reservation res1 = new Reservation();
        res1.setReservationId(RES_ID1);
        res1.setHostId(HOST_ID1);
        res1.setGuestId(GUEST_ID1);
        res1.setStartDate("today");


        User user1 = new User();
        user1.setUserId(USER_ID1);

        User user2 = new User();
        user2.setUserId(USER_ID2);

        Host host1 = new Host();
        host1.setHostId(HOST_ID1);
        host1.setUserId(USER_ID1);


        Guest guest1 = new Guest();
        guest1.setGuestId(GUEST_ID1);
        guest1.setUserId(USER_ID2);

        HostDetails hostDetails1 = new HostDetails(host1, user1);
        GuestDetails guestDetails1 = new GuestDetails(guest1, user2);

        ReservationDetails resDetails1 = new ReservationDetails(res1, hostDetails1, guestDetails1);

        ReservationSearchResponse reservationSearchResponse = new ReservationSearchResponse("Retrieved reservation " +
                "resId1", List.of(resDetails1));

        Mockito.when(reservationRepository.findByReservationId(RES_ID1)).thenReturn(Mono.just(res1));
        Mockito.when(hostRepository.findByHostId(HOST_ID1)).thenReturn(Mono.just(host1));
        Mockito.when(guestRepository.findByGuestId(GUEST_ID1)).thenReturn(Mono.just(guest1));
        Mockito.when(userRepository.findByUserId(USER_ID1)).thenReturn(Mono.just(user1));
        Mockito.when(userRepository.findByUserId(USER_ID2)).thenReturn(Mono.just(user2));

        StepVerifier.create(reservationService.getReservationById(RES_ID1)).expectNext(reservationSearchResponse).verifyComplete();
    }

    @Test
    void createReservation() {
        Reservation res = new Reservation();
        res.setHostId(HOST_ID1);
        res.setGuestId(GUEST_ID1);
        res.setStartDate("2024-01-01");
        res.setEndDate("2024-01-05");

        ReservationCreateUpdateRequest request = new ReservationCreateUpdateRequest();
        request.setReservation(res);

        User user1 = new User();
        user1.setUserId(USER_ID1);

        User user2 = new User();
        user2.setUserId(USER_ID2);

        Host host1 = new Host();
        host1.setHostId(HOST_ID1);
        host1.setUserId(USER_ID1);

        Guest guest1 = new Guest();
        guest1.setGuestId(GUEST_ID1);
        guest1.setUserId(USER_ID2);

        ReservationCreateUpdateResponse response =
                new ReservationCreateUpdateResponse(ResConstants.RESERVATION_CREATE + "null", res);

        Mockito.when(reservationRepository.findByHostIdAndGuestIdAndStartDate(HOST_ID1, GUEST_ID1, "2024-01-01")).thenReturn(Mono.empty());
        Mockito.when(reservationRepository.findByHostId(HOST_ID1)).thenReturn(Flux.empty());
        Mockito.when(reservationRepository.save(Mockito.any())).thenReturn(Mono.just(res));
        Mockito.when(hostRepository.findByHostId(HOST_ID1)).thenReturn(Mono.just(host1));
        Mockito.when(guestRepository.findByGuestId(GUEST_ID1)).thenReturn(Mono.just(guest1));

        StepVerifier.create(reservationService.createReservation(res, "today"))
                .expectNextMatches(resp ->
                        resp.getReservation().getHostId().equals(response.getReservation().getHostId())
                                && resp.getReservation().getGuestId().equals(response.getReservation().getGuestId())
                                && resp.getReservation().getStartDate().equals(response.getReservation().getStartDate())
                                && resp.getReservation().getEndDate().equals(response.getReservation().getEndDate()))
//                .expectNext(response)
                .verifyComplete();
    }

    //todo:
    // 1. existing reservation
    // 2. bad hostId
    // 3. bad guestId
    // 4. date conflict(s)

    @Test
    void updateReservation() {
        Reservation requestRes = new Reservation();
        requestRes.setReservationId(RES_ID1);
        requestRes.setHostId(HOST_ID1);
        requestRes.setGuestId(GUEST_ID1);
        requestRes.setStartDate("2024-01-01");
        requestRes.setEndDate("2024-01-05");

        Reservation existingRes = new Reservation();
        existingRes.setReservationId(RES_ID1);
        existingRes.setHostId(HOST_ID1);
        existingRes.setGuestId(GUEST_ID1);
        existingRes.setStartDate("2023-12-30");
        existingRes.setEndDate("2024-01-01");

        ReservationCreateUpdateRequest request = new ReservationCreateUpdateRequest();
        request.setReservation(requestRes);

        User user1 = new User();
        user1.setUserId(USER_ID1);

        User user2 = new User();
        user2.setUserId(USER_ID2);

        Host host1 = new Host();
        host1.setHostId(HOST_ID1);
        host1.setUserId(USER_ID1);

        Guest guest1 = new Guest();
        guest1.setGuestId(GUEST_ID1);
        guest1.setUserId(USER_ID2);

        ReservationCreateUpdateResponse response =
                new ReservationCreateUpdateResponse(ResConstants.RESERVATION_UPDATE + RES_ID1, requestRes);

        Mockito.when(reservationRepository.findByReservationId(RES_ID1)).thenReturn(Mono.just(existingRes));
        Mockito.when(reservationRepository.findByHostId(HOST_ID1)).thenReturn(Flux.just(existingRes));
        Mockito.when(reservationRepository.save(Mockito.any())).thenReturn(Mono.just(requestRes));

        StepVerifier.create(reservationService.updateReservation(requestRes, "today"))
                .expectNext(response)
                .verifyComplete();
    }

    //todo:
    // 1. reservation not found
    // 2. date conflict(s)


}
