package com.github.javakira.simbir.transport;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Transport")
public class TransportController {
    private final TransportService service;

    @PostMapping
    public ResponseEntity<?> addNewTransport(@RequestBody TransportAddRequest request) {
        //todo если запрос сделан неверно, то все по пизде идёт
        service.addNewTransport(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getTransport(@PathVariable Long id) {
        return ResponseEntity.ok("all ok");
    }
}
