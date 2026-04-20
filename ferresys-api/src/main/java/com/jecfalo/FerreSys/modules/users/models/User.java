package com.jecfalo.FerreSys.modules.users.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true)
    private String username;
    private String password;
    //le dice que hace referencia,
    //el fetch debe ir del lado de la referencia
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Profile profile;
    private boolean active;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //verificar el perfil y tipo asignado
        if(this.profile != null && this.profile.getTypeUser() != null ){
            return List.of(new SimpleGrantedAuthority("ROLE_"+ this.profile.getTypeUser().name()));
        }
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        //return UserDetails.super.isEnabled(); por defecto le permite el acceso a todo
        return this.active;
    }
}
