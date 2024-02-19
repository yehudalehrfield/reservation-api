package com.yl.reservation.service.reservation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.Reservation;
import com.yl.reservation.repository.GuestRepository;
import com.yl.reservation.repository.HostRepository;
import com.yl.reservation.repository.ReservationRepository;
import com.yl.reservation.repository.UserRepository;
import com.yl.reservation.service.guest.GuestDetails;
import com.yl.reservation.service.host.HostDetails;
import com.yl.reservation.util.ResConstants;
import com.yl.reservation.util.ResUtil;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReservationService {

  @Autowired
  ReservationRepository reservationRepository;

  @Autowired
  HostRepository hostRepository;

  @Autowired
  GuestRepository guestRepository;

  @Autowired
  UserRepository userRepository;

  public Mono<ReservationSearchResponse> getAllReservations() {
    ReservationSearchResponse response = new ReservationSearchResponse();
    return reservationRepository.findAll()
        .flatMap(res -> Mono
            .zip(hostRepository.findByHostId(res.getHostId()), guestRepository.findByGuestId(res.getGuestId()))
            .flatMap(reservationHostAndGuest -> Mono
                .zip(userRepository.findByUserId(reservationHostAndGuest.getT1().getUserId()),
                    userRepository.findByUserId(reservationHostAndGuest.getT2().getUserId()))
                .flatMap(hostUserAndGuestUser -> {
                  ReservationDetails reservationDetails = new ReservationDetails();
                  reservationDetails.setReservation(res);
                  reservationDetails
                      .setHostDetails(new HostDetails(reservationHostAndGuest.getT1(), hostUserAndGuestUser.getT1()));
                  reservationDetails
                      .setGuestDetails(new GuestDetails(reservationHostAndGuest.getT2(), hostUserAndGuestUser.getT2()));
                  return Mono.just(reservationDetails);
                })))
        .collectList()
        .map(reservationList -> {
          response.setReservationDetailsList(reservationList);
          response.setMessage(ResConstants.RESERVATION_FIND_ALL);
          return response;
        });
  }

  public Mono<ReservationSearchResponse> getReservationById(String reservationId) {
    return reservationRepository.findByReservationId(reservationId)
        .flatMap(res -> Mono
            .zip(hostRepository.findByHostId(res.getHostId()), guestRepository.findByGuestId(res.getGuestId()))
            .flatMap(reservationHostAndGuest -> Mono
                .zip(userRepository.findByUserId(reservationHostAndGuest.getT1().getUserId()),
                    userRepository.findByUserId(reservationHostAndGuest.getT2().getUserId()))
                .map(hostUserAndGuestUser -> {
                  ReservationDetails reservationDetails = new ReservationDetails();
                  reservationDetails.setReservation(res);
                  reservationDetails
                      .setHostDetails(new HostDetails(reservationHostAndGuest.getT1(), hostUserAndGuestUser.getT1()));
                  reservationDetails
                      .setGuestDetails(new GuestDetails(reservationHostAndGuest.getT2(), hostUserAndGuestUser.getT2()));
                  ReservationSearchResponse response = new ReservationSearchResponse();
                  response.setReservationDetailsList(List.of(reservationDetails));
                  response.setMessage(ResConstants.RESERVATION_FIND + res.getReservationId());
                  return response;
                })));
  }

  public Mono<ReservationCreateUpdateResponse> createReservation(Reservation requestReservation,
      String createDateTime) {
    return validateNotExistingReservation(requestReservation)
        .flatMap(res -> {
          // todo: change strucutre of the validation method. throw error from within and
          // catch it from the controller... no need for if-else...
          // if successful, do the same for other api's as well...
          if (res.equals(Boolean.TRUE))
            throw new ResGraphException(ResConstants.RESERVATION_ALREADY_EXISTS_ERROR, HttpStatus.BAD_REQUEST);
          else {
            return Mono
                .zip(hostRepository.findByHostId(requestReservation.getHostId()),
                    guestRepository.findByGuestId(requestReservation.getGuestId()))
                .flatMap(hostAndGuest -> {
                  // todo: validate both host and user exist
                  // todo: validate dates do not conflict with existing reservation for this host
                  // -> get all reservations for this host and check the dates
                  requestReservation.setReservationId(ResUtil.generateId());
                  requestReservation.setCreatedDate(createDateTime);
                  requestReservation.setLastUpdated(createDateTime);
                  return reservationRepository.save(requestReservation)
                      .map(createdRes -> new ReservationCreateUpdateResponse(
                          ResConstants.RESERVATION_CREATE + createdRes.getReservationId(), createdRes));
                });
          }
        });
  }

  public Mono<ReservationCreateUpdateResponse> updateReservation(Reservation requestReservation,
      String updateDateTime) {
    return reservationRepository.findByReservationId(requestReservation.getReservationId())
        .flatMap(existingReservation -> {
          // todo: update this to call Mapper method to update reservation
          Reservation updatedReservation = new Reservation();
          return reservationRepository.save(updatedReservation)
              .map(updatedRes -> new ReservationCreateUpdateResponse(
                  ResConstants.RESERVATION_UPDATE + updatedRes.getReservationId(), updatedRes));
        })
        .switchIfEmpty(
            Mono.error(new ResGraphException(
                ResConstants.RESERVATION_NOT_FOUND_WITH_ID + requestReservation.getReservationId(),
                HttpStatus.NOT_FOUND)));

  }

  private Mono<Boolean> validateNotExistingReservation(Reservation reservation) {
    return reservationRepository
        .findByHostIdAndGuestIdAndStartDate(reservation.getHostId(), reservation.getGuestId(),
            reservation.getStartDate())
        .map(res -> true)
        .switchIfEmpty(Mono.just(false));
  }

}
