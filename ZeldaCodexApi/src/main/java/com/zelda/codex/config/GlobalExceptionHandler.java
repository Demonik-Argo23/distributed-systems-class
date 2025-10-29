package com.zelda.codex.config;

import com.zelda.codex.exceptions.WeaponAlreadyExistsException;
import com.zelda.codex.exceptions.WeaponNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WeaponNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleWeaponNotFound(WeaponNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        error.put("timestamp", System.currentTimeMillis());
        
        if (ex.getWeaponId() != null) {
            error.put("weaponId", ex.getWeaponId().toString());
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(WeaponAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleWeaponAlreadyExists(WeaponAlreadyExistsException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());
        error.put("timestamp", System.currentTimeMillis());
        
        if (ex.getWeaponName() != null) {
            error.put("weaponName", ex.getWeaponName());
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", "Error de validación en los datos de entrada");
        response.put("fieldErrors", fieldErrors);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", "Parámetro inválido: " + ex.getMessage());
        error.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Manejo genérico de excepciones no capturadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", "Error interno del servidor");
        error.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}