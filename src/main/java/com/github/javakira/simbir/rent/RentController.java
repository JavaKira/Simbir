package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.jwt.JwtService;
import com.github.javakira.simbir.transport.TransportDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Rent")
public class RentController {
    private final RentService service;
    private final JwtService jwtService;

    @Operation(summary = "Получение транспорта доступного для аренды по параметрам")
    @GetMapping("/Transport")
    public List<TransportDto> search(RentSearchParams params) {
        return service.findAvailable(params);
    }

    @Operation(summary = "Получение информации об аренде по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("{rentId}")
    public RentDto rentInfo(@PathVariable Long rentId, HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> service.rentInfo(rentId, userId));
    }

    @Operation(summary = "Получение истории аренд текущего аккаунта")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/MyHistory")
    public List<RentDto> rentHistory(HttpServletRequest request) {
        return jwtService.accessUser(request, service::accountHistory);
    }

    @Operation(summary = "Получение истории аренд транспорта")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/TransportHistory/{transportId}")
    public List<RentDto> transportHistory(@PathVariable Long transportId, HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> service.transportHistory(transportId, userId));
    }

    @Operation(summary = "Аренда транспорта в личное пользование")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/New/{transportId}")
    public RentDto rent(@PathVariable Long transportId, @RequestBody NewRentRequest newRentRequest, HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> service.rent(newRentRequest, transportId, userId));
    }

    @Operation(summary = "Завершение аренды транспорта по id аренды")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/End/{rentId}")
    public RentDto end(@PathVariable Long rentId, @RequestBody RentEndRequest rentEndRequest, HttpServletRequest request) {
        return jwtService.accessUser(request, userId -> service.end(rentId, rentEndRequest, userId));
    }
}
