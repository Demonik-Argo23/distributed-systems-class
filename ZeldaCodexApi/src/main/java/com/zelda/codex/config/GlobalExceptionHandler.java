package com.zelda.codex.config;

import com.zelda.codex.exceptions.*;
import com.zelda.codex.dtos.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.ws.soap.client.SoapFaultClientException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WeaponNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWeaponNotFound(WeaponNotFoundException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(404);
        error.setError("Weapon Not Found");
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(WeaponAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleWeaponAlreadyExists(WeaponAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(409);
        error.setError("Weapon Already Exists");
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse error = new ErrorResponse();
        error.setStatus(400);
        error.setError("Validation Failed");
        error.setMessage("Los datos proporcionados no son válidos");
        error.setDetails(fieldErrors);
        error.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(SoapServiceException.class)
    public ResponseEntity<ErrorResponse> handleSoapService(SoapServiceException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(502);
        error.setError("Bad Gateway");
        error.setMessage("Error en el servicio SOAP: " + ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler(SoapServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleSoapServiceUnavailable(SoapServiceUnavailableException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(502);
        error.setError("Service Unavailable");
        error.setMessage("El servicio SOAP no está disponible: " + ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler(SoapValidationException.class)
    public ResponseEntity<ErrorResponse> handleSoapValidation(SoapValidationException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(424);
        error.setError("Failed Dependency");
        error.setMessage("Error de validación en el servicio SOAP: " + ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(error);
    }

    @ExceptionHandler(SoapFaultClientException.class)
    public ResponseEntity<ErrorResponse> handleSoapFault(SoapFaultClientException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(502);
        error.setError("SOAP Fault");
        error.setMessage("Error SOAP: " + ex.getFaultStringOrReason());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(400);
        error.setError("Bad Request");
        error.setMessage("Parámetro inválido: " + ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Manejo genérico de excepciones no capturadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(500);
        error.setError("Internal Server Error");
        error.setMessage("Error interno del servidor: " + ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}