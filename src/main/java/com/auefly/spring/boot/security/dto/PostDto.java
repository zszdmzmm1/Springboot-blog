package com.auefly.spring.boot.security.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PostDto {
    private Long id;
    private Long userId;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @Pattern(regexp = "post|resource")
    private String type = "post";

    private String description;

    private boolean status;

    private String cover;
}
