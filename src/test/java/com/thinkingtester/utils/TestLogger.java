package com.thinkingtester.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.qameta.allure.Allure;

/**
 * Centralized logging utility for test automation framework.
 * Provides structured logging with Allure integration.
 */
public class TestLogger {

    private final Logger logger;
    private final String testContext; // "API", "UI", or null for general

    private TestLogger(Class<?> clazz, String testContext) {
        this.logger = LogManager.getLogger(clazz);
        this.testContext = testContext;
    }

    /**
     * Get logger for a test class
     */
    public static TestLogger getLogger(Class<?> clazz) {
        return new TestLogger(clazz, null);
    }

    /**
     * Get logger for API tests with "API:" prefix
     */
    public static TestLogger getApiLogger(Class<?> clazz) {
        return new TestLogger(clazz, "API");
    }

    /**
     * Get logger for UI tests with "UI:" prefix
     */
    public static TestLogger getUiLogger(Class<?> clazz) {
        return new TestLogger(clazz, "UI");
    }

    /**
     * Get logger for Hybrid tests (supports both API and UI contexts)
     */
    public static TestLogger getHybridLogger(Class<?> clazz) {
        return new TestLogger(clazz, null);
    }

    // ===== SETUP & CLEANUP LOGGING =====

    public void setup(String message) {
        String formattedMsg = formatMessage("SETUP", message);
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void setup(String message, Object... params) {
        String formattedMsg = formatMessage("SETUP", String.format(message, params));
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void cleanup(String message) {
        String formattedMsg = formatMessage("CLEANUP", message);
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void cleanup(String message, Object... params) {
        String formattedMsg = formatMessage("CLEANUP", String.format(message, params));
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    // ===== INFO LEVEL LOGGING =====

    public void info(String message) {
        String formattedMsg = formatMessage(testContext, message);
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void info(String message, Object... params) {
        String formattedMsg = formatMessage(testContext, String.format(message, params));
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    // ===== API & UI SPECIFIC LOGGING =====

    public void api(String message) {
        String formattedMsg = formatMessage("API", message);
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void api(String message, Object... params) {
        String formattedMsg = formatMessage("API", String.format(message, params));
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void ui(String message) {
        String formattedMsg = formatMessage("UI", message);
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void ui(String message, Object... params) {
        String formattedMsg = formatMessage("UI", String.format(message, params));
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    // ===== DEBUG LEVEL LOGGING (for response bodies) =====

    public void debug(String message) {
        logger.debug(formatMessage(testContext, message));
    }

    public void debug(String message, Object... params) {
        logger.debug(formatMessage(testContext, String.format(message, params)));
    }

    public void debugResponse(String operation, String responseBody) {
        String formattedMsg = formatMessage(testContext,
            String.format("%s Response Body:%n%s", operation, responseBody));
        logger.debug(formattedMsg);

        // Add to Allure as attachment for DEBUG mode
        if (logger.isDebugEnabled()) {
            Allure.addAttachment(operation + " Response", "application/json", responseBody, ".json");
        }
    }

    // ===== VALIDATION & ERROR LOGGING =====

    public void validation(String message) {
        String formattedMsg = formatMessage(testContext, message);
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void validation(String message, Object... params) {
        String formattedMsg = formatMessage(testContext, String.format(message, params));
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void success(String message) {
        String formattedMsg = formatMessage(testContext, message);
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void success(String message, Object... params) {
        String formattedMsg = formatMessage(testContext, String.format(message, params));
        logger.info(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void failure(String message) {
        String formattedMsg = formatMessage(testContext, message);
        logger.warn(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void failure(String message, Object... params) {
        String formattedMsg = formatMessage(testContext, String.format(message, params));
        logger.warn(formattedMsg);
        addToAllure(formattedMsg);
    }

    public void error(String message, Throwable throwable) {
        String formattedMsg = formatMessage(testContext, message);
        logger.error(formattedMsg, throwable);
        addToAllure(formattedMsg + " - Error: " + throwable.getMessage());
    }

    // ===== TRACE LEVEL LOGGING =====

    public void trace(String message) {
        logger.trace(formatMessage(testContext, message));
    }

    public void trace(String message, Object... params) {
        logger.trace(formatMessage(testContext, String.format(message, params)));
    }

    // ===== HELPER METHODS =====

    private String formatMessage(String context, String message) {
        if (context != null && !context.isEmpty()) {
            return context + ": " + message;
        }
        return message;
    }

    private void addToAllure(String message) {
        try {
            Allure.step(message);
        } catch (Exception e) {
            // Silently ignore if Allure is not available
            logger.trace("Could not add to Allure: " + e.getMessage());
        }
    }

    // ===== LOG LEVEL CHECKS =====

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }
}
