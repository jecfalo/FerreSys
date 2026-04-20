package com.jecfalo.FerreSys.core.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la API REST.
 * Intercepta las excepciones lanzadas por cualquier controlador y las convierte
 * en respuestas JSON estandarizadas usando {@link ErrorResponse}.
 *
 * <p>Jerarquía de captura (de más específica a más general):</p>
 * <ol>
 *   <li>{@link MethodArgumentNotValidException} → Errores de validación de DTOs (400)</li>
 *   <li>{@link DataIntegrityViolationException} → Violaciones de restricciones en BD (409)</li>
 *   <li>{@link IllegalArgumentException} → Argumentos inválidos en la lógica de negocio (400)</li>
 *   <li>{@link RuntimeException} → Errores de procesamiento lanzados manualmente en servicios (400)</li>
 *   <li>{@link Exception} → Cualquier otro error no controlado (500)</li>
 * </ol>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura errores de validación de campos (@Valid en DTOs).
     * Retorna un mapa detallado campo→mensaje para que el frontend pueda
     * mostrar cada error junto al campo correspondiente.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Uno o más campos tienen errores de validación.",
                errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Captura violaciones de integridad en la base de datos (claves duplicadas,
     * restricciones de claves foráneas, etc.). Traduce el mensaje técnico de
     * PostgreSQL a un mensaje legible para el usuario.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String detailMessage = ex.getMostSpecificCause().getMessage();
        String safeMessage = "La operación viola las restricciones de la base de datos.";
        
        if (detailMessage != null && detailMessage.contains("duplicate key value")) {
            safeMessage = "El registro ya existe en el sistema.";
        } else if (detailMessage != null && detailMessage.contains("violates foreign key")) {
            safeMessage = "El registro tiene dependencias en otras partes del sistema y no puede ser eliminado o actualizado.";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Data Integrity Violation",
                safeMessage
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Captura argumentos inválidos lanzados con {@code throw new IllegalArgumentException(...)}.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Captura excepciones de negocio lanzadas con {@code throw new RuntimeException(...)}.
     * Devuelve el mensaje personalizado del servicio directamente al frontend
     * para que el usuario sepa exactamente qué falló (ej. "Stock insuficiente").
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Error de Procesamiento",
                ex.getMessage() != null ? ex.getMessage() : "Se ha producido un error durante el procesamiento de la solicitud."
        );
        ex.printStackTrace();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Última línea de defensa: captura cualquier excepción no controlada.
     * NO expone el mensaje interno al frontend por seguridad; en su lugar,
     * devuelve un mensaje genérico y registra el error en consola para debugging.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ha ocurrido un error inesperado. Por favor, contacte con el administrador."
        );
        ex.printStackTrace();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
