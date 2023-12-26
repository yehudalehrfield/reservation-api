package com.yl.reservation.controller;

import com.yl.reservation.exception.HostException;
import com.yl.reservation.model.Host;
import com.yl.reservation.service.HostUpdateRequest;
import com.yl.reservation.repository.HostRepository;
import com.yl.reservation.service.HostResponse;
import com.yl.reservation.service.HostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class HostControllerTest {
/*
    @InjectMocks
    HostController hostController;

    @Mock
    HostService hostService;

    @Mock
    HostRepository hostRepository;

    @Test
    void getHosts() {
        List<Host> hosts = new ArrayList<>();
        Host host1 = new Host();
        host1.setLastName("smith");
        host1.setFirstName("joe");
        Host host2 = new Host();
        host2.setLastName("doe");
        host2.setFirstName("john");

        hosts.add(host1);
        hosts.add(host2);

        Mockito.when(hostService.getHosts()).thenReturn(hosts);

        ResponseEntity<List<Host>> resp = hostController.getHosts();

        Assertions.assertEquals(resp.getBody(), hosts);

    }

    @Test
    void findHost() {
        Host host = new Host();
        host.setLastName("smith");
        host.setFirstName("Joe");
        host.setId("abc");

        Mockito.when(hostService.getHostById(Mockito.anyString())).thenReturn(host, null);
//        Mockito.when(hostService.getHostById("error")).thenThrow(new RuntimeException());

        ResponseEntity<HostResponse> resp = hostController.findHost("abc");
        ResponseEntity<HostResponse> resp1 = hostController.findHost("bogus");


        HostResponse expectedResponse = new HostResponse("Retrieved host abc", host);
        HostResponse expectedNotFound = new HostResponse("Could not find host bogus", null);

        Assertions.assertEquals(resp.getBody(), expectedResponse);
        Assertions.assertEquals(resp1.getBody(), expectedNotFound);

    }

    @Test
    void findHostException() {
        Mockito.when(hostService.getHostById(Mockito.anyString())).thenThrow(new RuntimeException());

        HostResponse expectedResponse = new HostResponse("Sorry, something went wrong with host api...", null);

        ResponseEntity<HostResponse> resp = hostController.findHost("error");

        Assertions.assertEquals(resp.getBody(), expectedResponse);

    }

    @Test
    void update() {
        HostUpdateRequest request = new HostUpdateRequest();
        Host host = new Host();
        host.setId("abc");
        host.setLastName("smith");
        request.setHost(host);

//        Mockito.when(hostRepository.findByLastNameAndAddress(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(host));
        HostResponse expectedResp = new HostResponse("Updated host abc", host);
        Mockito.when(hostService.updateHost(request)).thenReturn(expectedResp);

        ResponseEntity<HostResponse> resp = hostController.updateHost(request);

        Assertions.assertEquals(resp.getBody(), expectedResp);
    }

    @Test
    void updateHostException(){
        HostUpdateRequest request = new HostUpdateRequest();
        Mockito.when(hostService.updateHost(request)).thenThrow(new HostException(HttpStatus.BAD_REQUEST, "No host included in the request"));
//        Assertions.assertThrows(HostException.class,()->hostController.updateHost(request));
        ResponseEntity resp = hostController.updateHost(request);
        Assertions.assertEquals(resp.getBody(), new HostResponse("No host included in the request",null));
    }

    @Test
    void updateException(){
        HostUpdateRequest request = new HostUpdateRequest();
        Mockito.when(hostService.updateHost(request)).thenThrow(new RuntimeException());
//        Assertions.assertThrows(HostException.class,()->hostController.updateHost(request));
        ResponseEntity resp = hostController.updateHost(request);
        Assertions.assertEquals(resp.getBody(), new HostResponse("Sorry, something went wrong with host api...",null));
    }

    @Test
    void deleteHost(){
        Host host = new Host();
        host.setId("abc");
        HostResponse expectedResponse = new HostResponse("Successfully deleted host",host);
        Mockito.when(hostService.deleteHost(Mockito.anyString())).thenReturn(Optional.of(host));
        Assertions.assertEquals(hostController.deleteHost("abc").getBody(),expectedResponse);
    }

    @Test
    void deleteHostNotFound(){
        Host host = new Host();
        host.setId("abc");
        HostResponse expectedResponse = new HostResponse("No host found to delete",null);
        Mockito.when(hostService.deleteHost(Mockito.anyString())).thenReturn(Optional.empty());
        Assertions.assertEquals(hostController.deleteHost("abc").getBody(),expectedResponse);
    }

 */
}

