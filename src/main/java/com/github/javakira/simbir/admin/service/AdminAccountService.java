package com.github.javakira.simbir.admin.service;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.Role;
import com.github.javakira.simbir.admin.schema.GetAccountsRequest;
import com.github.javakira.simbir.admin.schema.RegisterByAdminRequest;
import com.github.javakira.simbir.admin.schema.UpdateByAdminRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminAccountService {
    private final AccountRepository accountRepository;

    public List<Long> accounts(GetAccountsRequest request) {
        //todo accountRepository.get(request)
        List<Account> accounts = accountRepository.findAll();
        return accounts.subList(request.getStart(), Math.max(request.getStart() + request.getCount(), accounts.size())).stream().map(Account::getId).toList();
    }

    public Optional<Account> accountInfo(Long id) {
        return accountRepository.findById(id);
    }

    public void registerAccount(RegisterByAdminRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent())
            throw new IllegalArgumentException("Username '%s' is already in use".formatted(request.getUsername()));

        Account account = Account
                .builder()
                .role(request.isAdmin() ? Role.admin : Role.user)
                .username(request.getUsername())
                .password(request.getPassword())
                .money(request.getBalance())
                .build();
        accountRepository.save(account);
    }

    public void updateAccount(Long id, UpdateByAdminRequest request) {
        Optional<Account> accountOptional = accountRepository.findByUsername(request.getUsername());
        if (accountOptional.isPresent() && !accountOptional.get().getId().equals(id)) {
            throw new IllegalArgumentException("Username '%s' is already in use".formatted(request.getUsername()));
        }

        accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty())
            throw new IllegalArgumentException("Account with id %d doesnt exist".formatted(id));

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
    }

    public void deleteAccount(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty())
            throw new IllegalArgumentException("Account with id %d doesnt exist".formatted(id));

        accountRepository.delete(accountOptional.get());
    }
}
