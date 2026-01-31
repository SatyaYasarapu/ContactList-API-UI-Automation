package com.thinkingtester.utils;

import java.util.UUID;

public final class TestDataUtil {

    private TestDataUtil() {
    }

    public static String generateUniqueEmail() {
        return "testuser"
                // + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "_"
                + UUID.randomUUID().toString().substring(0, 6)
                + "@test.com";
    }

    public static String generateInvalidEmail() {
        return "Testuser_"
                + UUID.randomUUID().toString().substring(0, 6)
                + "test.com";
    }

    public static String generatePhoneNumber() {
        return "9" + String.valueOf(System.nanoTime()).substring(5, 14);
    }

    public static String generateInvalidPhoneNumber() {
        return "hfsdjhflsdsdf";
    }
}
