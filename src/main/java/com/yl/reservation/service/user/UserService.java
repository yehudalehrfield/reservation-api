package com.yl.reservation.service.user;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.*;
import com.yl.reservation.repository.HostRepository;
import com.yl.reservation.repository.UserRepository;
import com.yl.reservation.util.CreateUpdateMapper;
import com.yl.reservation.util.RequestValidatorService;
import com.yl.reservation.util.ResConstants;
import com.yl.reservation.util.ResUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Mono<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .switchIfEmpty(Mono.error(new ResGraphException("No users found", HttpStatus.NOT_FOUND)))
                .collectList()
                .map(userList -> new UserResponse("Retrieved all users", userList));
    }

    public Mono<UserResponse> getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .map(user -> new UserResponse(ResConstants.USER_FIND + user.getUserId(), List.of(user)))
                .switchIfEmpty(Mono.error(new ResGraphException("No user " + userId, HttpStatus.NOT_FOUND)));
    }

    public Mono<UserResponse> createNewUser(User requestUser, String createDateTime) {
        RequestValidatorService.validateCreateUserInfo(requestUser);
        return validateNotExistingUser(requestUser)
                .flatMap(res -> {
                    if (res.equals(Boolean.TRUE))
                        return Mono.error(
                                new ResGraphException(ResConstants.USER_ALREADY_EXISTS_ERROR, HttpStatus.BAD_REQUEST));
                    else {
                        requestUser.setUserId(ResUtil.generateId());
                        requestUser.setCreatedDate(createDateTime);
                        requestUser.setLastUpdated(createDateTime);
                        return userRepository.save(requestUser)
                                .map(createdUser -> new UserResponse(ResConstants.USER_CREATE + createdUser.getUserId(),
                                        List.of(createdUser)));
                    }
                });

    }

    private Mono<Boolean> validateNotExistingUser(User user) {
        if (user.getPrimaryContactMethod().equals(ContactMethod.PHONE)) {
            return userRepository
                    .findByLastNameAndPrimaryPhone(user.getLastName(),
                            user.getPhone().stream().filter(Phone::isPrimary).toList().get(0).getValue())
                    .map(res -> true)
                    .switchIfEmpty(Mono.just(false));
        } else {
            return userRepository
                    .findByLastNameAndPrimaryEmail(user.getLastName(),
                            user.getEmail().stream().filter(Email::isPrimary).toList().get(0).getValue())
                    .map(res -> true)
                    .switchIfEmpty(Mono.just(false));
        }
    }

    public Mono<UserResponse> updateUser(User requestUser, String updateDateTime) {
        RequestValidatorService.validateUpdateUserInfo(requestUser);
        if (requestUser.getUserId() != null) {
            String userIdToSearch = requestUser.getUserId();
            return userRepository.findByUserId(userIdToSearch)
                    .flatMap(existingUser -> {
                        User updatedUser = CreateUpdateMapper.updateUser(existingUser, requestUser, updateDateTime);
                        return userRepository.save(updatedUser)
                                .map(user -> new UserResponse(ResConstants.USER_UPDATE + user.getUserId(),
                                        List.of(user)));
                    })
                    .switchIfEmpty(Mono.error(new ResGraphException(
                            ResConstants.USER_NOT_FOUND_WITH_ID + requestUser.getUserId(), HttpStatus.NOT_FOUND)));
        } else if (requestUser.getLastName() != null && requestUser.getPrimaryContactMethod() != null) {
            return fetchByPrimaryContactInfo(requestUser)
                    .flatMap(existingUser -> {
                        User updatedUser = CreateUpdateMapper.updateUser(existingUser, requestUser, updateDateTime);
                        return userRepository.save(updatedUser)
                                .map(user -> new UserResponse(ResConstants.USER_UPDATE + user.getUserId(),
                                        List.of(user)));
                    })
                    .switchIfEmpty(Mono.error(new ResGraphException(
                            ResConstants.USER_NOT_FOUND_WITH_ID + requestUser.getUserId(), HttpStatus.NOT_FOUND)));
        } else {
            throw new ResGraphException(ResConstants.USER_NO_IDENTIFYING_ERROR, HttpStatus.BAD_REQUEST);
        }
    }

    private Mono<User> fetchByPrimaryContactInfo(User user) {
        if (user.getPrimaryContactMethod().equals(ContactMethod.PHONE)) {
            return userRepository.findByLastNameAndPrimaryPhone(
                    user.getLastName(),
                    user.getPhone().stream().filter(Phone::isPrimary).toList().get(0).getValue());
        } else {
            return userRepository.findByLastNameAndPrimaryEmail(
                    user.getLastName(),
                    user.getEmail().stream().filter(Email::isPrimary).toList().get(0).getValue());
        }
    }

}
