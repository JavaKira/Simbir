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

    @Operation(summary = "Get data of current user")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> ResponseEntity.ok(service.accountInfo(userId)));
    }

    //todo вывод сообщений ошибок сюда тоже нужно
    @Operation(summary = "Get new jwt token")
    @PostMapping("/SingIn")
    public ResponseEntity<AuthResponse> singIn(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.singIn(request));
    }

    @Operation(summary = "Register new account")
    @PostMapping("/SingUp")
    public ResponseEntity<?> singUp(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(service.singUp(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
        }
    }

    @Operation(summary = "Logout from account")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/SingOut")
    public ResponseEntity<?> singOut(HttpServletRequest request) {
        Optional<String> token = jwtService.token(request);
        service.singOut(token.orElseThrow());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //todo
    @Operation(summary = "Update user data")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/Update")
    public ResponseEntity<?> update(HttpServletRequest request, @RequestBody UpdateRequest updateRequest) {
        return jwtService.accessUser(request, userId -> {
           service.update(userId, updateRequest);
           return new ResponseEntity<>(HttpStatus.OK);
        });
    }
}
