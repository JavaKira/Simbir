package com.github.javakira.simbir.transport;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransportService {
    private final TransportRepository repository;

    public TransportDto addNew(TransportAddRequest request, long ownerId) {
        if (request.isCanBeRented() && request.getMinutePrice() == null && request.getDayPrice() == null)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transport cannot be available for rent without specifying the rental price. Set 'canBeRented' to false, or set the rental price per minute or per day"
            );

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
        return TransportDto.from(transport);
    }

    public void delete(long transportId, long userId) {
        Transport transport = transport(transportId);

        if (!transport.getOwnerId().equals(userId))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only owner of transport with id %d can delete him".formatted(transportId)
            );

        repository.delete(transport);
    }

    public TransportDto transportInfo(long id) {
        return TransportDto.from(transport(id));
    }

    public TransportDto update(long id, long userId, TransportUpdateRequest request) {
        Transport transport = transport(id);

        if (!transport.getOwnerId().equals(userId))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only owner of transport with id %d can update him".formatted(id)
            );

        if (request.isCanBeRented() && request.getMinutePrice() == null && request.getDayPrice() == null)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transport cannot be available for rent without specifying the rental price. Set 'canBeRented' to false, or set the rental price per minute or per day"
            );

        updateTransport(request, transport);
        repository.save(transport);
        return TransportDto.from(transport);
    }

    //Intellij idea idea
    private static void updateTransport(TransportUpdateRequest request, Transport transport) {
        transport.setCanBeRented(request.isCanBeRented());
        transport.setModel(request.getModel());
        transport.setColor(request.getColor());
        transport.setIdentifier(request.getIdentifier());
        transport.setDescription(request.getDescription());
        transport.setLatitude(request.getLatitude());
        transport.setLongitude(request.getLongitude());
        transport.setMinutePrice(request.getMinutePrice());
        transport.setDayPrice(request.getDayPrice());
    }

    public Transport transport(long id) {
        Optional<Transport> transportOptional = repository.findById(id);
        if (transportOptional.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Transport with id %d doesnt exist".formatted(id)
            );

        return transportOptional.get();
    }
}
