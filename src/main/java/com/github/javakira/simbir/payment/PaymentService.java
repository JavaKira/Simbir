package com.github.javakira.simbir.payment;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final AccountRepository accountRepository;

    public ResponseEntity<?> hesoyam(long accountId, long userId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Account with id %d doesnt exist".formatted(accountId));

        if (userId == accountId) {
            account.get().setMoney(account.get().getMoney() + 250_000);
            accountRepository.save(account.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }

        Optional<Account> user = accountRepository.findById(userId);
        if (user.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Account with id %d doesnt exist".formatted(userId));

        if (user.get().getRole() == Role.admin) {
            account.get().setMoney(account.get().getMoney() + 250_000);
            accountRepository.save(account.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Only admin can replenish money for other accounts");
        }
    }
}
