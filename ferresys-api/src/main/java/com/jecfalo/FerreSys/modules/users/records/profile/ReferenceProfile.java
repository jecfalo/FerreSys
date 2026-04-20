package com.jecfalo.FerreSys.modules.users.records.profile;

import com.jecfalo.FerreSys.modules.users.models.Profile;
import com.jecfalo.FerreSys.modules.users.models.TypeUser;

public record ReferenceProfile(
        Long id,
        String username,
        String names,
        String surnames,
        String email,
        TypeUser user,
        Boolean active
) {
    public ReferenceProfile(Profile p){
        this(
                p.getUser().getId(),
                p.getUser().getUsername(),
                p.getNames(),
                p.getSurnames(),
                p.getEmail(),
                p.getTypeUser(),
                p.isActive()
        );
    }
}