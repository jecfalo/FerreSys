package com.jecfalo.FerreSys.modules.users.services;

import com.jecfalo.FerreSys.modules.users.records.profile.DataListProfile;
import com.jecfalo.FerreSys.modules.users.records.profile.ReferenceProfile;
import com.jecfalo.FerreSys.modules.users.records.profile.UpdateProfileEmail;
import com.jecfalo.FerreSys.modules.users.records.profile.UpdateProfileRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProfileService {
    Page<DataListProfile> loadAllActive(Pageable page);
    ReferenceProfile getProfileById(Long id);
    ReferenceProfile getProfileByDocument(String document);
    ReferenceProfile getProfileByUsername(String username);
    ReferenceProfile udateEmail(Long id, UpdateProfileEmail uPE);
    ReferenceProfile updateRole(Long id, UpdateProfileRole uPR);
    void logicalDelete(Long id);
}
