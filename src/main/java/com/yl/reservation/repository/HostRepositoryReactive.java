package com.yl.reservation.repository;

import com.yl.reservation.model.Address;
import com.yl.reservation.model.Host;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface HostRepositoryReactive extends ReactiveMongoRepository<Host,String> {
    Mono<Host> findByLastNameAndAddress(String lastName, Address address
    );

    @Query(value = "{ 'phone' : {$elemMatch: { 'value' : ?1} }}")
    Mono<Host> findByLastNameAndPrimaryPhone(String lastName, String phone);
}
