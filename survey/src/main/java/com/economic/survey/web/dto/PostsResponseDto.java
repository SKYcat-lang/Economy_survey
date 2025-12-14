package com.economic.survey.web.dto;
import com.economic.survey.domain.posts.Posts;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostsResponseDto {
    private Long id;
    private String title;
    private String content;
    private String author; // 여기가 마스킹될 필드
    private String modifiedDate; // 날짜 포맷팅 (선택사항)
    private int likesCount;
    private boolean isLiked;
    private List<CommentResponseDto> comments;

    public PostsResponseDto(Posts entity, int likesCount, boolean isLiked) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();

        // ★ 수정됨: 그냥 가져오는 게 아니라 마스킹 메소드를 거칩니다.
        this.author = maskEmail(entity.getAuthor());

        // 날짜 포맷팅 (선택사항)
        // ★ 핵심: 날짜를 예쁘게 포맷팅
        this.modifiedDate = toStringDateTime(entity.getModifiedDate());

        this.likesCount = likesCount;
        this.isLiked = isLiked;

        this.comments = entity.getComments().stream()
                .filter(c -> c.getParent() == null)
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    // ★ 이메일 마스킹 로직 추가 (gahee3074@gmail.com -> gah***)
    private String maskEmail(String email) {
        if (email == null) return "(알수없음)";

        // @ 앞부분만 잘라서 앞 3글자 + *** 처리
        String idPart = email.split("@")[0];
        if (idPart.length() < 3) return idPart + "***";
        return idPart.substring(0, 3) + "***";
    }

    // 날짜 포맷팅 메소드
    private String toStringDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return localDateTime != null ? localDateTime.format(formatter) : "";
    }
}