package com.example.security.services;

import com.example.security.model.Account;
import com.example.security.model.AccountDetails;
import com.example.security.repo.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Account> account = this.accountRepository.findByUsername(username);
        if(account.isPresent()){
            return new AccountDetails(account.get());
        }

        throw new UsernameNotFoundException("User not found");
    }


    public void saveAccount(Account account){
        this.accountRepository.save(account);
    }


    public Optional<Account> findByUsername(String username){
        return this.accountRepository.findByUsername(username);
    }
}
