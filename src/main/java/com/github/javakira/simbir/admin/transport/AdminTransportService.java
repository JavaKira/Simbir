package com.github.javakira.simbir.admin.transport;

import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportDto;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class AdminTransportService {
    private final TransportRepository repository;

    public ResponseEntity<?> transports(GetTransportsRequest request) {
        if (request.getStart() < 0 || request.getCount() < 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("'start' and 'count' must be > 0");

        List<Transport> transports = repository.findAll();
        transports = transports
                .stream()
                .filter(transport -> request.getSearchTransportType().fits(transport.getTransportType()))
                .toList();
        return ResponseEntity.ok(transports
                .subList(Math.min(request.getStart(), transports.size()), Math.min(request.getStart() + request.getCount(), transports.size()))
                .stream()
                .map(Transport::getId)
                .toList()
        );
    }

    public ResponseEntity<?> transportInfo(long id) {
        Optional<Transport> transport = repository.findById(id);
        if (transport.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Transport with id %d doesnt exist".formatted(id));

        return ResponseEntity.ok(TransportDto.from(transport.get()));
    }

    public ResponseEntity<TransportDto> registerTransport(RegisterTransportByAdminRequest request) {
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
        return ResponseEntity.ok(TransportDto.from(transport));
    }

    public ResponseEntity<?> updateTransport(long id, UpdateTransportByAdminRequest request) {
        Optional<Transport> old = repository.findById(id);
        if (old.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Transport with id %d doesnt exist".formatted(id));

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
                .rentHistory(old.get().getRentHistory())
                .id(old.get().getId())
                .build();
        repository.save(transport);
        return ResponseEntity.ok(TransportDto.from(transport));
    }

    public ResponseEntity<?> deleteTransport(long id) {
        Optional<Transport> transport = repository.findById(id);
        if (transport.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Transport with id %d doesnt exist".formatted(id));

        repository.delete(transport.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}