package com.jecfalo.FerreSys.modules.users.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "profiles")
@EntityListeners(AuditingEntityListener.class)
public class Profile {
    @Id
    Long id;
    @Column(unique = true)
    private String document;
    private String names;
    private String surnames;
    @Column(unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private TypeUser typeUser;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updateAt;
    @OneToOne//le dice que es la due;a de la relacion
    @MapsId //usa el mismo id que el usuario
    @JoinColumn(name = "id") //pk y fk usan el mismo id
    private User user;
    private boolean active;
}
