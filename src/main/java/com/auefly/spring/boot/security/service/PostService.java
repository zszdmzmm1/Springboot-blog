package com.auefly.spring.boot.security.service;

import com.auefly.spring.boot.security.dto.PostDto;
import com.auefly.spring.boot.security.entity.Post;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Page<Post> findAll(int pageNumber, int pageSize);

    Post savePost(PostDto postDto);

    void deleteByIds(List<Long> ids);

    Optional<Post> findById(Long id);

    void deleteById(Long id);

    Page<Post> findAllPosts(int currentPage, int pageSize);

    Page<Post> findAllResources(int currentPage, int pageSize);
}
