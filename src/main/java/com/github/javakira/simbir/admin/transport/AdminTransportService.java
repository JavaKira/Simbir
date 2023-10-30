package com.github.javakira.simbir.admin.transport;

import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportDto;
import com.github.javakira.simbir.transport.TransportRepository;
import com.github.javakira.simbir.transport.TransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class AdminTransportService {
    private final TransportService service;
    private final TransportRepository repository;

    public List<Long> transports(GetTransportsRequest request) {
        if (request.getStart() < 0 || request.getCount() < 0)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'start' and 'count' must be > 0"
            );

        List<Transport> transports = repository.findAll();
        transports = transports
                .stream()
                .filter(transport -> request.getSearchTransportType().fits(transport.getTransportType()))
                .toList();
        return transports
                .subList(Math.min(request.getStart(), transports.size()), Math.min(request.getStart() + request.getCount(), transports.size()))
                .stream()
                .map(Transport::getId)
                .toList();
    }

    public TransportDto transportInfo(long id) {
        return TransportDto.from(service.transport(id));
    }

    public TransportDto registerTransport(RegisterTransportByAdminRequest request) {
        Transport transport = Transport
                .builder()
                .ownerId(request.getOwnerId())
                .dayPrice(request.getDayPrice())
                .minutePrice(request.getMinutePrice())
                .description(request.getDescription())
                .canBeRented(request.getCanBeRented())
                .model(request.getModel())
                .identifier(request.getIdentifier())
                .color(request.getColor())
                .transportType(request.getTransportType())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        repository.save(transport);
        return TransportDto.from(transport);
    }

    public TransportDto updateTransport(long id, UpdateTransportByAdminRequest request) {
        Transport old = service.transport(id);

        Transport transport = Transport
                .builder()
                .ownerId(request.getOwnerId())
                .dayPrice(request.getDayPrice())
                .minutePrice(request.getMinutePrice())
                .description(request.getDescription())
                .canBeRented(request.getCanBeRented())
                .model(request.getModel())
                .identifier(request.getIdentifier())
                .color(request.getColor())
                .transportType(request.getTransportType())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .rentHistory(old.getRentHistory())
                .id(old.getId())
                .build();
        repository.save(transport);
        return TransportDto.from(transport);
    }

    public void deleteTransport(long id) {
        repository.delete(service.transport(id));
    }

    public void deleteTransportByOwner(long ownerId) {
        repository.deleteAll(
                repository
                .findAll()
                .stream()
                .filter(transport -> transport.getOwnerId().equals(ownerId))
                .toList()
        );
    }
}
