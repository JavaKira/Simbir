package com.github.javakira.simbir.admin.controller;

import com.github.javakira.simbir.admin.schema.NewRentAdminRequest;
import com.github.javakira.simbir.admin.schema.RentEndRequest;
import com.github.javakira.simbir.admin.service.AdminRentService;
import com.github.javakira.simbir.admin.service.AdminService;
import com.github.javakira.simbir.rent.Rent;
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
@RequestMapping("/api/Admin")
public class AdminRentController {
    private final AdminRentService service;
    private final AdminService adminService;

    @Operation(summary = "Get rent data")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Rent/{rentId}")
    public ResponseEntity<?> rentInfo(@PathVariable long rentId, HttpServletRequest request) {
        return adminService.checkAdmin(request, userId -> service.getRent(rentId));
    }

    @Operation(summary = "Get user history of rents by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/UserHistory/{userId}")
    public ResponseEntity<?> userHistory(@PathVariable long userId, HttpServletRequest request) {
        return adminService.checkAdmin(request, currentUserId -> service.userHistory(userId));
    }

    @Operation(summary = "Get transport history of rents by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/TransportHistory/{transportId}")
    public ResponseEntity<?> transportHistory(@PathVariable long transportId, HttpServletRequest request) {
        return adminService.checkAdmin(request, currentUserId -> service.transportHistory(transportId));
    }

    @Operation(summary = "Create new rent")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/Rent")
    public ResponseEntity<?> newRent(HttpServletRequest request, NewRentAdminRequest newRentAdminRequest) {
        return adminService.checkAdmin(request, currentUserId -> service.newRent(newRentAdminRequest));
    }

    @Operation(summary = "End rent by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/Rent/End/{id}")
    public ResponseEntity<?> endRent(HttpServletRequest request, RentEndRequest rentEndRequest, @PathVariable long id) {
        return adminService.checkAdmin(request, currentUserId -> service.endRent(id, rentEndRequest));
    }
}
