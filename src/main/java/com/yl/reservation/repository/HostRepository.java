package com.yl.reservation.repository;

import com.yl.reservation.model.Address;
import com.yl.reservation.model.Host;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostRepository extends MongoRepository<Host, String> {
    Optional<Host> findByLastNameAndAddress(String lastName, Address address);

    @Query(value = "{$and: [{'lastName': ?0}, { 'phone' : {$elemMatch: { 'value' : ?1} } }] }")
    List<Host> findByLastNameAndPrimaryPhone(String lastName, String phone);

    @Query(value = "{$and: [{'lastName': ?0}, { 'email' : {$elemMatch: { 'value' : ?1} } }] }")
    List<Host> findByLastNameAndPrimaryEmail(String lastName, String email);
}
