package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        if (!transport.get().getOwnerId().equals(userId))
            throw new IllegalArgumentException("Only owner of transport %d can get rent history".formatted(transportId));

        return transport.get().getRentHistory();
    }

    public List<Rent> accountHistory(Long userId) {
        Optional<Account> account = accountRepository.findById(userId);
        if (account.isEmpty())
            throw new IllegalArgumentException("Account with id %d doesnt exist".formatted(userId));

        return account.get().getRentHistory();
    }


    //todo ставить canBeRented на false. Если он и так false, то аренда невозможна
    public Rent rent(Rent.RentType rentType, Long transportId, Long accountId) {
        Optional<Transport> transport = transportRepository.findById(transportId);
        if (transport.isEmpty())
            throw new IllegalArgumentException("Transport with id %d doesnt exist".formatted(transportId));

        if (transport.get().getOwnerId().equals(accountId))
            throw new IllegalArgumentException("Trying to rent own transport");

        double unitPrice = 0;
        //todo добавить проверки
        if (rentType == Rent.RentType.Minutes)
            unitPrice = transport.get().getMinutePrice();
        else if (rentType == Rent.RentType.Days)
            unitPrice = transport.get().getDayPrice();

        Rent rent = Rent
                .builder()
                .rentType(rentType)
                .ownerId(accountId)
                .rentState(Rent.RentState.opened)
                .timeStart(LocalDateTime.now())
                .priceOfUnit(unitPrice)
                .transportId(transportId)
                .build();
        repository.save(rent);
        return rent;
    }

    //todo ставить canBeRented на true
    public void end(Long rentId, RentEndRequest rentEndRequest, Long userId) {
        Optional<Rent> rentOptional = repository.findById(rentId);
        if (rentOptional.isEmpty())
            throw new IllegalArgumentException("Rent with id %d doesnt exist".formatted(rentId));

        if (!rentOptional.get().getOwnerId().equals(userId))
            throw new IllegalArgumentException("Only rent owner can end rent");

        if (rentOptional.get().getRentState() == Rent.RentState.ended)
            throw new IllegalStateException("Rent already ended");

        Rent rent = rentOptional.get();
        Account account = accountRepository.findById(rent.getOwnerId()).orElseThrow();
        Transport transport = transportRepository.findById(rentOptional.get().getTransportId()).orElseThrow();
        //Updating Transport location to rent end location
        transport.setLongitude(rentEndRequest.getLongitude());
        transport.setLatitude(rentEndRequest.getLat());
        //Closing rent
        rent.setRentState(Rent.RentState.ended);
        rent.setTimeEnd(LocalDateTime.now());
        rent.setFinalPrice(rent.getRentType().price(rent));
        //Taking off money
        account.setMoney(account.getMoney() - rent.getFinalPrice()); //todo стоит задуматься об длинной арифметике для счета денег
        //Adding rent to rentHistory of account and transport
        account.getRentHistory().add(rent);
        transport.getRentHistory().add(rent);
        //Saving entities to repositories
        repository.save(rent);
        accountRepository.save(account);
        transportRepository.save(transport);
    }
}
