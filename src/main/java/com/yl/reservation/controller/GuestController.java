package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.service.*;
import com.yl.reservation.util.ResConstants;
import com.yl.reservation.util.ResUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class GuestController {

    @Autowired
    GuestService guestService;

    private static final Logger logger = LoggerFactory.getLogger(GuestController.class);

    @QueryMapping
    public Mono<GuestSearchResponse> getAllGuests(@Argument boolean includeUserInfo) {
        return guestService.getAllGuests(includeUserInfo);
    }

    @QueryMapping
    public Mono<GuestSearchResponse> getGuestById(@Argument String guestId, @Argument boolean includeUserInfo) {
        return guestService.getGuestById(guestId, includeUserInfo)
                .switchIfEmpty(Mono.error(new ResGraphException(ResConstants.GUEST_NOT_FOUND_WITH_ID + guestId, HttpStatus.NOT_FOUND)))
                .doOnSuccess(res -> logger.info("Fetched guest: {}", res.getGuestDetailsList()))
                .onErrorResume(error -> {
                    logger.error("Error fetching all guest {}", guestId);
                    return Mono.error(error);
                })
                .cache();
    }

    @MutationMapping
    Mono<GuestCreateUpdateResponse> createGuest(@Argument GuestCreateUpdateRequest guestCreateUpdateRequest){
        String createDateTime = ResUtil.getCurrentDateTimeString();
        return guestService.createNewGuest(guestCreateUpdateRequest.getGuest(), createDateTime)
                .doOnSuccess(res -> logger.info("Created guest: {}", res.getGuest()))
                .onErrorResume(error -> {
                    logger.error("Error creating guest {}", guestCreateUpdateRequest.getGuest());
                    return Mono.error(error);
                })
                .cache();
    }

    @MutationMapping
    Mono<GuestCreateUpdateResponse> updateGuest(@Argument GuestCreateUpdateRequest guestCreateUpdateRequest){
        String createDateTime = ResUtil.getCurrentDateTimeString();
        return guestService.updateGuest(guestCreateUpdateRequest.getGuest(), createDateTime)
                .doOnSuccess(res -> logger.info("Created guest: {}", res.getGuest()))
                .onErrorResume(error -> {
                    logger.error("Error updating guest {}", guestCreateUpdateRequest.getGuest());
                    return Mono.error(error);
                })
                .cache();
    }



}
