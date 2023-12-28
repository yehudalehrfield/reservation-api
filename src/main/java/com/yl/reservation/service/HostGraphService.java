package com.yl.reservation.service;

import com.yl.reservation.exception.GraphQLException;
import com.yl.reservation.exception.HostException;
import com.yl.reservation.model.*;
import com.yl.reservation.repository.HostRepositoryReactive;
import com.yl.reservation.repository.UserRepositoryReactive;
import com.yl.reservation.util.ResConstants;
import com.yl.reservation.util.ResUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class HostGraphService {
    @Autowired
    HostRepositoryReactive hostRepositoryReactive;

    @Autowired
    UserRepositoryReactive userRepositoryReactive;

    public Mono<HostSearchResponse> getHostById(String hostId, boolean includeUserInfo) {
        return hostRepositoryReactive.findByHostId(hostId)
                .flatMap(host -> {
                    HostDetails hostDetails = new HostDetails();
                    HostSearchResponse response = new HostSearchResponse();
                    hostDetails.setHost(host);
                    response.setHostDetails(List.of(hostDetails));
                    if (includeUserInfo) {
                        return userRepositoryReactive.findByUserId(host.getUserId())
                                .flatMap(user -> {
                                    hostDetails.setUser(user);
                                    response.setMessage(ResConstants.HOST_FIND + host.getHostId() + " with user info...");
                                    return Mono.just(response);
                                })
                                .switchIfEmpty(Mono.error(new GraphQLException(ResConstants.USER_NOT_FOUND_WITH_ID + host.getUserId() + "for host " + hostId, HttpStatus.NOT_FOUND)));
                    }
                    response.setMessage(ResConstants.HOST_FIND + host.getHostId());
                    return Mono.just(response);
                });
    }

    public Mono<HostSearchResponse> getAllHosts(boolean includeUserInfo) {
        HostSearchResponse response = new HostSearchResponse();
        List<HostDetails> hostDetailsList = new ArrayList<>();
        if (includeUserInfo) {
            return hostRepositoryReactive.findAll()
                    .flatMap(host -> Mono.just(host)
                            .zipWith(userRepositoryReactive.findByUserId(host.getUserId()))
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
                        return response;
                    });
        } else {
            HostDetails hostDetails = new HostDetails();
            return hostRepositoryReactive.findAll().collectList()
                    .flatMap(hostList -> {
                        hostList.forEach(host -> {
                            hostDetails.setHost(host);
                            hostDetailsList.add(hostDetails);
                        });
                        response.setHostDetails(hostDetailsList);
                        response.setMessage("Retrieved all hosts without user info");
                        return Mono.just(response);
                    });
        }

    }

    public Mono<HostUpdateResponse> createUpdateHost(HostUpdateRequest hostUpdateRequest) {
        String createUpdateDateTime = ResUtil.getCurrentDateTimeString();
        // check if host is in the request; throw an error if not.
        if (hostUpdateRequest.getHost() == null && hostUpdateRequest.getUser() == null) {
            throw new GraphQLException(ResConstants.NO_HOST_OR_USER_ERROR, HttpStatus.BAD_REQUEST);
        }
        // check for host in request
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

    private Mono<HostUpdateResponse> updateHostByUserIdAndAddress(HostUpdateRequest hostUpdateRequest, String createUpdateDateTime) {
        return hostRepositoryReactive.findByUserIdAndAddress(hostUpdateRequest.getHost().getUserId(),
                        hostUpdateRequest.getHost().getAddress())
                .flatMap(hostToUpdate -> {
                    Host updatedHost = ResUtil.updateHost(hostToUpdate, hostUpdateRequest.getHost(),
                            hostUpdateRequest.getIsAddressUpdate(), createUpdateDateTime);
                    if (Boolean.TRUE.equals(hostUpdateRequest.getIsUserUpdate())) {
                        return zipUserUpdateWithHostUpdate(hostUpdateRequest, hostToUpdate, updatedHost, createUpdateDateTime);
                    }
                    return hostRepositoryReactive.save(updatedHost)
                            .flatMap(savedHost -> Mono.just(new HostUpdateResponse(ResConstants.HOST_UPDATE + savedHost.getId(), savedHost, null)));
                })
                .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(),
                        HttpStatus.BAD_REQUEST)))
                .switchIfEmpty(createNewHost(hostUpdateRequest, createUpdateDateTime));
    }

    private Mono<HostUpdateResponse> updateHostByHostId(HostUpdateRequest hostUpdateRequest, String createUpdateDateTime) {
        return hostRepositoryReactive.findByHostId(hostUpdateRequest.getHost().getHostId())
                .flatMap(hostToUpdate -> {
                    Host updatedHost = ResUtil.updateHost(hostToUpdate, hostUpdateRequest.getHost(),
                            hostUpdateRequest.getIsAddressUpdate(), createUpdateDateTime);
                    if (Boolean.TRUE.equals(hostUpdateRequest.getIsUserUpdate())) {
                        return zipUserUpdateWithHostUpdate(hostUpdateRequest, hostToUpdate, updatedHost, createUpdateDateTime);
                    }
                    return hostRepositoryReactive.save(updatedHost)
                            .flatMap(host -> Mono.just(new HostUpdateResponse(ResConstants.HOST_UPDATE + host.getHostId(), host, null)));
                })
                .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(),
                        HttpStatus.BAD_REQUEST)))
                .switchIfEmpty(Mono.error(new GraphQLException(ResConstants.HOST_NOT_FOUND_WITH_ID + hostUpdateRequest.getHost().getHostId(), HttpStatus.BAD_REQUEST)));
    }

    private Mono<User> updateUserInfo(HostUpdateRequest hostUpdateRequest, String userId, String createUpdateDateTime) {
        if (Boolean.FALSE.equals(hostUpdateRequest.getIsUserUpdate())) {
            throw new GraphQLException(ResConstants.USER_UPDATE_ERROR, HttpStatus.BAD_REQUEST);
        }
        if (hostUpdateRequest.getUser().getUserId() != null || userId != null) {
            String userIdToSearch = (userId != null) ? userId : hostUpdateRequest.getUser().getUserId();
            return userRepositoryReactive.findByUserId(userIdToSearch)
                    .flatMap(user -> {
                        User updatedUser = ResUtil.updateUser(user, hostUpdateRequest.getUser(), createUpdateDateTime);
                        return userRepositoryReactive.save(updatedUser);
                    })
                    .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(),
                            HttpStatus.BAD_REQUEST)))
                    .switchIfEmpty(Mono.error(new GraphQLException(ResConstants.USER_NOT_FOUND_WITH_ID + hostUpdateRequest.getUser().getUserId(), HttpStatus.NOT_FOUND)));
        } else if (hostUpdateRequest.getUser().getLastName() != null && hostUpdateRequest.getUser().getPrimaryContactMethod() != null) {
            // find by lastName and primary contact if given in the request
            return fetchByPrimaryContactInfo(hostUpdateRequest)
                    .flatMap(user -> {
                        User updatedUser = ResUtil.updateUser(user, hostUpdateRequest.getUser(), createUpdateDateTime);
                        return userRepositoryReactive.save(updatedUser);
                    })
                    .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(),
                            HttpStatus.BAD_REQUEST)))
                    // create new user if no user is found
                    .switchIfEmpty(createNewUser(hostUpdateRequest, createUpdateDateTime));
        } else {
            throw new GraphQLException(ResConstants.USER_NO_IDENTIFYING_ERROR, HttpStatus.BAD_REQUEST);
        }
    }

    private Mono<User> fetchByPrimaryContactInfo(HostUpdateRequest hostUpdateRequest) {
        if (hostUpdateRequest.getUser().getPrimaryContactMethod().equals(ContactMethod.PHONE)) {
            return userRepositoryReactive.findByLastNameAndPrimaryPhone(
                    hostUpdateRequest.getUser().getLastName(),
                    hostUpdateRequest.getUser().getPhone().stream().filter(Phone::isPrimary).toList().get(0).getValue()
            );
        } else {
            return userRepositoryReactive.findByLastNameAndPrimaryEmail(
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
        return hostRepositoryReactive.save(host)
                .map(newHost -> new HostUpdateResponse(ResConstants.HOST_CREATE + host.getHostId(), newHost, null));
    }

    private Mono<User> createNewUser(HostUpdateRequest hostUpdateRequest, String createDateTime) {
        ResUtil.validateUserInfo(hostUpdateRequest.getUser());
        User user = hostUpdateRequest.getUser();
        user.setUserId(ResUtil.generateId());
        user.setCreatedDate(createDateTime);
        user.setLastUpdated(createDateTime);
        return userRepositoryReactive.save(user);
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
                .flatMap(user -> hostRepositoryReactive.save(updatedHost)
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

    private Mono<HostUpdateResponse> createNewHostOrApplyAddressUpdate(HostUpdateRequest hostUpdateRequest,
                                                                       String createUpdateDateTime) {
        return doesUserExist(hostUpdateRequest).flatMap(user -> {
            System.out.println("User Exists.");
            //todo: user updates if applicable
            //todo: what if it's an address update?
            if (hostUpdateRequest.getIsAddressUpdate()) {
                System.out.println("GOTTA FIGURE THIS OUT");
                return Mono.just(new HostUpdateResponse("Address Update", null, null));
            } else {
                //todo refactor: createHost
                return proceedWithCreateHost(hostUpdateRequest, createUpdateDateTime);
            }
        }).switchIfEmpty(createUserAndHost(hostUpdateRequest, createUpdateDateTime)).onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(), HttpStatus.BAD_REQUEST)));
    }

    private Mono<? extends HostUpdateResponse> createUserAndHost(HostUpdateRequest hostUpdateRequest,
                                                                 String createUpdateDateTime) {
        // validate User, firstname, lastname, primaryContact, phone or email
        ResUtil.validateUserInfo(hostUpdateRequest.getUser());
        ResUtil.validateHostCreationFields(hostUpdateRequest);
        userRepositoryReactive.save(hostUpdateRequest.getUser());
        return hostRepositoryReactive.save(hostUpdateRequest.getHost()).flatMap(host -> Mono.just(new HostUpdateResponse(
                "Created host: " + host.getHostId(), host, hostUpdateRequest.getUser())));
    }

    private Mono<HostUpdateResponse> proceedWithCreateHost(HostUpdateRequest hostUpdateRequest, String createUpdateDateTime) {
        //todo: new validation -> userId, address
//        HostUtil.validateHostCreationFields(hostUpdateRequest);

        Host updatedHost = hostUpdateRequest.getHost();
        updatedHost.setHostId(ResUtil.generateId());
        updatedHost.setCreatedDate(createUpdateDateTime);
        updatedHost.setLastUpdated(createUpdateDateTime);
        return hostRepositoryReactive.save(updatedHost).flatMap(savedHost -> Mono.just(new HostUpdateResponse("Created host" +
                " " + savedHost.getId(), savedHost, null)));
    }

    private Mono<User> doesUserExist(HostUpdateRequest request) {
        if (request.getHost().getUserId() != null) {
            return userRepositoryReactive.findByUserId(request.getHost().getUserId());
        }
        // no id, user must be given
        if (request.getUser() == null) {
            throw new HostException(HttpStatus.BAD_REQUEST, "no user field in request");
        }
        if (request.getUser().getPrimaryContactMethod() == ContactMethod.PHONE) {
            return userRepositoryReactive.findByLastNameAndPrimaryPhone(request.getUser().getLastName(),
                    request.getUser().getPhone().stream().filter(Phone::isPrimary).toList().get(0).getValue());
        } else {
            return userRepositoryReactive.findByLastNameAndPrimaryEmail(request.getUser().getLastName(),
                    request.getUser().getEmail().stream().filter(Email::isPrimary).toList().get(0).getValue());
        }
    }

}
