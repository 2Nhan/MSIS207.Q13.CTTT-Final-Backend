package com.crm.project.service;

import com.crm.project.dto.request.LoginRequest;
import com.crm.project.dto.response.AuthenticationResponse;
import com.crm.project.entity.User;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.redis.redishash.LogoutToken;
import com.crm.project.redis.repository.BlackListRepository;
import com.crm.project.repository.UserRepository;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;
import java.text.ParseException;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final BlackListRepository blackListRepository;

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

    public void logout(HttpServletRequest request) throws ParseException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            SignedJWT signedJWT = SignedJWT.parse(token);
            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration.before(new Date())) {
                return;
            }
            LogoutToken logoutToken = LogoutToken.builder()
                    .jit(jit)
                    .ttl(expiration.getTime() - new Date().getTime())
                    .build();
            blackListRepository.save(logoutToken);
        }
    }

    public List<LogoutToken> getAllBlacklistTokens() {
        List<LogoutToken> result = new ArrayList<>();
        blackListRepository.findAll().forEach(result::add);
        return result;
    }
}
