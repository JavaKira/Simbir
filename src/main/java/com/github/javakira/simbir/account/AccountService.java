package com.github.javakira.simbir.account;

import com.github.javakira.simbir.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AccountDto accountInfo(long id) {
        Account account = account(id);
        return AccountDto.from(account);
    }

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
        if (repository.findByUsername(request.getUsername()).isPresent())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username '%s' is already in use".formatted(request.getUsername())
            );

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.user)
                .build();
        repository.save(account);
        String jwtToken = jwtService.generateToken(account);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse update(long id, UpdateRequest request) {
        Optional<Account> accountOptional = repository.findByUsername(request.getUsername());
        if (accountOptional.isPresent() && !accountOptional.get().getId().equals(id))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username '%s' is already in use".formatted(request.getUsername())
            );

        Account account = account(id);
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        repository.save(account);
        return singIn(new AuthRequest(request.getUsername(), request.getPassword()));
    }

    public void singOut(String token) {
        jwtService.banToken(token);
    }

    public Account account(long id) {
        Optional<Account> accountOptional = repository.findById(id);
        if (accountOptional.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Account with id %d doesnt exist".formatted(id)
            );

        return accountOptional.get();
    }
}
