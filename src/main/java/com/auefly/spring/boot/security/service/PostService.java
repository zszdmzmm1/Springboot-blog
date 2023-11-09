package com.auefly.spring.boot.security.service;

import com.auefly.spring.boot.security.entity.Post;
import org.springframework.data.domain.Page;

public interface PostService {
    Page<Post> findAll(int pageNumber, int pageSize);
}
