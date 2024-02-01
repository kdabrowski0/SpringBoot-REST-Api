package com.ug.projekt1blog.controllers;

import com.ug.projekt1blog.dto.PostDetailsDto;
import com.ug.projekt1blog.dto.PostDto;
import com.ug.projekt1blog.models.Account;
import com.ug.projekt1blog.models.Comment;
import com.ug.projekt1blog.models.Post;
import com.ug.projekt1blog.models.Rating;
import com.ug.projekt1blog.repositories.RatingRepository;
import com.ug.projekt1blog.services.AccountService;
import com.ug.projekt1blog.services.CommentService;
import com.ug.projekt1blog.services.PostService;
import com.ug.projekt1blog.services.RatingService;
import jakarta.faces.annotation.RequestMap;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.io.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final AccountService accountService;
    private final CommentService commentService;
    private final RatingService ratingService;


    @GetMapping("/{id}")
    public ResponseEntity<PostDetailsDto> getPost(@PathVariable Long id ) {
        Optional<Post> optionalPost = postService.getPostById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            List<Comment> comments = commentService.getAllPostComments(id);
            List<Rating> ratings = ratingService.getAllPostRatingByPostId(id);

            PostDetailsDto postDetailsDto = PostDetailsDto.fromPost(post, comments, ratings);

            return ResponseEntity.ok(postDetailsDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/comment/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, @PathVariable Long commentId, Principal principal) {
        Optional<Post> optionalPost = postService.getPostById(id);
        Optional<Comment> optionalComment = commentService.getCommentById(commentId);
        String email = principal.getName();
        Optional<Account> optionalAccount = accountService.getAccountByEmail(email);
        if (optionalPost.isPresent() && optionalComment.isPresent() && optionalAccount.isPresent()) {
            Comment comment = optionalComment.get();
            Account account = optionalAccount.get();
            if (comment.getAuthor().equals(account) || hasRoleAdmin(principal)) {
                commentService.deleteComment(comment);
                return ResponseEntity.ok("Comment deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to delete this comment");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/rating")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> addRating(@PathVariable Long id, @RequestParam(name = "ratingValue") int rating, Principal principal) {
        Optional<Post> optionalPost = postService.getPostById(id);

        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            String userEmail = principal.getName();
            Account account = accountService.getAccountByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("Account not found"));

            Optional<Rating> existingRating = ratingService.getRatingByAccountIdAndPostId(account.getId(), post.getId());

            if (existingRating.isPresent()) {
                Rating ratingToUpdate = existingRating.get();
                ratingToUpdate.setRatingValue(rating);
            } else {
                Rating newRating = Rating.builder()
                        .ratingValue(rating)
                        .post(post)
                        .account(account)
                        .build();
                ratingService.saveRating(newRating);
            }

            return ResponseEntity.ok("Rating added successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping("/{id}/comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> addComment(@PathVariable Long id,
                                             @RequestParam String content,
                                             Principal principal) {

        if (content == null|| content.isEmpty() || content.isBlank()  || content.length() < 3) {
            return ResponseEntity.badRequest().body("Comment  must be at least 3 characters long and cannot be empty or blank");
        }
        Optional<Post> optionalPost = postService.getPostById(id);
        String userEmail = principal.getName();
        Optional<Account> optionalAccount = accountService.getAccountByEmail(userEmail);

        try {
            Post post = optionalPost.orElseThrow(() -> new NotFoundException("Post not found"));
            Account account = optionalAccount.orElseThrow(() -> new NotFoundException("Account not found"));

            Comment comment = Comment.builder()
                    .content(content)
                    .post(post)
                    .author(account)
                    .build();
            commentService.saveComment(comment);

            return ResponseEntity.ok("Comment added successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/form")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> savePost(@RequestBody PostDto postDTO, Principal principal) {

        if (postDTO.getTitle() == null || postDTO.getTitle().isEmpty() || postDTO.getTitle().isBlank() || postDTO.getTitle().length() < 3) {
            return ResponseEntity.badRequest().body("Title must be at least 3 characters long and cannot be empty or blank");
        }
        if (postDTO.getContent() == null || postDTO.getContent().isEmpty() || postDTO.getContent().isBlank() || postDTO.getContent().length() < 3) {
            return ResponseEntity.badRequest().body("Content must be at least 3 characters long and cannot be empty or blank");
        }

        String email = principal.getName();
        Account account = accountService.getAccountByEmail(email).orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Post post = Post.builder()
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .authors(new ArrayList<>())
                .build();

        Set<Account> uniqueAuthors = new HashSet<>();

        uniqueAuthors.add(account);

        List<String> authorEmails = postDTO.getAuthorEmails();

        if (authorEmails != null && !authorEmails.isEmpty()) {
            List<Account> additionalAuthors = accountService.getAccountsByEmails(authorEmails);
            uniqueAuthors.addAll(additionalAuthors);
        }

        post.getAuthors().addAll(uniqueAuthors);

        postService.savePost(post);
        return ResponseEntity.ok("Post saved successfully");
    }
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updatePost(@PathVariable Long id, @Valid @RequestBody PostDto postDTO, Principal principal) {
        Optional<Post> optionalPost = postService.getPostById(id);

        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            String currentUsername = principal.getName();

            boolean isAuthor = existingPost.getAuthors().stream()
                    .anyMatch(author -> author.getEmail().equals(currentUsername));

            if (isAuthor || hasRoleAdmin(principal)) {
                existingPost.setTitle(postDTO.getTitle());
                existingPost.setContent(postDTO.getContent());

            List<Account> updatedAuthors = (postDTO.getAuthorEmails() != null && !postDTO.getAuthorEmails().isEmpty())
                    ? accountService.getAccountsByEmails(postDTO.getAuthorEmails())
                    : existingPost.getAuthors();
            existingPost.updateAuthors(updatedAuthors);

             postService.savePost(existingPost);

                return ResponseEntity.ok("Post updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to update this post");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deletePost(@PathVariable Long id, Principal principal) {
        Optional<Post> optionalPost = postService.getPostById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            String currentUsername = principal.getName();

            boolean isAuthor = post.getAuthors().stream()
                    .anyMatch(author -> author.getEmail().equals(currentUsername));

            if (isAuthor || hasRoleAdmin(principal)) {
                postService.deletePost(post);
                return ResponseEntity.ok("Post deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You don't have permission to delete this post");
            }
        } else {
            return new ResponseEntity<>("Post not found", HttpStatus.BAD_REQUEST);
        }
    }
    private boolean hasRoleAdmin(Principal principal) {
        return principal instanceof Authentication
                && ((Authentication) principal).getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}
