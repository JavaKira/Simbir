package com.github.javakira.simbir.account;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService service;

    @GetMapping(value = "/Me", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String me() {
        return "";
    }

    @PostMapping(value = "/SingIn", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> singIn(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.singIn(request));
    }

    @PostMapping(value = "/SingUp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> singUp(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.singUp(request));
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
