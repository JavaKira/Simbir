package com.github.javakira.simbir.transport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransportService {
    private final TransportRepository repository;

    public void addNewTransport(TransportAddRequest request) {
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
}
