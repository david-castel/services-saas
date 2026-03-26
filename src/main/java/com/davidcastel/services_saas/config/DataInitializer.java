package com.davidcastel.services_saas.config;

import com.davidcastel.services_saas.user.Role;
import com.davidcastel.services_saas.user.User;
import com.davidcastel.services_saas.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Forma fácil para arrancar: data initializer
 *
 * Aquí creamos un inicializador para sembrar un usuario admin al arrancar.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@test.com")) {
            userRepository.save(new User(
                    "admin@test.com",
                    passwordEncoder.encode("admin123"),
                    Role.ADMIN
            ));
        }

        if (!userRepository.existsByEmail("user@test.com")) {
            userRepository.save(new User(
                    "user@test.com",
                    passwordEncoder.encode("user123"),
                    Role.USER
            ));
        }
    }
}