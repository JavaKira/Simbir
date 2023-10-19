package com.github.javakira.simbir.admin;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
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
}
