package com.github.javakira.simbir.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoResponse {
    private Long id;
    private double money;
    private String username;
    private Role role;
}
