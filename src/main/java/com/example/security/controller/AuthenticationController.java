package com.example.security.controller;


import com.example.security.dto.LoginRequest;
import com.example.security.services.AccountDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthenticationController {



    private final AuthenticationManager authenticationManager;
    @Autowired
    private AccountDetailsService accountDetailsService;

    public AuthenticationController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @RequestMapping( value ="/signin", method = RequestMethod.GET )
    public String signIn(@RequestParam(required = false) String error, Model model){
        try{
            if(error.equals("true")){
                model.addAttribute("valid",0);
            }
            return "signin";
        }
        catch (NullPointerException e){
            return "signin";
        }
    }



    @RequestMapping(value = "demo", method = RequestMethod.POST)
    public RedirectView demo(@RequestBody LoginRequest loginRequest,HttpServletRequest request){

        System.out.println("test");
        UserDetails userDetails = accountDetailsService.loadUserByUsername(loginRequest.getUsername());
        UsernamePasswordAuthenticationToken newToken =
                UsernamePasswordAuthenticationToken.authenticated(userDetails.getUsername(), loginRequest.getPassword(),userDetails.getAuthorities());
        newToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(newToken);
        if (authenticationResponse.isAuthenticated()){
            System.out.println("authenticated");
            SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
            System.out.println(SecurityContextHolder.getContext().getAuthentication());

            return new RedirectView("/account");
        }
        return new RedirectView("signin?error=true");

    }

}
