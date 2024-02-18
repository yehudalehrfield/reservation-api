package com.yl.reservation.repository;

import com.yl.reservation.model.Reservation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {
    Mono<Reservation> findByReservationId(String reservationId);

    Mono<Reservation> findByHostIdAndGuestIdAndStartDate(String hostId, String guestId, String startDate);

    Flux<Reservation> findByHostIdAndGuestId(String hostId, String guestId);

}
