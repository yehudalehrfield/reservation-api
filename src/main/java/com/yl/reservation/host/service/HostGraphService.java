package com.yl.reservation.host.service;

import com.yl.reservation.host.model.Host;
import com.yl.reservation.host.repository.HostRepositoryReactive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HostGraphService {
    @Autowired
    HostRepositoryReactive hostRepositoryReactive;

    public Mono<Host> getHostById(String id){
        return hostRepositoryReactive.findById(id);
    }

    public Flux<Host> getAllHosts(){
        return hostRepositoryReactive.findAll();
    }
}
