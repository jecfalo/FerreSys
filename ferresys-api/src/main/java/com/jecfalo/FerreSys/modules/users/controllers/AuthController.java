package com.jecfalo.FerreSys.modules.users.controllers;

import com.jecfalo.FerreSys.core.config.JwtToken;
import com.jecfalo.FerreSys.core.security.TokenService;
import com.jecfalo.FerreSys.modules.users.models.User;
import com.jecfalo.FerreSys.modules.users.records.AuthenticatedUser;
import com.jecfalo.FerreSys.modules.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador REST encargado de la autenticación de usuarios.
 * Recibe credenciales, las valida y emite un par de tokens JWT (access + refresh)
 * para autorizar futuras peticiones.
 */
@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private TokenService service;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private UserRepository userRepository;

    /**
     * Procesa la solicitud de inicio de sesión (Login).
     * @param auth Objeto que contiene las credenciales recabadas por el Frontend (username y password).
     * @return ResponseEntity con el access token y refresh token recién generados.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtToken> authentication(@RequestBody AuthenticatedUser auth){
        // Encapsulamos las credenciales planas en un objeto de tipo Token para que Spring Security las procese
        Authentication authToken = new UsernamePasswordAuthenticationToken(auth.username(), auth.password());

        // El AuthenticationManager verifica que el usuario exista y la contraseña coincida con el hash en la DB
        var userAuth = manager.authenticate(authToken);

        User user = (User) userAuth.getPrincipal();

        // Generamos ambos tokens
        var accessToken = service.generateToken(user);
        var refreshToken = service.generateRefreshToken(user);

        // Retornamos ambos tokens al frontend
        return ResponseEntity.ok(new JwtToken(accessToken, refreshToken));
    }

    /**
     * Endpoint para renovar la sesión usando un Refresh Token válido.
     * Emite un nuevo par de tokens (access + refresh) sin necesidad de re-ingresar credenciales.
     * @param body Mapa con clave "refreshToken" que contiene el refresh token actual.
     * @return ResponseEntity con los nuevos tokens.
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtToken> refreshToken(@RequestBody Map<String, String> body){
        String refreshToken = body.get("refreshToken");
        if(refreshToken == null || refreshToken.isBlank()){
            throw new RuntimeException("El refresh token es requerido.");
        }

        // Validamos que sea un refresh token válido y extraemos el username
        String username = service.validateRefreshToken(refreshToken);

        // Buscamos el usuario actualizado en la base de datos
        var userDetails = userRepository.findByUsername(username);
        if(userDetails == null){
            throw new RuntimeException("El usuario asociado a este token ya no existe o fue desactivado.");
        }

        User user = (User) userDetails;

        // Generamos un nuevo par de tokens
        var newAccessToken = service.generateToken(user);
        var newRefreshToken = service.generateRefreshToken(user);

        return ResponseEntity.ok(new JwtToken(newAccessToken, newRefreshToken));
    }
}
