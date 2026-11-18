package com.jecfalo.FerreSys.core.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jecfalo.FerreSys.modules.users.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    private static final long ACCESS_TOKEN_MINUTES = 30;
    private static final long REFRESH_TOKEN_DAYS = 7;

    /**
     * Genera un Access Token de corta duración (30 minutos).
     * Contiene los claims necesarios para autorizar peticiones (username, rol, profileId).
     */
    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("ferresys")
                    .withSubject(user.getUsername())
                    .withExpiresAt(accessTokenExpiration())
                    .withClaim("id", user.getProfile().getId())
                    .withClaim("role", user.getProfile().getTypeUser().name())
                    .withClaim("type", "access")
                    .sign(algorithm);
        }catch (JWTCreationException ex){
            throw new RuntimeException("Error al generar el access token", ex);
        }
    }

    /**
     * Genera un Refresh Token de larga duración (7 días).
     * Solo contiene el username (subject) y el tipo. No transporta claims de negocio.
     */
    public String generateRefreshToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("ferresys")
                    .withSubject(user.getUsername())
                    .withExpiresAt(refreshTokenExpiration())
                    .withClaim("type", "refresh")
                    .sign(algorithm);
        }catch (JWTCreationException ex){
            throw new RuntimeException("Error al generar el refresh token", ex);
        }
    }

    /**
     * Extrae el subject (username) de un token JWT válido.
     */
    public String getSubject(String jwtToken){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("ferresys")
                    .build()
                    .verify(jwtToken)
                    .getSubject();
        }catch (JWTVerificationException ex){
            throw new RuntimeException("Este token puede ser invalido");
        }
    }

    /**
     * Valida que un token sea específicamente de tipo "refresh".
     * Retorna el username (subject) si es válido, o lanza excepción si no lo es.
     */
    public String validateRefreshToken(String refreshToken){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decoded = JWT.require(algorithm)
                    .withIssuer("ferresys")
                    .withClaim("type", "refresh")
                    .build()
                    .verify(refreshToken);
            return decoded.getSubject();
        }catch (JWTVerificationException ex){
            throw new RuntimeException("El refresh token es inválido o ha expirado. Por favor inicie sesión nuevamente.");
        }
    }

    // Se usa Instant (siempre UTC). Antes se tomaba LocalDateTime.now() como si fuera UTC y,
    // en una máquina con hora de Ecuador (UTC-5), el access token nacía ya vencido.
    private Instant accessTokenExpiration(){
        return Instant.now().plus(ACCESS_TOKEN_MINUTES, ChronoUnit.MINUTES);
    }

    private Instant refreshTokenExpiration(){
        return Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS);
    }
}
