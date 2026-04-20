package com.jecfalo.FerreSys.core.security;

import com.jecfalo.FerreSys.core.config.CorsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * Configuración central de Spring Security para la API REST.
 *
 * <p>Define:</p>
 * <ul>
 *   <li>Política de sesiones: STATELESS (sin estado, basado en JWT).</li>
 *   <li>Endpoints públicos: login, registro de usuario y renovación de token.</li>
 *   <li>Restricciones por rol: ADMIN para gestión, ADMIN/SELLER para operaciones de venta.</li>
 *   <li>Cadena de filtros: RateLimitFilter → SecurityFilter → filtros de Spring Security.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurations {
    @Autowired
    SecurityFilter securityFilter;
    @Autowired
    RateLimitFilter rateLimitFilter;

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Define qué endpoints son públicos, cuáles requieren roles específicos,
     * y el orden de ejecución de los filtros personalizados.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(csrf-> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->{
                            // --- Endpoints públicos (sin autenticación) ---
                            auth.requestMatchers(HttpMethod.POST, "/api/users").permitAll();
                            auth.requestMatchers(HttpMethod.POST,"/api/login").permitAll();
                            auth.requestMatchers(HttpMethod.POST,"/api/refresh").permitAll();
                            auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                            // --- Swagger: solo accesible para administradores ---
                            auth.requestMatchers("/v3/api-docs/**",
                                    "/swagger-ui.html",
                                    "/swagger-ui/**").permitAll();
                            //.hasRole("ADMIN");
                            // --- Endpoints protegidos por rol ---
                            auth.requestMatchers("/api/storages-room/**", "/api/users/**", "/api/profiles/**").hasRole("ADMIN");
                            auth.requestMatchers("/api/products/**", "/api/crud-inv/**", "/api/orders/**", "/api/cash-registers/**").hasAnyRole("ADMIN", "SELLER");

                            // --- Cualquier otra petición requiere autenticación ---
                            auth.anyRequest().authenticated();
                        }
                )
                // RateLimitFilter se ejecuta primero para bloquear abuso antes de procesar el JWT
                .addFilterBefore(rateLimitFilter, LogoutFilter.class)
                // SecurityFilter valida el JWT y establece el contexto de autenticación
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /** Codificador de contraseñas usando BCrypt (recomendado para producción). */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /** Expone el AuthenticationManager de Spring Security como Bean para inyectarlo en controladores. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration ac){
        return ac.getAuthenticationManager();
    }
}
