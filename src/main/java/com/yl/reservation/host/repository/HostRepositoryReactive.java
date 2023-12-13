package com.yl.reservation.host.repository;

import com.yl.reservation.host.model.Host;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface HostRepositoryReactive extends ReactiveMongoRepository<Host,String> {

}
