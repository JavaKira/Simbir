package com.github.javakira.simbir.admin;

import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.Role;
import com.github.javakira.simbir.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final JwtService jwtService;
    private final AccountRepository accountRepository;

    public ResponseEntity<?> checkAdmin(HttpServletRequest request, Function<Long, ResponseEntity<?>> adminConsumer) {
        Optional<String> jwt = jwtService.token(request);
        if (jwt.isPresent()) {
            Long userId = jwtService.extractId(jwt.get());
            //Role role = jwtService.extractRole(jwt.get());
            Role role = accountRepository.findById(userId).get().getRole();
            if (role != Role.admin)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can use this endpoint");

            try {
                return adminConsumer.apply(userId);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
