package com.jecfalo.FerreSys.core.security;

import com.jecfalo.FerreSys.modules.users.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad que se ejecuta una vez por cada petición HTTP.
 * Extrae el JWT del header "Authorization", lo valida mediante {@link TokenService},
 * y si es válido, establece la autenticación en el contexto de Spring Security
 * para que los endpoints protegidos reconozcan al usuario.
 *
 * <p>Flujo:</p>
 * <ol>
 *   <li>Extrae el token del header {@code Authorization: Bearer <token>}.</li>
 *   <li>Valida el token y obtiene el username (subject).</li>
 *   <li>Busca el usuario en la BD para obtener sus roles actualizados.</li>
 *   <li>Establece la autenticación en el {@link SecurityContextHolder}.</li>
 *   <li>Si el token es nulo, inválido o el usuario no existe, la petición continúa sin autenticación.</li>
 * </ol>
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService service;
    @Autowired
    private UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = extractToken(request);
        if(tokenJWT != null){
            var subject = service.getSubject(tokenJWT);
            if(subject != null){
                var user = repository.findByUsername(subject);
                // Verificación de null: el usuario pudo ser eliminado/desactivado después de emitir el token
                if(user != null) {
                    var authenticate = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticate);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header "Authorization" de la petición.
     * @param request La petición HTTP entrante.
     * @return El token JWT sin el prefijo "Bearer ", o null si no hay header.
     */
    private String extractToken(HttpServletRequest request){
        var headerAuthorization = request.getHeader("Authorization");
        if(headerAuthorization != null){
            return headerAuthorization.replace("Bearer ", "");
        }
        return null;
    }
}
