package com.yl.reservation.service;

import com.yl.reservation.exception.GraphQLException;
import com.yl.reservation.exception.HostException;
import com.yl.reservation.model.ContactMethod;
import com.yl.reservation.model.Email;
import com.yl.reservation.model.Host;
import com.yl.reservation.model.Phone;
import com.yl.reservation.repository.HostRepositoryReactive;
import com.yl.reservation.util.HostUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class HostGraphService {
    @Autowired
    HostRepositoryReactive hostRepositoryReactive;

    public Mono<Host> getHostById(String id) {
        return hostRepositoryReactive.findById(id);
    }

    public Flux<Host> getAllHosts() {
        return hostRepositoryReactive.findAll();
    }

    public Mono<HostResponse> createUpdateHost(HostUpdateRequest hostUpdateRequest) {
        String createUpdateDateTime = HostUtil.getCurrentDateTimeString();
        // check if host is in the request; throw an error if not.
        if (hostUpdateRequest.getHost() == null) {
            throw new HostException(HttpStatus.BAD_REQUEST, "No host included in the request");
        }
        // check if an id is given in the request and if so fetch by id
        if (hostUpdateRequest.getHost().getId() != null) {
            return hostRepositoryReactive.findById(hostUpdateRequest.getHost().getId())
                    .flatMap(hostToUpdate -> updateExistingHost(hostToUpdate, hostUpdateRequest.getHost(),
                            hostUpdateRequest.getIsAddressUpdate(), createUpdateDateTime))
                    .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(),
                            HttpStatus.BAD_REQUEST)))
                    .switchIfEmpty(Mono.error(new HostException(HttpStatus.BAD_REQUEST,
                            "No host with id: " + hostUpdateRequest.getHost().getId())));
        } else {
            // if no id is given, fetch the host by last name and address
            //todo: add a field in Address to check if it is an update to address. this will help in creation of new
            // host (throw an error if the host already exists)
            // also if it is change to the address, we cannot fetch by address and name. maybe 1) check for primary
            // and then fetch by lastname and email/phone...
            return hostRepositoryReactive.findByLastNameAndAddress(hostUpdateRequest.getHost().getLastName(),
                            hostUpdateRequest.getHost().getAddress())
                    .flatMap(hostToUpdate -> updateExistingHost(hostToUpdate, hostUpdateRequest.getHost(),
                            hostUpdateRequest.getIsAddressUpdate(), createUpdateDateTime))
                    .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(),
                            HttpStatus.BAD_REQUEST)))
                    .switchIfEmpty(createNewHostOrApplyAddressUpdate(hostUpdateRequest, createUpdateDateTime));
        }
    }

    private Mono<HostResponse> updateExistingHost(Host hostToUpdate, Host hostFromRequest, boolean isAddressUpdate,
                                                  String createUpdateDateTime) {
        HostUtil.updateHostFields(hostToUpdate, hostFromRequest, isAddressUpdate);
        hostToUpdate.setLastUpdated(createUpdateDateTime);
        return hostRepositoryReactive.save(hostToUpdate)
                .flatMap(savedHost -> Mono.just(new HostResponse("Updated host: " + savedHost.getId(), savedHost)));
    }

    private Mono<HostResponse> createNewHostOrApplyAddressUpdate(HostUpdateRequest hostUpdateRequest,
                                                                 String createUpdateDateTime) {

        return doesHostExistDifferentAddress(hostUpdateRequest.getHost())
                .flatMap(hostToUpdate -> {
                    System.out.println("Host Already Exists. Working on updating address.");
                    if (hostUpdateRequest.getIsAddressUpdate()) {
                        HostUtil.updateHostFields(hostToUpdate, hostUpdateRequest.getHost(), true);
                        return hostRepositoryReactive.save(hostToUpdate)
                                .flatMap(savedHost -> Mono.just(new HostResponse("Updated host " + savedHost.getId() + " including address change.", savedHost)));
                    } else {
                        throw new HostException(HttpStatus.BAD_REQUEST, "Cannot apply update. Change of address " +
                                "identified but isAddressUpdate is not 'true'");
                    }
                })
                .switchIfEmpty(proceedWithCreateHost(hostUpdateRequest, createUpdateDateTime))
                .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(), HttpStatus.BAD_REQUEST)));
    }

    private Mono<HostResponse> proceedWithCreateHost(HostUpdateRequest hostUpdateRequest, String createUpdateDateTime) {
        HostUtil.validateHostCreationFields(hostUpdateRequest);

        Host updatedHost = hostUpdateRequest.getHost();
        updatedHost.setCreatedDate(createUpdateDateTime);
        updatedHost.setLastUpdated(createUpdateDateTime);
        return hostRepositoryReactive.save(updatedHost)
                .flatMap(savedHost -> Mono.just(new HostResponse("Created host " + savedHost.getId(), savedHost)));
    }

    private Mono<Host> doesHostExistDifferentAddress(Host requestHost) {
        if (requestHost.getPrimaryContactMethod() == ContactMethod.PHONE) {
            return hostRepositoryReactive.findByLastNameAndPrimaryPhone(requestHost.getLastName(),
                            requestHost.getPhone().stream().filter(Phone::isPrimary).toList().get(0).getValue())
                    .collectList()
                    .flatMap(hostList -> {
                        if (hostList.size() > 1)
                            throw new HostException(
                                    HttpStatus.BAD_REQUEST,
                                    "Multiple hosts with given lastName and primary contact info. Provide hostId to " +
                                            "update host info."
                            );
                        return hostList.isEmpty() ? Mono.empty() : Mono.just(hostList.get(0));
                    });
        } else {
            return hostRepositoryReactive.findByLastNameAndPrimaryEmail(requestHost.getLastName(),
                            requestHost.getEmail().stream().filter(Email::isPrimary).toList().get(0).getValue())
                    .collectList()
                    .flatMap(hostList -> {
                        if (hostList.size() > 1)
                            throw new HostException(
                                    HttpStatus.BAD_REQUEST,
                                    "Multiple hosts with given lastName and primary contact info. Provide hostId to " +
                                            "update host info."
                            );
                        return hostList.isEmpty() ? Mono.empty() : Mono.just(hostList.get(0));
                    });
        }
    }

}
