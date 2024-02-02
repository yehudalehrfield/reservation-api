package com.yl.reservation.repository;

import com.yl.reservation.model.Guest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface GuestRepository extends ReactiveMongoRepository<Guest, String> {
    Mono<Guest> findByUserIdAndNickName(String userId, String nickName);

    Mono<Guest> findByGuestId(String guestId);

}
