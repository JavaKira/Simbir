package com.github.javakira.simbir.admin.account;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountDto;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.Role;
import com.github.javakira.simbir.admin.rent.AdminRentService;
import com.github.javakira.simbir.admin.transport.AdminTransportService;
import com.github.javakira.simbir.rent.RentRepository;
import com.github.javakira.simbir.transport.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminAccountService {
    private final AccountRepository accountRepository;
    private final AdminRentService rentService;
    private final AdminTransportService transportService;

    public ResponseEntity<?> accounts(GetAccountsRequest request) {
        if (request.getStart() < 0 || request.getCount() < 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("'start' and 'count' must be > 0");

        List<Account> accounts = accountRepository.findAll();
        return ResponseEntity.ok(
                accounts
                .subList(Math.min(request.getStart(), accounts.size()), Math.min(request.getStart() + request.getCount(), accounts.size()))
                .stream()
                .map(Account::getId)
                .toList()
        );
    }

    public ResponseEntity<?> accountInfo(long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Account with id %d doesnt exist".formatted(id));

        return ResponseEntity.ok(AccountDto.from(accountOptional.get()));
    }

    public ResponseEntity<?> registerAccount(RegisterByAdminRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username '%s' is already in use".formatted(request.getUsername()));

        Account account = Account
                .builder()
                .role(request.isAdmin() ? Role.admin : Role.user)
                .username(request.getUsername())
                .password(request.getPassword())
                .money(request.getBalance())
                .build();
        accountRepository.save(account);
        return ResponseEntity.ok(AccountDto.from(account));
    }

    public ResponseEntity<?> updateAccount(long id, UpdateByAdminRequest request) {
        Optional<Account> accountOptional = accountRepository.findByUsername(request.getUsername());
        if (accountOptional.isPresent() && !accountOptional.get().getId().equals(id))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username '%s' is already in use".formatted(request.getUsername()));


        accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Account with id %d doesnt exist".formatted(id));

        Account account = Account
                .builder()
                .id(id)
                .role(request.isAdmin() ? Role.admin : Role.user)
                .username(request.getUsername())
                .password(request.getPassword())
                .money(request.getBalance())
                .rentHistory(accountOptional.get().getRentHistory())
                .build();
        accountRepository.save(account);
        return ResponseEntity.ok(AccountDto.from(account));
    }

    public ResponseEntity<?> deleteAccount(long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Account with id %d doesnt exist".formatted(id));

        //todo можно было и связностью в JPA это сделать
        rentService.deleteRentByOwner(id);
        transportService.deleteTransportByOwner(id);
        accountRepository.delete(accountOptional.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
