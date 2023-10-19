package com.github.javakira.simbir.payment;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final AccountRepository accountRepository;

    public void addMoney(Long accountId, Long userId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty())
            throw new IllegalArgumentException("Account with id %d doesnt exist".formatted(userId));

        if (userId.equals(accountId)) {
            account.get().setMoney(account.get().getMoney() + 250_000);
            accountRepository.save(account.get());
            return;
        }

        Optional<Account> user = accountRepository.findById(userId);
        if (user.isEmpty())
            throw new IllegalArgumentException("Account with id %d doesnt exist".formatted(userId));

        if (user.get().getRole() == Role.admin) {
            account.get().setMoney(account.get().getMoney() + 250_000);
            accountRepository.save(account.get());
        } else {
            throw new IllegalArgumentException("Only admin can replenish money for other accounts");
        }
    }
}
