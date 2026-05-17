package com.proyecto.carrito.exeption;

import java.time.LocalDateTime;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontradoException(NoEncontradoException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", ex.getMessage());
        respuesta.put("error", "Recurso no encontrado");
        respuesta.put("timestamp", LocalDateTime.now().toString());
        respuesta.put("status", HttpStatus.NOT_FOUND.value()); // Esto pondrá el número 404 en el JSON

        // El segundo parámetro mapea formalmente el código de estado HTTP a 404 Not
        // Found en Postman
        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoDisponibleException.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailable(NoDisponibleException e) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", e.getMessage());
        respuesta.put("error", "Servicio no disponible");
        respuesta.put("timestamp", LocalDateTime.now().toString());
        respuesta.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());

        return new ResponseEntity<>(respuesta, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, Object> errores = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errores);
    }

    // 2. Error de Formato JSON (Ej: enviar letras en campos de precio o cantidad)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleReadableException(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Formato de JSON inválido");
        response.put("mensaje",
                "Revisa que los tipos de datos sean correctos (ID, Cantidad y Precio deben ser números)");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 3. Manejo de errores generales (Cualquier otro fallo inesperado)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error interno del servidor");
        response.put("mensaje", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
