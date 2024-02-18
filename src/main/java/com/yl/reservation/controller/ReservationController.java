package com.yl.reservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.service.reservation.ReservationCreateUpdateRequest;
import com.yl.reservation.service.reservation.ReservationCreateUpdateResponse;
import com.yl.reservation.service.reservation.ReservationSearchResponse;
import com.yl.reservation.service.reservation.ReservationService;
import com.yl.reservation.util.ResConstants;
import com.yl.reservation.util.ResLogger;
import com.yl.reservation.util.ResUtil;

import reactor.core.publisher.Mono;

@Controller
public class ReservationController {

  @Autowired
  ReservationService reservationService;

  @QueryMapping
  public Mono<ReservationSearchResponse> getAllReservations() {
    ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "getAllReservations");
    return reservationService.getAllReservations()
        .switchIfEmpty(returnNotFound(resLogger, "No reservations found"))
        .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
        .onErrorResume(error -> {
          resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
          return Mono.error(error);
        })
        .cache();
  }

  @QueryMapping
  public Mono<ReservationSearchResponse> getReservationById(@Argument String reservationId) {
    ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "getReservationById");
    return reservationService.getReservationById(reservationId)
        .switchIfEmpty(returnNotFound(resLogger, ResConstants.RESERVATION_NOT_FOUND_WITH_ID + reservationId))
        .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
        .onErrorResume(error -> {
          resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, error.toString());
          return Mono.error(error);
        })
        .cache();
  }

  @MutationMapping
  public Mono<ReservationCreateUpdateResponse> createReservation(
      @Argument ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
    ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "createGuest");
    String createDateTime = ResUtil.getCurrentDateTimeString();
    return reservationService.createReservation(reservationCreateUpdateRequest.getReservation(), createDateTime)
        .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
        .onErrorResume(error -> {
          resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, null);
          return Mono.error(error);
        });
  }

  @MutationMapping
  public Mono<ReservationCreateUpdateResponse> updateReservation(
      @Argument ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
    ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "createGuest");
    String updateDateTime = ResUtil.getCurrentDateTimeString();
    return reservationService.updateReservation(reservationCreateUpdateRequest.getReservation(), updateDateTime)
        .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
        .onErrorResume(error -> {
          resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, null);
          return Mono.error(error);
        });
  }

  // todo: make this a util method
  private static Mono<ReservationSearchResponse> returnNotFound(ResLogger resLogger, String message) {
    resLogger.setValuesToLogger(HttpStatus.NOT_FOUND, null);
    return Mono.error(new ResGraphException(message, HttpStatus.NOT_FOUND));
  }

}
