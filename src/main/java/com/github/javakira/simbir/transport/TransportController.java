package com.github.javakira.simbir.transport;

import com.github.javakira.simbir.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtService jwtService;

    @Operation(summary = "Add new transport")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<?> addNew(@RequestBody TransportAddRequest transportAddRequest, HttpServletRequest request) {
        //todo если запрос сделан неверно, то все по пизде идёт
        Optional<String> jwt = jwtService.token(request);
        if (jwt.isPresent()) {
            Long id = jwtService.extractId(jwt.get());
            service.addNew(transportAddRequest, id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Operation(summary = "Get transport data by id")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Transport> transport = service.get(id);

        if (transport.isPresent())
            return ResponseEntity.ok(transport.get());
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Delete transport by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Optional<Transport> optional = service.get(id);
        if (optional.isPresent()) {
            Optional<String> jwt = jwtService.token(request);
            if (jwt.isPresent()) {
                Transport transport = optional.get();
                Long userId = jwtService.extractId(jwt.get());
                if (userId.equals(transport.getOwnerId())) {
                    service.remove(optional.get());
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Operation(summary = "Update transport data")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody TransportUpdateRequest transportUpdateRequest, HttpServletRequest request) {
        Optional<Transport> optional = service.get(id);
        if (optional.isPresent()) {
            Optional<String> jwt = jwtService.token(request);
            if (jwt.isPresent()) {
                Transport transport = optional.get();
                Long userId = jwtService.extractId(jwt.get());
                if (userId.equals(transport.getOwnerId())) {
                    service.update(id, transportUpdateRequest);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
