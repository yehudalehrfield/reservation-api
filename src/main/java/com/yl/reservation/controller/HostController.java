 package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.service.HostService;
import com.yl.reservation.service.HostCreateUpdateResponse;
import com.yl.reservation.service.HostSearchResponse;
import com.yl.reservation.service.HostCreateUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

 //todo: fix logging

 @RestController
public class HostController {

    @Autowired
    HostService hostService;

    private static final Logger logger = LoggerFactory.getLogger(HostController.class);
    @QueryMapping
    public Mono<HostSearchResponse> getAllHosts(@Argument boolean includeUserInfo){
        return hostService.getAllHosts(includeUserInfo)
                .switchIfEmpty(Mono.error(new ResGraphException("No Hosts Found", HttpStatus.NOT_FOUND)))
                .doOnSuccess(res -> logger.info("Fetched all hosts: {}", res.getHostDetailsList()))
                .onErrorResume(error -> {
                    logger.error("Error fetching all hosts");
                    return Mono.error(error);
                })
                .cache();
    }
    @QueryMapping
    public Mono<HostSearchResponse> getHostById(@Argument String hostId, @Argument boolean includeUserInfo){
        return hostService.getHostById(hostId, includeUserInfo)
                .switchIfEmpty(Mono.error(new ResGraphException("Host not found with id: " + hostId, HttpStatus.NOT_FOUND)))
                .doOnSuccess(res -> logger.info("Fetched host: {}", res.getHostDetailsList()))
                .onErrorResume(error -> {
                    logger.error("Error fetching host {}", hostId);
                    return Mono.error(error);
                })
                .cache();
    }

    @MutationMapping
    public Mono<HostCreateUpdateResponse> createUpdateHost(@Argument HostCreateUpdateRequest hostCreateUpdateRequest){
        return hostService.createUpdateHost(hostCreateUpdateRequest)
                .doOnSuccess(res -> logger.info(
                        "Successful {} of host {}",
                        (res.getHost().getCreatedDate().equals(res.getHost().getLastUpdated())) ? "creation" : "update",
                        res.getHost().getHostId())
                )
                .onErrorResume(error -> {
                    logger.error("Error in create/update host {}", hostCreateUpdateRequest.getHost());
                    return Mono.error(error);
                })
                .cache();
    }




}
