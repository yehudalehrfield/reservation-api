package com.yl.reservation.util;

public class ResConstants {
    public static final String GENERAL_RESERVATION_ERROR = "Sorry, something went wrong with reservation api...";


    // USER
    public static final String USER_UPDATE = "Updated user ";
    public static final String USER_CREATE = "Created user ";
    public static final String USER_NOT_FOUND_WITH_ID = "No user with id: ";
    public static final String USER_NO_IDENTIFYING_ERROR = "Cannot create or update user without identifying details";

    // HOST
    public static final String HOST_FIND = "Retrieved host ";
    public static final String HOST_FIND_ALL_USER_INFO = "Retrieved all hosts with user info";
    public static final String HOST_FIND_ALL_NO_USER_INFO = "Retrieved all hosts without user info";
    public static final String HOST_UPDATE = "Updated host ";
    public static final String HOST_CREATE = "Created host ";
    public static final String HOST_NOT_FOUND_WITH_ID = "No host with id: ";
    public static final String HOST_NOT_FOUND_USER_ID_ADDRESS = "Host not found with userId: %s and address: %s";
    public static final String HOST_NO_IDENTIFYING_ERROR = "Cannot create or update host without identifying details";
    public static final String HOST_ID_REQUIRED_FOR_ADDRESS_UPDATE = "hostId is required to make an address update";

    // GUEST
    public static final String GUEST_FIND = "Retrieved guest ";
    public static final String GUEST_FIND_ALL_USER_INFO = "Retrieved all guests with user info";
    public static final String GUEST_FIND_ALL_NO_USER_INFO = "Retrieved all guests without user info";
    public static final String GUEST_UPDATE = "Updated guest ";
    public static final String GUEST_CREATE = "Created guest ";
    public static final String GUEST_NOT_FOUND_WITH_ID = "No guest with id: ";
    public static final String GUEST_NOT_FOUND_USER_ID_NICKNAME =  "Guest not found with userId: %s and nickname: %s";
    public static final String GUEST_NO_IDENTIFYING_ERROR = "Cannot create or update guest without identifying details";
    public static final String GUEST_ID_REQUIRED_FOR_NICKNAME_UPDATE = "guestId is required to make a nickname update";

    // UTIL
    // public static
}
