package com.example.security.controller;

import com.example.security.model.Account;
import com.example.security.model.Authority;
import com.example.security.model.User;
import com.example.security.services.AccountDetailsService;
import com.example.security.services.UserService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;


@Controller
@ComponentScan
public class AccountController {


    private  AccountDetailsService accountDetailsService;
    private UserService userService;

    public AccountController(AccountDetailsService accountDetailsService, UserService userService) {
        this.accountDetailsService = accountDetailsService;
        this.userService = userService;
    }


    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @RequestMapping( value ="/login", method = RequestMethod.GET )
    public String signIn(@RequestParam(required = false) String error, Model model){
        try{
            if(error.equals("true")){
                System.out.println("true");
                model.addAttribute("valid",0);
            }
            return "signin";
        }
        catch (NullPointerException e){
            return "signin";
        }
    }
    @RequestMapping(value = "/signup",method = RequestMethod.GET)
    public String signUp(){
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public RedirectView NewAccount(@RequestParam Map<String,String> allRequestParams, RedirectAttributes redirectAttributes) {
        String password = allRequestParams.get("password");
        String repassword = allRequestParams.get("repassword");

        if(password.equals(repassword)){
            User user = userService.createUser(allRequestParams);
            String username = allRequestParams.get("username");
            Account account = new Account(username,passwordEncoder().encode(password));
            account.setAuthority(Authority.USER);
            user.setAccount(account);
            account.setUser(user);
            accountDetailsService.saveAccount(account);
            return new RedirectView("/");
        }
        System.out.println("false");
        redirectAttributes.addFlashAttribute("valid",1);
        return new RedirectView("/signup");
    }
}
