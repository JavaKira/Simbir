package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.jwt.JwtService;
import com.github.javakira.simbir.transport.Transport;
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
    @GetMapping("/Transport")
    public ResponseEntity<List<Transport>> search(@RequestBody RentSearchParams params) {
        return ResponseEntity.ok(null);
    }
    //todo ограничения: Только арендатор и владелец транспорта
    @GetMapping("{rentId}")
    public ResponseEntity<?> rentInfo(@PathVariable Long rentId) {
        Optional<Rent> optionalRent = service.get(rentId);
        if (optionalRent.isPresent()) {
            return ResponseEntity.ok(optionalRent.get());
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    //todo добавить в Account историю аренд
    @GetMapping("/MyHistory")
    public ResponseEntity<List<Rent>> rentHistory() {
        return ResponseEntity.ok(null);
    }
    //todo граничения: Только владелец этого транспорта
    //todo добавить в Transport историю аренд
    @GetMapping("/TransportHistory/{transportId}")
    public ResponseEntity<?> transportHistory(@PathVariable Long transportId) {
        return ResponseEntity.ok(null);
    }
    //todo Только авторизованные пользователи, нельзя брать в аренду собственный транспорт
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
            return ResponseEntity.badRequest().body(e);
        }
    }

    //todo ограничения: только человек который создавал эту аренду. У Rent должен быть Account owner
    @PostMapping("/End/{rentId}")
    public ResponseEntity<?> end(@PathVariable Long rentId, @RequestBody RentEndRequest request) {
        return ResponseEntity.ok(null);
    }
}
