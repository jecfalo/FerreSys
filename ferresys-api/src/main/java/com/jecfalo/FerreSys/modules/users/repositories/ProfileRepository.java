package com.jecfalo.FerreSys.modules.users.repositories;

import com.jecfalo.FerreSys.modules.users.models.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Page<Profile> findByActiveTrue(Pageable page);
    java.util.Optional<Profile> findByDocument(String document);
    java.util.Optional<Profile> findByUserUsername(String username);
}
