package com.studentstay.service;

import com.studentstay.domain.Role;
import com.studentstay.domain.User;
import com.studentstay.dto.ApiDtos;
import com.studentstay.exception.ConflictException;
import com.studentstay.exception.ResourceNotFoundException;
import com.studentstay.repository.UserRepository;
import com.studentstay.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public ApiDtos.AuthResponse register(ApiDtos.RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String username = request.getUsername().trim().toLowerCase(Locale.ROOT);
        if (userRepository.findByLoginName(username).isPresent()) {
            throw new ConflictException("Username already exists. Please try another username.");
        }
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ConflictException("User already exists for email: " + request.getEmail());
        }
        Role role = Role.valueOf(request.getRole().trim().toUpperCase(Locale.ROOT));
        if (role == Role.ADMIN) {
            throw new ConflictException("Administrator accounts cannot be registered here");
        }
        User user = new User(
                request.getFullName().trim(),
                email,
                username,
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                role
        );
        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            Throwable cause = exception.getCause();
            while (cause != null && !(cause instanceof ConstraintViolationException)) {
                cause = cause.getCause();
            }
            if (cause instanceof ConstraintViolationException constraint
                    && constraint.getConstraintName() != null
                    && constraint.getConstraintName().toLowerCase(Locale.ROOT).contains("username")) {
                throw new ConflictException("Username already exists. Please try another username.");
            }
            throw exception;
        }
        return buildTokenResponse(user);
    }

    public ApiDtos.AuthResponse login(ApiDtos.LoginRequest request) {
        String identifier = request.getUsername() != null && !request.getUsername().isBlank()
                ? request.getUsername().trim().toLowerCase(Locale.ROOT)
                : request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("Enter your username or email");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(identifier, request.getPassword()));
        User user = userRepository.findByLoginName(identifier)
                .or(() -> userRepository.findByEmailIgnoreCase(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return buildTokenResponse(user);
    }

    private ApiDtos.AuthResponse buildTokenResponse(User user) {
        String token = jwtService.generateToken(user);
        return new ApiDtos.AuthResponse(token, user.getEmail(), user.getLoginName(), user.getFullName(), user.getRole().name());
    }
}
