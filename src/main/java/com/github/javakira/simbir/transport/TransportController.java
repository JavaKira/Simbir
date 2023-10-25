package com.github.javakira.simbir.transport;

import com.github.javakira.simbir.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
        return jwtService.accessUser(request, userId -> service.addNew(transportAddRequest, userId));
    }

    @Operation(summary = "Get transport data by id")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable long id) {
        return service.get(id);
    }

    //todo refactor
    @Operation(summary = "Delete transport by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id, HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> service.delete(id, userId));
    }

    @Operation(summary = "Update transport data")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody TransportUpdateRequest transportUpdateRequest, HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> service.update(id, userId, transportUpdateRequest));
    }
}
