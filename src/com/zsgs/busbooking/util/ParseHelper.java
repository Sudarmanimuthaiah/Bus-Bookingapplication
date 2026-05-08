package com.zsgs.busbooking.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ParseHelper {

    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm";
    private static final String EMPTY_PLACEHOLDER = "-";

    private ParseHelper() {
    }

    public static Integer parseNonNegativeInt(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return null;
        long result = 0L;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (c < '0' || c > '9') return null;
            result = result * 10 + (c - '0');
            if (result > Integer.MAX_VALUE) return null;
        }
        return (int) result;
    }

    public static Double parseNonNegativeDouble(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return null;
        try {
            double value = Double.parseDouble(trimmed);
            if (value < 0 || Double.isNaN(value) || Double.isInfinite(value)) return null;
            return value;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isYes(String input) {
        if (input == null) return false;
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return false;
        char first = trimmed.charAt(0);
        return first == 'Y' || first == 'y';
    }

    public static Long parseDate(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.ROOT);
        format.setLenient(false);
        format.setTimeZone(TimeZone.getDefault());
        try {
            return format.parse(input.trim()).getTime();
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDate(Long millis) {
        if (millis == null) return EMPTY_PLACEHOLDER;
        return new SimpleDateFormat(DATE_PATTERN, Locale.ROOT).format(new Date(millis));
    }

    public static String formatDateTime(Long millis) {
        if (millis == null) return EMPTY_PLACEHOLDER;
        return new SimpleDateFormat(DATE_TIME_PATTERN, Locale.ROOT).format(new Date(millis));
    }

    public static boolean isTodayOrFuture(long millis) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return millis >= today.getTimeInMillis();
    }
}
