package com.ug.projekt1blog.controllers;

import com.ug.projekt1blog.dto.PostsDto;
import com.ug.projekt1blog.models.Account;
import com.ug.projekt1blog.models.Post;
import com.ug.projekt1blog.services.AccountService;
import jakarta.faces.annotation.RequestMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserPostsController {

    private final AccountService accountService;

    @GetMapping("/posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PostsDto>> getUserPosts(Principal principal) {
        String email = principal.getName();
        Optional<Account> optionalAccount = accountService.getAccountByEmail(email);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            List<Post> userPosts = account.getAuthoredPosts();

            List<PostsDto> postsDtoList = userPosts.stream()
                    .map(PostsDto::fromPost)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(postsDtoList);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

}