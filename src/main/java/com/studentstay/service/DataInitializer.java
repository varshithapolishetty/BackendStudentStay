package com.studentstay.service;

import com.studentstay.domain.Role;
import com.studentstay.domain.User;
import com.studentstay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.email:admin@studentstay.local}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.username:admin}")
    private String adminUsername;

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
        String username = adminUsername.trim().toLowerCase(Locale.ROOT);
        if (username.isBlank()) {
            throw new IllegalStateException("Bootstrap administrator username must not be blank");
        }

        var existingAdmin = userRepository.findByEmailIgnoreCase(adminEmail);
        if (existingAdmin.isPresent()) {
            User admin = existingAdmin.get();
            boolean changed = false;
            if ("System Admin".equals(admin.getFullName()) && admin.getRole() != Role.ADMIN) {
                admin.setRole(Role.ADMIN);
                changed = true;
            }
            if (admin.getRole() == Role.ADMIN
                    && (admin.getLoginName() == null || admin.getLoginName().isBlank())) {
                admin.setLoginName(username);
                changed = true;
            }
            if (changed) {
                userRepository.save(admin);
            }
            return;
        }

        User admin = new User("System Admin", adminEmail, username,
                passwordEncoder.encode(adminPassword), null, Role.ADMIN);
        userRepository.save(admin);
    }
}
