package com.yl.reservation.service;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.*;
import com.yl.reservation.repository.HostRepository;
import com.yl.reservation.repository.UserRepository;
import com.yl.reservation.util.ResConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class HostServiceTest {

    @InjectMocks
    HostService hostService;

    @Mock
    HostRepository hostRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CreateUpdateMapper createUpdateMapper;

    @Test
    void getHostById() {
        Host host1 = new Host();
        host1.setHostId("hostId1");
        host1.setUserId("userId1");

        User user1 = new User();
        user1.setUserId("userId1");

        HostDetails hostDetailsWithUserInfo = new HostDetails(host1, user1);
        HostDetails hostDetailsNoUserInfo = new HostDetails(host1, null);

        HostSearchResponse responseWithUserInfo = new HostSearchResponse("Retrieved host hostId1 with user info...",
                List.of(hostDetailsWithUserInfo));
        HostSearchResponse responseNoUserInfo = new HostSearchResponse("Retrieved host hostId1",
                List.of(hostDetailsNoUserInfo));

        Mockito.when(hostRepository.findByHostId(Mockito.anyString())).thenReturn(Mono.just(host1));
        Mockito.when(userRepository.findByUserId(Mockito.anyString())).thenReturn(Mono.just(user1));

        StepVerifier.create(hostService.getHostById("host1", true))
                .expectNext(responseWithUserInfo)
                .verifyComplete();

        StepVerifier.create(hostService.getHostById("host1", false))
                .expectNext(responseNoUserInfo)
                .verifyComplete();
    }

    @Test
    void getAllHosts() {
        Host host1 = new Host();
        host1.setHostId("hostId1");
        host1.setUserId("userId1");
        Host host2 = new Host();
        host2.setHostId("hostId2");
        host2.setUserId("userId2");

        User user1 = new User();
        user1.setUserId("userId1");
        User user2 = new User();
        user2.setUserId("userId2");

        HostDetails hostDetails1 = new HostDetails(host1, user1);
        HostDetails hostDetails2 = new HostDetails(host2, user2);
        HostDetails hostDetails3 = new HostDetails(host1, null);
        HostDetails hostDetails4 = new HostDetails(host2, null);

        HostSearchResponse responseWithUserInfo = new HostSearchResponse("Retrieved all hosts with user info",
                List.of(hostDetails1, hostDetails2));
        HostSearchResponse responseNoUserInfo = new HostSearchResponse("Retrieved all hosts without user info",
                List.of(hostDetails3, hostDetails4));

        Mockito.when(hostRepository.findAll()).thenReturn(Flux.just(host1, host2));
        Mockito.when(userRepository.findByUserId(Mockito.anyString())).thenReturn(Mono.just(user1), Mono.just(user2));

        StepVerifier.create(hostService.getAllHosts(true))
                .expectNext(responseWithUserInfo)
                .verifyComplete();

        StepVerifier.create(hostService.getAllHosts(false))
                .expectNext(responseNoUserInfo)
                .verifyComplete();
    }

    @Test
    void updateHostGivenHostId() {
        HostCreateUpdateRequest request = new HostCreateUpdateRequest();

        Host requestHost = new Host();
        requestHost.setHostId("hostId1");
        requestHost.setNotes("new notes");

        request.setHost(requestHost);

        Host existingHost = new Host();
        existingHost.setHostId("hostId1");

        HostCreateUpdateResponse response = new HostCreateUpdateResponse("Updated host hostId1", requestHost);

        Mockito.when(hostRepository.findByHostId(Mockito.anyString())).thenReturn(Mono.just(existingHost));
        Mockito.when(hostRepository.save(Mockito.any())).thenReturn(Mono.just(requestHost));

        StepVerifier.create(hostService.updateHost(request.getHost(), "today"))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void updateHostGivenUserIdAndAddress() {
        HostCreateUpdateRequest request = new HostCreateUpdateRequest();

        Host requestHost = new Host();

        Address hostAddress = new Address("123 Main St.", null, "New York", State.NY, "10001");

        requestHost.setUserId("userId1");
        requestHost.setAddress(hostAddress);
        requestHost.setNotes("new notes");

        request.setHost(requestHost);

        Host existingHost = new Host();
        existingHost.setAddress(hostAddress);

        ResGraphException expectedError = new ResGraphException(ResConstants.HOST_ID_REQUIRED_FOR_ADDRESS_UPDATE, HttpStatus.BAD_REQUEST);

        ResGraphException actualError = Assertions.assertThrows(ResGraphException.class, () -> hostService.updateHost(request.getHost(), "today"));
        Assertions.assertEquals(expectedError, actualError);

    }

    @Test
    void createHost() {
        HostCreateUpdateRequest request = new HostCreateUpdateRequest();

        Host requestHost = new Host();

        Address hostAddress = new Address("123 Main St.", null, "New York", State.NY, "10001");

        requestHost.setUserId("userId1");
        requestHost.setAddress(hostAddress);
        requestHost.setNotes("new notes");

        request.setHost(requestHost);

        Mockito.when(hostRepository.findByUserIdAndAddress(Mockito.anyString(), Mockito.any()))
                .thenReturn(Mono.empty());
        Mockito.when(hostRepository.save(Mockito.any())).thenReturn(Mono.just(requestHost));

        StepVerifier.create(hostService.createHost(request.getHost(), "today"))
                .expectNextCount(1)
                .verifyComplete();

    }


    // todo: error tests
}
