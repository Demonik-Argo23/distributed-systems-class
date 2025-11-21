package com.zelda.codex.exceptions;

public class SoapValidationException extends SoapServiceException {
    public SoapValidationException(String message) {
        super(message);
    }

    public SoapValidationException(String message, String soapFaultCode, String soapFaultString) {
        super(message, soapFaultCode, soapFaultString);
    }
}