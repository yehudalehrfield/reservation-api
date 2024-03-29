package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.service.host.HostCreateUpdateRequest;
import com.yl.reservation.service.host.HostCreateUpdateResponse;
import com.yl.reservation.service.host.HostSearchResponse;
import com.yl.reservation.service.host.HostService;
import com.yl.reservation.util.ResLogger;
import com.yl.reservation.util.ResUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HostController {

    @Autowired
    HostService hostService;

    @QueryMapping
    public Mono<HostSearchResponse> getAllHosts(@Argument boolean includeUserInfo) {
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "getAllHosts");
        return hostService.getAllHosts(includeUserInfo)
                .switchIfEmpty(returnNotFound(resLogger, "No hosts found"))
                .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                    return Mono.error(error);
                })
                .cache();
    }


    @QueryMapping
    public Mono<HostSearchResponse> getHostById(@Argument String hostId, @Argument boolean includeUserInfo) {
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "getHostById");
        return hostService.getHostById(hostId, includeUserInfo)
                .switchIfEmpty(returnNotFound(resLogger, "Host not found with id: " + hostId))
                .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                    return Mono.error(error);
                })
                .cache();
    }

    @MutationMapping
    public Mono<HostCreateUpdateResponse> createHost(@Argument HostCreateUpdateRequest hostCreateUpdateRequest) {
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "createHost");
        String createDateTime = ResUtil.getCurrentDateTimeString();
        return hostService.createHost(hostCreateUpdateRequest.getHost(), createDateTime)
                .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, null);
                    return Mono.error(error);
                })
                .cache();

    }

    @MutationMapping
    Mono<HostCreateUpdateResponse> updateHost(@Argument HostCreateUpdateRequest hostCreateUpdateRequest) {
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "updateHost");
        String updateDateTime = ResUtil.getCurrentDateTimeString();
        return hostService
                .updateHost(hostCreateUpdateRequest.getHost(), hostCreateUpdateRequest.getIsAddressUpdate(),
                        updateDateTime)
                .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, null);
                    return Mono.error(error);
                })
                .cache();
    }

    private static Mono<HostSearchResponse> returnNotFound(ResLogger resLogger, String message) {
        resLogger.setValuesToLogger(HttpStatus.NOT_FOUND, null);
        return Mono.error(new ResGraphException(message, HttpStatus.NOT_FOUND));
    }

}
