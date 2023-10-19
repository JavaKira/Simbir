package com.github.javakira.simbir.payment;

import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Payment")
public class PaymentController {
    private final JwtService jwtService;
    private final PaymentService paymentService;

    //todo потом отсаётся поебаться со списыванием
    //todo метод сервиса можно переименовать на hesoyam, это будет иметь смысл
    @Operation(summary = "Adds 250 000 money on account with id accountId")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/Hesoyam/{accountId}")
    public ResponseEntity<?> addMoney(HttpServletRequest request, @PathVariable Long accountId) {
        try {
            Optional<String> jwt = jwtService.token(request);
            if (jwt.isPresent()) {
                Long userId = jwtService.extractId(jwt.get());
                paymentService.addMoney(accountId, userId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
