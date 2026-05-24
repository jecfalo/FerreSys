package com.jecfalo.FerreSys.modules.users.repositories;

import com.jecfalo.FerreSys.modules.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN FETCH u.profile WHERE u.username = :username AND u.active = true")
    UserDetails findByUsername(@Param("username") String username);

}
