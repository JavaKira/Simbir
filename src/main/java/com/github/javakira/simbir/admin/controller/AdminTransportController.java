package com.github.javakira.simbir.admin.controller;

import com.github.javakira.simbir.admin.schema.GetTransportsRequest;
import com.github.javakira.simbir.admin.schema.RegisterTransportByAdminRequest;
import com.github.javakira.simbir.admin.schema.UpdateTransportByAdminRequest;
import com.github.javakira.simbir.admin.service.AdminService;
import com.github.javakira.simbir.admin.service.AdminTransportService;
import com.github.javakira.simbir.transport.Transport;
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
@RequestMapping("/api/Admin/Transport")
public class AdminTransportController {
    private final AdminTransportService service;
    private final AdminService adminService;

    @GetMapping
    @Operation(summary = "Get list of all transport")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> transports(HttpServletRequest request, GetTransportsRequest getTransportsRequest) {
        return adminService.checkAdmin(request, userId -> ResponseEntity.ok(service.transports(getTransportsRequest)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get info about transport by id")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> transportId(HttpServletRequest request, @PathVariable Long id) {
        return adminService.checkAdmin(request, userId -> {
            Optional<Transport> transportOptional = service.transportInfo(id);
            if (transportOptional.isEmpty())
                return ResponseEntity.badRequest().body("Transport with id %d doesnt exist".formatted(id));

            return ResponseEntity.ok().body(transportOptional.get());
        });
    }

    @PostMapping
    @Operation(summary = "Create new transport")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> registerTransport(
            HttpServletRequest request,
            @RequestBody RegisterTransportByAdminRequest registerTransportByAdminRequest
    ) {
        return adminService.checkAdmin(request, userId -> ResponseEntity.ok(service.registerTransport(registerTransportByAdminRequest)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit transport by id")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> updateTransport(
            HttpServletRequest request,
            @RequestBody UpdateTransportByAdminRequest updateTransportByAdminRequest,
            @PathVariable Long id
    ) {
        return adminService.checkAdmin(request, userId -> ResponseEntity.ok(service.updateTransport(id, updateTransportByAdminRequest)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transport by id")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> deleteTransport(
            HttpServletRequest request,
            @PathVariable Long id
    ) {
        return adminService.checkAdmin(request, userId -> {
            service.deleteTransport(id);
            return new ResponseEntity<>(HttpStatus.OK);
        });
    }
}
