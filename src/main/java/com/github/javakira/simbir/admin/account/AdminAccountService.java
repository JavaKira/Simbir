package com.github.javakira.simbir.admin.account;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.AccountRepository;
import com.github.javakira.simbir.account.Role;
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

    public List<Long> accounts(GetAccountsRequest request) {
        //todo accountRepository.get(request)
        List<Account> accounts = accountRepository.findAll();
        return accounts.subList(request.getStart(), Math.max(request.getStart() + request.getCount(), accounts.size())).stream().map(Account::getId).toList();
    }

    //todo можно сделать также как в пакете account: AccountInfoResponse
    public ResponseEntity<?> accountInfo(long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Account with id %d doesnt exist".formatted(id));

        return ResponseEntity.ok(accountOptional.get());
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
        return new ResponseEntity<>(HttpStatus.OK);
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
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> deleteAccount(long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Account with id %d doesnt exist".formatted(id));

        accountRepository.delete(accountOptional.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
