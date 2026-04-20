import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import Swal from 'sweetalert2';

/**
 * Interceptor global de errores HTTP.
 * Captura todas las respuestas de error de la API y las muestra al usuario
 * mediante alertas SweetAlert2. Es capaz de interpretar la estructura
 * {@code ErrorResponse} del backend (message, error, fieldErrors).
 *
 * Tipos de error manejados:
 * - Errores estructurados del backend (con fieldErrors para validaciones).
 * - 401 No autorizado.
 * - 403 Acceso denegado.
 * - 404 Recurso no encontrado.
 * - 0   Error de conexión (servidor caído o sin internet).
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  /**
   * Escapa caracteres HTML especiales (&, <, >, ", ')
   * para prevenir inyección XSS cuando se renderizan mensajes en SweetAlert.
   */
  private escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.appendChild(document.createTextNode(text));
    return div.innerHTML;
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Ha ocurrido un error inesperado.';
        let errorTitle = 'Error';
        let useHtml = false;

        if (error.error && error.error.message) {
          // El backend envió un ErrorResponse estructurado
          errorMessage = this.escapeHtml(error.error.message);
          errorTitle = this.escapeHtml(error.error.error || 'Error');

          if (error.error.fieldErrors) {
            // Errores de validación por campo → renderizar como lista HTML (sanitizada)
            useHtml = true;
            errorMessage += '<br><ul style="text-align: left; margin-top: 10px;">';
            for (const field in error.error.fieldErrors) {
              if (error.error.fieldErrors.hasOwnProperty(field)) {
                errorMessage += `<li><b>${this.escapeHtml(field)}</b>: ${this.escapeHtml(error.error.fieldErrors[field])}</li>`;
              }
            }
            errorMessage += '</ul>';
          }
        } else if (error.status === 401) {
          errorTitle = 'No autorizado';
          errorMessage = 'Tu sesión ha expirado o no tienes acceso.';
        } else if (error.status === 403) {
          errorTitle = 'Acceso Denegado';
          errorMessage = 'No tienes permisos para realizar esta acción.';
        } else if (error.status === 404) {
          errorTitle = 'No Encontrado';
          errorMessage = 'El recurso solicitado no fue encontrado.';
        } else if (error.status === 0) {
           errorTitle = 'Error de Conexión';
           errorMessage = 'No se pudo conectar con el servidor. Verifica tu conexión a internet o asegúrate de que el servidor esté en ejecución.';
        }

        // Mostrar alerta: usa 'html' solo cuando hay fieldErrors, 'text' para el resto (más seguro)
        if (useHtml) {
          Swal.fire({
            icon: 'error',
            title: errorTitle,
            html: errorMessage,
            confirmButtonColor: '#3085d6'
          });
        } else {
          Swal.fire({
            icon: 'error',
            title: errorTitle,
            text: errorMessage,
            confirmButtonColor: '#3085d6'
          });
        }

        return throwError(() => error);
      })
    );
  }
}
