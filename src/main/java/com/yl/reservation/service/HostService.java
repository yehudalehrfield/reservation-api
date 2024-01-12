package com.yl.reservation.service;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.*;
import com.yl.reservation.repository.HostRepository;
import com.yl.reservation.repository.UserRepository;
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
public class HostService {
    @Autowired
    HostRepository hostRepository;

    @Autowired
    UserRepository userRepository;

    public Mono<HostSearchResponse> getAllHosts(boolean includeUserInfo) {
        HostSearchResponse response = new HostSearchResponse();
        List<HostDetails> hostDetailsList = new ArrayList<>();

        // include user info if requested
        if (includeUserInfo) {
            return hostRepository.findAll()
                    .flatMap(host -> Mono.just(host)
                            .zipWith(userRepository.findByUserId(host.getUserId()))
                            .flatMap(hostAndUser -> {
                                HostDetails hostDetails = new HostDetails();
                                hostDetails.setHost(hostAndUser.getT1());
                                hostDetails.setUser(hostAndUser.getT2());
                                hostDetailsList.add(hostDetails);
                                return Mono.just(hostDetails);
                            }))
                    .collectList()
                    .map(hostList -> {
                        response.setHostDetailsList(hostList);
                        response.setMessage(ResConstants.HOST_FIND_ALL_USER_INFO);
                        return response;
                    });
        } else {
            // return response with no user info
            return hostRepository.findAll().collectList()
                    .flatMap(hostList -> {
                        hostList.forEach(host -> {
                            HostDetails hostDetails = new HostDetails();
                            hostDetails.setHost(host);
                            hostDetailsList.add(hostDetails);
                        });
                        response.setHostDetailsList(hostDetailsList);
                        response.setMessage(ResConstants.HOST_FIND_ALL_NO_USER_INFO);
                        return Mono.just(response);
                    });
        }

    }

    public Mono<HostSearchResponse> getHostById(String hostId, boolean includeUserInfo) {
        return hostRepository.findByHostId(hostId)
                .flatMap(host -> {
                    HostDetails hostDetails = new HostDetails();
                    HostSearchResponse response = new HostSearchResponse();
                    hostDetails.setHost(host);
                    response.setHostDetailsList(List.of(hostDetails));

                    // include user info if requested
                    if (includeUserInfo) {
                        return userRepository.findByUserId(host.getUserId())
                                .flatMap(user -> {
                                    hostDetails.setUser(user);
                                    response.setMessage(ResConstants.HOST_FIND + host.getHostId() + " with user info...");
                                    return Mono.just(response);
                                })
                                .switchIfEmpty(Mono.error(new ResGraphException(ResConstants.USER_NOT_FOUND_WITH_ID + host.getUserId() + " for host " + hostId, HttpStatus.NOT_FOUND)));
                    }
                    // if no user info is requested return response with no user info
                    response.setMessage(ResConstants.HOST_FIND + host.getHostId());
                    return Mono.just(response);
                });
    }

    //todo: break this into two api's - create and update (as with guest and user)
    public Mono<HostCreateUpdateResponse> createUpdateHost(HostCreateUpdateRequest hostCreateUpdateRequest) {
        String createUpdateDateTime = ResUtil.getCurrentDateTimeString();
        // check if host or user is in the request; throw an error if not.
        if (hostCreateUpdateRequest.getHost() == null && hostCreateUpdateRequest.getUser() == null) {
            throw new ResGraphException(ResConstants.NO_HOST_OR_USER_ERROR, HttpStatus.BAD_REQUEST);
        }
        // check for host in request. if yes, update host.
        if (hostCreateUpdateRequest.getHost() != null) {
            if (hostCreateUpdateRequest.getHost().getHostId() != null) {
                return updateHostByHostId(hostCreateUpdateRequest, createUpdateDateTime);
            } else if (hostCreateUpdateRequest.getHost().getUserId() != null && hostCreateUpdateRequest.getHost().getAddress() != null) {
                if (Boolean.TRUE.equals(hostCreateUpdateRequest.getIsAddressUpdate())) {
                    throw new ResGraphException(ResConstants.HOST_ID_REQUIRED_FOR_ADDRESS_UPDATE_ERROR, HttpStatus.BAD_REQUEST);
                }
                return updateHostByUserIdAndAddress(hostCreateUpdateRequest, createUpdateDateTime);
            } else {
                throw new ResGraphException(ResConstants.HOST_NO_IDENTIFYING_ERROR, HttpStatus.BAD_REQUEST);
            }
        }
        // no host is given. user must be given. update user.
        else {
            return updateUserInfo(hostCreateUpdateRequest, null, createUpdateDateTime)
                    .map(user -> new HostCreateUpdateResponse((user.getLastUpdated().equals(user.getCreatedDate()) ?
                            ResConstants.USER_CREATE : ResConstants.USER_UPDATE) + user.getUserId(), null, user));

        }

    }

    private Mono<HostCreateUpdateResponse> updateHostByHostId(HostCreateUpdateRequest hostCreateUpdateRequest, String createUpdateDateTime) {
        return hostRepository.findByHostId(hostCreateUpdateRequest.getHost().getHostId())
                .flatMap(hostToUpdate -> {
                    Host updatedHost = CreateUpdateMapper.updateHost(hostToUpdate, hostCreateUpdateRequest.getHost(),
                            hostCreateUpdateRequest.getIsAddressUpdate(), createUpdateDateTime);
                    if (Boolean.TRUE.equals(hostCreateUpdateRequest.getIsUserUpdate())) {
                        return zipUserUpdateWithHostUpdate(hostCreateUpdateRequest, hostToUpdate, updatedHost, createUpdateDateTime);
                    }
                    return hostRepository.save(updatedHost)
                            .flatMap(host -> Mono.just(new HostCreateUpdateResponse(ResConstants.HOST_UPDATE + host.getHostId(), host, null)));
                })
                .onErrorResume(error -> Mono.error(new ResGraphException(error.getMessage(), HttpStatus.BAD_REQUEST)))
                .switchIfEmpty(Mono.error(new ResGraphException(ResConstants.HOST_NOT_FOUND_WITH_ID + hostCreateUpdateRequest.getHost().getHostId(), HttpStatus.BAD_REQUEST)));
    }

    private Mono<HostCreateUpdateResponse> updateHostByUserIdAndAddress(HostCreateUpdateRequest hostCreateUpdateRequest, String createUpdateDateTime) {
        return hostRepository.findByUserIdAndAddress(hostCreateUpdateRequest.getHost().getUserId(),
                        hostCreateUpdateRequest.getHost().getAddress())
                .flatMap(hostToUpdate -> {
                    Host updatedHost = CreateUpdateMapper.updateHost(hostToUpdate, hostCreateUpdateRequest.getHost(),
                            hostCreateUpdateRequest.getIsAddressUpdate(), createUpdateDateTime);
                    if (Boolean.TRUE.equals(hostCreateUpdateRequest.getIsUserUpdate())) {
                        return zipUserUpdateWithHostUpdate(hostCreateUpdateRequest, hostToUpdate, updatedHost, createUpdateDateTime);
                    }
                    return hostRepository.save(updatedHost)
                            .flatMap(savedHost -> Mono.just(new HostCreateUpdateResponse(ResConstants.HOST_UPDATE + savedHost.getId(), savedHost, null)));
                })
                .onErrorResume(error -> Mono.error(new ResGraphException(error.getMessage(),
                        HttpStatus.BAD_REQUEST)))
                .switchIfEmpty(Mono.defer(() -> createNewHost(hostCreateUpdateRequest, createUpdateDateTime)));
    }


    //todo: remove this method and use the one from the user service class
    private Mono<User> updateUserInfo(HostCreateUpdateRequest hostCreateUpdateRequest, String userId, String createUpdateDateTime) {
        if (Boolean.FALSE.equals(hostCreateUpdateRequest.getIsUserUpdate())) {
            throw new ResGraphException(ResConstants.USER_UPDATE_ERROR, HttpStatus.BAD_REQUEST);
        }
        if (hostCreateUpdateRequest.getUser().getUserId() != null || userId != null) {
            String userIdToSearch = (userId != null) ? userId : hostCreateUpdateRequest.getUser().getUserId();
            return userRepository.findByUserId(userIdToSearch)
                    .flatMap(user -> {
                        User updatedUser = CreateUpdateMapper.updateUser(user, hostCreateUpdateRequest.getUser(), createUpdateDateTime);
                        return userRepository.save(updatedUser);
                    })
                    .onErrorResume(error -> Mono.error(new ResGraphException(error.getMessage(),
                            HttpStatus.BAD_REQUEST)))
                    .switchIfEmpty(Mono.error(new ResGraphException(ResConstants.USER_NOT_FOUND_WITH_ID + hostCreateUpdateRequest.getUser().getUserId(), HttpStatus.NOT_FOUND)));
        } else if (hostCreateUpdateRequest.getUser().getLastName() != null && hostCreateUpdateRequest.getUser().getPrimaryContactMethod() != null) {
            // todo: what if i want to update primary contact method? it'll create a new user here...
            //  --> Don't allow primaryContactMethod update without useId...
            // find by lastName and primary contact if given in the request
            return fetchByPrimaryContactInfo(hostCreateUpdateRequest)
                    .flatMap(user -> {
                        User updatedUser = CreateUpdateMapper.updateUser(user, hostCreateUpdateRequest.getUser(), createUpdateDateTime);
                        return userRepository.save(updatedUser);
                    })
                    .onErrorResume(error -> Mono.error(new ResGraphException(error.getMessage(), HttpStatus.BAD_REQUEST)))
                    // create new user if no user is found
                    .switchIfEmpty(Mono.defer(()-> createNewUser(hostCreateUpdateRequest, createUpdateDateTime)));
        } else {
            throw new ResGraphException(ResConstants.USER_NO_IDENTIFYING_ERROR, HttpStatus.BAD_REQUEST);
        }
    }

    private Mono<User> fetchByPrimaryContactInfo(HostCreateUpdateRequest hostCreateUpdateRequest) {
        if (hostCreateUpdateRequest.getUser().getPrimaryContactMethod().equals(ContactMethod.PHONE)) {
            return userRepository.findByLastNameAndPrimaryPhone(
                    hostCreateUpdateRequest.getUser().getLastName(),
                    hostCreateUpdateRequest.getUser().getPhone().stream().filter(Phone::isPrimary).toList().get(0).getValue()
            );
        } else {
            return userRepository.findByLastNameAndPrimaryEmail(
                    hostCreateUpdateRequest.getUser().getLastName(),
                    hostCreateUpdateRequest.getUser().getEmail().stream().filter(Email::isPrimary).toList().get(0).getValue()
            );
        }
    }

    private Mono<HostCreateUpdateResponse> createNewHost(HostCreateUpdateRequest hostCreateUpdateRequest, String createDateTime) {
        RequestValidatorService.validateCreateHostInfo(hostCreateUpdateRequest.getHost());
        Host host = hostCreateUpdateRequest.getHost();
        host.setHostId(ResUtil.generateId());
        host.setCreatedDate(createDateTime);
        host.setLastUpdated(createDateTime);
        return hostRepository.save(host)
                .map(newHost -> new HostCreateUpdateResponse(ResConstants.HOST_CREATE + host.getHostId(), newHost, null));
    }

    //todo: remove this method and use the one from the user service class
    private Mono<User> createNewUser(HostCreateUpdateRequest hostCreateUpdateRequest, String createDateTime) {
        RequestValidatorService.validateCreateUserInfo(hostCreateUpdateRequest.getUser());
        User user = hostCreateUpdateRequest.getUser();
        user.setUserId(ResUtil.generateId());
        user.setCreatedDate(createDateTime);
        user.setLastUpdated(createDateTime);
        return userRepository.save(user);
    }

    private Mono<HostCreateUpdateResponse> zipUserUpdateWithHostUpdate(HostCreateUpdateRequest hostCreateUpdateRequest, Host hostToUpdate,
                                                                       Host updatedHost,
                                                                       String createUpdateDateTime) {
        if (hostCreateUpdateRequest.getUser() == null) {
            throw new ResGraphException("No user in request...", HttpStatus.BAD_REQUEST);
        }
        // if there are no updates to apply to the user, an error is thrown and no updates are applied
        return updateUserInfo(hostCreateUpdateRequest, hostToUpdate.getUserId(),
                createUpdateDateTime)
                .flatMap(user -> hostRepository.save(updatedHost)
                        .zipWith(Mono.just(user))
                        .flatMap(hostAndUserTuple ->
                                Mono.just(new HostCreateUpdateResponse(
                                        ResConstants.HOST_UPDATE + hostAndUserTuple.getT1().getHostId(),
                                        hostAndUserTuple.getT1(),
                                        hostAndUserTuple.getT2())
                                )
                        )
                );

    }

}
