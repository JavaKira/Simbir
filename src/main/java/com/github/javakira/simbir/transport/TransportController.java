package com.github.javakira.simbir.transport;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public ResponseEntity<?> getTransport(@PathVariable Long id) {
        Optional<Transport> transport = service.get(id);

        if (transport.isPresent())
            return ResponseEntity.ok(transport.get());
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
