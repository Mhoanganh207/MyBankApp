package com.example.security.config;

import com.example.security.services.AccountDetailsService;
import com.example.security.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtService jwtService;

    private AccountDetailsService accountDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, AccountDetailsService accountDetailsService) {
        this.jwtService = jwtService;
        this.accountDetailsService = accountDetailsService;
    }


    @Override
    protected void doFilterInternal(


            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

           final String authorizationHeader = request.getHeader("Authorization");
           final String jwt,username;


           if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
               filterChain.doFilter(request, response);
               return;
           }
           jwt = authorizationHeader.substring(7);
           username = jwtService.extractUsername(jwt);
           if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                  UserDetails userDetails = this.accountDetailsService.loadUserByUsername(username);
                  if (jwtService.isValidToken(jwt, userDetails)) {
                      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                  }

           }
           filterChain.doFilter(request, response);

    }
}