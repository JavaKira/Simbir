package com.github.javakira.simbir.transport;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransportService {
    private final TransportRepository repository;

    public ResponseEntity<?> addNew(TransportAddRequest request, long ownerId) {
        if (request.isCanBeRented() && request.getMinutePrice() == null && request.getDayPrice() == null)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Transport cannot be available for rent without specifying the rental price. Set 'canBeRented' to false, or set the rental price per minute or per day");


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
        return ResponseEntity.ok(TransportDto.from(transport));
    }

    public ResponseEntity<?> delete(long transportId, long userId) {
        Optional<Transport> transport = repository.findById(transportId);
        if (transport.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Transport with id %d doesnt exist".formatted(transportId));

        if (!transport.get().getOwnerId().equals(userId))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Only owner of transport with id %d can delete him".formatted(transportId));

        repository.delete(transport.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> get(Long id) {
        Optional<Transport> transport = repository.findById(id);
        if (transport.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Transport with id %d doesnt exist".formatted(id));

        return ResponseEntity.ok(TransportDto.from(transport.get()));
    }

    public ResponseEntity<?> update(long id, long userId, TransportUpdateRequest request) {
        Optional<Transport> transportOptional = repository.findById(id);
        if (transportOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Transport with id %d doesnt exist".formatted(id));

        if (!transportOptional.get().getOwnerId().equals(userId))
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only owner of transportOptional with id %d can update him".formatted(id));

        if (request.isCanBeRented() && request.getMinutePrice() == null && request.getDayPrice() == null)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Transport cannot be available for rent without specifying the rental price. Set 'canBeRented' to false, or set the rental price per minute or per day");

        Transport transport = getTransport(request, transportOptional.orElseThrow());
        repository.save(transport);
        return ResponseEntity.ok(TransportDto.from(transport));
    }

    private static Transport getTransport(TransportUpdateRequest request, Transport transport) {
        transport.setCanBeRented(request.isCanBeRented());
        transport.setModel(request.getModel());
        transport.setColor(request.getColor());
        transport.setIdentifier(request.getIdentifier());
        transport.setDescription(request.getDescription());
        transport.setLatitude(request.getLatitude());
        transport.setLongitude(request.getLongitude());
        transport.setMinutePrice(request.getMinutePrice());
        transport.setDayPrice(request.getDayPrice());
        return transport;
    }
}
