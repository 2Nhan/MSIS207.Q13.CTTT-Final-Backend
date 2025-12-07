package com.crm.project.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final String[] PUBLIC_ENDPOINTS = {"/api/v1/authentication/registration", "/api/v1/authentication/login"};

    @Autowired
    private JwtDecoderConfiguration jwtDecoder;

    @Autowired
    private AuthenticationEntryPointConfiguration authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/openapi.yml").permitAll()
                .anyRequest().authenticated());
        http.oauth2ResourceServer(resourceServer -> resourceServer
                .jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder))
                .authenticationEntryPoint(authenticationEntryPoint));
        http.sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.cors(cors -> {
        });
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("https://vero-3mfn.onrender.com");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:8088");
        config.addAllowedOrigin("https://vera-sage.vercel.app");
        config.addAllowedOrigin("http://3.26.45.29:8088");
        config.addAllowedOrigin("http://3.26.45.29");

        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
