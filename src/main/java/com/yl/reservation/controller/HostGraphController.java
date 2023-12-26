 package com.yl.reservation.controller;

import com.yl.reservation.exception.GraphQLException;
import com.yl.reservation.model.Host;
import com.yl.reservation.service.HostGraphService;
import com.yl.reservation.service.HostResponse;
import com.yl.reservation.service.HostUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
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
    public Mono<Host> hostById(@Argument String hostId){
        //todo: fix logging
        return hostGraphService.getHostById(hostId)
                .switchIfEmpty(Mono.error(new GraphQLException("Host not found with id: " + hostId,
                        HttpStatus.NOT_FOUND)))
                .doOnNext(res -> logger.info(String.valueOf(res)))
                .doFinally(res -> logger.info("Search for host with id: {}, response: {}", hostId, res))
                .cache();
    }

    @MutationMapping
    //todo: logging
    public Mono<HostResponse> createUpdateHost(@Argument HostUpdateRequest hostUpdateRequest){
        return hostGraphService.createUpdateHost(hostUpdateRequest);
    }




}
