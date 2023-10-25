package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentService {
    private final RentRepository repository;
    private final TransportRepository transportRepository;
    private final AccountRepository accountRepository;

    public List<Transport> findAvailable(RentSearchParams params) {
        List<Transport> transports = transportRepository.findAll();

        return transports
                .stream()
                .filter(transport -> transport.getTransportType().equals(params.getType()))
                .filter(this::canBeRented)
                .filter(transport ->
                        isInRange(
                                transport.getLongitude(),
                                transport.getLatitude(),
                                params.getLongitude(),
                                params.getLat(),
                                params.getRadius()
                        )
                )
                .toList();
    }

    public ResponseEntity<?> rentInfo(long rentId, long userId) {
        Optional<Rent> rentOptional = repository.findById(rentId);
        if (rentOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Rent with id %d doesnt exist".formatted(rentId));

        Optional<Transport> transportOptional = transportRepository.findById(rentOptional.get().getTransportId());
        if (!rentOptional.get().getOwnerId().equals(userId) &&
            !transportOptional.orElseThrow().getOwnerId().equals(userId))
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only owner of transport and renter can get info about rent");

        return ResponseEntity.ok(rentOptional.get());
    }

    public ResponseEntity<?> accountHistory(long userId) {
        Optional<Account> account = accountRepository.findById(userId);
        if (account.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("User with id %d doesnt exist".formatted(userId));

        return ResponseEntity.ok(account.get().getRentHistory());
    }

    public ResponseEntity<?> transportHistory(long transportId, long userId) {
        Optional<Transport> transport = transportRepository.findById(transportId);
        if (transport.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Transport with id %d doesnt exist".formatted(transportId));

        if (!transport.get().getOwnerId().equals(userId))
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only owner of transport get rent history");

        return ResponseEntity.ok(transport.get().getRentHistory());
    }

    public ResponseEntity<?> rent(NewRentRequest request, long transportId, long userId) {
        Optional<Transport> transport = transportRepository.findById(transportId);
        if (transport.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Transport with id %d doesnt exist".formatted(userId));

        if (transport.get().getOwnerId().equals(userId))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Rent own transport is not allowed");

        if (!transport.get().isCanBeRented())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Transport with id %d cant be rented".formatted(transportId));

        double unitPrice = 0;
        //todo добавить проверки
        if (request.getRentType() == Rent.RentType.Minutes)
            unitPrice = transport.get().getMinutePrice();
        else if (request.getRentType() == Rent.RentType.Days)
            unitPrice = transport.get().getDayPrice();

        Rent rent = Rent
                .builder()
                .rentType(request.getRentType())
                .ownerId(userId)
                .rentState(Rent.RentState.opened)
                .timeStart(LocalDateTime.now())
                .priceOfUnit(unitPrice)
                .transportId(transportId)
                .build();
        transport.get().setCanBeRented(false);
        repository.save(rent);
        transportRepository.save(transport.get());
        return ResponseEntity.ok(rent);
    }

    public ResponseEntity<?> end(Long rentId, RentEndRequest rentEndRequest, Long userId) {
        Optional<Rent> rentOptional = repository.findById(rentId);
        if (rentOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Rent with id %d doesnt exist".formatted(rentId));

        if (!rentOptional.get().getOwnerId().equals(userId))
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only rent owner can end rent");

        if (rentOptional.get().getRentState() == Rent.RentState.ended)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Rent with id %d already ended".formatted(rentId));

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
        transport.setCanBeRented(true);
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

    private boolean isInRange(
            double pointLongitude,
            double pointLatitude,
            double centerLongitude,
            double centerLatitude,
            double radius
    ) {
        double distance = Math.sqrt(Math.pow(pointLongitude - centerLongitude, 2) + Math.pow(pointLatitude - centerLatitude, 2));
        return distance <= radius;
    }

    private boolean canBeRented(Transport transport) {
        return transport.isCanBeRented();
    }

    public Optional<Rent> get(Long id) {
        return repository.findById(id);
    }
}
