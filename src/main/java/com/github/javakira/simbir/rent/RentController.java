package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.jwt.JwtService;
import com.github.javakira.simbir.transport.Transport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Rent")
public class RentController {
    private final RentService service;
    private final JwtService jwtService;

    @Operation(summary = "Get available transport by params")
    @GetMapping("/Transport")
    public ResponseEntity<List<Transport>> search(RentSearchParams params) {
        return ResponseEntity.ok(service.findAvailable(params));
    }

    @Operation(summary = "Get info about rent by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("{rentId}")
    public ResponseEntity<?> rentInfo(@PathVariable Long rentId, HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> service.rentInfo(rentId, userId));
    }

    @Operation(summary = "Get history of current user")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/MyHistory")
    public ResponseEntity<?> rentHistory(HttpServletRequest request) {
        return jwtService.accessUser(request, service::accountHistory);
    }

    @Operation(summary = "Get rent history of transport by transport id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/TransportHistory/{transportId}")
    public ResponseEntity<?> transportHistory(@PathVariable Long transportId, HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> service.transportHistory(transportId, userId));
    }

    @Operation(summary = "Add new rent for current user")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/New/{transportId}")
    public ResponseEntity<?> rent(@PathVariable Long transportId, @RequestBody NewRentRequest newRentRequest, HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> service.rent(newRentRequest, transportId, userId));
    }

    @Operation(summary = "End rent by rent id")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/End/{rentId}")
    public ResponseEntity<?> end(@PathVariable Long rentId, @RequestBody RentEndRequest rentEndRequest, HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> service.end(rentId, rentEndRequest, userId));
    }
}
