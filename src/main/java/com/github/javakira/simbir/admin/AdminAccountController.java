package com.github.javakira.simbir.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
