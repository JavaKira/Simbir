package com.github.javakira.simbir.admin.service;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.Role;
import com.github.javakira.simbir.admin.schema.GetAccountsRequest;
import com.github.javakira.simbir.admin.schema.RegisterByAdminRequest;
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
                .money((long) request.getBalance()) //todo мб баланс сделать в типе double
                .build();
        accountRepository.save(account);
    }
}
