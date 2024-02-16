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

        Address hostAddress = new Address("123 Main St.", null, "New York", State.NY, "10001");

        Host requestHost = new Host();
        requestHost.setHostId("hostId1");
        requestHost.setAddress(hostAddress);
        requestHost.setNotes("new notes");

        request.setHost(requestHost);

        Host existingHost = new Host();
        existingHost.setHostId("hostId1");

        HostCreateUpdateResponse response = new HostCreateUpdateResponse("Updated host hostId1", requestHost);

        Mockito.when(hostRepository.findByHostId(Mockito.anyString())).thenReturn(Mono.just(existingHost));
        Mockito.when(hostRepository.save(Mockito.any())).thenReturn(Mono.just(requestHost));

        StepVerifier.create(hostService.updateHost(request.getHost(), true, "today"))
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
        existingHost.setHostId("hostId1");
        existingHost.setAddress(hostAddress);

        HostCreateUpdateResponse response = new HostCreateUpdateResponse("Updated host null", requestHost);

        Mockito.when(hostRepository.findByUserIdAndAddress(Mockito.anyString(), Mockito.any())).thenReturn(Mono.just(existingHost));
        Mockito.when(hostRepository.save(Mockito.any())).thenReturn(Mono.just(requestHost));

        StepVerifier.create(hostService.updateHost(requestHost, false,"today"))
                .expectNext(response)
                .verifyComplete();

    }

    @Test
    void updateHostGivenUserIdAndAddress_AddressUpdateError() {
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

        ResGraphException actualError = Assertions.assertThrows(ResGraphException.class, () -> hostService.updateHost(request.getHost(), true,"today"));
        Assertions.assertEquals(expectedError, actualError);

    }

    @Test
    void updateGuest_noGuestIdentifying(){
        Host requestHost = new Host();
        requestHost.setUserId("userId1");

        ResGraphException expectedError = new ResGraphException(ResConstants.HOST_NO_IDENTIFYING_ERROR, HttpStatus.BAD_REQUEST);

        StepVerifier.create(hostService.updateHost(requestHost,false, "today"))
                .expectErrorMatches(error -> error.equals(expectedError))
                .verify();

    }

    @Test
    void createHost() {
        HostCreateUpdateRequest request = new HostCreateUpdateRequest();

        Host requestHost = new Host();

        Address hostAddress = new Address("123 Main St.", null, "New York", State.NY, "10001");

        requestHost.setUserId("userId1");
        requestHost.setAddress(hostAddress);

        Host existingHost = new Host();
        existingHost.setHostId("hostId1");
        existingHost.setUserId("userId1");
        existingHost.setAddress(hostAddress);

        request.setHost(requestHost);

        Mockito.when(hostRepository.findByUserIdAndAddress(Mockito.anyString(), Mockito.any()))
                .thenReturn(Mono.just(existingHost));

        StepVerifier.create(hostService.createHost(request.getHost(), "today"))
                .expectError()
                .verify();

    }

    @Test
    void createHost_hostAlreadyExistsError() {
        HostCreateUpdateRequest request = new HostCreateUpdateRequest();

        Host requestHost = new Host();

        Address hostAddress = new Address("123 Main St.", null, "New York", State.NY, "10001");

        requestHost.setUserId("userId1");
        requestHost.setAddress(hostAddress);

        request.setHost(requestHost);

        Host existingHost = new Host();
        existingHost.setHostId("hostId1");
        existingHost.setUserId("userId1");
        existingHost.setAddress(hostAddress);

        Mockito.when(hostRepository.findByUserIdAndAddress(Mockito.any(), Mockito.any())).thenReturn(Mono.just(existingHost));

        ResGraphException expectedError = new ResGraphException(ResConstants.HOST_ALREADY_EXISTS_ERROR, HttpStatus.BAD_REQUEST);

        StepVerifier.create(hostService.createHost(request.getHost(), "today"))
                .expectErrorMatches(error -> error.equals(expectedError))
                .verify();

    }

    @Test
    void createNewGuest_noSuchUser() {
        Host requestHost = new Host();
        requestHost.setUserId("userId1");

        Address hostAddress = new Address("123 Main St.", null, "New York", State.NY, "10001");
        requestHost.setAddress(hostAddress);

        Mockito.when(hostRepository.findByUserIdAndAddress(Mockito.anyString(), Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(userRepository.findByUserId(Mockito.any())).thenReturn(Mono.empty());

        ResGraphException error = new ResGraphException("No user with id: userId1", HttpStatus.BAD_REQUEST);

        StepVerifier.create(hostService.createHost(requestHost, "today"))
                .expectErrorMatches(errorResponse -> errorResponse.equals(error))
                .verify();
    }

}
