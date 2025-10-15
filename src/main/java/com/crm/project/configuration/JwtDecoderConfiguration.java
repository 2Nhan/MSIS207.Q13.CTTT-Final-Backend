package com.crm.project.configuration;

import com.crm.project.service.JwtService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;
import java.text.ParseException;

@Component
public class JwtDecoderConfiguration implements JwtDecoder {
    @Value("${jwt.key}")
    private String secretKey;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Autowired
    private JwtService jwtService;

    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            boolean isValid = jwtService.verifyToken(token, secretKey);
            if (!isValid) {
                throw new JwtException("Invalid token");
            }
        } catch (JOSEException | ParseException e) {
            throw new JwtException("Invalid token");
        }

        if(Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HS256");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS256).build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}
