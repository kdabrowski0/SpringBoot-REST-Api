package com.ug.projekt1blog.dto;

import com.ug.projekt1blog.models.Comment;
import com.ug.projekt1blog.models.Post;
import com.ug.projekt1blog.models.Rating;
import com.ug.projekt1blog.models.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailsDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> authorEmails;
    private List<Comment> comments;
    private List<Rating> ratings;
    private double averageRating;

    public static PostDetailsDto fromPost(Post post, List<Comment> comments, List<Rating> ratings) {
        double averageRating = calculateAverageRating(ratings);
        return PostDetailsDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .authorEmails(getAuthorEmails(post.getAuthors()))
                .comments(comments)
                .ratings(ratings)
                .averageRating(averageRating)
                .build();
    }

    private static List<String> getAuthorEmails(List<Account> authors) {
        return authors.stream()
                .map(Account::getEmail)
                .toList();
    }

    private static double calculateAverageRating(List<Rating> ratings) {
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0.0);
    }
}