package com.yl.reservation.util;

public class ResConstants {

    private ResConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String GENERAL_RESERVATION_ERROR = "Sorry, something went wrong with reservation api...";

    // USER
    public static final String USER_FIND = "Retrieved user ";
    public static final String USER_UPDATE = "Updated user ";
    public static final String USER_CREATE = "Created user ";
    public static final String USER_NOT_FOUND_WITH_ID = "No user with id ";
    public static final String USER_NO_IDENTIFYING_ERROR = "Cannot create or update user without identifying details";
    public static final String USER_ALREADY_EXISTS_ERROR = "User already exists";

    // HOST
    public static final String HOST_FIND = "Retrieved host ";
    public static final String HOST_FIND_ALL_USER_INFO = "Retrieved all hosts with user info";
    public static final String HOST_FIND_ALL_NO_USER_INFO = "Retrieved all hosts without user info";
    public static final String HOST_UPDATE = "Updated host ";
    public static final String HOST_CREATE = "Created host ";
    public static final String HOST_NOT_FOUND_WITH_ID = "No host with id ";
    public static final String HOST_NOT_FOUND_USER_ID_ADDRESS = "Host not found with userId: %s and address: %s";
    public static final String HOST_NO_IDENTIFYING_ERROR = "Cannot create or update host without identifying details";
    public static final String HOST_ID_REQUIRED_FOR_ADDRESS_UPDATE = "hostId is required to make an address update";
    public static final String HOST_ALREADY_EXISTS_ERROR = "Host already exists";

    // GUEST
    public static final String GUEST_FIND = "Retrieved guest ";
    public static final String GUEST_FIND_ALL_USER_INFO = "Retrieved all guests with user info";
    public static final String GUEST_FIND_ALL_NO_USER_INFO = "Retrieved all guests without user info";
    public static final String GUEST_UPDATE = "Updated guest ";
    public static final String GUEST_CREATE = "Created guest ";
    public static final String GUEST_NOT_FOUND_WITH_ID = "No guest with id: ";
    public static final String GUEST_NOT_FOUND_USER_ID_NICKNAME = "Guest not found with userId: %s and nickname: %s";
    public static final String GUEST_NO_IDENTIFYING_ERROR = "Cannot create or update guest without identifying details";
    public static final String GUEST_ID_REQUIRED_FOR_NICKNAME_UPDATE = "guestId is required to make a nickname update";
    public static final String GUEST_ALREADY_EXISTS_ERROR = "Guest already exists";

    // RESERVATION
    public static final String RESERVATION_FIND = "Retrieved reservation ";
    public static final String RESERVATION_FIND_ALL = "Retrieved all reservations";
    public static final String RESERVATION_UPDATE = "Updated reservation ";
    public static final String RESERVATION_CREATE = "Created reservation ";
    public static final String RESERVATION_NOT_FOUND_WITH_ID = "No reservation with id ";
    public static final String RESERVATION_ALREADY_EXISTS_ERROR = "Reservation already exists";

    // GENERAL
    public static final String MISSING_FIELD = " is missing or blank";
    public static final String NO_UPDATES_APPLICABLE = "No updates to apply";
    public static final String INVALID_VALUE = "Invalid value for ";
}
