package com.yl.reservation.host.util;

import java.time.LocalDateTime;

public class HostUtil {

    public static String getCurrentDateTimeString(){
        return LocalDateTime.now().toString();
    }
}
