package com.github.javakira.simbir.account;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @GetMapping(value = "/Me", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String me() {
        return "hello";
    }

    @PostMapping(value = "/SingIn", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String singIn() {
        return "";
    }
}
