package com.github.javakira.simbir.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private Long id;
    private double money;
    private String username;
    private Role role;

    public static AccountDto from(Account account) {
        return AccountDto
                .builder()
                .id(account.getId())
                .money(account.getMoney())
                .role(account.getRole())
                .username(account.getUsername())
                .build();
    }
}
