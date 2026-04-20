package com.jecfalo.FerreSys.modules.users.records.profile;

import com.jecfalo.FerreSys.modules.users.models.Profile;
import com.jecfalo.FerreSys.modules.users.models.TypeUser;

public record DataListProfile(
        Long id,
        String username,
        String email,
        TypeUser user,
        Boolean active
) {
    public DataListProfile(Profile p){
        this(
                p.getUser().getId(),
                p.getUser().getUsername(),
                p.getEmail(),
                p.getTypeUser(),
                p.isActive()
        );
    }
}
