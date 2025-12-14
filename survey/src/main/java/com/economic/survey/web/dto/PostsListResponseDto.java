package com.economic.survey.web.dto;

import com.economic.survey.domain.posts.Posts;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class PostsListResponseDto {
    private Long id;
    private String title;
    private String author;
    private String modifiedDate; // LocalDateTime -> String으로 변경

    public PostsListResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.author = maskEmail(entity.getAuthor());
        // ★ 핵심: 날짜를 예쁘게 포맷팅
        this.modifiedDate = toStringDateTime(entity.getModifiedDate());
    }

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