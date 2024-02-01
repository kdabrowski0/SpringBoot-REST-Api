package com.ug.projekt1blog.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    @NotBlank(message = "Title cannot be blank")
    @NotEmpty(message = "Content cannot be null")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters long")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @NotEmpty(message = "Content cannot be empty")
    @Size(min = 3, message = "Content must be at least 3 characters long")
    @Column(columnDefinition = "TEXT")
    private String content;

    private List<String> authorEmails;
}
