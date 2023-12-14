package com.yl.reservation.host.repository;

import com.yl.reservation.host.model.Address;
import com.yl.reservation.host.model.Host;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface HostRepositoryReactive extends ReactiveMongoRepository<Host,String> {
    public Mono<Host> findByLastNameAndAddress(String lastName, Address address
    );
}
