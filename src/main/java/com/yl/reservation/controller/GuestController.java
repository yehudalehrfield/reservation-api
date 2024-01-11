package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.service.*;
import com.yl.reservation.util.ResConstants;
import com.yl.reservation.util.ResUtil;
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

    @QueryMapping
    public Mono<GuestSearchResponse> getAllGuests(@Argument boolean includeUserInfo) {
        return guestService.getAllGuests(includeUserInfo);
    }

    @QueryMapping
    public Mono<GuestSearchResponse> getGuestById(@Argument String guestId, @Argument boolean includeUserInfo) {
        return guestService.getGuestById(guestId, includeUserInfo)
                .switchIfEmpty(Mono.error(new ResGraphException(ResConstants.GUEST_NOT_FOUND_WITH_ID + guestId, HttpStatus.NOT_FOUND)))
                .cache();
    }

    @MutationMapping
    Mono<GuestResponse> createGuest(@Argument CreateUpdateGuestRequest createUpdateGuestRequest){
        String createDateTime = ResUtil.getCurrentDateTimeString();
        return guestService.createNewGuest(createUpdateGuestRequest.getGuest(), createDateTime);
    }

    @MutationMapping
    Mono<GuestResponse> updateGuest(@Argument CreateUpdateGuestRequest createUpdateGuestRequest){
        String createDateTime = ResUtil.getCurrentDateTimeString();
        return guestService.updateGuest(createUpdateGuestRequest.getGuest(), createDateTime);
    }



}
