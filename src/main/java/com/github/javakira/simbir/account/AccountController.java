package com.github.javakira.simbir.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Account")
public class AccountController {
    private final AccountService service;

    //todo можно добавить дату регестраций и другой шлак чтобы не так скучно выглядело
    @Operation(summary = "Get data of current user")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/Me", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String me() {
        return "";
    }

    @Operation(summary = "Get new jwt token")
    @PostMapping(value = "/SingIn", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> singIn(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.singIn(request));
    }

    //todo Нельзя создать аккаунт с существующим username
    @Operation(summary = "Register new account")
    @PostMapping(value = "/SingUp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> singUp(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.singUp(request));
    }

    @Operation(summary = "Logout from account")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/SingOut", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String singOut() {
        return "";
    }

    @Operation(summary = "Update user data")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/Update", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String update() {
        return "";
    }
}
