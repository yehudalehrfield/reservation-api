package com.yl.reservation.host.controller;

import com.yl.reservation.host.exception.GraphQLException;
import com.yl.reservation.host.model.Host;
import com.yl.reservation.host.service.HostGraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class HostGraphController {

    @Autowired
    HostGraphService hostGraphService;

    private static final Logger logger = LoggerFactory.getLogger(HostGraphController.class);
    @QueryMapping
    public Flux<Host> getAllHosts(){
        return hostGraphService.getAllHosts()
                .switchIfEmpty(Mono.error(new GraphQLException("No Hosts Found", HttpStatus.NOT_FOUND)))
                .doOnNext(res -> logger.info(String.valueOf(res)))
                .doFinally(res -> logger.info("Search for all hosts with response: {}",res))
                .cache();
    }
    @QueryMapping
    public Mono<Host> hostById(@Argument String id){
        return hostGraphService.getHostById(id)
                .switchIfEmpty(Mono.error(new GraphQLException("Host not found with id: " + id, HttpStatus.NOT_FOUND)))
                .doOnNext(res -> logger.info(String.valueOf(res)))
                .doFinally(res -> logger.info("Search for host with id: {}, response: {}", id, res))
                .cache();
    }




}
