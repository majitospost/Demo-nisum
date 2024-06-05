package com.example.demo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class JWTUtil {
    private static final String SECRET_KEY = "secret";
    private static final long EXPIRATION_TIME_IN_MINUTES = 60;

    public static String generateToken(String subject) {
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(EXPIRATION_TIME_IN_MINUTES);
        Date expirationDate = Date.from(expirationDateTime.toInstant(ZoneOffset.UTC));
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        String token = JWT.create()
                .withSubject(subject)
                .withExpiresAt(expirationDate)
                .sign(algorithm);

        return token;
    }

    public static String validateToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        return jwt.getSubject();
    }
}
