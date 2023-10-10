package com.github.javakira.simbir.account;

import com.github.javakira.simbir.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthResponse singIn(AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Account account = repository.findByUsername(request.getUsername())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(account);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse singUp(RegisterRequest request) {
        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.user)
                .build();
        repository.save(account);
        String jwtToken = jwtService.generateToken(account);
        return new AuthResponse(jwtToken);
    }
}
