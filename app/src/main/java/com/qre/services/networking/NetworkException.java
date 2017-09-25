package com.qre.services.networking;

public class NetworkException extends Exception {

    private int code;

    public NetworkException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}