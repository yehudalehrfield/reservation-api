package com.yl.reservation.host.controller;

import com.yl.reservation.host.exception.GraphQLException;
import com.yl.reservation.host.model.Host;
import com.yl.reservation.host.service.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HostGraphController {

    @Autowired
    HostService hostService;

    @QueryMapping
    public Host hostById(@Argument String id){
        Host host = hostService.getHostById(id);
        if (host == null) throw new GraphQLException("Host not found with id: " + id, HttpStatus.NOT_FOUND);
        return host;
    }



}
