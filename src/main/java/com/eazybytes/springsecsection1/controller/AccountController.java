package com.eazybytes.springsecsection1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    @GetMapping("/myAccount")
    public String getMyAccount() {
        return "fetching all data from my account";
    }

    @GetMapping("/myCards")
    public String getMyCards() {
        return "fetching all card details";
    }
}
