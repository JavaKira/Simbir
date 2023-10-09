package com.github.javakira.simbir.account;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @GetMapping(value = "/Me", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String me() {
        return "";
    }

    @PostMapping(value = "/SingIn", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String singIn() {
        return "";
    }

    @PostMapping(value = "/SingUp", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String singUp() {
        return "";
    }

    @PostMapping(value = "/SingOut", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String singOut() {
        return "";
    }

    @PutMapping(value = "/Update", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String update() {
        return "";
    }
}
