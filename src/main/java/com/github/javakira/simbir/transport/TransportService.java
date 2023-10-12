package com.github.javakira.simbir.transport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransportService {
    private final TransportRepository repository;

    public void addNew(TransportAddRequest request) {
        Transport transport = Transport
                .builder()
                .canBeRented(request.isCanBeRented())
                .transportType(request.getTransportType())
                .model(request.getModel())
                .color(request.getColor())
                .identifier(request.getIdentifier())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .minutePrice(request.getMinutePrice())
                .dayPrice(request.getDayPrice())
                .build();
        repository.save(transport);
    }

    public Optional<Transport> get(Long id) {
        return repository.findById(id);
    }

    public void update(Long id, TransportUpdateRequest request) {
        Optional<Transport> optional = get(id);
        if (optional.isEmpty())
            return;

        Transport oldTransport = optional.get();
        Transport transport = Transport
                .builder()
                .id(id)
                .canBeRented(request.isCanBeRented())
                .transportType(oldTransport.getTransportType())
                .model(request.getModel())
                .color(request.getColor())
                .identifier(request.getIdentifier())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .minutePrice(request.getMinutePrice())
                .dayPrice(request.getDayPrice())
                .build();
        repository.save(transport);
    }
}
