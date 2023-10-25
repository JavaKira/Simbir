package com.github.javakira.simbir.admin.transport;

import com.github.javakira.simbir.admin.transport.GetTransportsRequest;
import com.github.javakira.simbir.admin.transport.RegisterTransportByAdminRequest;
import com.github.javakira.simbir.admin.transport.UpdateTransportByAdminRequest;
import com.github.javakira.simbir.admin.AdminService;
import com.github.javakira.simbir.admin.transport.AdminTransportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Admin/Transport")
public class AdminTransportController {
    private final AdminTransportService service;
    private final AdminService adminService;

    //TODO test
    @GetMapping
    @Operation(summary = "Get list of all transport")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> transports(HttpServletRequest request, GetTransportsRequest getTransportsRequest) {
        return adminService.checkAdmin(request, userId -> service.transports(getTransportsRequest));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get info about transport by id")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> transportId(HttpServletRequest request, @PathVariable Long id) {
        return adminService.checkAdmin(request, userId -> service.transportInfo(id));
    }

    @PostMapping
    @Operation(summary = "Create new transport")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> registerTransport(
            HttpServletRequest request,
            @RequestBody RegisterTransportByAdminRequest registerTransportByAdminRequest
    ) {
        return adminService.checkAdmin(request, userId -> service.registerTransport(registerTransportByAdminRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit transport by id")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> updateTransport(
            HttpServletRequest request,
            @RequestBody UpdateTransportByAdminRequest updateTransportByAdminRequest,
            @PathVariable Long id
    ) {
        return adminService.checkAdmin(request, userId -> service.updateTransport(id, updateTransportByAdminRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transport by id")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> deleteTransport(
            HttpServletRequest request,
            @PathVariable Long id
    ) {
        return adminService.checkAdmin(request, userId -> service.deleteTransport(id));
    }
}
