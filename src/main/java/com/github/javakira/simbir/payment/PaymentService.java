package com.github.javakira.simbir.payment;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.AccountService;
import com.github.javakira.simbir.account.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final AccountRepository accountRepository;
    private final AccountService service;

    public void hesoyam(long accountId, long userId) {
        Account account = service.account(accountId);

        if (userId == accountId) {
            account.setMoney(account.getMoney() + 250_000);
            accountRepository.save(account);
            return;
        }

        Account user = service.account(userId);

        if (user.getRole() == Role.admin) {
            account.setMoney(account.getMoney() + 250_000);
            accountRepository.save(account);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only admin can replenish money for other accounts"
            );
        }
    }

    public void transferMoney(double amount, @NonNull Account from, @NonNull Account to) {
        from.setMoney(BigDecimal.valueOf(from.getMoney()).subtract(BigDecimal.valueOf(amount)).doubleValue());
        to.setMoney(BigDecimal.valueOf(to.getMoney()).add(BigDecimal.valueOf(amount)).doubleValue());
    }

    public void updateTransferMoney(double old, double newAmount, @NonNull Account from, @NonNull Account to) {
        transferMoney(-old, from, to);
        transferMoney(newAmount, from, to);
    }
}
