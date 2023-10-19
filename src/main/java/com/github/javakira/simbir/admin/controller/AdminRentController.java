package com.github.javakira.simbir.admin.controller;

import com.github.javakira.simbir.admin.service.AdminRentService;
import com.github.javakira.simbir.admin.service.AdminService;
import com.github.javakira.simbir.rent.Rent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Admin")
public class AdminRentController {
    private final AdminRentService service;
    private final AdminService adminService;

    @Operation(summary = "Get rent data")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Rent/{rentId}")
    public ResponseEntity<?> rentInfo(@PathVariable Long rentId, HttpServletRequest request) {
        return adminService.checkAdmin(request, userId -> {
            Optional<Rent> rentOptional = service.getRent(rentId);
            if (rentOptional.isEmpty())
                return ResponseEntity.badRequest().body("Rent with id %d doesnt exist".formatted(rentId));

            return ResponseEntity.ok().body(rentOptional.get());
        });
    }

    @Operation(summary = "Get user history of rents by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/UserHistory/{userId}")
    public ResponseEntity<?> userHistory(@PathVariable Long userId, HttpServletRequest request) {
        return adminService.checkAdmin(request, currentUserId -> ResponseEntity.ok().body(service.userHistory(userId)));
    }

    @Operation(summary = "Get transport history of rents by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/TransportHistory/{transportId}")
    public ResponseEntity<?> transportHistory(@PathVariable Long transportId, HttpServletRequest request) {
        return adminService.checkAdmin(request, currentUserId -> ResponseEntity.ok().body(service.transportHistory(transportId)));
    }
}
