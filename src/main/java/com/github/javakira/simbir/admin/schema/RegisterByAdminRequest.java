package com.github.javakira.simbir.admin.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterByAdminRequest {
    private String username;
    private String password;
    private boolean isAdmin;
    private double balance;
}
