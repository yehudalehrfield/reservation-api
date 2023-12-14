package com.yl.reservation.repository;

import com.yl.reservation.model.Address;
import com.yl.reservation.model.Host;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface HostRepositoryReactive extends ReactiveMongoRepository<Host,String> {
    public Mono<Host> findByLastNameAndAddress(String lastName, Address address
    );
}
