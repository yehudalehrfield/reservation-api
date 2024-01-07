package com.yl.reservation.service;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.model.ContactMethod;
import com.yl.reservation.model.Email;
import com.yl.reservation.model.Phone;
import com.yl.reservation.model.User;
import com.yl.reservation.repository.UserRepository;
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

    @Autowired
    UserRepository userRepository;

    public Mono<UserResponse> getAllUsers(){
        return userRepository.findAll()
                .collectList()
                .map(userList -> new UserResponse("Retrieved all users", userList))
                .switchIfEmpty(Mono.error(new ResGraphException("No users found", HttpStatus.NOT_FOUND)));
    }

    public Mono<UserResponse> getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .map(user -> new UserResponse("Retrieved user " + user.getUserId(), List.of(user)))
                .switchIfEmpty(Mono.error(new ResGraphException("No user " + userId,HttpStatus.NOT_FOUND)));
    }

    public Mono<UserResponse> createNewUser(User requestUser, String createDateTime){
        RequestValidatorService.validateCreateUserInfo(requestUser);
        User user = requestUser;
        user.setUserId(ResUtil.generateId());
        user.setCreatedDate(createDateTime);
        user.setLastUpdated(createDateTime);
        return userRepository.save(user).map(createdUser -> new UserResponse("Created user " + createdUser.getUserId(), List.of(createdUser)));
    }

    public Mono<UserResponse> updateUser(User requestUser, String updateDateTime) {
        if (requestUser.getUserId() != null) {
            String userIdToSearch = requestUser.getUserId();
            return userRepository.findByUserId(userIdToSearch)
                    .flatMap(existingUser -> {
                        User updatedUser = ResUtil.updateUser(existingUser, requestUser, updateDateTime);
                        return userRepository.save(updatedUser).map(user -> new UserResponse("Updated user " + user.getUserId(), List.of(user)));
                    })
                    .switchIfEmpty(Mono.error(new ResGraphException(ResConstants.USER_NOT_FOUND_WITH_ID + requestUser.getUserId(), HttpStatus.NOT_FOUND)));
        } else if (requestUser.getLastName() != null && requestUser.getPrimaryContactMethod() != null) {
            return fetchByPrimaryContactInfo(requestUser)
                    .flatMap(existingUser -> {
                        User updatedUser = ResUtil.updateUser(existingUser, requestUser, updateDateTime);
                        return userRepository.save(updatedUser).map(user -> new UserResponse("Updated user " + user.getUserId(), List.of(user)));
                    })
                    .switchIfEmpty(Mono.error(new ResGraphException(ResConstants.USER_NOT_FOUND_WITH_ID + requestUser.getUserId(), HttpStatus.NOT_FOUND)));
        } else {
            throw new ResGraphException(ResConstants.USER_NO_IDENTIFYING_ERROR, HttpStatus.BAD_REQUEST);
        }

    }

    private Mono<User> fetchByPrimaryContactInfo(User user) {
        if (user.getPrimaryContactMethod().equals(ContactMethod.PHONE)) {
            return userRepository.findByLastNameAndPrimaryPhone(
                    user.getLastName(),
                    user.getPhone().stream().filter(Phone::isPrimary).toList().get(0).getValue()
            );
        } else {
            return userRepository.findByLastNameAndPrimaryEmail(
                    user.getLastName(),
                    user.getEmail().stream().filter(Email::isPrimary).toList().get(0).getValue()
            );
        }
    }
}
