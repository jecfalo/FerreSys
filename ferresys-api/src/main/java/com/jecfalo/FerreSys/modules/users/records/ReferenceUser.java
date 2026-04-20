package com.jecfalo.FerreSys.modules.users.records;

import com.jecfalo.FerreSys.modules.users.models.User;

public record ReferenceUser(
        Long id,
        String username,
        String names,
        String surnames,
        String email,
        Boolean active
) {
    public ReferenceUser(User user){
        this(
                user.getId(),
                user.getUsername(),
                user.getProfile().getNames(),
                user.getProfile().getSurnames(),
                user.getProfile().getEmail(),
                user.isActive()
        );
    }
}
