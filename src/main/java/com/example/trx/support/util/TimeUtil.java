package com.example.trx.support.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER= DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter DATE_FORMATTER= DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER= DateTimeFormatter.ofPattern("HHmmss");

    private static final ZoneId KST=ZoneId.of("Asia/Seoul");

    public static String getCurrentDateTimeString() {
        return LocalDateTime.now(KST).format(DATE_TIME_FORMATTER);
    }

    public static long getCurrentDateTimeLong() {
        return Long.parseLong(LocalDateTime.now(KST).format(DATE_TIME_FORMATTER));
    }

    public static long getCurrentTimeLong() {
        return Long.parseLong(LocalDateTime.now(KST).format(TIME_FORMATTER));
    }

    public static String getCurrentTimeString() {
        return LocalDateTime.now(KST).format(TIME_FORMATTER);
    }

        public static String getCurrentDateString() {
        return LocalDateTime.now(KST).format(DATE_FORMATTER);
    }
}
