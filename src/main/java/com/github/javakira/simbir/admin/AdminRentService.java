package com.github.javakira.simbir.admin;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.rent.Rent;
import com.github.javakira.simbir.rent.RentRepository;
import com.github.javakira.simbir.transport.Transport;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
