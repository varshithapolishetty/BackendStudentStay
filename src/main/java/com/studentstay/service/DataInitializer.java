package com.studentstay.service;

import com.studentstay.domain.Role;
import com.studentstay.domain.User;
import com.studentstay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.email:admin@studentstay.local}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password:}")
    private String adminPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminPassword == null || adminPassword.isBlank()) {
            return;
        }
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User("System Admin", adminEmail, passwordEncoder.encode(adminPassword), null, Role.ADMIN);
            userRepository.save(admin);
        }
    }
}
