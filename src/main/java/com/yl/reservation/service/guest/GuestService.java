package com.yl.reservation.service.guest;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.*;
import com.yl.reservation.repository.GuestRepository;
import com.yl.reservation.repository.UserRepository;
import com.yl.reservation.util.CreateUpdateMapper;
import com.yl.reservation.util.RequestValidatorService;
import com.yl.reservation.util.ResConstants;
import com.yl.reservation.util.ResUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuestService {

    @Autowired
    GuestRepository guestRepository;

    @Autowired
    UserRepository userRepository;

    // todo: handle empty response
    public Mono<GuestSearchResponse> getAllGuests(boolean includeUserInfo) {
        GuestSearchResponse response = new GuestSearchResponse();
        List<GuestDetails> guestDetailsList = new ArrayList<>();

        // include user info if requested
        if (includeUserInfo) {
            return guestRepository.findAll()
                    .flatMap(host -> Mono.just(host)
                            .zipWith(userRepository.findByUserId(host.getUserId()))
                            .flatMap(guestAndUser -> {
                                GuestDetails guestDetails = new GuestDetails();
                                guestDetails.setGuest(guestAndUser.getT1());
                                guestDetails.setUser(guestAndUser.getT2());
                                guestDetailsList.add(guestDetails);
                                return Mono.just(guestDetails);
                            }))
                    .collectList()
                    .map(hostList -> {
                        response.setGuestDetailsList(hostList);
                        response.setMessage(ResConstants.GUEST_FIND_ALL_USER_INFO);
                        return response;
                    });
        } else {
            // return response with no user info
            return guestRepository.findAll().collectList()
                    .flatMap(guestList -> {
                        guestList.forEach(guest -> {
                            GuestDetails guestDetails = new GuestDetails();
                            guestDetails.setGuest(guest);
                            guestDetailsList.add(guestDetails);
                        });
                        response.setGuestDetailsList(guestDetailsList);
                        response.setMessage(ResConstants.GUEST_FIND_ALL_NO_USER_INFO);
                        return Mono.just(response);
                    });
        }

    }

    public Mono<GuestSearchResponse> getGuestById(String guestId, boolean includeUserInfo) {
        return guestRepository.findByGuestId(guestId)
                .flatMap(guest -> {
                    GuestDetails guestDetails = new GuestDetails();
                    GuestSearchResponse response = new GuestSearchResponse();
                    guestDetails.setGuest(guest);
                    response.setGuestDetailsList(List.of(guestDetails));

                    // include user info if requested
                    if (includeUserInfo) {
                        return userRepository.findByUserId(guest.getUserId())
                                .flatMap(user -> {
                                    guestDetails.setUser(user);
                                    response.setMessage(
                                            ResConstants.GUEST_FIND + guest.getGuestId() + " with user info...");
                                    return Mono.just(response);
                                })
                                .switchIfEmpty(Mono.error(new ResGraphException(ResConstants.USER_NOT_FOUND_WITH_ID
                                        + guest.getUserId() + "for guest " + guestId, HttpStatus.NOT_FOUND)));
                    }
                    // if no user info is requested return response with no user info
                    response.setMessage(ResConstants.GUEST_FIND + guest.getGuestId());
                    return Mono.just(response);
                });
    }

    public Mono<GuestCreateUpdateResponse> createGuest(Guest requestGuest, String createDateTime) {
        RequestValidatorService.validateCreateGuestInfo(requestGuest);
        return validateNotExistingGuest(requestGuest)
                .flatMap(res -> {
                    if (res.equals(Boolean.TRUE))
                        throw new ResGraphException(ResConstants.GUEST_ALREADY_EXISTS_ERROR, HttpStatus.BAD_REQUEST);
                    else {
                        return userRepository.findByUserId(requestGuest.getUserId())
                                .flatMap(user -> {
                                    requestGuest.setGuestId(ResUtil.generateId());
                                    requestGuest.setCreatedDate(createDateTime);
                                    requestGuest.setLastUpdated(createDateTime);
                                    return guestRepository.save(requestGuest)
                                            .map(createdGuest -> new GuestCreateUpdateResponse(
                                                    ResConstants.GUEST_CREATE + createdGuest.getGuestId(),
                                                    createdGuest));
                                })
                                .switchIfEmpty(Mono.error(new ResGraphException(
                                        ResConstants.USER_NOT_FOUND_WITH_ID + requestGuest.getUserId(),
                                        HttpStatus.BAD_REQUEST)));
                    }
                });
    }

    private Mono<Boolean> validateNotExistingGuest(Guest guest) {
        return guestRepository.findByUserIdAndNickName(guest.getUserId(), guest.getNickName())
                .map(res -> true)
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<GuestCreateUpdateResponse> updateGuest(Guest requestGuest, String updateDateTime) {
        // todo: validation?
        // 1. nickname must be unique to this user
        // RequestValidatorService.validateUpdateGuest(requestGuest);
        if (requestGuest.getGuestId() != null) {
            String guestIdToSearch = requestGuest.getUserId();
            return guestRepository.findByGuestId(guestIdToSearch)
                    .flatMap(existingGuest -> {
                        Guest updatedGuest = CreateUpdateMapper.updateGuest(existingGuest, requestGuest,
                                updateDateTime);
                        return guestRepository.save(updatedGuest).map(guest -> new GuestCreateUpdateResponse(
                                ResConstants.GUEST_UPDATE + guest.getGuestId(), guest));
                    })
                    .switchIfEmpty(Mono.error(new ResGraphException(
                            ResConstants.GUEST_NOT_FOUND_WITH_ID + requestGuest.getGuestId(), HttpStatus.NOT_FOUND)));
        } else if (requestGuest.getUserId() != null && requestGuest.getNickName() != null) {
            return guestRepository.findByUserIdAndNickName(requestGuest.getUserId(), requestGuest.getNickName())
                    .flatMap(existingGuest -> {
                        Guest updatedGuest = CreateUpdateMapper.updateGuest(existingGuest, requestGuest,
                                updateDateTime);
                        return guestRepository.save(updatedGuest).map(guest -> new GuestCreateUpdateResponse(
                                ResConstants.GUEST_UPDATE + guest.getGuestId(), guest));
                    })
                    .switchIfEmpty(Mono.error(new ResGraphException(
                            String.format(ResConstants.GUEST_NOT_FOUND_USER_ID_NICKNAME, requestGuest.getUserId(),
                                    requestGuest.getNickName()),
                            HttpStatus.NOT_FOUND)));
        } else {
            return Mono.error(new ResGraphException(ResConstants.GUEST_NO_IDENTIFYING_ERROR, HttpStatus.BAD_REQUEST));
        }
    }

}
