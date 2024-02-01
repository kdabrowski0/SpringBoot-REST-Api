package com.ug.projekt1blog.controllers;

import com.ug.projekt1blog.dto.LoginDto;
import com.ug.projekt1blog.dto.SignUpDto;
import com.ug.projekt1blog.models.Account;
import com.ug.projekt1blog.models.Authority;
import com.ug.projekt1blog.repositories.AccountRepository;
import com.ug.projekt1blog.repositories.AuthorityRepository;
import com.ug.projekt1blog.services.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashSet;
import java.util.Set;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccountService accountService;

    private final AuthorityRepository authorityRepository;

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("User logged in successfully", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpDto signUpDto){

        if(accountRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }
        if(!signUpDto.getPassword().equals(signUpDto.getConfirmPassword())){
            return new ResponseEntity<>("Passwords do not match!", HttpStatus.BAD_REQUEST);
        }
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById("ROLE_USER").ifPresent(authorities::add);
        Account newAccount = Account.builder()
                .email(signUpDto.getEmail())
                .lastName(signUpDto.getLastName())
                .firstName(signUpDto.getFirstName())
                .confirmPassword(signUpDto.getConfirmPassword())
                .password(signUpDto.getPassword())
                .authorities(authorities)
                .build();


        accountService.saveAccount(newAccount);
            return new ResponseEntity<>("User registered successfully", HttpStatus.OK);

    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return new ResponseEntity<>("User logged out successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not logged in", HttpStatus.BAD_REQUEST);
    }

}