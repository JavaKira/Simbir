package com.github.javakira.simbir.admin;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.Role;
import com.github.javakira.simbir.jwt.JwtService;
import com.github.javakira.simbir.rent.Rent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.function.Function;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Admin")
public class AdminRentController {
    private final AdminRentService service;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;

    @Operation(summary = "Get rent data")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Rent/{rentId}")
    public ResponseEntity<?> rentInfo(@PathVariable Long rentId, HttpServletRequest request) {
        return checkAdmin(request, userId -> {
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
        return checkAdmin(request, currentUserId -> ResponseEntity.ok().body(service.userHistory(userId)));
    }

    @Operation(summary = "Get transport history of rents by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/TransportHistory/{transportId}")
    public ResponseEntity<?> transportHistory(@PathVariable Long transportId, HttpServletRequest request) {
        return checkAdmin(request, currentUserId -> ResponseEntity.ok().body(service.transportHistory(transportId)));
    }

    public ResponseEntity<?> checkAdmin(HttpServletRequest request, Function<Long, ResponseEntity<?>> adminConsumer) {
        Optional<String> jwt = jwtService.token(request);
        if (jwt.isPresent()) {
            Long userId = jwtService.extractId(jwt.get());
            //Role role = jwtService.extractRole(jwt.get());
            Role role = accountRepository.findById(userId).get().getRole();
            if (role != Role.admin)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can use this endpoint");

            try {
                return adminConsumer.apply(userId);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
