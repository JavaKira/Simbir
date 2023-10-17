package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentService {
    private final RentRepository repository;
    private final TransportRepository transportRepository;

    public Optional<Rent> get(Long id) {
        return repository.findById(id);
    }

    public Rent rent(RentType rentType, Long transportId, Long accountId) {
        Optional<Transport> transport = transportRepository.findById(transportId);
        if (transport.isEmpty())
            throw new IllegalArgumentException("Transport with id %d doesnt exist".formatted(transportId));

        if (transport.get().getOwnerId().equals(accountId))
            throw new IllegalArgumentException("Trying to rent own transport");

        Rent rent = Rent
                .builder()
                .rentType(rentType)
                .ownerId(accountId)
                .transportId(transportId)
                .build();
        repository.save(rent);
        return rent;
    }

    public void end(Long rentId, RentEndRequest rentEndRequest, Long userId) {
        Optional<Rent> rent = repository.findById(rentId);
        if (rent.isEmpty())
            throw new IllegalArgumentException("Rent with id %d doesnt exist".formatted(rentId));

        if (!rent.get().getOwnerId().equals(userId))
            throw new IllegalArgumentException("Only owner can end rent");

        Transport transport = transportRepository.findById(rent.get().getTransportId()).get();
        transport.setLatitude(rentEndRequest.getLat());
        transport.setLongitude(rentEndRequest.getLongitude());
        transportRepository.save(transport);
        repository.delete(rent.get());
    }
}
