package com.yl.reservation.util;

import com.yl.reservation.exception.ResException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RequestValidatorServiceTest {

    @InjectMocks
    RequestValidatorService requestValidatorService;

    @Test
    void phoneValidation(){
        ResException ex =  Assertions.assertThrows(ResException.class,
                () -> RequestValidatorService.validatePhoneNumber(
                "123456789"));
        Assertions.assertEquals("Invalid phone number: 123456789", ex.getMessage());

    }

    @Test
    void emailValidation(){
        ResException ex = Assertions.assertThrows(ResException.class, () -> RequestValidatorService.validateEmail(
                "emailAddress"));
        Assertions.assertEquals("Invalid email address: emailAddress", ex.getMessage());

    }
}
