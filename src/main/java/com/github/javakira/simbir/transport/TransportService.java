package com.github.javakira.simbir.transport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransportService {
    private final TransportRepository repository;

    public void addNew(TransportAddRequest request, Long ownerId) {
        Transport transport = Transport
                .builder()
                .ownerId(ownerId)
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

    public void delete(long transportId, long userId) {
        Optional<Transport> transport = repository.findById(transportId);
        if (transport.isEmpty())
            throw new IllegalArgumentException("Transport with id %d doesnt exist".formatted(transportId));

        if (!transport.get().getOwnerId().equals(userId))
            throw new IllegalArgumentException("Only owner of transport with id %d can delete him".formatted(transportId));

        delete(transport.get());
    }

    public void delete(Transport transport) {
        repository.delete(transport);
    }

    public Optional<Transport> get(Long id) {
        return repository.findById(id);
    }

    public void update(long id, long userId, TransportUpdateRequest request) {
        Optional<Transport> transportOptional = repository.findById(id);
        if (transportOptional.isEmpty())
            throw new IllegalArgumentException("Transport with id %d doesnt exist".formatted(id));

        //todo такая проблема, что возращаемый код не получится поставить на FORBIDDEN либо NOT_FOUND, всегда будет BAD_REQUEST при ошибке
        if (!transportOptional.get().getOwnerId().equals(userId))
            throw new IllegalArgumentException("Only owner of transportOptional with id %d can update him".formatted(id));

        Transport transport = transportOptional.get();
        transport.setCanBeRented(request.isCanBeRented());
        transport.setModel(request.getModel());
        transport.setColor(request.getColor());
        transport.setIdentifier(request.getIdentifier());
        transport.setDescription(request.getDescription());
        transport.setLatitude(request.getLatitude());
        transport.setLongitude(request.getLongitude());
        transport.setMinutePrice(request.getMinutePrice());
        transport.setDayPrice(request.getDayPrice());
        repository.save(transport);
    }
}
