package com.example.security.controller;

import com.example.security.model.Account;
import com.example.security.services.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ServiceController {

    @Autowired
    private AccountDetailsService accountService;

    @GetMapping("/account")
    public String myAccount(Model model, Authentication authentication){
        String username = authentication.getName();
        Account account = accountService.findByUsername(username).get();
        model.addAttribute("greet","Hello " + account.getUsername());
        return "account";
    }


    @GetMapping("account/info")
    public String MyInfo(){
        return "info";
    }

    @GetMapping("account/services")
    public String MyServices(){
        return "services";
    }

    @GetMapping("account/card")
    public String MyCard(){
        return "card";
    }


}
