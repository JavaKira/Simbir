package com.github.javakira.simbir.admin.rent;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.AccountService;
import com.github.javakira.simbir.payment.PaymentService;
import com.github.javakira.simbir.rent.Rent;
import com.github.javakira.simbir.rent.RentDto;
import com.github.javakira.simbir.rent.RentRepository;
import com.github.javakira.simbir.rent.RentService;
import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportRepository;
import com.github.javakira.simbir.transport.TransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminRentService {
    private final RentRepository repository;
    private final AccountService accountService;
    private final TransportService transportService;
    private final RentService rentService;
    private final AccountRepository accountRepository;
    private final TransportRepository transportRepository;
    private final PaymentService paymentService;

    public RentDto rentInfo(long rentId) {
        return RentDto.from(rentService.rent(rentId));
    }

    public List<RentDto> userHistory(long userId) {
        return accountService
                .account(userId)
                .getRentHistory()
                .stream()
                .map(RentDto::from)
                .toList();
    }

    public List<RentDto> transportHistory(long transportId) {
        return transportService
                .transport(transportId)
                .getRentHistory()
                .stream()
                .map(RentDto::from)
                .toList();
    }

    public RentDto newRent(NewRentAdminRequest request) {
        Transport transport = transportService.transport(request.getTransportId());

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
        transport.setRented(true);
        if (request.getTimeEnd() != null) {
            rent.setTimeEnd(LocalDateTime.parse(request.getTimeEnd()));
            rent.setRentState(Rent.RentState.ended);
            transport.setRented(false);
        }

        repository.save(rent);
        return RentDto.from(rent);
    }

    public RentDto endRent(long id, RentEndRequest rentEndRequest) {
        Rent rent = rentService.rent(id);

        if (rent.getRentState() == Rent.RentState.ended)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rent with id %d already ended".formatted(id)
            );

        Account account = accountService.account(rent.getOwnerId());
        Transport transport = transportService.transport(rent.getTransportId());
        Account transportOwner = accountService.account(transport.getOwnerId());
        //Updating Transport location to rent end location
        transport.setLongitude(rentEndRequest.getLongitude());
        transport.setLatitude(rentEndRequest.getLat());
        transport.setRented(false);
        //Closing rent
        rent.setRentState(Rent.RentState.ended);
        rent.setTimeEnd(LocalDateTime.now());
        rent.setFinalPrice(rentService.price(rent));
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

    public RentDto update(UpdateRentAdminRequest request, long id) {
        Rent oldRent = rentService.rent(id);

        Rent rent = Rent
                .builder()
                .id(id)
                .ownerId(oldRent.getOwnerId())
                .rentState(Rent.RentState.opened)
                .transportId(request.getTransportId())
                .timeStart(LocalDateTime.parse(request.getTimeStart()))
                .timeEnd(null)
                .rentType(request.getRentType())
                .ownerId(request.getUserId())
                .priceOfUnit(request.getPriceOfUnit())
                .finalPrice(request.getFinalPrice())
                .build();

        if (request.getTimeEnd() != null) {
            rent.setTimeEnd(LocalDateTime.parse(request.getTimeEnd()));
            rent.setRentState(Rent.RentState.ended);
        }

        repository.save(rent);
        return RentDto.from(rent);
    }

    public void delete(long id) {
        rawDelete(id);
    }

    public void deleteRentByOwner(long ownerId) {
        repository
                .findAll()
                .stream()
                .filter(rent -> rent.getOwnerId().equals(ownerId))
                .forEach(rent -> rawDelete(rent.getId()));
    }

    public void rawDelete(long id) {
        Rent rent = rentService.rent(id);
        Account account = accountService.account(rent.getOwnerId());
        Transport transport = transportService.transport(rent.getTransportId());
        account.getRentHistory().removeIf(rent1 -> rent.getId().equals(id));
        transport.getRentHistory().removeIf(rent1 -> rent.getId().equals(id));
        accountRepository.save(account);
        transportRepository.save(transport);
        repository.delete(rent);
    }
}
