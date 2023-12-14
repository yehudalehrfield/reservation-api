package com.yl.reservation.host.service;

import com.yl.reservation.host.exception.GraphQLException;
import com.yl.reservation.host.exception.HostException;
import com.yl.reservation.host.model.Host;
import com.yl.reservation.host.repository.HostRepositoryReactive;
import com.yl.reservation.host.util.HostUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HostGraphService {
    @Autowired
    HostRepositoryReactive hostRepositoryReactive;

    public Mono<Host> getHostById(String id){
        return hostRepositoryReactive.findById(id);
    }

    public Flux<Host> getAllHosts(){
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
                    .flatMap(hostToUpdate -> updateExistingHost(hostToUpdate,hostUpdateRequest.getHost(),createUpdateDateTime))
                    .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(), HttpStatus.BAD_REQUEST)))
                    .switchIfEmpty(Mono.error(new HostException(HttpStatus.BAD_REQUEST, "No host with id: " + hostUpdateRequest.getHost().getId())));
        } else {
            // if no id is given, fetch the host by last name and address
            //todo: add a field in Address to check if it is an update to address. this will help in creation of new host (throw an error if the host already exists)
            // also if it is change to the address, we cannot fetch by address and name. maybe 1) check for primary and then fetch by lastname and email/phone...
            return hostRepositoryReactive.findByLastNameAndAddress(hostUpdateRequest.getHost().getLastName(), hostUpdateRequest.getHost().getAddress())
                    .flatMap(hostToUpdate -> updateExistingHost(hostToUpdate,hostUpdateRequest.getHost(),createUpdateDateTime))
                    .onErrorResume(error -> Mono.error(new GraphQLException(error.getMessage(), HttpStatus.BAD_REQUEST)))
                    .switchIfEmpty(createNewHost(hostUpdateRequest,createUpdateDateTime));
        }
    }
    private Mono<HostResponse> updateExistingHost(Host hostToUpdate, Host hostFromRequest, String createUpdateDateTime){
        updateHostFields(hostToUpdate, hostFromRequest);
        hostToUpdate.setLastUpdated(createUpdateDateTime);
        return hostRepositoryReactive.save(hostToUpdate)
                .flatMap(savedHost -> Mono.just(new HostResponse("Updated host: " + savedHost.getId(),savedHost)));
    }

    private Mono<HostResponse> createNewHost(HostUpdateRequest hostUpdateRequest, String createUpdateDateTime){
        //todo: check if this is an update to address, if no, fetch by lastname and address to validate new host...
        HostUtil.validateHostCreationFields(hostUpdateRequest);

        Host updatedHost = hostUpdateRequest.getHost();
        updatedHost.setCreatedDate(createUpdateDateTime);
        updatedHost.setLastUpdated(createUpdateDateTime);
        return hostRepositoryReactive.save(updatedHost)
                .flatMap(savedHost -> Mono.just(new HostResponse("Created host " + savedHost.getId(),savedHost)));
    }

    //todo: move to util?
    private void updateHostFields(Host updatedHost, Host requestHost) {
        if (!HostUtil.isUpdate(updatedHost, requestHost)) {
            throw new HostException(HttpStatus.OK, "No updates to apply");
        } else {
            if (requestHost.getAddress() != null) updatedHost.setAddress(requestHost.getAddress());
            updateContactInfo(requestHost,updatedHost);
            updateReservationInfo(requestHost,updatedHost);
        }

    }
    //todo: move to util?
    private void updateContactInfo(Host requestHost, Host updatedHost){
        if (requestHost.getEmail() != null) updatedHost.setEmail(requestHost.getEmail());
        if (requestHost.getPhone() != null) updatedHost.setPhone(requestHost.getPhone());
        if (requestHost.getPrimaryContactMethod() != null) updatedHost.setPrimaryContactMethod(requestHost.getPrimaryContactMethod());
    }

    //todo: move to util?
    private void updateReservationInfo(Host requestHost, Host updatedHost){
        if (requestHost.getBeds() > 0) updatedHost.setBeds(requestHost.getBeds());
        if (requestHost.getNotes() != null) updatedHost.setNotes(requestHost.getNotes());
        if (requestHost.getCrib() != null) updatedHost.setCrib(requestHost.getCrib());
        if (requestHost.getFullBath() != null) updatedHost.setFullBath(requestHost.getFullBath());
        if (requestHost.getPrivateEntrance() != null) updatedHost.setPrivateEntrance(requestHost.getPrivateEntrance());
    }
}
