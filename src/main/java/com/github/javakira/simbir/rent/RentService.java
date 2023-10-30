package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.AccountService;
import com.github.javakira.simbir.payment.PaymentService;
import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportDto;
import com.github.javakira.simbir.transport.TransportRepository;
import com.github.javakira.simbir.transport.TransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentService {
    private final RentRepository repository;
    private final AccountService accountService;
    private final TransportService transportService;
    private final PaymentService paymentService;
    private final TransportRepository transportRepository;
    private final AccountRepository accountRepository;

    //todo задуматься, не нужно ли возращать список айдишников
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

    public RentDto rentInfo(long rentId, long userId) {
        Rent rent = rent(rentId);
        Transport transport = transportService.transport(rent.getTransportId());

        if (!rent.getOwnerId().equals(userId) &&
            !transport.getOwnerId().equals(userId))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only owner of transport and renter can get info about rent"
            );

        return RentDto.from(rent);
    }

    //todo сдесь возможно тоже нужно возращать только список айдишников
    public List<RentDto> accountHistory(long userId) {
        Account user = accountService.account(userId);

        return user
                .getRentHistory()
                .stream()
                .map(RentDto::from)
                .toList();
    }
    public List<RentDto> transportHistory(long transportId, long userId) {
        Transport transport = transportService.transport(transportId);

        if (!transport.getOwnerId().equals(userId))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only owner of transport get rent history"
            );

        return transport
                .getRentHistory()
                .stream()
                .map(RentDto::from)
                .toList();
    }

    public RentDto rent(NewRentRequest request, long transportId, long userId) {
        Transport transport = transportService.transport(transportId);
        Account user = accountService.account(userId);

        if (transport.getOwnerId().equals(userId))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rent own transport is not allowed"
            );

        if (!transport.isCanBeRented())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transport with id %d cant be rented".formatted(transportId)
            );

        if (transport.isRented())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transport with id %d is busy".formatted(transportId)
            );

        if (user.getMoney() < 0)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cant rent transport: account with id %d have negative balance".formatted(userId)
            );

        double unitPrice = 0;
        try {
            unitPrice = priceUnit(transport, request.getRentType());
        } catch (Exception e) {
            cantBeRented(transportId, request.getRentType());
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
        transport.setRented(true);
        repository.save(rent);
        transportRepository.save(transport);
        return RentDto.from(rent);
    }

    public RentDto end(long rentId, RentEndRequest rentEndRequest, long userId) {
        Rent rent = rent(rentId);

        if (!rent.getOwnerId().equals(userId))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only rent owner can end rent"
            );

        if (rent.getRentState() == Rent.RentState.ended)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rent with id %d already ended".formatted(rentId)
            );

        Account account = accountRepository.findById(rent.getOwnerId()).orElseThrow();
        Transport transport = transportRepository.findById(rent.getTransportId()).orElseThrow();
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
        return RentDto.from(rent);
    }

    public Rent rent(long id) {
        Optional<Rent> rentOptional = repository.findById(id);
        if (rentOptional.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Rent with id %d doesnt exist".formatted(id)
            );

        return rentOptional.get();
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

    private void cantBeRented(long transportId, RentType rentType) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Transport with id %d cant be rented with rentType %s".formatted(transportId, rentType)
        );
    }

    public Optional<Rent> get(Long id) {
        return repository.findById(id);
    }
}
