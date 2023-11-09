package com.auefly.spring.boot.security.repository;

import com.auefly.spring.boot.security.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
