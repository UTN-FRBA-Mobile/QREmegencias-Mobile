package com.qre.utils;

public final class Constants {

    private Constants() {
        throw new IllegalAccessError("This is an utility class");
    }

    public static final String API_AUTHORIZATION = "oauth2";
    public static final String INTENT_EXTRA_USER = "user";
    public static final String INTENT_EXTRA_SCAN_RESULT = "result";
    public static final String INTENT_EXTRA_UUID = "uuid";
    public static final String INTENT_EXTRA_TEMP_CODE = "tempCode";
}