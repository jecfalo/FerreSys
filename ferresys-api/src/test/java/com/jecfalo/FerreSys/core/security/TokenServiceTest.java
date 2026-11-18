package com.jecfalo.FerreSys.core.security;

import com.jecfalo.FerreSys.modules.users.models.Profile;
import com.jecfalo.FerreSys.modules.users.models.TypeUser;
import com.jecfalo.FerreSys.modules.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Pruebas unitarias de TokenService: generación y validación de JWT. */
class TokenServiceTest {

    private TokenService service;
    private User usuario;

    @BeforeEach
    void setUp() {
        service = new TokenService();
        ReflectionTestUtils.setField(service, "secret", "clave-de-pruebas");

        usuario = new User();
        usuario.setUsername("vendedor1");
        Profile perfil = new Profile();
        perfil.setId(3L);
        perfil.setTypeUser(TypeUser.SELLER);
        usuario.setProfile(perfil);
    }

    @Test
    void elAccessTokenDevuelveElUsername() {
        String token = service.generateToken(usuario);

        assertThat(service.getSubject(token)).isEqualTo("vendedor1");
    }

    @Test
    void elRefreshTokenEsValidoComoRefresh() {
        String refresh = service.generateRefreshToken(usuario);

        assertThat(service.validateRefreshToken(refresh)).isEqualTo("vendedor1");
    }

    @Test
    void unAccessTokenNoSirveComoRefresh() {
        String access = service.generateToken(usuario);

        assertThatThrownBy(() -> service.validateRefreshToken(access))
                .hasMessageContaining("refresh token es inválido");
    }

    @Test
    void unTokenFirmadoConOtraClaveEsRechazado() {
        TokenService otro = new TokenService();
        ReflectionTestUtils.setField(otro, "secret", "otra-clave");
        String ajeno = otro.generateToken(usuario);

        assertThatThrownBy(() -> service.getSubject(ajeno))
                .hasMessageContaining("invalido");
    }
}
