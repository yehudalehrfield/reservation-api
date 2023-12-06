package com.yl.reservation.host.service;

import com.yl.reservation.host.exception.HostException;
import com.yl.reservation.host.model.*;
import com.yl.reservation.host.repository.HostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HostServiceTest {

    @InjectMocks
    HostService hostService;
    @Mock
    HostRepository hostRepository;

    @Test
    void getHosts(){
        List<Host> hosts = new ArrayList<>();
        Host host1 = new Host();
        host1.setLastName("smith");
        host1.setFirstName("joe");
        Host host2 = new Host();
        host2.setLastName("doe");
        host2.setFirstName("john");

        when(hostRepository.findAll()).thenReturn(hosts);

        assertEquals(hostService.getHosts(),hosts);
    }

    @Test
    void deleteHost(){
        Host host = new Host();
        host.setId("abc");
        when(hostRepository.findById(Mockito.anyString())).thenReturn(Optional.of(host));
        assertEquals(hostService.deleteHost("abc"),Optional.of(host));
    }

    @Test
    void updateHost(){
        Host hostToUpdate = new Host();
        hostToUpdate.setId("abc");
        hostToUpdate.setLastName("smith");
        hostToUpdate.setFirstName("joe");

        when(hostRepository.findById(Mockito.anyString())).thenReturn(Optional.of(hostToUpdate));
//        Mockito.when(hostRepository.save(Mockito.any())).thenReturn(hostToUpdate);

        Host requestHost = new Host();
        requestHost.setId("abc");
        requestHost.setNotes("notes");
        requestHost.setPrimaryContactMethod(ContactMethod.PHONE);

        HostRequest request = new HostRequest(requestHost);

        HostResponse resp = hostService.updateHost(request);
        String updateTime = LocalDateTime.now().toString().substring(0,16);
        assertEquals(updateTime, resp.getHost().getLastUpdated().substring(0,16));
        assertEquals("notes", resp.getHost().getNotes());
        assertEquals(ContactMethod.PHONE,resp.getHost().getPrimaryContactMethod());
        assertEquals("Updated host abc", resp.getMessage());
    }

    @Test
    void updateHostNoHost(){
        HostRequest request = new HostRequest();
        HostException exception = Assertions.assertThrows(HostException.class,()->hostService.updateHost(request));
        assertEquals("No host included in the request", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void updateHostNotFound(){
        Host requestHost = new Host();
        requestHost.setId("abc");
        HostRequest request = new HostRequest(requestHost);
        when(hostRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        HostException exception = Assertions.assertThrows(HostException.class,() -> hostService.updateHost(request));
        assertEquals("No host with id: abc", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

    }

    @Test
    void createNewHost(){
        Host requestHost = new Host();
        requestHost.setLastName("smith");
        requestHost.setFirstName("joe");
        Address address = new Address("123 Main St. ", null, "New York", State.NY, "10000");
        HostRequest request = new HostRequest(requestHost);
        when(hostRepository.save(Mockito.any())).thenReturn(requestHost);
        HostResponse hostResponse = hostService.updateHost(request);
        assertEquals("smith",hostResponse.getHost().getLastName());
        String updateTime = LocalDateTime.now().toString().substring(0,16);
        assertEquals(updateTime, hostResponse.getHost().getLastUpdated().substring(0,16));
    }

    @Test
    void getHostById(){
        Host host = new Host();
        host.setId("abc");
        when(hostRepository.findById(Mockito.anyString())).thenReturn(Optional.of(host)).thenReturn(Optional.empty());
        Assertions.assertEquals("abc",hostService.getHostById("abc").getId());
        Assertions.assertNull(hostService.getHostById("abc"));
    }
}
