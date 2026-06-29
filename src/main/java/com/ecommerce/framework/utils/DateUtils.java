package com.ecommerce.framework.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DateUtils — Timestamp formatting utilities for reports and file naming.
 */
public final class DateUtils {

    private static final DateTimeFormatter REPORT_FORMATTER   = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter LOG_FORMATTER      = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private DateUtils() {}

    /** Returns the current timestamp formatted for Extent Report headers. */
    public static String getReportTimestamp() {
        return LocalDateTime.now().format(REPORT_FORMATTER);
    }

    /** Returns the current timestamp safe for file system usage (no colons). */
    public static String getFileTimestamp() {
        return LocalDateTime.now().format(FILENAME_FORMATTER);
    }

    /** Returns the current timestamp formatted for log output. */
    public static String getLogTimestamp() {
        return LocalDateTime.now().format(LOG_FORMATTER);
    }

    /** Returns a custom-formatted timestamp. */
    public static String getTimestamp(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }
}
