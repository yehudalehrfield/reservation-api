package com.yl.reservation.service;

import com.yl.reservation.exception.GraphQLException;
import com.yl.reservation.model.*;
import com.yl.reservation.repository.HostRepository;
import com.yl.reservation.repository.UserRepository;
import com.yl.reservation.util.ResUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
    ResUtil resUtil;


    @Test
    void getHostById(){
        Host host1 = new Host();
        host1.setHostId("hostId1");
        host1.setUserId("userId1");

        User user1 = new User();
        user1.setUserId("userId1");

        HostDetails hostDetailsWithUserInfo = new HostDetails(host1,user1);
        HostDetails hostDetailsNoUserInfo = new HostDetails(host1,null);

        HostSearchResponse responseWithUserInfo = new HostSearchResponse("Retrieved host hostId1 with user info...", List.of(hostDetailsWithUserInfo));
        HostSearchResponse responseNoUserInfo = new HostSearchResponse("Retrieved host hostId1", List.of(hostDetailsNoUserInfo));

        Mockito.when(hostRepository.findByHostId(Mockito.anyString())).thenReturn(Mono.just(host1));
        Mockito.when(userRepository.findByUserId(Mockito.anyString())).thenReturn(Mono.just(user1));

        StepVerifier.create(hostService.getHostById("host1",true))
                .expectNext(responseWithUserInfo)
                .verifyComplete();

        StepVerifier.create(hostService.getHostById("host1",false))
                .expectNext(responseNoUserInfo)
                .verifyComplete();
    }

    @Test
    void getAllHosts(){
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
        HostDetails hostDetails3 = new HostDetails(host1,null);
        HostDetails hostDetails4 = new HostDetails(host2, null);

        HostSearchResponse responseWithUserInfo = new HostSearchResponse("Retrieved all hosts with user info", List.of(hostDetails1, hostDetails2));
        HostSearchResponse responseNoUserInfo = new HostSearchResponse("Retrieved all hosts without user info", List.of(hostDetails3,hostDetails4));

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
    void updateHostGivenHostId(){
        HostUpdateRequest request = new HostUpdateRequest();

        Host requestHost = new Host();
        requestHost.setHostId("hostId1");
        requestHost.setNotes("new notes");

        request.setHost(requestHost);
        request.setIsAddressUpdate(Boolean.FALSE);
        request.setIsUserUpdate(Boolean.FALSE);

        Host existingHost = new Host();
        existingHost.setHostId("hostId1");

        HostUpdateResponse response = new HostUpdateResponse("Updated host hostId1",requestHost,null);

        Mockito.when(hostRepository.findByHostId(Mockito.anyString())).thenReturn(Mono.just(existingHost));
        Mockito.when(hostRepository.save(Mockito.any())).thenReturn(Mono.just(requestHost));

        StepVerifier.create(hostService.createUpdateHost(request))
                .expectNext(response)
                .verifyComplete();
    }


    @Test
    void updateHostAndUserGivenHostId(){
        HostUpdateRequest request = new HostUpdateRequest();

        Host requestHost = new Host();
        requestHost.setHostId("hostId1");
        requestHost.setUserId("userId1");
        requestHost.setNotes("new notes");

        User requestUser = new User();
        requestUser.setUserId("userId1");
        requestUser.setPrimaryContactMethod(ContactMethod.EMAIL);

        Host existingHost = new Host();
        existingHost.setHostId("hostId1");

        User existingUser = new User();
        existingUser.setPrimaryContactMethod(ContactMethod.PHONE);

        request.setHost(requestHost);
        request.setUser(requestUser);
        request.setIsAddressUpdate(Boolean.FALSE);
        request.setIsUserUpdate(Boolean.TRUE);

        HostUpdateResponse response = new HostUpdateResponse("Updated host hostId1",requestHost,requestUser);

        Mockito.when(hostRepository.findByHostId(Mockito.anyString())).thenReturn(Mono.just(existingHost));
        Mockito.when(hostRepository.save(Mockito.any())).thenReturn(Mono.just(requestHost));
        Mockito.when(userRepository.findByUserId(Mockito.anyString())).thenReturn(Mono.just(existingUser));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(requestUser));


        StepVerifier.create(hostService.createUpdateHost(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void updateHostGivenUserIdAndAddress(){
        HostUpdateRequest request = new HostUpdateRequest();

        Host requestHost = new Host();

        Address hostAddress = new Address("123 Main St.",null,"New York", State.NY,"10001");

        requestHost.setUserId("userId1");
        requestHost.setAddress(hostAddress);
        requestHost.setNotes("new notes");

        request.setHost(requestHost);
        request.setIsAddressUpdate(Boolean.FALSE);
        request.setIsUserUpdate(Boolean.FALSE);

        Host existingHost = new Host();
        existingHost.setAddress(hostAddress);

        HostUpdateResponse response = new HostUpdateResponse("Updated host null",requestHost,null);

        Mockito.when(hostRepository.findByUserIdAndAddress(Mockito.anyString(), Mockito.any())).thenReturn(Mono.just(existingHost));
        Mockito.when(hostRepository.save(Mockito.any())).thenReturn(Mono.just(requestHost));

        StepVerifier.create(hostService.createUpdateHost(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void updateHostCreateNewHost() {
        HostUpdateRequest request = new HostUpdateRequest();

        Host requestHost = new Host();

        Address hostAddress = new Address("123 Main St.", null, "New York", State.NY, "10001");

        requestHost.setUserId("userId1");
        requestHost.setAddress(hostAddress);
        requestHost.setNotes("new notes");

        request.setHost(requestHost);
        request.setIsAddressUpdate(Boolean.FALSE);
        request.setIsUserUpdate(Boolean.FALSE);

        Mockito.when(hostRepository.findByUserIdAndAddress(Mockito.anyString(), Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(hostRepository.save(Mockito.any())).thenReturn(Mono.just(requestHost));

        StepVerifier.create(hostService.createUpdateHost(request))
                .expectNextCount(1)
                .verifyComplete();

    }

//    @Test
//    void hostUpdateErrors(){
//        HostUpdateRequest requestNoHostNoUser = new HostUpdateRequest();
//        StepVerifier.create(hostService.createUpdateHost(requestNoHostNoUser))
//                .expectErrorMatches(error -> error instanceof GraphQLException)
//                .verify();
//    }

    @Test
    void updateUserInfoGivenUserId(){
        HostUpdateRequest request = new HostUpdateRequest();
        User requestUser = new User();

        requestUser.setUserId("userId1");
        requestUser.setPrimaryContactMethod(ContactMethod.EMAIL);
        requestUser.setLastUpdated("today");
        requestUser.setCreatedDate("yesterday");

        User existingUser = new User();
        existingUser.setPrimaryContactMethod(ContactMethod.PHONE);

        request.setUser(requestUser);

        Mockito.when(userRepository.findByUserId(Mockito.anyString())).thenReturn(Mono.just(existingUser));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(requestUser));

        HostUpdateResponse response = new HostUpdateResponse();
        response.setMessage("Updated user userId1");
        response.setUser(requestUser);

        StepVerifier.create(hostService.createUpdateHost(request)).expectNext(response).verifyComplete();
    }

    @Test
    void updateUserInfoGivenLastNameAndPhone(){
        HostUpdateRequest request = new HostUpdateRequest();
        User requestUser = new User();

        requestUser.setLastName("smith");
        requestUser.setPhone(List.of(new Phone(Phone.PhoneType.HOME, "1234567890",true)));
        requestUser.setEmail(List.of(new Email(Email.EmailType.PERSONAL,"gmail@gmail.com",true)));
        requestUser.setPrimaryContactMethod(ContactMethod.PHONE);
        requestUser.setLastUpdated("today");
        requestUser.setCreatedDate("yesterday");

        User existingUser = new User();
        existingUser.setLastName("smith");
        existingUser.setPrimaryContactMethod(ContactMethod.PHONE);
        existingUser.setEmail(List.of(new Email(Email.EmailType.PERSONAL,"email@email.com",true)));

        request.setUser(requestUser);

        Mockito.when(userRepository.findByLastNameAndPrimaryPhone(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(existingUser));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(requestUser));

        HostUpdateResponse response = new HostUpdateResponse();
        response.setMessage("Updated user null"); // will be null here because if we pass it in with an id, we aren't testing this approach...
        response.setUser(requestUser);

        StepVerifier.create(hostService.createUpdateHost(request)).expectNext(response).verifyComplete();
    }

    @Test
    void updateUserInfoGivenLastNameAndEmail(){
        HostUpdateRequest request = new HostUpdateRequest();
        User requestUser = new User();

        requestUser.setLastName("smith");
        requestUser.setPhone(List.of(new Phone(Phone.PhoneType.HOME, "1234567890",true)));
        requestUser.setEmail(List.of(new Email(Email.EmailType.PERSONAL,"gmail@gmail.com",true)));
        requestUser.setPrimaryContactMethod(ContactMethod.EMAIL);
        requestUser.setLastUpdated("today");
        requestUser.setCreatedDate("yesterday");

        User existingUser = new User();
        existingUser.setLastName("smith");
        existingUser.setPrimaryContactMethod(ContactMethod.EMAIL);
        requestUser.setPhone(List.of(new Phone(Phone.PhoneType.HOME, "1112223333",true)));
        existingUser.setEmail(List.of(new Email(Email.EmailType.PERSONAL,"email@email.com",true)));

        request.setUser(requestUser);

        Mockito.when(userRepository.findByLastNameAndPrimaryEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(existingUser));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(requestUser));

        HostUpdateResponse response = new HostUpdateResponse();
        response.setMessage("Updated user null"); // will be null here because if we pass it in with an id, we aren't testing this approach...
        response.setUser(requestUser);

        StepVerifier.create(hostService.createUpdateHost(request)).expectNext(response).verifyComplete();
    }

    @Test
    void updateUserCreateNewUser(){
        HostUpdateRequest request = new HostUpdateRequest();
        User requestUser = new User();

        requestUser.setFirstName("sam");
        requestUser.setLastName("smith");
        requestUser.setPhone(List.of(new Phone(Phone.PhoneType.HOME, "1234567890",true)));
        requestUser.setEmail(List.of(new Email(Email.EmailType.PERSONAL,"gmail@gmail.com",true)));
        requestUser.setPrimaryContactMethod(ContactMethod.EMAIL);
        requestUser.setLastUpdated("today");
        requestUser.setCreatedDate("yesterday");

        request.setUser(requestUser);

        Mockito.when(userRepository.findByLastNameAndPrimaryEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(requestUser));

        HostUpdateResponse response = new HostUpdateResponse();
        response.setMessage("Created user null"); // will be null here because if we pass it in with an id, we aren't testing this approach...
        response.setUser(requestUser);

        StepVerifier.create(hostService.createUpdateHost(request))
//                .expectNext(response)
                .expectNextCount(1)
                .verifyComplete();
    }

    //todo: error tests
}

