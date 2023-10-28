package com.github.javakira.simbir.admin.rent;

import com.github.javakira.simbir.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Admin")
public class AdminRentController {
    private final AdminRentService service;
    private final AdminService adminService;

    @Operation(summary = "Получение информации по аренде по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Rent/{rentId}")
    public ResponseEntity<?> rentInfo(@PathVariable long rentId, HttpServletRequest request) {
        return adminService.checkAdmin(request, userId -> service.getRent(rentId));
    }

    @Operation(summary = "Получение истории аренд пользователя с id={userId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/UserHistory/{userId}")
    public ResponseEntity<?> userHistory(@PathVariable long userId, HttpServletRequest request) {
        return adminService.checkAdmin(request, currentUserId -> service.userHistory(userId));
    }

    @Operation(summary = "Получение истории аренд транспорта с id={transportId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/TransportHistory/{transportId}")
    public ResponseEntity<?> transportHistory(@PathVariable long transportId, HttpServletRequest request) {
        return adminService.checkAdmin(request, currentUserId -> service.transportHistory(transportId));
    }

    @Operation(summary = "Создание новой аренды")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/Rent")
    public ResponseEntity<?> newRent(HttpServletRequest request, @RequestBody NewRentAdminRequest newRentAdminRequest) {
        return adminService.checkAdmin(request, currentUserId -> service.newRent(newRentAdminRequest));
    }

    @Operation(summary = "Завершение аренды транспорта по id аренды")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/Rent/End/{id}")
    public ResponseEntity<?> endRent(HttpServletRequest request, @RequestBody RentEndRequest rentEndRequest, @PathVariable long id) {
        return adminService.checkAdmin(request, currentUserId -> service.endRent(id, rentEndRequest));
    }

    @Operation(summary = "Изменение записи об аренде по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/Rent/{id}")
    public ResponseEntity<?> update(HttpServletRequest request, @RequestBody UpdateRentAdminRequest updateRentAdminRequest, @PathVariable long id) {
        return adminService.checkAdmin(request, userId -> service.update(updateRentAdminRequest, id));
    }

    @Operation(summary = "Удаление информации об аренде по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/Rent/{id}")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable long id) {
        return adminService.checkAdmin(request, userId -> service.delete(id));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleException(DateTimeParseException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.toString());
    }
}
