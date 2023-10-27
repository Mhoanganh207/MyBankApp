package com.example.security.config;


import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


public class MyAuthenticationProvider extends DaoAuthenticationProvider{

    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;


    public MyAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        super();
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
    }

}
