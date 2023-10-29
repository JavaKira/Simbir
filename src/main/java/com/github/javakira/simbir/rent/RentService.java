package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.payment.PaymentService;
import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportDto;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentService {
    private final RentRepository repository;
    private final PaymentService paymentService;
    private final TransportRepository transportRepository;
    private final AccountRepository accountRepository;

    public List<TransportDto> findAvailable(RentSearchParams params) {
        List<Transport> transports = transportRepository.findAll();

        return transports
                .stream()
                .filter(transport -> params.getType().fits(transport.getTransportType()))
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
                .map(TransportDto::from)
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

        return ResponseEntity.ok(RentDto.from(rentOptional.get()));
    }

    public ResponseEntity<?> accountHistory(long userId) {
        Optional<Account> account = accountRepository.findById(userId);
        if (account.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("User with id %d doesnt exist".formatted(userId));

        return ResponseEntity.ok(account.get().getRentHistory().stream().map(RentDto::from).toList());
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

        return ResponseEntity.ok(transport.get().getRentHistory().stream().map(RentDto::from).toList());
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

        if (!transport.get().isRented())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Transport with id %d is busy".formatted(transportId));

        Optional<Account> accountOptional = accountRepository.findById(userId);
        if (accountOptional.orElseThrow().getMoney() < 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Cant rent transport: account with id %d have negative balance".formatted(userId));

        double unitPrice;
        try {
            unitPrice = priceUnit(transport.get(), request.getRentType());
        } catch (Exception e) {
            return cantBeRented(transportId, request.getRentType());
        }

        Rent rent = Rent
                .builder()
                .rentType(request.getRentType())
                .ownerId(userId)
                .rentState(Rent.RentState.opened)
                .timeStart(LocalDateTime.now())
                .priceOfUnit(unitPrice)
                .transportId(transportId)
                .build();
        transport.get().setRented(true);
        repository.save(rent);
        transportRepository.save(transport.get());
        return ResponseEntity.ok(RentDto.from(rent));
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
        Account transportOwner = accountRepository.findById(transport.getOwnerId()).orElseThrow();
        //Updating Transport location to rent end location
        transport.setLongitude(rentEndRequest.getLongitude());
        transport.setLatitude(rentEndRequest.getLat());
        //Closing rent
        rent.setRentState(Rent.RentState.ended);
        rent.setTimeEnd(LocalDateTime.now());
        rent.setFinalPrice(price(rent));
        transport.setRented(false);
        //Taking off money
        paymentService.transferMoney(
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

    public double price(Rent rent) {
        return rent.getRentType().price(rent);
    }

    public double priceUnit(Transport transport, RentType rentType) throws IllegalArgumentException {
        return rentType.priceUnit(transport);
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

    private ResponseEntity<?> cantBeRented(long transportId, RentType rentType) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Transport with id %d cant be rented with rentType %s".formatted(transportId, rentType));
    }

    public Optional<Rent> get(Long id) {
        return repository.findById(id);
    }
}
