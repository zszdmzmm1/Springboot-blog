package com.auefly.spring.boot.security.service;

import com.auefly.spring.boot.security.dto.PostDto;
import com.auefly.spring.boot.security.entity.Post;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    Page<Post> findAll(int pageNumber, int pageSize);

    Post savePost(PostDto postDto);

    void deleteByIds(List<Long> ids);
}
