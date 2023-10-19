package com.yl.reservation.host.controller;

import com.yl.reservation.host.model.Host;
import com.yl.reservation.host.service.HostResponse;
import com.yl.reservation.host.service.HostService;
import com.yl.reservation.host.util.HostConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hosts")
public class HostController {
    @Autowired
    HostService hostService;

    @GetMapping()
    public ResponseEntity<List<Host>> getHosts() {
        return new ResponseEntity<>(hostService.getHosts(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HostResponse> findHost(@PathVariable("id") String id){
        HostResponse hostResponse = new HostResponse();
        try {
            Host host = hostService.getHostById(id);
            hostResponse.setHost(host);
            if (host == null) {
                hostResponse.setMessage("Could not find host " + id);
            } else {
                hostResponse.setMessage("Retrieved host " + id);
            }
            return new ResponseEntity<>(hostResponse,HttpStatus.OK);
        } catch (Exception ex){
            hostResponse.setMessage(HostConstants.GENERAL_HOST_ERROR);
            return new ResponseEntity<>(hostResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    public ResponseEntity<HostResponse> createHost(@RequestBody Host host) {
        try {
            HostResponse hostResponse = new HostResponse();
            Host newHost = hostService.createHost(host);
            if (null != newHost) {
                hostResponse.setMessage("Successful creation of new host");
                hostResponse.setHost(newHost);
            } else {
                hostResponse.setMessage(String.format("Host %s %s at %s already exists", host.getFirstName(), host.getLastName(), host.getAddress()));
            }
            return new ResponseEntity<>(hostResponse, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new HostResponse(HostConstants.GENERAL_HOST_ERROR, null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/update")
    public ResponseEntity<HostResponse> updateHost(@RequestBody Host host) {
        try {
            Host updatedHost = hostService.updateHost(host);
            HostResponse hostResponse = new HostResponse();
            if (null != updatedHost){
                hostResponse.setHost(updatedHost);
                hostResponse.setMessage(String.format("Host %s updated", updatedHost.getId()));
                return new ResponseEntity<>(hostResponse,HttpStatus.OK);
            } else {
                hostResponse.setMessage("No such host");
                return new ResponseEntity<>(hostResponse, HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {
            return new ResponseEntity<>(new HostResponse(HostConstants.GENERAL_HOST_ERROR, null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HostResponse> deleteHost(@PathVariable("id") String id) {
        HostResponse hostResponse = new HostResponse();

        hostService.deleteHost(id).ifPresentOrElse(host -> {
            hostResponse.setHost(host);
            hostResponse.setMessage("Successfully deleted host");
        }, () -> hostResponse.setMessage("No host found to delete"));

        return new ResponseEntity<>(hostResponse, HttpStatus.OK);

    }
}
