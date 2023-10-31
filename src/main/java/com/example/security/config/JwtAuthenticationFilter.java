package com.example.security.config;

import com.example.security.services.AccountDetailsService;
import com.example.security.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountDetailsService accountDetailsService;



    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie[] cookies = request.getCookies();
            Optional<Cookie> isTokenThere = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("JwtToken")).findFirst();

            if(isTokenThere.isEmpty()){
                System.out.println("empty");
                filterChain.doFilter(request,response);
                return;
            }
            System.out.println("exist");
            String token,username;
            token = isTokenThere.get().getValue();
            username = jwtService.extractUsername(token);
            System.out.println("username: " + username);
            System.out.println("token: " + token);
            System.out.println("auth: " + SecurityContextHolder.getContext().getAuthentication());
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {


                UserDetails userDetails = accountDetailsService.loadUserByUsername(username);
                System.out.println(userDetails.getUsername());

                if (jwtService.isValidToken(token,userDetails)){
                    System.out.println("valid");
                    UsernamePasswordAuthenticationToken newtoken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    newtoken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(newtoken);

                }
                filterChain.doFilter(request,response);

            }
        }
        catch (NullPointerException e){
           filterChain.doFilter(request,response);
        }


    }
}
