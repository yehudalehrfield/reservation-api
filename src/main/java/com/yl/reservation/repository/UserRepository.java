package com.yl.reservation.repository;

import com.yl.reservation.model.Host;
import com.yl.reservation.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUserId(String userId);
}
