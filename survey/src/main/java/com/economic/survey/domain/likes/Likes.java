package com.economic.survey.domain.likes;

import com.economic.survey.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(
        uniqueConstraints={
                @UniqueConstraint(
                        name="likes_unique",
                        columnNames={"posts_id", "userEmail"}
                )
        }
)
public class Likes {
    // 한 사람이 한 글에 좋아요는 한 번만!

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "posts_id")
    private Posts posts;

    private String userEmail; // 추천한 사람 이메일

    @Builder
    public Likes(Posts posts, String userEmail) {
        this.posts = posts;
        this.userEmail = userEmail;
    }
}