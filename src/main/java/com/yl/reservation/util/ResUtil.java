package com.yl.reservation.util;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResUtil {

    public static String getCurrentDateTimeString() {
        return LocalDateTime.now().toString();
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

}
