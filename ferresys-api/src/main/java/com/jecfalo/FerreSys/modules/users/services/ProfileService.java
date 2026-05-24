package com.jecfalo.FerreSys.modules.users.services;

import com.jecfalo.FerreSys.modules.users.models.Profile;
import com.jecfalo.FerreSys.modules.users.models.TypeUser;
import com.jecfalo.FerreSys.modules.users.records.profile.DataListProfile;
import com.jecfalo.FerreSys.modules.users.records.profile.ReferenceProfile;
import com.jecfalo.FerreSys.modules.users.records.profile.UpdateProfileEmail;
import com.jecfalo.FerreSys.modules.users.records.profile.UpdateProfileRole;
import com.jecfalo.FerreSys.modules.users.repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService implements IProfileService{
    @Autowired
    private ProfileRepository repository;

    @Override
    public Page<DataListProfile> loadAllActive(Pageable page) {
        Page<Profile> profiles = repository.findByActiveTrue(page);
        return profiles.map(DataListProfile::new);
    }

    @Override
    @Transactional
    public ReferenceProfile getProfileById(Long id) {
        Profile profile = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No existe un perfil con ese id"));
        return new ReferenceProfile(profile);
    }

    @Override
    public ReferenceProfile getProfileByDocument(String document) {
        Profile profile = repository.findByDocument(document)
                .orElseThrow(()-> new RuntimeException("No existe un perfil con ese documento"));
        return new ReferenceProfile(profile);
    }

    @Override
    public ReferenceProfile getProfileByUsername(String username) {
        Profile profile = repository.findByUserUsername(username)
                .orElseThrow(()-> new RuntimeException("No existe un perfil con ese username"));
        return new ReferenceProfile(profile);
    }

    @Override
    public ReferenceProfile udateEmail(Long id, UpdateProfileEmail uPE) {
        Profile profile = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No existe un perfil con ese id"));
        profile.setEmail(uPE.email());
        Profile updateEmail = repository.save(profile);
        return new ReferenceProfile(updateEmail);
    }

    @Override
    public ReferenceProfile updateRole(Long id, UpdateProfileRole uPR) {
        Profile profile = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No existe un perfil con ese id"));
        TypeUser typeState = profile.getTypeUser();
        TypeUser newType = uPR.userType();
        if(typeState == TypeUser.CLIENT || typeState == TypeUser.SELLER && newType == TypeUser.ADMIN || newType == TypeUser.SELLER) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String profileType = authentication.getAuthorities().iterator().next().getAuthority();
            if (profileType.equals("ROLE_CLIENT") || profileType.equals("ROLE_SELLER")) {
                throw new RuntimeException("No tienes privilegios necesarios para realizar estos cambios");
            }
        }
        profile.setTypeUser(newType);
        repository.save(profile);
        return new ReferenceProfile(profile);
    }

    @Override
    public void logicalDelete(Long id) {
        Profile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe un perfil con ese id"));
        profile.setActive(false);
        repository.save(profile);
    }
}
