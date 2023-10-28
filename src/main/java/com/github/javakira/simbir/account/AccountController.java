package com.github.javakira.simbir.account;

import com.github.javakira.simbir.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Account")
public class AccountController {
    private final AccountService service;
    private final JwtService jwtService;

    @Operation(summary = "Получение данных о текущем аккаунте")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        return jwtService.accessUser(request, service::accountInfo);
    }

    @Operation(summary = "Получение нового jwt токена пользователя")
    @PostMapping("/SingIn")
    public ResponseEntity<?> singIn(@RequestBody AuthRequest request) {
        return service.singIn(request);
    }

    @Operation(summary = "Регистрация нового аккаунта")
    @PostMapping("/SingUp")
    public ResponseEntity<?> singUp(@RequestBody RegisterRequest request) {
        return service.singUp(request);
    }

    @Operation(summary = "Выход из аккаунта")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/SingOut")
    public ResponseEntity<?> singOut(HttpServletRequest request) {
        Optional<String> token = jwtService.token(request);
        return service.singOut(token.orElseThrow());
    }

    @Operation(summary = "Обновление своего аккаунта")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/Update")
    public ResponseEntity<?> update(HttpServletRequest request, @RequestBody UpdateRequest updateRequest) {
        return jwtService.accessUser(request, userId -> service.update(userId, updateRequest));
    }
}
