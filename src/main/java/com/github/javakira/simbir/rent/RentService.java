package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.account.Account;
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

        //todo использование ownerUsername
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
}
