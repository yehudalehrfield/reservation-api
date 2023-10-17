package com.yl.reservation.host.controller;

import com.yl.reservation.host.model.Host;
import com.yl.reservation.host.service.HostResponse;
import com.yl.reservation.host.service.HostService;
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
    public ResponseEntity<List<Host>> getHosts(){
        return new ResponseEntity<>(hostService.getHosts(), HttpStatus.OK);
    }
    @PostMapping()
    public ResponseEntity<HostResponse> createHost(@RequestBody Host host){
        try{
            HostResponse hostResponse = new HostResponse();
            Host newHost = hostService.createHost(host);
            if (null != newHost) {
                hostResponse.setMessage("Successful creation of new host");
                hostResponse.setHost(newHost);
            } else {
                hostResponse.setMessage(String.format("Host %s %s at %s already exists", host.getFirstName(), host.getLastName(), host.getAddress()));
            }
            return new ResponseEntity<>(hostResponse, HttpStatus.OK);
        } catch (Exception ex){
            return new ResponseEntity<>(new HostResponse("Sorry, something went wrong...", null),
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
