package com.github.javakira.simbir.transport;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Transport")
public class TransportController {
    @PostMapping
    public ResponseEntity<String> addNewTransport() {
        return ResponseEntity.ok("all ok");
    }
}
