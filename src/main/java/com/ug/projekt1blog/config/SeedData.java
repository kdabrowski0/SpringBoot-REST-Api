package com.ug.projekt1blog.config;

import com.ug.projekt1blog.models.*;
import com.ug.projekt1blog.repositories.AuthorityRepository;
import com.ug.projekt1blog.services.AccountService;
import com.ug.projekt1blog.services.CommentService;
import com.ug.projekt1blog.services.PostService;
import com.ug.projekt1blog.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SeedData implements CommandLineRunner {

    private final CommentService commentService;
    private final PostService postService;
    private final AccountService accountService;
    private final AuthorityRepository authorityRepository;
    private final RatingService ratingService;

    private final ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        List<Post> posts = postService.getAllPosts(null);

        if (posts.isEmpty()) {
            ApplicationContext context = new ClassPathXmlApplicationContext("seedData.xml");

            Authority user = context.getBean("userAuthority", Authority.class);
            Authority admin = context.getBean("adminAuthority", Authority.class);
            Account account1 = context.getBean("adminAccount", Account.class);
            Account account2 = context.getBean("userAccount", Account.class);
            Post post1 = context.getBean("post1", Post.class);
            Post post2 = context.getBean("post2", Post.class);
            Rating rating = context.getBean("rating", Rating.class);
            Comment comment1 = context.getBean("comment1", Comment.class);
            Comment comment2 = context.getBean("comment2", Comment.class);
            Comment comment3 = context.getBean("comment3", Comment.class);

            authorityRepository.save(user);
            authorityRepository.save(admin);
            accountService.saveAccount(account1);
            accountService.saveAccount(account2);
            postService.savePost(post1);
            postService.savePost(post2);
            ratingService.saveRating(rating);
            commentService.saveComment(comment1);
            commentService.saveComment(comment2);
            commentService.saveComment(comment3);
        }
    }
}