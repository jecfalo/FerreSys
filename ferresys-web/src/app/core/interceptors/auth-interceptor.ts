import { Injectable } from '@angular/core';
import { AuthService } from '../../features/users/services/auth-service';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { BehaviorSubject, catchError, filter, Observable, switchMap, take, throwError } from 'rxjs';

/**
 * Interceptor de autenticación que:
 * 1. Adjunta el access token a todas las peticiones hacia la API.
 * 2. Detecta respuestas 401 (token expirado) e intenta renovar automáticamente
 *    usando el refresh token antes de cerrar sesión.
 * 3. Encola peticiones concurrentes mientras se renueva el token para evitar
 *    múltiples llamadas al endpoint de refresh.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

  constructor(private authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    const isApiUrl = req.url.startsWith('http://localhost:8080');
    const isAuthUrl = req.url.includes('/api/login') || req.url.includes('/api/refresh');

    // Adjuntar token solo a peticiones de la API que no sean de autenticación
    if (token && isApiUrl && !isAuthUrl) {
      req = this.addToken(req, token);
    }

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Si el error es 401 y no es una petición de auth, intentar refrescar
        if (error.status === 401 && !isAuthUrl) {
          return this.handle401Error(req, next);
        } else if (error.status === 403) {
          console.warn('No tienes permisos suficientes para acceder a este recurso.');
          this.authService.logout('/home');
        }
        return throwError(() => error);
      })
    );
  }

  /**
   * Agrega el header Authorization con el Bearer token a la petición.
   */
  private addToken(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  /**
   * Maneja errores 401 intentando renovar el token automáticamente.
   * Si ya hay un refresh en curso, encola la petición hasta que finalice.
   */
  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      const refreshToken = this.authService.getRefreshToken();

      // Si no hay refresh token, cerrar sesión directamente
      if (!refreshToken) {
        this.isRefreshing = false;
        console.warn('No hay refresh token disponible. Cerrando sesión.');
        this.authService.logout('/home');
        return throwError(() => new HttpErrorResponse({ status: 401 }));
      }

      return this.authService.refreshToken().pipe(
        switchMap((tokens) => {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(tokens.token);
          // Reintentar la petición original con el nuevo token
          return next.handle(this.addToken(request, tokens.token));
        }),
        catchError((err) => {
          this.isRefreshing = false;
          // El refresh token también falló → sesión completamente expirada
          console.warn('El refresh token ha expirado. Cerrando sesión.');
          this.authService.logout('/home');
          return throwError(() => err);
        })
      );
    } else {
      // Ya hay un refresh en curso → esperar a que termine y reintentar
      return this.refreshTokenSubject.pipe(
        filter(token => token !== null),
        take(1),
        switchMap(token => next.handle(this.addToken(request, token!)))
      );
    }
  }
}
