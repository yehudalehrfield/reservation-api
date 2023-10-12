package com.yl.reservation.host.controller;

import com.yl.reservation.host.model.Host;
import com.yl.reservation.host.service.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Host> createHost(@RequestBody Host host){
        return new ResponseEntity<>(hostService.createHost(host), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Optional<Host>> deleteHost(@PathVariable("id") String id){
        return new ResponseEntity<>(hostService.deleteHost(id), HttpStatus.OK);
    }
}
