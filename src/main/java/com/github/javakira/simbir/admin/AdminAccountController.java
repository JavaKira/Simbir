package com.github.javakira.simbir.admin;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.rent.Rent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Admin")
public class AdminAccountController {
    private final AdminAccountService service;
    private final AdminService adminService;

    @Operation(summary = "Get list of all accounts")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Account")
    public ResponseEntity<?> accounts(GetAccountsRequest getAccountsRequest, HttpServletRequest request) {
        return adminService.checkAdmin(request, userId -> ResponseEntity.ok(service.accounts(getAccountsRequest)));
    }

    @Operation(summary = "Get info about user by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Account/{id}")
    public ResponseEntity<?> accountInfo(HttpServletRequest request, @PathVariable Long id) {
        return adminService.checkAdmin(request, userId -> {
            Optional<Account> accountOptional = service.accountInfo(id);
            if (accountOptional.isEmpty())
                return ResponseEntity.badRequest().body("Account with id %d doesnt exist".formatted(id));

            return ResponseEntity.ok().body(accountOptional.get());
        });
    }
}
