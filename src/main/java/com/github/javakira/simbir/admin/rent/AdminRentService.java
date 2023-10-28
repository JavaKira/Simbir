package com.github.javakira.simbir.admin.rent;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.admin.rent.NewRentAdminRequest;
import com.github.javakira.simbir.admin.rent.RentEndRequest;
import com.github.javakira.simbir.rent.Rent;
import com.github.javakira.simbir.rent.RentDto;
import com.github.javakira.simbir.rent.RentRepository;
import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.NonNull;
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

        return ResponseEntity.ok(RentDto.from(rentOptional.get()));
    }

    public ResponseEntity<?> userHistory(long userId) {
        Optional<Account> account = accountRepository.findById(userId);
        if (account.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Account with id %d doesnt exist".formatted(userId));

        return ResponseEntity.ok(account.get().getRentHistory().stream().map(RentDto::from).toList());
    }

    public ResponseEntity<?> transportHistory(Long transportId) {
        Optional<Transport> transport = transportRepository.findById(transportId);
        if (transport.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Transport with id %d doesnt exist".formatted(transportId));

        return ResponseEntity.ok(transport.get().getRentHistory().stream().map(RentDto::from).toList());
    }

    public ResponseEntity<RentDto> newRent(NewRentAdminRequest request) {
        Rent rent = Rent
                .builder()
                .rentState(Rent.RentState.opened)
                .transportId(request.getTransportId())
                .timeStart(LocalDateTime.parse(request.getTimeStart()))
                .timeEnd(null)
                .rentType(request.getRentType())
                .ownerId(request.getUserId())
                .priceOfUnit(request.getPriceOfUnit())
                .finalPrice(request.getFinalPrice())
                .build();

        if (request.getTimeEnd() != null)
            rent.setTimeEnd(LocalDateTime.parse(request.getTimeEnd()));

        repository.save(rent);
        return ResponseEntity.ok(RentDto.from(rent));
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
        Account transportOwner = accountRepository.findById(transport.getOwnerId()).orElseThrow();
        //Updating Transport location to rent end location
        transport.setLongitude(rentEndRequest.getLongitude());
        transport.setLatitude(rentEndRequest.getLat());
        //Closing rent
        rent.setRentState(Rent.RentState.ended);
        rent.setTimeEnd(LocalDateTime.now());
        rent.setFinalPrice(rent.getRentType().price(rent));
        //Taking off money
        transferMoney(
                rent.getFinalPrice(),
                account,
                transportOwner
        );
        //Adding rent to rentHistory of account and transport
        account.getRentHistory().add(rent);
        transport.getRentHistory().add(rent);
        //Saving entities to repositories
        repository.save(rent);
        accountRepository.save(account);
        transportRepository.save(transport);
        return ResponseEntity.ok(RentDto.from(rent));
    }

    public ResponseEntity<?> update(UpdateRentAdminRequest request, long id) {
        Optional<Rent> rentOptional = repository.findById(id);
        if (rentOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Rent with id %d doesnt exist".formatted(id));

        Rent rent = Rent
                .builder()
                .id(id)
                .ownerId(rentOptional.get().getOwnerId())
                .rentState(Rent.RentState.opened)
                .transportId(request.getTransportId())
                .timeStart(LocalDateTime.parse(request.getTimeStart()))
                .timeEnd(null)
                .rentType(request.getRentType())
                .ownerId(request.getUserId())
                .priceOfUnit(request.getPriceOfUnit())
                .finalPrice(request.getFinalPrice())
                .build();

        if (request.getTimeEnd() != null)
            rent.setTimeEnd(LocalDateTime.parse(request.getTimeEnd()));

        repository.save(rent);
        return ResponseEntity.ok(RentDto.from(rent));
    }

    public ResponseEntity<?> delete(long id) {
        Optional<Rent> rentOptional = repository.findById(id);
        if (rentOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Rent with id %d doesnt exist".formatted(id));

        rawDelete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void deleteRentByOwner(long ownerId) {
        repository
                .findAll()
                .stream()
                .filter(rent -> rent.getOwnerId().equals(ownerId))
                .forEach(rent -> rawDelete(rent.getId()));
    }

    private void transferMoney(double amount, @NonNull Account from, @NonNull Account to) {
        from.setMoney(BigDecimal.valueOf(from.getMoney()).subtract(BigDecimal.valueOf(amount)).doubleValue());
        to.setMoney(BigDecimal.valueOf(to.getMoney()).add(BigDecimal.valueOf(amount)).doubleValue());
    }

    public void rawDelete(long id) {
        Optional<Rent> rentOptional = repository.findById(id);
        Optional<Account> account = accountRepository.findById(rentOptional.orElseThrow().getOwnerId());
        Optional<Transport> transport = transportRepository.findById(rentOptional.get().getTransportId());
        account.orElseThrow().getRentHistory().removeIf(rent -> rent.getId().equals(id));
        transport.orElseThrow().getRentHistory().removeIf(rent -> rent.getId().equals(id));
        accountRepository.save(account.get());
        transportRepository.save(transport.get());
        repository.delete(rentOptional.get());
    }
}
