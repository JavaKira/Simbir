package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
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
    private final AccountRepository accountRepository;

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

    public List<Rent> accountHistory(Long userId) {
        Optional<Account> account = accountRepository.findById(userId);
        if (account.isEmpty())
            throw new IllegalArgumentException("Account with id %d doesnt exist".formatted(userId));

        return account.get().getRentHistory();
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

        if (rent.get().getRentState() == Rent.RentState.ended)
            throw new IllegalStateException("Rent already ended");

        Account account = accountRepository.findById(userId).get();
        Transport transport = transportRepository.findById(rent.get().getTransportId()).get();
        transport.setLatitude(rentEndRequest.getLat());
        transport.setLongitude(rentEndRequest.getLongitude());
        transport.getRentHistory().add(rent.get());
        account.getRentHistory().add(rent.get());
        rent.get().setRentState(Rent.RentState.ended);
        transportRepository.save(transport);
        repository.save(rent.get());
        accountRepository.save(account);
    }
}
