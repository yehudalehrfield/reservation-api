package com.yl.reservation.repository;

import com.yl.reservation.model.Address;
import com.yl.reservation.model.Guest;
import com.yl.reservation.model.Host;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface GuestRepository extends ReactiveMongoRepository<Guest, String> {
    Mono<Guest> findByUserIdAndNickName(String userId, Address nickName);

    Mono<Guest> findByGuestId(String guestId);

}
