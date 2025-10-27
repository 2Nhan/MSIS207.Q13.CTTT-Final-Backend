package com.crm.project.service;

import com.crm.project.dto.request.LoginRequest;
import com.crm.project.dto.response.AuthenticationResponse;
import com.crm.project.entity.User;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthenticationResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()) || user.isDeleted()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String token = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .accessToken(token)
                .build();
    }
}
