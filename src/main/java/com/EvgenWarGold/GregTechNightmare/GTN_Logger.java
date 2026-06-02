package com.EvgenWarGold.GregTechNightmare;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;

public class GTN_Logger {

    // region Constant
    private static final String LOGGER_PREFIX = "GTN:";
    private static final String SERVER_SUFFIX = "S";
    private static final String CLIENT_SUFFIX = "C";
    private static final String DEFAULT_EXCEPTION_MESSAGE = "Exception: ";
    // endregion

    // region Core
    private GTN_Logger() {}

    private static Logger getLogger() {
        final String loggerName = LOGGER_PREFIX + (GTN_Utils.isServer() ? SERVER_SUFFIX : CLIENT_SUFFIX);
        return LogManager.getLogger(loggerName);
    }

    public static void printStackTrace(Level level) {
        log(level, "Stack trace:");
        for (StackTraceElement traceElement : Thread.currentThread()
            .getStackTrace()) log(level, "\t " + traceElement);
    }

    public static boolean isLogEnabled() {
        return true;
    }

    public static boolean isDebugLogEnabled() {
        return true;
    }
    // endregion

    // region Log
    public static void logSimple(@Nonnull final Level level, @Nonnull final String message) {
        if (isLogEnabled()) {
            final Logger logger = getLogger();

            logger.log(level, message);
        }
    }

    public static void log(@Nonnull final Level level, @Nonnull final String message, final Object... params) {
        if (isLogEnabled()) {
            final String formattedMessage = String.format(message, params);
            final Logger logger = getLogger();

            logger.log(level, formattedMessage);
        }
    }

    public static void log(@Nonnull final Level level, @Nonnull final Throwable exception, @Nonnull String message,
        final Object... params) {
        if (isLogEnabled()) {
            final String formattedMessage = String.format(message, params);
            final Logger logger = getLogger();

            logger.log(level, formattedMessage, exception);
        }
    }
    // endregion

    // region Info
    public static void info(@Nonnull final String format, final Object... params) {
        log(Level.INFO, format, params);
    }

    public static void info(@Nonnull final Throwable exception) {
        log(Level.INFO, exception, DEFAULT_EXCEPTION_MESSAGE);
    }

    public static void info(@Nonnull final Throwable exception, @Nonnull final String message) {
        log(Level.INFO, exception, message);
    }
    // endregion

    // region Warn
    public static void warn(@Nonnull final String format, final Object... params) {
        log(Level.WARN, format, params);
    }

    public static void warn(@Nonnull final Throwable exception) {
        log(Level.WARN, exception, DEFAULT_EXCEPTION_MESSAGE);
    }

    public static void warn(@Nonnull final Throwable exception, @Nonnull final String message) {
        log(Level.WARN, exception, message);
    }
    // endregion

    // region Error
    public static void error(@Nonnull final String format, final Object... params) {
        log(Level.ERROR, format, params);
    }

    public static void error(@Nonnull final Throwable exception) {
        log(Level.ERROR, exception, DEFAULT_EXCEPTION_MESSAGE);
    }

    public static void error(@Nonnull final Throwable exception, @Nonnull final String message) {
        log(Level.ERROR, exception, message);
    }
    // endregion

    // region Debug
    public static void debug(@Nonnull final String format, final Object... data) {
        if (isDebugLogEnabled()) {
            log(Level.DEBUG, format, data);
        }
    }

    public static void debug(@Nonnull final Throwable exception) {
        if (isDebugLogEnabled()) {
            log(Level.DEBUG, exception, DEFAULT_EXCEPTION_MESSAGE);
        }
    }

    public static void debug(@Nonnull final Throwable exception, @Nonnull final String message) {
        if (isDebugLogEnabled()) {
            log(Level.DEBUG, exception, message);
        }
    }
    // endregion
}
