package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentService {
    private final RentRepository repository;
    private final TransportRepository transportRepository;

    public Optional<Rent> get(Long id) {
        return repository.findById(id);
    }

    public List<Rent> transportHistory(Long transportId, Long userId) {
        Optional<Transport> transport = transportRepository.findById(transportId);
        if (transport.isEmpty())
            throw new IllegalArgumentException("Transport with id %d doesnt exist".formatted(transportId));

        if (transport.get().getOwnerId().equals(userId))
            throw new IllegalArgumentException("Only owner of transport %d can get history");

        return transport.get().getRentHistory();
    }


    //todo ставить canBeRented на false. Если он и так false, то аренда невозможна
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

    //todo ставить canBeRented на true
    public void end(Long rentId, RentEndRequest rentEndRequest, Long userId) {
        Optional<Rent> rent = repository.findById(rentId);
        if (rent.isEmpty())
            throw new IllegalArgumentException("Rent with id %d doesnt exist".formatted(rentId));

        if (!rent.get().getOwnerId().equals(userId))
            throw new IllegalArgumentException("Only rent owner can end rent");

        Transport transport = transportRepository.findById(rent.get().getTransportId()).get();
        transport.setLatitude(rentEndRequest.getLat());
        transport.setLongitude(rentEndRequest.getLongitude());
        transport.getRentHistory().add(rent.get());
        rent.get().setRentState(Rent.RentState.closed);
        transportRepository.save(transport);
        repository.save(rent.get());
    }
}
