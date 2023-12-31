package com.yl.reservation.util;

public class ResConstants {
    public static final String GENERAL_RESERVATION_ERROR = "Sorry, something went wrong with reservation api...";

//    ERROR MESSAGES
    public static final String NO_HOST_OR_USER_ERROR = "No host or user included in the request...";
    public static final String HOST_ID_REQUIRED_FOR_ADDRESS_UPDATE_ERROR = "HostId is required to make an address update";
    public static final String HOST_NO_IDENTIFYING_ERROR = "Cannot create or update host without identifying details";
    public static final String USER_NO_IDENTIFYING_ERROR = "Cannot create or update user without identifying details";
    public static final String USER_UPDATE_ERROR = "Cannot apply update to user with isUserUpdate not set to true";

//    HOST
    public static final String HOST_FIND = "Retrieved host ";
    public static final String HOST_FIND_ALL_USER_INFO = "Retrieved all hosts with user info";
    public static final String HOST_FIND_ALL_NO_USER_INFO = "Retrieved all hosts without user info";
    public static final String HOST_UPDATE = "Updated host ";
    public static final String HOST_CREATE = "Created host ";
    public static final String HOST_NOT_FOUND_WITH_ID = "No host with id: ";

//    USER
    public static final String USER_UPDATE = "Updated user ";
    public static final String USER_CREATE = "Created user ";
    public static final String USER_NOT_FOUND_WITH_ID = "No user with id: ";
}
