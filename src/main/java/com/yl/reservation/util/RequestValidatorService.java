package com.yl.reservation.util;

import com.yl.reservation.exception.GraphQLException;
import com.yl.reservation.exception.HostException;
import com.yl.reservation.model.ContactMethod;
import com.yl.reservation.model.Host;
import com.yl.reservation.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestValidatorService {

    private static final Pattern phoneRegexPattern = Pattern.compile(
            "^[+]?(\\d{1,2})?[\\s.-]?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$");
    private static final Pattern emailRegexPattern = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static void validateUserInfo(User user) {
        if (!StringUtils.hasText(user.getLastName()))
            throw new HostException(HttpStatus.BAD_REQUEST, "Last name is blank or missing from the request");
        if (!StringUtils.hasText(user.getFirstName()))
            throw new HostException(HttpStatus.BAD_REQUEST,"First name is blank or missing from the request");
        validateContactInfo(user);
    }

    public static void validateHostInfo(Host host) {
        //todo: there may be a scenario where we will not need userId (if we are creating a user...)
        if (host.getUserId() == null)
            throw new GraphQLException("userId is missing from the request", HttpStatus.BAD_REQUEST);
        if (host.getAddress() == null)
            throw new GraphQLException("Address is missing from the request", HttpStatus.BAD_REQUEST);
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
            throw new HostException(HttpStatus.BAD_REQUEST,"Contact information is missing");
    }

    public static void validatePhoneNumber(String phoneNumber){
        Matcher matchPhone = phoneRegexPattern.matcher(phoneNumber);
        if (!matchPhone.find()) {
            throw new HostException(HttpStatus.BAD_REQUEST, "Invalid phone number: " + phoneNumber);
        }
    }

    public static void validateEmail(String email){
        Matcher matchPhone = emailRegexPattern.matcher(email);
        if (!matchPhone.find()) {
            throw new HostException(HttpStatus.BAD_REQUEST, "Invalid email address: " + email);
        }
    }
}
