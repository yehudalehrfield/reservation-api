package com.yl.reservation.repository;

import com.yl.reservation.model.Address;
import com.yl.reservation.model.Host;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface HostRepository extends ReactiveMongoRepository<Host, String> {
    Mono<Host> findByUserIdAndAddress(String userId, Address address);

    Mono<Host> findByHostId(String hostId);

}
