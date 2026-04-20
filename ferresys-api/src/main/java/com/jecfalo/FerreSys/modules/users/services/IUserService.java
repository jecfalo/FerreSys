package com.jecfalo.FerreSys.modules.users.services;

import com.jecfalo.FerreSys.modules.users.records.ReferenceUser;
import com.jecfalo.FerreSys.modules.users.records.RegisterUser;

public interface IUserService {
    ReferenceUser create(RegisterUser userDTO);
}
