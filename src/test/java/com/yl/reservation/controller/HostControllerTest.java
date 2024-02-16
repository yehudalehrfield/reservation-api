package com.yl.reservation.controller;

import com.yl.reservation.model.Host;
import com.yl.reservation.model.User;
import com.yl.reservation.service.host.HostDetails;
import com.yl.reservation.service.host.HostSearchResponse;
import com.yl.reservation.service.host.HostService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class HostControllerTest {

        @InjectMocks
        HostController hostController;

        @Mock
        HostService hostService;

        @Test
        void getAllHosts() {
                Host host1 = new Host();
                host1.setHostId("id1");
                host1.setUserId("userId1");
                Host host2 = new Host();
                host2.setHostId("id2");
                host2.setUserId("userId2");

                User user1 = new User();
                user1.setUserId("userId1");
                User user2 = new User();
                user2.setUserId("userId2");

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
        void getHostById() {
                Host host = new Host();
                host.setHostId("hostId");
                host.setUserId("userId");
                User user = new User();
                user.setUserId("userId");
                HostDetails hostDetails = new HostDetails(host, user);
                HostDetails hostDetails2 = new HostDetails(host, null);

                HostSearchResponse responseUserInfo = new HostSearchResponse("success", List.of(hostDetails));

                HostSearchResponse responseNoUserInfo = new HostSearchResponse("success", List.of(hostDetails2));

                Mockito.when(hostService.getHostById("hostId", true)).thenReturn(Mono.just(responseUserInfo));
                Mockito.when(hostService.getHostById("hostId", false)).thenReturn(Mono.just(responseNoUserInfo));

                StepVerifier.create(hostController.getHostById("hostId", true))
                                .expectNext(responseUserInfo)
                                .verifyComplete();

                StepVerifier.create(hostController.getHostById("hostId", false))
                                .expectNext(responseNoUserInfo)
                                .verifyComplete();
        }
}
