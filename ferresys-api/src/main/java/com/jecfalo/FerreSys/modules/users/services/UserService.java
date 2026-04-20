package com.jecfalo.FerreSys.modules.users.services;

import com.jecfalo.FerreSys.modules.users.models.Profile;
import com.jecfalo.FerreSys.modules.users.models.TypeUser;
import com.jecfalo.FerreSys.modules.users.models.User;
import com.jecfalo.FerreSys.modules.users.records.ReferenceUser;
import com.jecfalo.FerreSys.modules.users.records.RegisterUser;
import com.jecfalo.FerreSys.modules.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements  IUserService{
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder encoder;
    @Override
    @Transactional
    public ReferenceUser create(RegisterUser userDTO) {
        User user = new User();
        user.setUsername(userDTO.username());
        user.setPassword(encoder.encode(userDTO.password()));
        user.setActive(true);
        //usando el builder en la entidad
        Profile profile = Profile.builder()
                .document(userDTO.document())
                .names(userDTO.names())
                .surnames(userDTO.surnames())
                .email(userDTO.email())
                .typeUser(TypeUser.CLIENT)
                .active(true)
                .build();
        //enlazar ambos objetos en el usuario
        profile.setUser(user);//al perfil le asignamos el usuario
        user.setProfile(profile);
        var saveUser = repository.save(user);
        //se guarda el por el cascade = all, guardando ambas tablas
        return new ReferenceUser(saveUser);
    }
}
