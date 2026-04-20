package com.jecfalo.FerreSys.core.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Estructura estándar de respuesta de error que se envía al frontend.
 * Garantiza un formato JSON consistente para todas las excepciones capturadas por el
 * {@link GlobalExceptionHandler}, facilitando su interpretación por el interceptor
 * de errores del frontend (ErrorInterceptor).
 *
 * @param timestamp   Fecha y hora del error.
 * @param status      Código HTTP numérico (400, 404, 409, 500, etc.).
 * @param error       Categoría del error (ej. "Validation Error", "Data Integrity Violation").
 * @param message     Mensaje legible para el usuario final.
 * @param fieldErrors Mapa campo→mensaje con errores de validación por campo (puede ser null).
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors
) {
    /** Constructor simplificado para errores sin errores de campo. */
    public ErrorResponse(int status, String error, String message) {
        this(LocalDateTime.now(), status, error, message, null);
    }

    /** Constructor para errores de validación que incluyen detalle por campo. */
    public ErrorResponse(int status, String error, String message, Map<String, String> fieldErrors) {
        this(LocalDateTime.now(), status, error, message, fieldErrors);
    }
}
