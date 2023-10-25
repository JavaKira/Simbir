package com.github.javakira.simbir.admin.rent;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.admin.rent.NewRentAdminRequest;
import com.github.javakira.simbir.admin.rent.RentEndRequest;
import com.github.javakira.simbir.rent.Rent;
import com.github.javakira.simbir.rent.RentRepository;
import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminRentService {
    private final RentRepository repository;
    private final AccountRepository accountRepository;
    private final TransportRepository transportRepository;

    public ResponseEntity<?> getRent(long rentId) {
        Optional<Rent> rentOptional = repository.findById(rentId);
        if (rentOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Rent with id %d doesnt exist".formatted(rentId));

        return ResponseEntity.ok(rentOptional.get());
    }

    public ResponseEntity<?> userHistory(long userId) {
        Optional<Account> account = accountRepository.findById(userId);
        if (account.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Account with id %d doesnt exist".formatted(userId));

        return ResponseEntity.ok(account.get().getRentHistory());
    }

    public ResponseEntity<?> transportHistory(Long transportId) {
        Optional<Transport> transport = transportRepository.findById(transportId);
        if (transport.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Transport with id %d doesnt exist".formatted(transportId));

        return ResponseEntity.ok(transport.get().getRentHistory());
    }

    public ResponseEntity<Rent> newRent(NewRentAdminRequest request) {
        Rent rent = Rent
                .builder()
                .rentState(Rent.RentState.opened)
                .transportId(request.getTransportId())
                .timeEnd(LocalDateTime.parse(request.getTimeEnd()))
                .timeStart(LocalDateTime.parse(request.getTimeStart()))
                .rentType(request.getRentType())
                .ownerId(request.getUserId())
                .priceOfUnit(request.getPriceOfUnit())
                .finalPrice(request.getFinalPrice())
                .build();
        repository.save(rent);
        return ResponseEntity.ok(rent);
    }

    public ResponseEntity<?> endRent(long id, RentEndRequest rentEndRequest) {
        Optional<Rent> rentOptional = repository.findById(id);
        if (rentOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Rent with id %d doesnt exist".formatted(id));

        if (rentOptional.get().getRentState() == Rent.RentState.ended)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Rent with id %d already ended".formatted(id));

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
        account.setMoney(BigDecimal.valueOf(account.getMoney()).subtract(BigDecimal.valueOf(rent.getFinalPrice())).doubleValue());
        //Adding rent to rentHistory of account and transport
        account.getRentHistory().add(rent);
        transport.getRentHistory().add(rent);
        //Saving entities to repositories
        repository.save(rent);
        accountRepository.save(account);
        transportRepository.save(transport);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
