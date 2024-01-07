package com.yl.reservation.repository;

import com.yl.reservation.model.Address;
import com.yl.reservation.model.Guest;
import com.yl.reservation.model.Reservation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {
    Mono<Reservation> findByReservationID(String reservationId);
    Flux<Reservation> findByHostIdAndGuestId(String hostId, String guestId);

}
