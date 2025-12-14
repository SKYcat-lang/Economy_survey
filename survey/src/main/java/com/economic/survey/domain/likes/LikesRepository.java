package com.economic.survey.domain.likes;

import com.economic.survey.domain.posts.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserEmailAndPosts(String userEmail, Posts posts);
    // 게시글별 좋아요 개수 세기 (추가)
    int countByPosts(Posts posts);
}