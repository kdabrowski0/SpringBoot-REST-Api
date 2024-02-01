package com.ug.projekt1blog.controllers;

import com.ug.projekt1blog.dto.PostsDto;
import com.ug.projekt1blog.models.Account;
import com.ug.projekt1blog.models.Post;
import com.ug.projekt1blog.models.Rating;
import com.ug.projekt1blog.services.PostService;
import com.ug.projekt1blog.services.RatingService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class HomeController {

    private final PostService postService;

    @GetMapping("/")
    public List<PostsDto> index(@RequestParam(value = "title", required = false) String title) {
        return postService.getAllPosts(title).stream()
                .map(PostsDto::fromPost)
                .collect(Collectors.toList());
    }
}