package com.github.javakira.simbir.account;

import com.github.javakira.simbir.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Account")
public class AccountController {
    private final AccountService service;
    private final JwtService jwtService;

    @Operation(summary = "Get data of current user")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/Me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> me(HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> ResponseEntity.ok(service.accountInfo(userId)));
    }

    //todo вывод сообщений ошибок сюда тоже нужно
    @Operation(summary = "Get new jwt token")
    @PostMapping(value = "/SingIn", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> singIn(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.singIn(request));
    }

    @Operation(summary = "Register new account")
    @PostMapping(value = "/SingUp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> singUp(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(service.singUp(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
        }
    }

    //todo реализовать singout
    @Operation(summary = "Logout from account")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/SingOut", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String singOut() {
        return "";
    }

    //todo
    @Operation(summary = "Update user data")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/Update", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String update() {
        return "";
    }
}
