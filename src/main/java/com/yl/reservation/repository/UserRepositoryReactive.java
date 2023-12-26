package com.yl.reservation.repository;

import com.yl.reservation.model.Host;
import com.yl.reservation.model.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepositoryReactive extends ReactiveMongoRepository<User, String> {
    Mono<User> findByUserId(String userId);

    @Query(value = "{$and: [{'lastName': ?0}, { 'phone' : {$elemMatch: { 'value' : ?1} } }] }")
    Mono<User> findByLastNameAndPrimaryPhone(String lastName, String phone);

    @Query(value = "{$and: [{'lastName': ?0}, { 'email' : {$elemMatch: { 'value' : ?1} } }] }")
    Mono<User> findByLastNameAndPrimaryEmail(String lastName, String email);
}
