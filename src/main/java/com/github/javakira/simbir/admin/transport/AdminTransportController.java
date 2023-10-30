package com.github.javakira.simbir.admin.transport;

import com.github.javakira.simbir.admin.AdminService;
import com.github.javakira.simbir.transport.TransportDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Admin/Transport")
public class AdminTransportController {
    private final AdminTransportService service;
    private final AdminService adminService;

    @GetMapping
    @Operation(summary = "Получение списка всех транспортных средств")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<Long> transports(HttpServletRequest request, GetTransportsRequest getTransportsRequest) {
        return adminService.checkAdmin(request, userId -> service.transports(getTransportsRequest));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение информации о транспортном средстве по id")
    @SecurityRequirement(name = "Bearer Authentication")
    public TransportDto transportInfo(HttpServletRequest request, @PathVariable Long id) {
        return adminService.checkAdmin(request, userId -> service.transportInfo(id));
    }

    @PostMapping
    @Operation(summary = "Создание нового транспортного средства")
    @SecurityRequirement(name = "Bearer Authentication")
    public TransportDto registerTransport(
            HttpServletRequest request,
            @RequestBody RegisterTransportByAdminRequest registerTransportByAdminRequest
    ) {
        return adminService.checkAdmin(request, userId -> service.registerTransport(registerTransportByAdminRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменение транспортного средства по id")
    @SecurityRequirement(name = "Bearer Authentication")
    public TransportDto updateTransport(
            HttpServletRequest request,
            @RequestBody UpdateTransportByAdminRequest updateTransportByAdminRequest,
            @PathVariable Long id
    ) {
        return adminService.checkAdmin(request, userId -> service.updateTransport(id, updateTransportByAdminRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление транспорта по id")
    @SecurityRequirement(name = "Bearer Authentication")
    public void deleteTransport(
            HttpServletRequest request,
            @PathVariable Long id
    ) {
        adminService.checkAdminVoid(request, userId -> service.deleteTransport(id));
    }
}
