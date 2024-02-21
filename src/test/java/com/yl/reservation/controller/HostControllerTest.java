package com.yl.reservation.controller;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.Host;
import com.yl.reservation.model.User;
import com.yl.reservation.service.host.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class HostControllerTest {

        @InjectMocks
        HostController hostController;

        @Mock
        HostService hostService;

        private String USER_ID1 = "userId1";
        private String USER_ID2 = "userId2";
        private String HOST_ID1 = "hostId1";
        private String HOST_ID2 = "hostId2";
        @Test
        void getAllHosts() {
                Host host1 = new Host();
                host1.setHostId(HOST_ID1);
                host1.setUserId(USER_ID1);
                Host host2 = new Host();
                host2.setHostId(HOST_ID2);
                host2.setUserId(USER_ID2);

                User user1 = new User();
                user1.setUserId(USER_ID1);
                User user2 = new User();
                user2.setUserId(USER_ID2);

                HostDetails hostDetails1 = new HostDetails(host1, user1);
                HostDetails hostDetails2 = new HostDetails(host2, user2);
                HostDetails hostDetails3 = new HostDetails(host1, null);
                HostDetails hostDetails4 = new HostDetails(host2, null);

                HostSearchResponse responseUserInfo = new HostSearchResponse("success",
                                List.of(hostDetails1, hostDetails2));
                HostSearchResponse responseNoUserInfo = new HostSearchResponse("success",
                                List.of(hostDetails3, hostDetails4));

                Mockito.when(hostService.getAllHosts(true)).thenReturn(Mono.just(responseUserInfo));
                Mockito.when(hostService.getAllHosts(false)).thenReturn(Mono.just(responseNoUserInfo));

                StepVerifier.create(hostController.getAllHosts(true))
                                .expectNext(responseUserInfo)
                                .verifyComplete();

                StepVerifier.create(hostController.getAllHosts(false))
                                .expectNext(responseNoUserInfo)
                                .verifyComplete();
        }

        @Test
        void getAllHostsError(){
                ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);
                Mockito.when(hostService.getAllHosts(false)).thenReturn(Mono.error(exception));
                StepVerifier.create(hostController.getAllHosts(false))
                        .expectErrorMatches(error -> error.equals(exception))
                        .verify();
        }

        @Test
        void getHostById() {
                Host host = new Host();
                host.setHostId(HOST_ID1);
                host.setUserId(USER_ID1);
                User user = new User();
                user.setUserId(USER_ID1);
                HostDetails hostDetails = new HostDetails(host, user);
                HostDetails hostDetails2 = new HostDetails(host, null);

                HostSearchResponse responseUserInfo = new HostSearchResponse("success", List.of(hostDetails));

                HostSearchResponse responseNoUserInfo = new HostSearchResponse("success", List.of(hostDetails2));

                Mockito.when(hostService.getHostById(HOST_ID1, true)).thenReturn(Mono.just(responseUserInfo));
                Mockito.when(hostService.getHostById(HOST_ID1, false)).thenReturn(Mono.just(responseNoUserInfo));

                StepVerifier.create(hostController.getHostById(HOST_ID1, true))
                                .expectNext(responseUserInfo)
                                .verifyComplete();

                StepVerifier.create(hostController.getHostById(HOST_ID1, false))
                                .expectNext(responseNoUserInfo)
                                .verifyComplete();
        }

        @Test
        void getGuestByIdError(){
                ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

                Mockito.when(hostService.getHostById(HOST_ID1, false)).thenReturn(Mono.error(exception));

                StepVerifier.create(hostController.getHostById(HOST_ID1,false))
                        .expectErrorMatches(error -> error.equals(exception))
                        .verify();
        }
        
        @Test
        void createHost(){
                Host host = new Host();

                HostCreateUpdateRequest request = new HostCreateUpdateRequest();
                request.setHost(host);

                HostCreateUpdateResponse response = new HostCreateUpdateResponse("Host created successfully", host);
                
                Mockito.when(hostService.createHost(Mockito.any(), Mockito.anyString()))
                        .thenReturn(Mono.just(response));
                
                StepVerifier.create(hostController.createHost(request))
                        .expectNext(response)
                .verifyComplete();
        }

        @Test
        void createHostError(){
                Host host = new Host();

                HostCreateUpdateRequest request = new HostCreateUpdateRequest();
                request.setHost(host);

                ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

                Mockito.when(hostService.createHost(Mockito.any(), Mockito.anyString()))
                        .thenReturn(Mono.error(exception));

                StepVerifier.create(hostController.createHost(request))
                        .expectErrorMatches(error -> error.equals(exception))
                        .verify();
        }

        @Test
        void updateHost(){
                Host host = new Host();

                HostCreateUpdateRequest request = new HostCreateUpdateRequest();
                request.setHost(host);
                request.setIsAddressUpdate(false);

                HostCreateUpdateResponse response = new HostCreateUpdateResponse("Host updated successfully", host);

                Mockito.when(hostService.updateHost(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
                        .thenReturn(Mono.just(response));

                StepVerifier.create(hostController.updateHost(request))
                        .expectNext(response)
                        .verifyComplete();
        }

        @Test
        void updateHostError(){
                Host host = new Host();

                HostCreateUpdateRequest request = new HostCreateUpdateRequest();
                request.setHost(host);
                request.setIsAddressUpdate(false);

                ResGraphException exception = new ResGraphException("error", HttpStatus.INTERNAL_SERVER_ERROR);

                Mockito.when(hostService.updateHost(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
                        .thenReturn(Mono.error(exception));

                StepVerifier.create(hostController.updateHost(request))
                        .expectErrorMatches(error -> error.equals(exception))
                        .verify();
        }
}
