package com.github.javakira.simbir.admin;

import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.Role;
import com.github.javakira.simbir.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final JwtService jwtService;
    private final AccountRepository accountRepository;

    public void checkAdminVoid(HttpServletRequest request, Consumer<Long> adminConsumer) {
        checkAdmin(request, userId -> {
            adminConsumer.accept(userId);
            return null;
        });
    }
    public <T> T checkAdmin(HttpServletRequest request, Function<Long, T> adminConsumer) {
        Optional<String> jwt = jwtService.token(request);
        long userId = jwtService.extractId(jwt.orElseThrow());
        Role role = accountRepository.findById(userId).orElseThrow().getRole();
        if (role != Role.admin)
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only admin can use this endpoint"
            );

        return adminConsumer.apply(userId);
    }
}
