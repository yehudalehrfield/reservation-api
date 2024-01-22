package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.service.*;
import com.yl.reservation.util.ResConstants;
import com.yl.reservation.util.ResLogger;
import com.yl.reservation.util.ResUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class GuestController {

    @Autowired
    GuestService guestService;

    @QueryMapping
    public Mono<GuestSearchResponse> getAllGuests(@Argument boolean includeUserInfo) {
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "getAllGuests");
        return guestService.getAllGuests(includeUserInfo)
                .switchIfEmpty(returnNotFound(resLogger, "No guests found"))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, null);
                    return Mono.error(error);
                });
    }

    @QueryMapping
    public Mono<GuestSearchResponse> getGuestById(@Argument String guestId, @Argument boolean includeUserInfo) {
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "getGuestById");
        return guestService.getGuestById(guestId, includeUserInfo)
                .switchIfEmpty(returnNotFound(resLogger, ResConstants.GUEST_NOT_FOUND_WITH_ID + guestId))
                .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, null);
                    return Mono.error(error);
                })
                .cache();
    }

    @MutationMapping
    Mono<GuestCreateUpdateResponse> createGuest(@Argument GuestCreateUpdateRequest guestCreateUpdateRequest){
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "createGuest");
        String createDateTime = ResUtil.getCurrentDateTimeString();
        return guestService.createNewGuest(guestCreateUpdateRequest.getGuest(), createDateTime)
                .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, null);
                    return Mono.error(error);
                })
                .cache();
    }

    @MutationMapping
    Mono<GuestCreateUpdateResponse> updateGuest(@Argument GuestCreateUpdateRequest guestCreateUpdateRequest){
        ResLogger resLogger = new ResLogger(System.currentTimeMillis(), HttpMethod.POST, "updateGuest");
        String updateDateTime = ResUtil.getCurrentDateTimeString();
        return guestService.updateGuest(guestCreateUpdateRequest.getGuest(), updateDateTime)
                .doOnSuccess(res -> resLogger.setValuesToLogger(HttpStatus.OK, res.toString()))
                .onErrorResume(error -> {
                    resLogger.setValuesToLogger(HttpStatus.INTERNAL_SERVER_ERROR, null);
                    return Mono.error(error);
                })
                .cache();
    }

    private static Mono<GuestSearchResponse> returnNotFound(ResLogger resLogger, String message){
        resLogger.setValuesToLogger(HttpStatus.NOT_FOUND, null);
        return Mono.error(new ResGraphException(message, HttpStatus.NOT_FOUND));
    }

}

