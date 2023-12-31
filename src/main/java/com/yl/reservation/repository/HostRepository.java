package com.yl.reservation.repository;

import com.yl.reservation.model.Address;
import com.yl.reservation.model.Host;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface HostRepository extends ReactiveMongoRepository<Host, String> {
    Mono<Host> findByUserIdAndAddress(String userId, Address address);

    Mono<Host> findByHostId(String hostId);

//    @Query(value = "{$and: [{'lastName': ?0}, { 'phone' : {$elemMatch: { 'value' : ?1} } }] }")
//    Flux<Host> findByLastNameAndPrimaryPhone(String lastName, String phone);

//    @Query(value = "{$and: [{'lastName': ?0}, { 'email' : {$elemMatch: { 'value' : ?1} } }] }")
//    Flux<Host> findByLastNameAndPrimaryEmail(String lastName, String email);
}
