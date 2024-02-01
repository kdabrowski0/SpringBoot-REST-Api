package com.ug.projekt1blog.services;

import com.ug.projekt1blog.models.Account;
import com.ug.projekt1blog.models.Post;
import com.ug.projekt1blog.models.Rating;
import com.ug.projekt1blog.repositories.AccountRepository;
import com.ug.projekt1blog.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {


    private final PostRepository postRepository;
    private final AccountRepository accountRepository;

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }


    public List<Post> getAllPosts(String title) {
        if (title != null) {
            title = title.trim().toLowerCase().replaceAll("\\s+", " ");
            if (!title.isEmpty()) {
                return postRepository.findByTitleLike("%" + title + "%");
            }
        }
        return postRepository.findAll();
    }

    public Post savePost(Post post) {
        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    public List<Account> getAllAuthors() {
        return accountRepository.findAll();
    }



}
