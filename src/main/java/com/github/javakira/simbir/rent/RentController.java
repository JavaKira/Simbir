package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.transport.Transport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Rent")
public class RentController {
    //todo добавить в вайтлист, тк доступен всем
    @GetMapping("/Transport")
    public ResponseEntity<Transport> search(@RequestBody RentSearchParams params) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("{rentId}")
    public ResponseEntity<Rent> rentInfo(@PathVariable Long rentId) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/MyHistory")
    public ResponseEntity<List<Rent>> rentHistory() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/TransportHistory/{transportId}")
    public ResponseEntity<?> transportHistory(@PathVariable Long transportId) {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/New/{transportId}")
    public ResponseEntity<?> rent(@PathVariable Long transportId, @RequestBody NewRentRequest request) {
        return ResponseEntity.ok(null);
    }

    //todo ограничения: только человек который создавал эту аренду
    @PostMapping("/End/{rentId}")
    public ResponseEntity<?> end(@PathVariable Long rentId, @RequestBody NewRentRequest request) {
        return ResponseEntity.ok(null);
    }
}
