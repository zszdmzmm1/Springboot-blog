package com.auefly.spring.boot.security.service.impl;

import com.auefly.spring.boot.security.dto.PostDto;
import com.auefly.spring.boot.security.entity.Post;
import com.auefly.spring.boot.security.entity.User;
import com.auefly.spring.boot.security.repository.PostRepository;
import com.auefly.spring.boot.security.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    PostRepository postRepository;

    @Override
    public Page<Post> findAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return postRepository.findAll(pageable);
    }

    @Override
    public Post savePost(PostDto postDto) {
        Post post = new Post();
        post.setContent(postDto.getContent());
        post.setTitle(postDto.getTitle());
        post.setCover(postDto.getCover());
        post.setDescription(postDto.getDescription());
        post.setUser(new User(postDto.getUserId()));
        return postRepository.save(post);
    }
}
