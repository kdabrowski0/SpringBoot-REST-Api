package com.ug.projekt1blog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUpDto {
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one uppercase letter, one lowercase letter and one digit")
    private String password;
    @NotBlank(message = "Password cannot be blank")
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one uppercase letter, one lowercase letter and one digit")
    private String confirmPassword;

    @NotBlank(message = "First name cannot be blank")
    @NotEmpty(message = "First name cannot be empty")
    @Size(min = 3, max = 100, message = "First name must be between 3 and 100 characters long")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @NotEmpty(message = "Last name cannot be empty")
    @Size(min = 3, max = 100, message = "Last name must be between 3 and 100 characters long")
    private String lastName;
}