package com.github.javakira.simbir.admin.service;

import com.github.javakira.simbir.admin.schema.GetTransportsRequest;
import com.github.javakira.simbir.admin.schema.RegisterTransportByAdminRequest;
import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class AdminTransportService {
    private final TransportRepository repository;

    public List<Long> transports(GetTransportsRequest request) {
        //todo accountRepository.get(request)
        List<Transport> transports = repository.findAll();
        transports = transports
                .stream()
                .filter(transport -> request.getSearchTransportType().fits(transport.getTransportType()))
                .toList();
        return transports
                .subList(request.getStart(), Math.max(request.getStart() + request.getCount(), transports.size()))
                .stream()
                .map(Transport::getId)
                .toList();
    }

    public Optional<Transport> transportInfo(Long id) {
        return repository.findById(id);
    }

    public Transport registerTransport(RegisterTransportByAdminRequest request) {
        Transport transport = Transport
                .builder()
                .ownerId(request.getOwnerId())
                .dayPrice(request.getDayPrice())
                .minutePrice(request.getMinutePrice())
                .description(request.getDescription())
                .canBeRented(request.isCanBeRented())
                .model(request.getModel())
                .identifier(request.getIdentifier())
                .color(request.getColor())
                .transportType(request.getTransportType())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        repository.save(transport);
        return transport;
    }
}
