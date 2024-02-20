package com.yl.reservation.util;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.exception.ResException;
import com.yl.reservation.model.ContactMethod;
import com.yl.reservation.model.Guest;
import com.yl.reservation.model.Host;
import com.yl.reservation.model.Reservation;
import com.yl.reservation.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RequestValidatorService {

    private static final Pattern PHONE_REGEX_PATTERN = Pattern.compile(
            "^[+]?(\\d{1,2})?[\\s.-]?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$");
    private static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_REGEX_PATTERN = Pattern.compile(
            "^(20|21)\\d\\d[-](0[1-9]|1[012])[-](0?[1-9]|[12][0-9]|3[01])$");

    // ╔══════╗
    // ║ USER ║
    // ╚══════╝

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
        if (user.getPhone() != null)
            user.getPhone().forEach(phone -> validatePhoneNumber(phone.getValue()));
        if (user.getEmail() != null)
            user.getEmail().forEach(email -> validateEmail(email.getValue()));
    }

    private static void validateContactInfo(User user) {
        boolean hasEmail = user.getEmail() != null && StringUtils.hasText(user.getEmail().get(0).getValue());
        boolean hasPhone = user.getPhone() != null && StringUtils.hasText(user.getPhone().get(0).getValue());
        boolean hasPrimaryContact = false;
        if ((hasEmail && hasPhone)) {
            user.getPhone().forEach(phone -> validatePhoneNumber(phone.getValue()));
            user.getEmail().forEach(email -> validateEmail(email.getValue()));
            hasPrimaryContact = (user.getPrimaryContactMethod() == ContactMethod.PHONE
                    || user.getPrimaryContactMethod() == ContactMethod.EMAIL);
        } else {
            if (hasPhone) {
                user.getPhone().forEach(phone -> validatePhoneNumber(phone.getValue()));
                hasPrimaryContact = user.getPrimaryContactMethod() == ContactMethod.PHONE;
            } else if (hasEmail) {
                user.getEmail().forEach(email -> validateEmail(email.getValue()));
                hasPrimaryContact = user.getPrimaryContactMethod() == ContactMethod.EMAIL;
            }
        }

        boolean hasContactInfo = (hasEmail || hasPhone) && hasPrimaryContact;
        if (!hasContactInfo)
            throw new ResException("Contact information is missing", HttpStatus.BAD_REQUEST);
    }

    public static void validatePhoneNumber(String phoneNumber) {
        Matcher matchPhone = PHONE_REGEX_PATTERN.matcher(phoneNumber);
        if (!matchPhone.find())
            throw new ResException("Invalid phone number: " + phoneNumber, HttpStatus.BAD_REQUEST);
    }

    public static void validateEmail(String email) {
        Matcher matchPhone = EMAIL_REGEX_PATTERN.matcher(email);
        if (!matchPhone.find())
            throw new ResException("Invalid email address: " + email, HttpStatus.BAD_REQUEST);
    }

    public static void validateDate(String date) {
        Matcher matchPhone = DATE_REGEX_PATTERN.matcher(date);
        if (!matchPhone.find())
            throw new ResException("Invalid date: " + date + ". Correct format is yyyy-DD-mm", HttpStatus.BAD_REQUEST);
    }

    // ╔══════╗
    // ║ HOST ║
    // ╚══════╝

    public static void validateCreateHostInfo(Host host) {
        if (host.getUserId() == null)
            throw new ResGraphException("userId is missing from the request", HttpStatus.BAD_REQUEST);
        if (host.getAddress() == null)
            throw new ResGraphException("Address is missing from the request", HttpStatus.BAD_REQUEST);
    }

    public static void validateUpdateHostInfo(Host host, boolean isAddressUpdate) {
        if (host.getHostId() == null && host.getAddress() != null && isAddressUpdate)
            throw new ResGraphException(ResConstants.HOST_ID_REQUIRED_FOR_ADDRESS_UPDATE, HttpStatus.BAD_REQUEST);
    }

    // ╔═══════╗
    // ║ GUEST ║
    // ╚═══════╝

    public static void validateCreateGuestInfo(Guest requestGuest) {
        if (requestGuest.getUserId() == null)
            throw new ResGraphException("userId required for guest creation", HttpStatus.BAD_REQUEST);
        if (requestGuest.getGuestId() != null)
            throw new ResGraphException("Guest already exists", HttpStatus.BAD_REQUEST);
    }

    public static void validateUpdateGuest(Guest guest) {
        if (guest.getGuestId() == null && guest.getNickName() != null)
            throw new ResException(ResConstants.GUEST_ID_REQUIRED_FOR_NICKNAME_UPDATE, HttpStatus.BAD_REQUEST);
        if (guest.getNumAdults() < 0)
            throw new ResException("Invalid value for " + "numAdults", HttpStatus.BAD_REQUEST);
        if (guest.getNumChildren() < 0)
            throw new ResException("Invalid value for " + "numChildren", HttpStatus.BAD_REQUEST);
    }

    // ╔═════════════╗
    // ║ RESERVATION ║
    // ╚═════════════╝

    public static void validateCreateReservationInfo(Reservation requestReservation) {
        if (!StringUtils.hasText(requestReservation.getStartDate()))
            throw new ResGraphException("Reservation startDate" + ResConstants.MISSING_FIELD, HttpStatus.BAD_REQUEST);
        if (!StringUtils.hasText(requestReservation.getEndDate()))
            throw new ResGraphException("Reservation endDate" + ResConstants.MISSING_FIELD, HttpStatus.BAD_REQUEST);
        validateDate(requestReservation.getStartDate());
        validateDate(requestReservation.getEndDate());
        LocalDate startdate = LocalDate.parse(requestReservation.getStartDate());
        LocalDate endDate = LocalDate.parse(requestReservation.getEndDate());
        if (startdate.isEqual(endDate) || startdate.isAfter(endDate))
            throw new ResGraphException("Reservation endDate cannot be on or before reservation startDate.",
                    HttpStatus.BAD_REQUEST);
    }

    public static void validateUpdateReservation(Reservation requestReservation) {
        if (StringUtils.hasText(requestReservation.getStartDate()))
            validateDate(requestReservation.getStartDate());
        if (StringUtils.hasText(requestReservation.getEndDate()))
            validateDate(requestReservation.getEndDate());
    }

    public static boolean checkForDateConflicts(List<Reservation> reservationListForHost,
            Reservation requestReservation) {
        LocalDate requestReservationStartDate = LocalDate.parse(requestReservation.getStartDate());
        LocalDate requestReservationEndDate = LocalDate.parse(requestReservation.getEndDate());
        // todo: are these all the possible scenarios?
        for (Reservation existingReservation : reservationListForHost) {
            LocalDate existingReservationStartDate = LocalDate.parse(existingReservation.getStartDate());
            LocalDate existingReservationEndDate = LocalDate.parse(existingReservation.getEndDate());
            if (requestReservationStartDate.isBefore(existingReservationStartDate)) {
                if (existingReservationStartDate.isBefore(requestReservationEndDate))
                    return false;
            } else if (requestReservationStartDate.isBefore(existingReservationEndDate))
                return false;
        }
        return true;
    }
}
