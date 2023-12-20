package com.yl.reservation.repository;

import com.yl.reservation.model.Address;
import com.yl.reservation.model.Host;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface HostRepositoryReactive extends ReactiveMongoRepository<Host, String> {
    Mono<Host> findByLastNameAndAddress(String lastName, Address address);

    @Query(value = "{$and: [{'lastName': ?0}, { 'phone' : {$elemMatch: { 'value' : ?1} } }] }")
    Flux<Host> findByLastNameAndPrimaryPhone(String lastName, String phone);

    @Query(value = "{$and: [{'lastName': ?0}, { 'email' : {$elemMatch: { 'value' : ?1} } }] }")
    Flux<Host> findByLastNameAndPrimaryEmail(String lastName, String email);
}
