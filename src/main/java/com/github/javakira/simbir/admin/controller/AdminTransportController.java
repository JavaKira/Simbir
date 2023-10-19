package com.github.javakira.simbir.admin.controller;

import com.github.javakira.simbir.admin.schema.GetTransportsRequest;
import com.github.javakira.simbir.admin.service.AdminService;
import com.github.javakira.simbir.admin.service.AdminTransportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Admin/Transport")
public class AdminTransportController {
    private final AdminTransportService service;
    private final AdminService adminService;

    @GetMapping
    @Operation(summary = "Get list of all transport")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> transports(HttpServletRequest request, @RequestBody GetTransportsRequest getTransportsRequest) {
        return adminService.checkAdmin(request, userId -> ResponseEntity.ok(service.transports(getTransportsRequest)));
    }
}
