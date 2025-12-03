package com.zelda.codex.exceptions;

public class SoapServiceUnavailableException extends SoapServiceException {
    public SoapServiceUnavailableException(String message) {
        super(message);
    }

    public SoapServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}