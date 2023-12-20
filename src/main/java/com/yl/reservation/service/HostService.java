package com.yl.reservation.service;

import com.yl.reservation.exception.HostException;
import com.yl.reservation.model.ContactMethod;
import com.yl.reservation.model.Email;
import com.yl.reservation.model.Host;
import com.yl.reservation.model.Phone;
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
            hostToUpdate = hostRepository.findByLastNameAndAddress(request.getHost().getLastName(),
                    request.getHost().getAddress());
        }
        // if host exists, update the existing document
        if (hostToUpdate.isPresent()) {
            updatedHost = hostToUpdate.get();

            HostUtil.updateHostFields(updatedHost, request.getHost(), request.getIsAddressUpdate());

            updatedHost.setLastUpdated(createUpdateDateTime);

            hostRepository.save(updatedHost);

            response.setMessage("Updated host " + updatedHost.getId());

        } else {
            // check if host exists with name and email/phone (but request has a different address)
            updatedHost = doesHostExistDifferentAddress(request.getHost());
            if (updatedHost != null) {
                System.out.println("Host Already Exists. Working on updating address.");
                if (request.getIsAddressUpdate() != null){
                    HostUtil.updateHostFields(updatedHost, request.getHost(), request.getIsAddressUpdate());
                    hostRepository.save(updatedHost);
                    response.setMessage("Updated host " + updatedHost.getId() + " including address change.");
                } else {
                    throw new HostException(HttpStatus.BAD_REQUEST, "Cannot apply update. Change of address identified but isAddressUpdate is null");
                }
            } else {
                // insert a new host document
                HostUtil.validateHostCreationFields(request);

                updatedHost = request.getHost();
                updatedHost.setCreatedDate(createUpdateDateTime);
                updatedHost.setLastUpdated(createUpdateDateTime);
                updatedHost = hostRepository.save(request.getHost());
                response.setMessage("Created host " + updatedHost.getId());
            }

        }

        response.setHost(updatedHost);
        return response;
    }

    public Host getHostById(String id) {
        Optional<Host> host = hostRepository.findById(id);
        return host.orElse(null);
    }

    public Host doesHostExistDifferentAddress(Host requestHost) {
        List<Host> existingHost;
        if (requestHost.getPrimaryContactMethod() == ContactMethod.PHONE) {
            existingHost = hostRepository.findByLastNameAndPrimaryPhone(requestHost.getLastName(),
                    requestHost.getPhone().stream().filter(Phone::isPrimary).toList().get(0).getValue());
        } else {
            existingHost = hostRepository.findByLastNameAndPrimaryEmail(requestHost.getLastName(),
                    requestHost.getEmail().stream().filter(Email::isPrimary).toList().get(0).getValue());
        }
        if (existingHost.size() > 1)
            throw new HostException(
                    HttpStatus.BAD_REQUEST,
                    "Multiple hosts with given lastName and primary contact info. Provide hostId to update host info."
            );
        return existingHost.isEmpty() ? null : existingHost.get(0);
    }
}
