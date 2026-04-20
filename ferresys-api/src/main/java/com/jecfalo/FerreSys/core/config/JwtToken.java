package com.jecfalo.FerreSys.core.config;

/**
 * DTO que transporta ambos tokens JWT al frontend tras autenticarse o refrescar sesión.
 * @param token       Access Token de corta duración (30 min) para autorizar peticiones.
 * @param refreshToken Refresh Token de larga duración (7 días) para renovar la sesión sin re-login.
 */
public record JwtToken(String token, String refreshToken) {
}
