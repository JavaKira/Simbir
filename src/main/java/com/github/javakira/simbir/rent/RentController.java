package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.transport.Transport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Rent")
public class RentController {
    //todo написать алгоритм поиска
    @GetMapping("/Transport")
    public ResponseEntity<Transport> search(@RequestBody RentSearchParams params) {
        return ResponseEntity.ok(null);
    }
    //todo ограничения: Только арендатор и владелец транспорта
    //todo написать класс Rent и сделать его репозиторий. у Rent должен быть RentType и тд фигня
    @GetMapping("{rentId}")
    public ResponseEntity<Rent> rentInfo(@PathVariable Long rentId) {
        return ResponseEntity.ok(null);
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
    public ResponseEntity<?> rent(@PathVariable Long transportId, @RequestBody NewRentRequest request) {
        return ResponseEntity.ok(null);
    }

    //todo ограничения: только человек который создавал эту аренду. У Rent должен быть Account owner
    @PostMapping("/End/{rentId}")
    public ResponseEntity<?> end(@PathVariable Long rentId, @RequestBody RentEndRequest request) {
        return ResponseEntity.ok(null);
    }
}
