package com.github.javakira.simbir.admin.service;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.admin.schema.NewRentAdminRequest;
import com.github.javakira.simbir.admin.schema.RentEndRequest;
import com.github.javakira.simbir.rent.Rent;
import com.github.javakira.simbir.rent.RentRepository;
import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminRentService {
    private final RentRepository repository;
    private final AccountRepository accountRepository;
    private final TransportRepository transportRepository;

    public Optional<Rent> getRent(Long rentId) {
        return repository.findById(rentId);
    }

    public List<Rent> userHistory(Long userId) {
        Optional<Account> account = accountRepository.findById(userId);
        if (account.isEmpty())
            throw new IllegalArgumentException("Account with id %d doesnt exist".formatted(userId));

        return account.get().getRentHistory();
    }

    public List<Rent> transportHistory(Long transportId) {
        Optional<Transport> transport = transportRepository.findById(transportId);
        if (transport.isEmpty())
            throw new IllegalArgumentException("Transport with id %d doesnt exist".formatted(transportId));

        return transport.get().getRentHistory();
    }

    public Rent newRent(NewRentAdminRequest request) {
        Rent rent = Rent
                .builder()
                .transportId(request.getTransportId())
                .timeEnd(LocalDateTime.parse(request.getTimeEnd()))
                .timeStart(LocalDateTime.parse(request.getTimeStart()))
                .rentType(request.getRentType())
                .ownerId(request.getUserId())
                .priceOfUnit(request.getPriceOfUnit())
                .finalPrice(request.getFinalPrice())
                .build();
        repository.save(rent);
        return rent;
    }

    public void endRent(Long id, RentEndRequest rentEndRequest) {
        Optional<Rent> rentOptional = repository.findById(id);
        if (rentOptional.isEmpty())
            throw new IllegalArgumentException("Rent with id %d doesnt exist".formatted(id));

        if (rentOptional.get().getRentState() == Rent.RentState.ended)
            throw new IllegalStateException("Rent already ended");

        Rent rent = rentOptional.get();
        Account account = accountRepository.findById(rent.getId()).orElseThrow();
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
