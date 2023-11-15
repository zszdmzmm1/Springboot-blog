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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    PostRepository postRepository;

    @Override
    public Page<Post> findAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("id").descending());
        return postRepository.findAll(pageable);
    }

    @Override
    public Post savePost(PostDto postDto) {
        Post post = new Post();

        if (postDto.getId() != null) {
            post = postRepository.findById(postDto.getId()).get();
            post.setUpdated_at(LocalDateTime.now());
        } else {
            post.setCreated_at(LocalDateTime.now());
        }

        post.setContent(postDto.getContent());
        post.setTitle(postDto.getTitle());
        post.setCover(postDto.getCover());
        post.setDescription(postDto.getDescription());
        post.setUser(new User(postDto.getUserId()));
        return postRepository.save(post);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        postRepository.deleteAllById(ids);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public void deleteByid(Long id) {
        postRepository.deleteById(id);
    }
}
