package com.example.security.config;


import com.example.security.services.AccountDetailsService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;


@Configuration
@EnableMethodSecurity
public class ProjectSecureConfig  {

    @Autowired
    public DataSource dataSource;

    @Resource
    private AccountDetailsService accountDetailsService;




    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        return new MyAuthenticationProvider(this.accountDetailsService,passwordEncoder());
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/account","account/services"
                                ,"account/info","account/card").authenticated()
                        .anyRequest().permitAll()
                );
        http.formLogin(form -> form.loginPage("/login")
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/account", true));
        http.rememberMe(rememberMe -> rememberMe
                .key("mySecretKey")
                .userDetailsService(this.accountDetailsService)
                .tokenValiditySeconds(20)
        );
        http.logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
        );

        http.csrf(AbstractHttpConfigurer::disable);





        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public InMemoryUserDetailsManager userDetailsManager(){

        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("titbandau"))
                .authorities("user")
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("titbandau"))
                .authorities("admin")
                .roles("ADMIN")
                .build();


        return new InMemoryUserDetailsManager(user,admin);

    }



}








