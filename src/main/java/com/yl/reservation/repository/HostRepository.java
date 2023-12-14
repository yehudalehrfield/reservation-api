package com.yl.reservation.repository;

import com.yl.reservation.model.Address;
import com.yl.reservation.model.Host;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HostRepository extends MongoRepository<Host, String> {
    Optional<Host> findByLastNameAndAddress(String lastName, Address address);
}
