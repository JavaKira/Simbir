package com.github.javakira.simbir.admin.controller;

import com.github.javakira.simbir.admin.schema.UpdateByAdminRequest;
import com.github.javakira.simbir.admin.service.AdminAccountService;
import com.github.javakira.simbir.admin.service.AdminService;
import com.github.javakira.simbir.admin.schema.GetAccountsRequest;
import com.github.javakira.simbir.admin.schema.RegisterByAdminRequest;
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

    @Operation(summary = "Get info about user by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Account/{id}")
    public ResponseEntity<?> accountInfo(HttpServletRequest request, @PathVariable long id) {
        return adminService.checkAdmin(request, userId -> service.accountInfo(id));
    }

    @Operation(summary = "Create new account")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/Account")
    public ResponseEntity<?> registerAccount(HttpServletRequest request, @RequestBody RegisterByAdminRequest registerByAdminRequest) {
        return adminService.checkAdmin(request, userId -> service.registerAccount(registerByAdminRequest));
    }

    @Operation(summary = "Update info about user by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/Account/{id}")
    public ResponseEntity<?> updateAccount(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateByAdminRequest updateByAdminRequest) {
        return adminService.checkAdmin(request, userId -> service.updateAccount(id, updateByAdminRequest));
    }

    @Operation(summary = "Delete user with id")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/Account/{id}")
    public ResponseEntity<?> deleteAccount(HttpServletRequest request, @PathVariable long id) {
        return adminService.checkAdmin(request, userId -> service.deleteAccount(id));
    }
}
