package com.yl.reservation.service;

import com.yl.reservation.exception.GraphQLException;
import com.yl.reservation.model.*;
import com.yl.reservation.repository.HostRepository;
import com.yl.reservation.repository.UserRepository;
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

    public Mono<HostSearchResponse> getHostById(String hostId, boolean includeUserInfo) {
        return hostRepository.findByHostId(hostId)
                .flatMap(host -> {
                    HostDetails hostDetails = new HostDetails();
                    HostSearchResponse response = new HostSearchResponse();
                    hostDetails.setHost(host);
                    response.setHostDetails(List.of(hostDetails));

                    // include user info if requested
                    if (includeUserInfo) {
                        return userRepository.findByUserId(host.getUserId())
                                .flatMap(user -> {
                                    hostDetails.setUser(user);
                                    response.setMessage(ResConstants.HOST_FIND + host.getHostId() + " with user info...");
                                    return Mono.just(response);
                                })
                                .switchIfEmpty(Mono.error(new GraphQLException(ResConstants.USER_NOT_FOUND_WITH_ID + host.getUserId() + "for host " + hostId, HttpStatus.NOT_FOUND)));
                    }

                    // if no user info is requested return response with no user info
                    response.setMessage(ResConstants.HOST_FIND + host.getHostId());
                    return Mono.just(response);
                });
    }

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
                        response.setHostDetails(hostList);
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
                        response.setHostDetails(hostDetailsList);
                        response.setMessage(ResConstants.HOST_FIND_ALL_NO_USER_INFO);
                        return Mono.just(response);
                    });
        }

    }

    public Mono<HostUpdateResponse> createUpdateHost(HostUpdateRequest hostUpdateRequest) {
        String createUpdateDateTime = ResUtil.getCurrentDateTimeString();
        // check if host or user is in the request; throw an error if not.
        if (hostUpdateRequest.getHost() == null && hostUpdateRequest.getUser() == null) {
            throw new GraphQLException(ResConstants.NO_HOST_OR_USER_ERROR, HttpStatus.BAD_REQUEST);
        }
        // check for host in request. if yes, update host.
        if (hostUpdateRequest.getHost() != null) {
            if (hostUpdateRequest.getHost().getHostId() != null) {
                return updateHostByHostId(hostUpdateRequest, createUpdateDateTime);
            } else if (hostUpdateRequest.getHost().getUserId() != null && hostUpdateRequest.getHost().getAddress() != null) {
                if (Boolean.TRUE.equals(hostUpdateRequest.getIsAddressUpdate())) {
                    throw new GraphQLException(ResConstants.HOST_ID_REQUIRED_FOR_ADDRESS_UPDATE_ERROR, HttpStatus.BAD_REQUEST);
                }
                return updateHostByUserIdAndAddress(hostUpdateRequest, createUpdateDateTime);
            } else {
                throw new GraphQLException(ResConstants.HOST_NO_IDENTIFYING_ERROR, HttpStatus.BAD_REQUEST);
            }
        }
        // no host is given. user must be given. update user.
        else {
            return updateUserInfo(hostUpdateRequest, null, createUpdateDateTime)
                    .map(user -> new HostUpdateResponse((user.getLastUpdated().equals(user.getCreatedDate()) ?
                            ResConstants.USER_CREATE : ResConstants.USER_UPDATE) + user.getUserId(), null, user));

        }

    }

    private Mono<HostUpdateResponse> updateHostByHostId(HostUpdateRequest hostUpdateRequest, String createUpdateDateTime) {
        return hostRepository.findByHostId(hostUpdateRequest.getHost().getHostId())
                .flatMap(hostToUpdate -> {
                    Host updatedHost = ResUtil.updateHost(hostToUpdate, hostUpdateRequest.getHost(),
                            hostUpdateRequest.getIsAddressUpdate(), createUpdateDateTime);
                    if (Boolean.TRUE.equals(hostUpdateRequest.getIsUserUpdate())) {
                        return zipUserUpdateWithHostUpdate(hostUpdateRequest, hostToUpdate, updatedHost, createUpdateDateTime);
                    }
                    return hostRepository.save(updatedHost)
                            .flatMap(host -> Mono.just(new HostUpdateResponse(ResConstants.HOST_UPDATE + host.getHostId(), host, null)));
                })
                .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(), HttpStatus.BAD_REQUEST)))
                .switchIfEmpty(Mono.error(new GraphQLException(ResConstants.HOST_NOT_FOUND_WITH_ID + hostUpdateRequest.getHost().getHostId(), HttpStatus.BAD_REQUEST)));
    }

    private Mono<HostUpdateResponse> updateHostByUserIdAndAddress(HostUpdateRequest hostUpdateRequest, String createUpdateDateTime) {
        return hostRepository.findByUserIdAndAddress(hostUpdateRequest.getHost().getUserId(),
                        hostUpdateRequest.getHost().getAddress())
                .flatMap(hostToUpdate -> {
                    Host updatedHost = ResUtil.updateHost(hostToUpdate, hostUpdateRequest.getHost(),
                            hostUpdateRequest.getIsAddressUpdate(), createUpdateDateTime);
                    if (Boolean.TRUE.equals(hostUpdateRequest.getIsUserUpdate())) {
                        return zipUserUpdateWithHostUpdate(hostUpdateRequest, hostToUpdate, updatedHost, createUpdateDateTime);
                    }
                    return hostRepository.save(updatedHost)
                            .flatMap(savedHost -> Mono.just(new HostUpdateResponse(ResConstants.HOST_UPDATE + savedHost.getId(), savedHost, null)));
                })
                .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(),
                        HttpStatus.BAD_REQUEST)))
                .switchIfEmpty(Mono.defer(() -> createNewHost(hostUpdateRequest, createUpdateDateTime)));
    }


    private Mono<User> updateUserInfo(HostUpdateRequest hostUpdateRequest, String userId, String createUpdateDateTime) {
        if (Boolean.FALSE.equals(hostUpdateRequest.getIsUserUpdate())) {
            throw new GraphQLException(ResConstants.USER_UPDATE_ERROR, HttpStatus.BAD_REQUEST);
        }
        if (hostUpdateRequest.getUser().getUserId() != null || userId != null) {
            String userIdToSearch = (userId != null) ? userId : hostUpdateRequest.getUser().getUserId();
            return userRepository.findByUserId(userIdToSearch)
                    .flatMap(user -> {
                        User updatedUser = ResUtil.updateUser(user, hostUpdateRequest.getUser(), createUpdateDateTime);
                        return userRepository.save(updatedUser);
                    })
                    .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(),
                            HttpStatus.BAD_REQUEST)))
                    .switchIfEmpty(Mono.error(new GraphQLException(ResConstants.USER_NOT_FOUND_WITH_ID + hostUpdateRequest.getUser().getUserId(), HttpStatus.NOT_FOUND)));
        } else if (hostUpdateRequest.getUser().getLastName() != null && hostUpdateRequest.getUser().getPrimaryContactMethod() != null) {
            // todo: what if i want to update primary contact method? it'll create a new user here...
            // find by lastName and primary contact if given in the request
            return fetchByPrimaryContactInfo(hostUpdateRequest)
                    .flatMap(user -> {
                        User updatedUser = ResUtil.updateUser(user, hostUpdateRequest.getUser(), createUpdateDateTime);
                        return userRepository.save(updatedUser);
                    })
                    .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(),
                            HttpStatus.BAD_REQUEST)))
                    // create new user if no user is found
                    .switchIfEmpty(Mono.defer(()-> createNewUser(hostUpdateRequest, createUpdateDateTime)));
        } else {
            throw new GraphQLException(ResConstants.USER_NO_IDENTIFYING_ERROR, HttpStatus.BAD_REQUEST);
        }
    }

    private Mono<User> fetchByPrimaryContactInfo(HostUpdateRequest hostUpdateRequest) {
        if (hostUpdateRequest.getUser().getPrimaryContactMethod().equals(ContactMethod.PHONE)) {
            return userRepository.findByLastNameAndPrimaryPhone(
                    hostUpdateRequest.getUser().getLastName(),
                    hostUpdateRequest.getUser().getPhone().stream().filter(Phone::isPrimary).toList().get(0).getValue()
            );
        } else {
            return userRepository.findByLastNameAndPrimaryEmail(
                    hostUpdateRequest.getUser().getLastName(),
                    hostUpdateRequest.getUser().getEmail().stream().filter(Email::isPrimary).toList().get(0).getValue()
            );
        }
    }

    private Mono<HostUpdateResponse> createNewHost(HostUpdateRequest hostUpdateRequest, String createDateTime) {
        ResUtil.validateHostInfo(hostUpdateRequest.getHost());
        Host host = hostUpdateRequest.getHost();
        host.setHostId(ResUtil.generateId());
        host.setCreatedDate(createDateTime);
        host.setLastUpdated(createDateTime);
        return hostRepository.save(host)
                .map(newHost -> new HostUpdateResponse(ResConstants.HOST_CREATE + host.getHostId(), newHost, null));
    }

    private Mono<User> createNewUser(HostUpdateRequest hostUpdateRequest, String createDateTime) {
        ResUtil.validateUserInfo(hostUpdateRequest.getUser());
        User user = hostUpdateRequest.getUser();
        user.setUserId(ResUtil.generateId());
        user.setCreatedDate(createDateTime);
        user.setLastUpdated(createDateTime);
        return userRepository.save(user);
    }

    private Mono<HostUpdateResponse> zipUserUpdateWithHostUpdate(HostUpdateRequest hostUpdateRequest, Host hostToUpdate,
                                                                 Host updatedHost,
                                                                 String createUpdateDateTime) {
        if (hostUpdateRequest.getUser() == null) {
            throw new GraphQLException("No user in request...", HttpStatus.BAD_REQUEST);
        }
        // if there are no updates to apply to the user, an error is thrown and no updates are applied
        return updateUserInfo(hostUpdateRequest, hostToUpdate.getUserId(),
                createUpdateDateTime)
                .flatMap(user -> hostRepository.save(updatedHost)
                        .zipWith(Mono.just(user))
                        .flatMap(hostAndUserTuple ->
                                Mono.just(new HostUpdateResponse(
                                        ResConstants.HOST_UPDATE + hostAndUserTuple.getT1().getHostId(),
                                        hostAndUserTuple.getT1(),
                                        hostAndUserTuple.getT2())
                                )
                        )
                );

    }

}
