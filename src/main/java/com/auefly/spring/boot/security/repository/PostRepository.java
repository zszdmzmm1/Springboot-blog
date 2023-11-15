package com.auefly.spring.boot.security.repository;

import com.auefly.spring.boot.security.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findFirstByTitle(String title);

    Page<Post> findAllByType(String resource, Pageable pageable);
}
