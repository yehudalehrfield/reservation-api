package com.yl.reservation.util;

import com.yl.reservation.exception.HostException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class RequestValidatorServiceTest {

    @InjectMocks
    RequestValidatorService requestValidatorService;

    @Test
    void phoneValidation(){
        HostException ex =  Assertions.assertThrows(HostException.class,
                () -> RequestValidatorService.validatePhoneNumber(
                "123456789"));
        Assertions.assertEquals("Invalid phone number: 123456789", ex.getMessage());

    }

    @Test
    void emailValidation(){
        HostException ex = Assertions.assertThrows(HostException.class, () -> RequestValidatorService.validateEmail(
                "emailAddress"));
        Assertions.assertEquals("Invalid email address: emailAddress", ex.getMessage());

    }
}
