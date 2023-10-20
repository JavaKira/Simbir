package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.jwt.JwtService;
import com.github.javakira.simbir.transport.Transport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Rent")
public class RentController {
    private final RentService service;
    private final JwtService jwtService;

    //todo написать алгоритм поиска
    @Operation(summary = "Get available transport by params")
    @GetMapping("/Transport")
    public ResponseEntity<List<Transport>> search(@RequestBody RentSearchParams params) {
        return ResponseEntity.ok(null);
    }
    //todo ограничения: Только арендатор и владелец транспорта
    @Operation(summary = "Get info about rent by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("{rentId}")
    public ResponseEntity<?> rentInfo(@PathVariable Long rentId) {
        Optional<Rent> optionalRent = service.get(rentId);
        if (optionalRent.isPresent()) {
            return ResponseEntity.ok(optionalRent.get());
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get history of current user")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/MyHistory")
    public ResponseEntity<?> rentHistory(HttpServletRequest request) {
        try {
            Optional<String> jwt = jwtService.token(request);
            if (jwt.isPresent()) {
                Long userId = jwtService.extractId(jwt.get());
                return ResponseEntity.ok(service.accountHistory(userId));
            } else
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get rent history of transport by transport id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/TransportHistory/{transportId}")
    public ResponseEntity<?> transportHistory(@PathVariable Long transportId, HttpServletRequest request) {
        try {
            Optional<String> jwt = jwtService.token(request);
            if (jwt.isPresent()) {
                Long userId = jwtService.extractId(jwt.get());
                return ResponseEntity.ok(service.transportHistory(transportId, userId));
            } else
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Add new rent for current user")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/New/{transportId}")
    public ResponseEntity<?> rent(@PathVariable Long transportId, @RequestBody NewRentRequest newRentRequest, HttpServletRequest request) {
        try {
            Optional<String> jwt = jwtService.token(request);
            if (jwt.isPresent()) {
                Long userId = jwtService.extractId(jwt.get());
                return ResponseEntity.ok(service.rent(newRentRequest.getRentType(), transportId, userId));
            } else
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "End rent by rent id")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/End/{rentId}")
    public ResponseEntity<?> end(@PathVariable Long rentId, @RequestBody RentEndRequest rentEndRequest, HttpServletRequest request) {
        try {
            Optional<String> jwt = jwtService.token(request);
            if (jwt.isPresent()) {
                Long userId = jwtService.extractId(jwt.get());
                service.end(rentId, rentEndRequest, userId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }
}
