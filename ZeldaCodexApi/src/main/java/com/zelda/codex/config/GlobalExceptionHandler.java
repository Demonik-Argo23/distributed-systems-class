package com.zelda.codex.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zelda.codex.dtos.ErrorResponse;
import com.zelda.codex.exceptions.SoapServiceException;
import com.zelda.codex.exceptions.SoapServiceUnavailableException;
import com.zelda.codex.exceptions.SoapValidationException;
import com.zelda.codex.exceptions.WeaponAlreadyExistsException;
import com.zelda.codex.exceptions.WeaponNotFoundException;

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

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(400);
        error.setError("Bad Request");
        error.setMessage("Formato de parámetro inválido: " + ex.getName() + " debe ser de tipo " + ex.getRequiredType().getSimpleName());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            org.springframework.web.bind.MissingServletRequestParameterException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(400);
        error.setError("Bad Request");
        error.setMessage("Parámetro requerido faltante: " + ex.getParameterName());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(403);
        error.setError("Access Denied");
        error.setMessage("No tienes permisos para acceder a este recurso. Verifica que tu token tenga los scopes necesarios.");
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Manejo genérico de excepciones no capturadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(500);
        error.setError("Internal Server Error");
        error.setMessage("Error interno del servidor. Contacta al administrador si el problema persiste.");
        error.setTimestamp(LocalDateTime.now());
        
        // Log del error para debugging
        System.err.println("Excepción no capturada: " + ex.getClass().getName() + " - " + ex.getMessage());
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}