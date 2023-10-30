package com.github.javakira.simbir.jwt;

import com.github.javakira.simbir.account.Account;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {
    String extractLogin(String token);

    long extractId(String token);

    Optional<String> token(HttpServletRequest request);

    void banToken(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateToken(Account account);

    <T> T accessUser(HttpServletRequest request, Function<Long, T> userConsumer);
}
