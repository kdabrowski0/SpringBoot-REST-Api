package com.ug.projekt1blog.services;

import com.ug.projekt1blog.models.Account;
import com.ug.projekt1blog.models.Authority;
import com.ug.projekt1blog.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ug.projekt1blog.repositories.AuthorityRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AuthorityRepository authorityRepository;

    public void saveAccount(Account account) {
        Optional<Account> existingAccount = accountRepository.findByEmail(account.getEmail());
        if (existingAccount.isPresent() && !existingAccount.get().getId().equals(account.getId())) {
            throw new IllegalStateException("Email is already taken");
        }

        if (account.getId() == null ) {
            if (account.getAuthorities().isEmpty() || account.getAuthorities() == null) {
                Set<Authority> authorities = new HashSet<>();
                authorityRepository.findById("ROLE_USER").ifPresent(authorities::add);
                account.setAuthorities(authorities);
            }
            account.setCreatedAt(LocalDateTime.now());
        }
        account.setUpdatedAt(LocalDateTime.now());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setConfirmPassword(passwordEncoder.encode(account.getConfirmPassword()));

        accountRepository.save(account);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<Account> getAllAccountsExceptCurrent(String email) {
        return accountRepository.findAllByEmailNot(email);
    }

    public List<Account> getAccountsByIds(List<Long> ids) {
        return accountRepository.findAllById(ids);
    }

    public List<Account> getAccountsByEmails(List<String> emails) {
        return accountRepository.findAllByEmailIn(emails);
    }




}
