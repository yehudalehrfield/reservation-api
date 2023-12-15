package com.yl.reservation.service;

import com.yl.reservation.exception.HostException;
import com.yl.reservation.model.Host;
import com.yl.reservation.repository.HostRepository;
import com.yl.reservation.util.HostUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HostService {

    @Autowired
    HostRepository hostRepository;

    public List<Host> getHosts() {
        return hostRepository.findAll();
    }

    public Optional<Host> deleteHost(String id) {
        Optional<Host> deletedHost = hostRepository.findById(id);
        hostRepository.deleteById(id);
        return deletedHost;
    }

    public HostResponse updateHost(HostUpdateRequest request) {
        String createUpdateDateTime = HostUtil.getCurrentDateTimeString();
        HostResponse response = new HostResponse();
        Optional<Host> hostToUpdate;
        Host updatedHost;
        if (request.getHost() == null) {
            throw new HostException(HttpStatus.BAD_REQUEST, "No host included in the request");
        }

        // check if host is in the request and retrieve the document
        if (request.getHost().getId() != null) {
            hostToUpdate = hostRepository.findById(request.getHost().getId());
            // if no host is found with the given id, throw a bad request error
            if (hostToUpdate.isEmpty()) {
                throw new HostException(HttpStatus.BAD_REQUEST, "No host with id: " + request.getHost().getId());
            }
        } else {
            // no id is given. find the host by last name and address
            hostToUpdate = hostRepository.findByLastNameAndAddress(request.getHost().getLastName(), request.getHost().getAddress());
        }
        // if host exists, update the existing document
        if (hostToUpdate.isPresent()) {
            updatedHost = hostToUpdate.get();

            HostUtil.updateHostFields(updatedHost, request.getHost());

            updatedHost.setLastUpdated(createUpdateDateTime);

            hostRepository.save(updatedHost);

            response.setMessage("Updated host " + updatedHost.getId());

        } else {
            //todo: check if this is an address change?
//            if (doesExist(request.getHost()))
//                throw new HostException(HttpStatus.BAD_REQUEST,"Host with given contact info already exists");

            // insert a new host document
            HostUtil.validateHostCreationFields(request);

            updatedHost = request.getHost();
            updatedHost.setCreatedDate(createUpdateDateTime);
            updatedHost.setLastUpdated(createUpdateDateTime);
            updatedHost = hostRepository.save(request.getHost());
            response.setMessage("Created host " + updatedHost.getId());
        }

        response.setHost(updatedHost);
        return response;
    }

    public Host getHostById(String id) {
        Optional<Host> host = hostRepository.findById(id);
        return host.orElse(null);
    }

    //todo:
//    public boolean doesExist(Host requestHost){
//        Optional<Host> existingHost;
//        if (requestHost.getPrimaryContactMethod() == ContactMethod.PHONE){
//            existingHost = hostRepository.findByLastAndEmail(requestHost.getLastName(), requestHost.getEmail());
//        } else {
//            existingHost = hostRepository.findByLastNameAndPhone(requestHost.getLastName(), requestHost.getPhone());
//        }
//        return existingHost.isPresent();
//    }
}
