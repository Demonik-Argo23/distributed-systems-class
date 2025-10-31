package com.zelda.codex.exceptions;

public class SoapServiceException extends RuntimeException {
    private final String soapFaultCode;
    private final String soapFaultString;

    public SoapServiceException(String message) {
        super(message);
        this.soapFaultCode = null;
        this.soapFaultString = null;
    }

    public SoapServiceException(String message, Throwable cause) {
        super(message, cause);
        this.soapFaultCode = null;
        this.soapFaultString = null;
    }

    public SoapServiceException(String message, String soapFaultCode, String soapFaultString) {
        super(message);
        this.soapFaultCode = soapFaultCode;
        this.soapFaultString = soapFaultString;
    }

    public SoapServiceException(String message, String soapFaultCode, String soapFaultString, Throwable cause) {
        super(message, cause);
        this.soapFaultCode = soapFaultCode;
        this.soapFaultString = soapFaultString;
    }

    public String getSoapFaultCode() {
        return soapFaultCode;
    }

    public String getSoapFaultString() {
        return soapFaultString;
    }
}