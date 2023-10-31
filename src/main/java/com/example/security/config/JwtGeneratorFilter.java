package com.example.security.config;

import com.example.security.model.AccountDetails;
import com.example.security.services.AccountDetailsService;
import com.example.security.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtGeneratorFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private AccountDetailsService accountDetailsService;

    @Override
    protected void doFilterInternal(
           @NonNull HttpServletRequest request,
           @NonNull HttpServletResponse response,
           @NonNull FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication && request.getRequestURI().equals("/signin")) {
            System.out.println("User: " + authentication.getName() + " is authenticated");
            UserDetails userDetails = accountDetailsService.loadUserByUsername(authentication.getName());
            String token = jwtService.generateToken((AccountDetails) userDetails);

            response.addCookie(new Cookie("JwtToken",token));
        }
        filterChain.doFilter(request,response);

    }
}
