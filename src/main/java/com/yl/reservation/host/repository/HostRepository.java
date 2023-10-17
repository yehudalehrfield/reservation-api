package com.yl.reservation.host.repository;

import com.yl.reservation.host.model.Address;
import com.yl.reservation.host.model.Host;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HostRepository extends MongoRepository<Host, String> {
    Optional<Host> findByLastNameAndAddress(String lastName, Address address);
}
