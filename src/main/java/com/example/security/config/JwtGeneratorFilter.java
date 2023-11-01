package com.example.security.config;

import com.example.security.services.AccountDetailsService;
import com.example.security.services.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class JwtGeneratorFilter extends AbstractAuthenticationProcessingFilter {


    private final JwtService jwtService = new JwtService();



    private AccountDetailsService accountDetailsService;



    protected JwtGeneratorFilter(AuthenticationManager authenticationManager, AccountDetailsService accountDetailsService) {
        super(new AntPathRequestMatcher("/auth"));
        this.accountDetailsService = accountDetailsService;
        super.setFilterProcessesUrl("/auth");
        this.setAuthenticationManager(authenticationManager);

    }

    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        System.out.println("attemptAuthentication");
        System.out.println(response.getStatus());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);
        if (request.getRequestURI().equals("/auth")) {
            System.out.println("auth");
            String username = request.getParameter("username");
            UsernamePasswordAuthenticationToken newtoken = UsernamePasswordAuthenticationToken.unauthenticated(
                    username, request.getParameter("password")
            );
            newtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            try {
                authentication = this.getAuthenticationManager().authenticate(newtoken);
            } catch (Exception e) {
                System.out.println("false");
                response.sendRedirect("login?error=true");
                return null;
            }
            Authentication authenticationResponse = this.getAuthenticationManager().authenticate(newtoken);
            SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
           if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
               String token = jwtService.generateToken(username);
               response.addCookie(new Cookie("JwtToken", token));
               response.sendRedirect("/account");
               System.out.println("true");
           }
           else {
               System.out.println("false");
               response.sendRedirect("login?error=true");
           }

        }

        return authentication;
    }
}
