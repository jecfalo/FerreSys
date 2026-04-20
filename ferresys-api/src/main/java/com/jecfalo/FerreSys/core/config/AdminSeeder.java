package com.jecfalo.FerreSys.core.config;

import com.jecfalo.FerreSys.modules.users.models.Profile;
import com.jecfalo.FerreSys.modules.users.models.TypeUser;
import com.jecfalo.FerreSys.modules.users.models.User;
import com.jecfalo.FerreSys.modules.users.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


public class AdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        seedConsumidorFinalIfNotExists();
        seedAdminIfNotExists();
    }

    private void seedConsumidorFinalIfNotExists() {
        if (userRepository.findByUsername("consumidor_final") == null) {
            User consumUser = new User();
            consumUser.setUsername("consumidor_final");
            consumUser.setPassword(passwordEncoder.encode("consumidor123"));
            consumUser.setActive(true);

            Profile consumProfile = new Profile();
            consumProfile.setDocument("9999999999");
            consumProfile.setNames("Consumidor");
            consumProfile.setSurnames("Final");
            consumProfile.setEmail("consumidor@ferresys.com");
            consumProfile.setTypeUser(TypeUser.CLIENT);
            consumProfile.setActive(true);

            consumProfile.setUser(consumUser);
            consumUser.setProfile(consumProfile);

            userRepository.save(consumUser);
            System.out.println("Usuario Consumidor Final inicializado con éxito.");
        }
    }

    private void seedAdminIfNotExists() {
        if (userRepository.findByUsername("admin") == null) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("superferresys"));
            adminUser.setActive(true);

            Profile adminProfile = new Profile();
            adminProfile.setDocument("0000000000");
            adminProfile.setNames("Super");
            adminProfile.setSurnames("Administrador");
            adminProfile.setEmail("admin@ferresys.com");
            adminProfile.setTypeUser(TypeUser.ADMIN);
            adminProfile.setActive(true);

            adminProfile.setUser(adminUser);
            adminUser.setProfile(adminProfile);

            userRepository.save(adminUser);
            System.out.println("Usuario Administrador inicializado con éxito.");
        }
    }
}
