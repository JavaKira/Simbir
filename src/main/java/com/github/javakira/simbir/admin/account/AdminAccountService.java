package com.github.javakira.simbir.admin.account;

import com.github.javakira.simbir.account.*;
import com.github.javakira.simbir.admin.rent.AdminRentService;
import com.github.javakira.simbir.admin.transport.AdminTransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminAccountService {
    private final AccountRepository accountRepository;
    private final AccountService service;
    private final AdminRentService rentService;
    private final PasswordEncoder passwordEncoder;
    private final AdminTransportService transportService;

    public List<Long> accounts(GetAccountsRequest request) {
        if (request.getStart() < 0 || request.getCount() < 0)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'start' and 'count' must be > 0"
            );

        List<Account> accounts = accountRepository.findAll();
        return accounts
                .subList(Math.min(request.getStart(), accounts.size()), Math.min(request.getStart() + request.getCount(), accounts.size()))
                .stream()
                .map(Account::getId)
                .toList();
    }

    public AccountDto accountInfo(long id) {
        return service.accountInfo(id);
    }

    public AccountDto registerAccount(RegisterByAdminRequest request) {
        service.checkUsername(request.getUsername());

        Account account = Account
                .builder()
                .role(request.isAdmin() ? Role.admin : Role.user)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .money(request.getBalance())
                .build();
        accountRepository.save(account);
        return AccountDto.from(account);
    }

    public AccountDto updateAccount(long id, UpdateByAdminRequest request) {
        service.checkUsername(request.getUsername(), id);
        Account oldAccount = service.account(id);

        Account account = Account
                .builder()
                .id(id)
                .role(request.isAdmin() ? Role.admin : Role.user)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .money(request.getBalance())
                .rentHistory(oldAccount.getRentHistory())
                .build();
        accountRepository.save(account);
        return AccountDto.from(account);
    }

    public void deleteAccount(long id) {
        Account account = service.account(id);

        rentService.deleteRentByOwner(id);
        transportService.deleteTransportByOwner(id);
        accountRepository.delete(account);
    }
}
