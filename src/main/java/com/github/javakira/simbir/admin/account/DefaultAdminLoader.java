package com.github.javakira.simbir.admin.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


//Need only for test
@Component
public class DefaultAdminLoader {
    @Autowired
    public DefaultAdminLoader(AdminAccountService service) {
        service.registerAccount(new RegisterByAdminRequest("admin", "admin", true, 0));
    }
}
