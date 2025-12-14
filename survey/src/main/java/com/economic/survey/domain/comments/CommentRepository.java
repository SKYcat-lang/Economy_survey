package com.economic.survey.domain.comments;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 기본 CRUD 외에 특정 게시글의 댓글만 가져오는 등의 기능이 필요하다면
    // 여기에 메소드를 추가할 수 있습니다.
    // 현재 구조(Posts에서 @OneToMany로 로딩)에서는 기본 메소드만으로 충분합니다.
}