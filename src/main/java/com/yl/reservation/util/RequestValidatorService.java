package com.yl.reservation.util;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.exception.ResException;
import com.yl.reservation.model.ContactMethod;
import com.yl.reservation.model.Guest;
import com.yl.reservation.model.Host;
import com.yl.reservation.model.User;
import com.yl.reservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RequestValidatorService {

    @Autowired
    private static UserRepository userRepository;

    private static final Pattern phoneRegexPattern = Pattern.compile(
            "^[+]?(\\d{1,2})?[\\s.-]?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$");
    private static final Pattern emailRegexPattern = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static void validateCreateUserInfo(User user) {
        if (StringUtils.hasText(user.getUserId()))
            throw new ResException("Cannot create new user with a given userId", HttpStatus.BAD_REQUEST);
        if (!StringUtils.hasText(user.getLastName()))
            throw new ResException("Last name is blank or missing from the request", HttpStatus.BAD_REQUEST);
        if (!StringUtils.hasText(user.getFirstName()))
            throw new ResException("First name is blank or missing from the request", HttpStatus.BAD_REQUEST);
        validateContactInfo(user);
    }

    public static void validateUpdateUserInfo(User user) {
        if (user.getPhone() != null) {
            user.getPhone().forEach(phone -> validatePhoneNumber(phone.getValue()));
        }
        if (user.getEmail() != null) {
            user.getEmail().forEach(email -> validateEmail(email.getValue()));
        }
        //todo: check this
        if (user.getPrimaryContactMethod() != null && user.getUserId() == null) {
            throw new ResException("Cannot update primary contact method without userId", HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateCreateHostInfo(Host host) {
        //todo: there may be a scenario where we will not need userId (if we are creating a user...)
        if (host.getUserId() == null)
            throw new ResGraphException("userId is missing from the request", HttpStatus.BAD_REQUEST);
        if (host.getAddress() == null)
            throw new ResGraphException("Address is missing from the request", HttpStatus.BAD_REQUEST);
    }

    public static void validateContactInfo(User user){
        boolean hasEmail = user.getEmail() != null && StringUtils.hasText(user.getEmail().get(0).getValue());
        boolean hasPhone = user.getPhone() != null && StringUtils.hasText(user.getPhone().get(0).getValue());
        boolean hasPrimaryContact = false;
        if ((hasEmail && hasPhone)) {
            user.getPhone().forEach(phone -> validatePhoneNumber(phone.getValue()));
            user.getEmail().forEach(email -> validateEmail(email.getValue()));
            hasPrimaryContact =
                    (user.getPrimaryContactMethod() == ContactMethod.PHONE || user.getPrimaryContactMethod() == ContactMethod.EMAIL);
        } else {
            if (hasPhone) {
                user.getPhone().forEach(phone -> validatePhoneNumber(phone.getValue()));
                hasPrimaryContact = user.getPrimaryContactMethod() == ContactMethod.PHONE;
            }
            else if (hasEmail) {
                user.getEmail().forEach(email -> validateEmail(email.getValue()));
                hasPrimaryContact = user.getPrimaryContactMethod() == ContactMethod.EMAIL;
            }
        }

        boolean hasContactInfo = (hasEmail || hasPhone) && hasPrimaryContact;
        if (!hasContactInfo)
            throw new ResException("Contact information is missing",HttpStatus.BAD_REQUEST);
    }

    public static void validatePhoneNumber(String phoneNumber){
        Matcher matchPhone = phoneRegexPattern.matcher(phoneNumber);
        if (!matchPhone.find()) {
            throw new ResException("Invalid phone number: " + phoneNumber,HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateEmail(String email){
        Matcher matchPhone = emailRegexPattern.matcher(email);
        if (!matchPhone.find()) {
            throw new ResException("Invalid email address: " + email,HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateCreateGuestInfo(Guest requestGuest) {
        if (requestGuest.getUserId() == null){
            throw new ResGraphException("userId required for guest creation", HttpStatus.BAD_REQUEST);
        }
        if (requestGuest.getGuestId() != null){
            throw new ResGraphException("Guest already exists", HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateUpdateGuest(Guest guestToUpdate){

    }

}
