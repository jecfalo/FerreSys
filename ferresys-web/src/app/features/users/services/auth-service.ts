import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { Observable, tap } from 'rxjs';

/**
 * Servicio central de autenticación y gestión de tokens JWT.
 *
 * Responsabilidades:
 * - Realizar login contra la API y obtener el par de tokens (access + refresh).
 * - Almacenar y recuperar tokens desde localStorage.
 * - Renovar tokens automáticamente usando el refresh token.
 * - Decodificar el JWT para extraer datos del usuario (username, rol).
 * - Verificar la validez temporal del token antes de permitir navegación.
 * - Cerrar sesión limpiando todos los tokens almacenados.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = "http://localhost:8080/api";
  private tokenKey = 'token';
  private refreshTokenKey = 'refreshToken';

  constructor(private http: HttpClient, private router: Router) { }

  /**
   * Envía las credenciales al backend y obtiene un par de tokens JWT.
   * @param username Nombre de usuario.
   * @param password Contraseña en texto plano (se valida con BCrypt en el backend).
   * @returns Observable con el access token y refresh token.
   */
  login(username: string, password: string) {
    return this.http.post<{ token: string, refreshToken: string }>(`${this.apiUrl}/login`, { username, password });
  }

  /**
   * Solicita un nuevo par de tokens al backend usando el refresh token actual.
   * Automáticamente actualiza ambos tokens en localStorage si la renovación es exitosa.
   * @returns Observable con los nuevos tokens.
   */
  refreshToken(): Observable<{ token: string, refreshToken: string }> {
    const refreshToken = this.getRefreshToken();
    return this.http.post<{ token: string, refreshToken: string }>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(response => {
        this.saveToken(response.token);
        this.saveRefreshToken(response.refreshToken);
      })
    );
  }

  /** Guarda el access token en localStorage. */
  saveToken(token: string) {
    localStorage.setItem(this.tokenKey, token);
  }

  /** Guarda el refresh token en localStorage. */
  saveRefreshToken(refreshToken: string) {
    localStorage.setItem(this.refreshTokenKey, refreshToken);
  }

  /** Recupera el access token desde localStorage, o null si no existe. */
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /** Recupera el refresh token desde localStorage, o null si no existe. */
  getRefreshToken(): string | null {
    return localStorage.getItem(this.refreshTokenKey);
  }

  /**
   * Cierra la sesión del usuario eliminando ambos tokens
   * y redirigiendo a la ruta especificada.
   * @param redirectUrl Ruta de destino tras el logout (por defecto '/login-user').
   */
  logout(redirectUrl: string = '/login-user'): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    this.router.navigate([redirectUrl]);
  }

  /** Verifica si existe un access token almacenado (no valida expiración). */
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  /**
   * Valida que el access token no haya expirado comparando el claim 'exp'
   * con la hora actual del sistema.
   * @returns true si el token existe y no ha expirado, false en caso contrario.
   */
  isTokenValid(): boolean {
    const token = this.getToken();
    if (!token) return false;
    try {
      const decoded: any = jwtDecode(token);
      const expirationDate = decoded.exp * 1000;
      const now = new Date().getTime();
      return expirationDate > now;
    } catch (error) {
      return false;
    }
  }

  /**
   * Extrae el nombre de usuario (claim 'sub') del access token.
   * @returns El username o null si el token no existe o es inválido.
   */
  getUsername(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decoded: any = jwtDecode(token);
      return decoded.sub || null;
    } catch (e) {
      return null;
    }
  }

  /**
   * Extrae el rol del usuario (claim 'role') del access token.
   * @returns El rol (ej. "ADMIN", "SELLER", "CLIENT") o null si no existe.
   */
  getUserRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decoded: any = jwtDecode(token);
      return decoded.role || null;
    } catch (e) {
      return null;
    }
  }
}
