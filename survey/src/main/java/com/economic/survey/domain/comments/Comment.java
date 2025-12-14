package com.economic.survey.domain.comments;

import com.economic.survey.domain.BaseTimeEntity;
import com.economic.survey.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author; // 작성자 이메일

    @ManyToOne
    @JoinColumn(name = "posts_id")
    private Posts posts;

    // ★ 대댓글의 핵심: 부모 댓글 참조
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // ★ 자식 댓글 리스트 (OneToMany)
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Comment(String content, String author, Posts posts, Comment parent) {
        this.content = content;
        this.author = author;
        this.posts = posts;
        this.parent = parent;
    }

    public void update(String content) {
        this.content = content;
    }
}