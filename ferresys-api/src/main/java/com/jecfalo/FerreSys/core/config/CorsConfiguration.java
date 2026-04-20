package com.jecfalo.FerreSys.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing) para la API.
 * Permite que el frontend Angular (localhost:4200) realice peticiones
 * a la API backend (localhost:8080) desde un origen diferente.
 *
 * <p>Nota: En producción, reemplazar "http://localhost:4200" por el dominio real del frontend.</p>
 */
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD");
    }
}
