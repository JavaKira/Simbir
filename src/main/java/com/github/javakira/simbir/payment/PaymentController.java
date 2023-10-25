package com.github.javakira.simbir.payment;

import com.github.javakira.simbir.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Payment")
public class PaymentController {
    private final JwtService jwtService;
    private final PaymentService paymentService;

    @Operation(summary = "Adds 250 000 money on account with id accountId")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/Hesoyam/{accountId}")
    public ResponseEntity<?> hesoyam(HttpServletRequest request, @PathVariable long accountId) {
        return jwtService.accessUser(request, userId -> paymentService.hesoyam(accountId, userId));
    }
}
