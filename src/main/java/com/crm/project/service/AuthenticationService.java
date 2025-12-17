package com.crm.project.service;

import com.crm.project.dto.request.LoginRequest;
import com.crm.project.dto.request.RefreshRequest;
import com.crm.project.dto.response.LoginResponse;
import com.crm.project.dto.response.RefreshResponse;
import com.crm.project.entity.User;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.redis.redishash.LogoutToken;
import com.crm.project.redis.repository.BlackListRepository;
import com.crm.project.repository.UserRepository;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
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

    private final RedisTemplate<Object, Object> redisTemplate;

    @Value("${jwt.refresh-duration}")
    private int REFRESH_DURATION;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsernameWithRole(loginRequest.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()) || user.isDeleted()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        redisTemplate.opsForValue().set(
                "refresh:" + user.getUsername(),
                refreshToken,
                REFRESH_DURATION,
                TimeUnit.SECONDS
        );

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .fullName(user.getFirstName() + " " + user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().getCode())
                .build();
    }

    public void logout(HttpServletRequest request) throws ParseException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            SignedJWT signedJWT = SignedJWT.parse(token);
            String username = signedJWT.getJWTClaimsSet().getSubject();
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
            redisTemplate.delete("refresh:" + username);
        }
    }

    public RefreshResponse refresh(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        String username = jwtService.extractUsername(refreshToken);

        String savedToken = (String) redisTemplate.opsForValue().get("refresh:" + username);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String newAccessToken = jwtService.generateAccessToken(user);

        return RefreshResponse.builder().accessToken(newAccessToken).build();
    }

    public List<LogoutToken> getAllBlacklistTokens() {
        List<LogoutToken> result = new ArrayList<>();
        blackListRepository.findAll().forEach(result::add);
        return result;
    }
}
