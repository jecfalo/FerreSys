package com.jecfalo.FerreSys.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filtro de limitación de peticiones (Rate Limiting) por dirección IP.
 * Protege los endpoints públicos contra ataques de fuerza bruta y
 * creación masiva de cuentas.
 *
 * Configuración actual:
 * - Máximo 10 peticiones por ventana de tiempo por IP.
 * - La ventana de tiempo se reinicia cada 60 segundos.
 * - Solo aplica a endpoints públicos: /api/users y /api/login.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MS = 60_000; // 60 segundos

    // Almacena el conteo de peticiones por IP
    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Solo aplicar rate limiting a los endpoints públicos sensibles
        boolean isRateLimitedEndpoint =
                (path.equals("/api/users") && method.equals("POST")) ||
                (path.equals("/api/login") && method.equals("POST")) ||
                (path.equals("/api/refresh") && method.equals("POST"));

        if (!isRateLimitedEndpoint) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        // Limpiar entradas expiradas periódicamente
        cleanExpiredEntries();

        RequestCounter counter = requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());

        if (counter.isExpired()) {
            counter.reset();
        }

        if (counter.incrementAndGet() > MAX_REQUESTS) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"timestamp\":\"" + java.time.LocalDateTime.now() + "\"," +
                    "\"status\":429," +
                    "\"error\":\"Demasiadas Solicitudes\"," +
                    "\"message\":\"Has excedido el límite de peticiones. Intenta de nuevo en unos segundos.\"}"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Obtiene la IP real del cliente considerando proxies inversos.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Tomar solo la primera IP (la del cliente original)
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Elimina entradas con ventana de tiempo expirada para evitar memory leaks.
     */
    private void cleanExpiredEntries() {
        requestCounts.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * Clase interna que almacena el conteo de peticiones y el inicio de la ventana de tiempo.
     */
    private static class RequestCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();

        public boolean isExpired() {
            return System.currentTimeMillis() - windowStart > WINDOW_MS;
        }

        public void reset() {
            count.set(0);
            windowStart = System.currentTimeMillis();
        }

        public int incrementAndGet() {
            return count.incrementAndGet();
        }
    }
}
