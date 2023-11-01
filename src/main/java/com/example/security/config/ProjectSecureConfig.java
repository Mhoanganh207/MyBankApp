package com.example.security.config;


import com.example.security.model.Authority;
import com.example.security.services.AccountDetailsService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.sql.DataSource;


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class ProjectSecureConfig  {

    @Autowired
    public DataSource dataSource;

    @Resource
    private AccountDetailsService accountDetailsService;


    private final JwtAuthenticationFilter jwtAuthenticationFilter;





    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        return new MyAuthenticationProvider(accountDetailsService,passwordEncoder());
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(authenticationProvider());
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin(form -> form.loginPage("/login")
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/account", true)
                .loginProcessingUrl("/login")
        );
        http.authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/account/**").authenticated()

                        .requestMatchers(HttpMethod.POST,"/login").permitAll()
                        .anyRequest().permitAll()
                );
        http.rememberMe(rememberMe -> rememberMe
                .key("mySecretKey")
                .userDetailsService(this.accountDetailsService)
                .tokenValiditySeconds(20)
        );
        http.logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JwtToken")
        );
        http.sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                );

        http.addFilterBefore(new JwtGeneratorFilter(authenticationManager(), accountDetailsService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
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








