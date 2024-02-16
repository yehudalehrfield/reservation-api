package com.yl.reservation.service;

import com.yl.reservation.exception.ResException;
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
                                .switchIfEmpty(Mono.error(new ResGraphException(
                                        ResConstants.USER_NOT_FOUND_WITH_ID + host.getUserId() + " for host " + hostId, HttpStatus.NOT_FOUND)));
                    }
                    // if no user info is requested return response with no user info
                    response.setMessage(ResConstants.HOST_FIND + host.getHostId());
                    return Mono.just(response);
                });
    }

    public Mono<HostCreateUpdateResponse> createHost(Host requestHost, String createDateTime) {
        RequestValidatorService.validateCreateHostInfo(requestHost);
        return validateNotExistingHost(requestHost)
                .flatMap(res -> {
                    if (res.equals(Boolean.TRUE)) {
                        return Mono.error(new ResGraphException(ResConstants.HOST_ALREADY_EXISTS_ERROR,
                                HttpStatus.BAD_REQUEST));
                    } else {
                        return userRepository.findByUserId(requestHost.getUserId())
                                .flatMap(user -> {
                                    requestHost.setHostId(ResUtil.generateId());
                                    requestHost.setCreatedDate(createDateTime);
                                    requestHost.setLastUpdated(createDateTime);
                                    return hostRepository.save(requestHost)
                                            .map(createdHost -> new HostCreateUpdateResponse(ResConstants.HOST_CREATE + createdHost.getHostId(), createdHost));
                                })
                                .switchIfEmpty(Mono.error(new ResGraphException(ResConstants.USER_NOT_FOUND_WITH_ID + requestHost.getUserId(), HttpStatus.BAD_REQUEST)));

                    }
                });
    }

    private Mono<Boolean> validateNotExistingHost(Host host) {
        return hostRepository.findByUserIdAndAddress(host.getUserId(), host.getAddress())
                .map(res -> true)
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<HostCreateUpdateResponse> updateHost(Host requestHost, boolean isAddressUpdate, String updateDateTime) {
        RequestValidatorService.validateUpdateHostInfo(requestHost, isAddressUpdate);
        if (requestHost.getHostId() != null) {
            String hostIdtoSearch = requestHost.getHostId();
            return hostRepository.findByHostId(hostIdtoSearch)
                    .flatMap(existingHost -> {
                        Host updateHost = CreateUpdateMapper.updateHost(existingHost, requestHost, updateDateTime);
                        return hostRepository.save(updateHost).map(host -> new HostCreateUpdateResponse(ResConstants.HOST_UPDATE + host.getHostId(), host));
                    })
                    .switchIfEmpty(Mono.error(new ResGraphException(ResConstants.HOST_NOT_FOUND_WITH_ID + requestHost.getHostId(), HttpStatus.BAD_REQUEST)));
        } else if (requestHost.getUserId() != null && requestHost.getAddress() != null) {
            return hostRepository.findByUserIdAndAddress(requestHost.getUserId(), requestHost.getAddress())
                    .flatMap(existingHost -> {
                        Host updateHost = CreateUpdateMapper.updateHost(existingHost, requestHost, updateDateTime);
                        return hostRepository.save(updateHost).map(host -> new HostCreateUpdateResponse(ResConstants.HOST_UPDATE + host.getHostId(), host));
                    })
                    .switchIfEmpty(Mono.error(new ResGraphException(String.format(ResConstants.HOST_NOT_FOUND_USER_ID_ADDRESS, requestHost.getUserId(), requestHost.getAddress()), HttpStatus.BAD_REQUEST)));
        } else {
            throw new ResGraphException(ResConstants.HOST_NO_IDENTIFYING_ERROR, HttpStatus.BAD_REQUEST);
        }
    }

}
