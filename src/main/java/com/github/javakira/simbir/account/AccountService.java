package com.github.javakira.simbir.account;

import com.github.javakira.simbir.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public ResponseEntity<?> accountInfo(Long id) {
        Optional<Account> accountOptional = repository.findById(id);
        if (accountOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Account with id %d doesnt exist".formatted(id));

        Account account = accountOptional.get();
        return ResponseEntity.ok(AccountDto.from(account));
    }

    public ResponseEntity<?> singIn(AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Account account = repository.findByUsername(request.getUsername())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(account);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    public ResponseEntity<?> singUp(RegisterRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username '%s' is already in use".formatted(request.getUsername()));

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.user)
                .build();
        repository.save(account);
        String jwtToken = jwtService.generateToken(account);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    public ResponseEntity<?> update(Long id, UpdateRequest request) {
        Optional<Account> accountOptional = repository.findByUsername(request.getUsername());
        if (accountOptional.isPresent() && !accountOptional.get().getId().equals(id))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username '%s' is already in use".formatted(request.getUsername()));

        accountOptional = repository.findById(id);
        if (accountOptional.isEmpty())
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Account with id %d doesnt exist".formatted(id));

        accountOptional.get().setUsername(request.getUsername());
        accountOptional.get().setPassword(passwordEncoder.encode(request.getPassword()));
        repository.save(accountOptional.get());
        return ResponseEntity.ok(AccountDto.from(accountOptional.get()));
    }

    public ResponseEntity<?> singOut(String token) {
        jwtService.banToken(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
