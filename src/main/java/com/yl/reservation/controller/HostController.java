package com.yl.reservation.controller;

import com.yl.reservation.exception.HostException;
import com.yl.reservation.model.Host;
import com.yl.reservation.service.HostUpdateRequest;
import com.yl.reservation.service.HostResponse;
import com.yl.reservation.service.HostService;
import com.yl.reservation.util.HostConstants;
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

    @PostMapping("/update")
    public ResponseEntity<HostResponse> updateHost(@RequestBody HostUpdateRequest request) {
        HostResponse response = new HostResponse();
        HttpStatus status;
        try {
            response = hostService.updateHost(request);
            status = HttpStatus.OK;
        } catch (HostException ex) {
            //todo: logging
            response.setMessage(ex.getMessage());
            status = ex.getStatus();
        } catch (Exception ex) {
            //todo: logging
            response.setMessage(HostConstants.GENERAL_HOST_ERROR);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(response,status);
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