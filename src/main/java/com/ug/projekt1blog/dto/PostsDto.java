package com.ug.projekt1blog.dto;

import com.ug.projekt1blog.models.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ug.projekt1blog.models.Account;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostsDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> authorEmails;

    public static PostsDto fromPost(Post post) {
        return PostsDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .authorEmails(getAuthorEmails(post.getAuthors()))
                .build();
    }

    private static List<String> getAuthorEmails(List<Account> authors) {
        return authors.stream()
                .map(Account::getEmail)
                .collect(Collectors.toList());
    }
}
