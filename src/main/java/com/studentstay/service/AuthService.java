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

    public ApiDtos.AuthResponse register(ApiDtos.RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail().trim()).isPresent()) {
            throw new ConflictException("User already exists for email: " + request.getEmail());
        }
        Role role = Role.valueOf(request.getRole().toUpperCase());
        User user = new User(
                request.getFullName().trim(),
                request.getEmail().trim(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                role
        );
        userRepository.save(user);
        return buildTokenResponse(user);
    }

    public ApiDtos.AuthResponse login(ApiDtos.LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return buildTokenResponse(user);
    }

    private ApiDtos.AuthResponse buildTokenResponse(User user) {
        String token = jwtService.generateToken(user);
        return new ApiDtos.AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
