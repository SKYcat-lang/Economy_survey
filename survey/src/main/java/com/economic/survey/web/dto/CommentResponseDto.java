package com.economic.survey.web.dto;
import com.economic.survey.domain.comments.Comment;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String author;
    private String modifiedDate;
    private List<CommentResponseDto> children; // 자식 댓글들

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = maskEmail(comment.getAuthor());
        this.modifiedDate = comment.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        // 자식 댓글들을 DTO로 변환 (재귀)
        this.children = comment.getChildren().stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    private String maskEmail(String email) {
        if (email == null) return "(알수없음)";

        // @ 앞부분만 잘라서 앞 3글자 + *** 처리
        String idPart = email.split("@")[0];
        if (idPart.length() < 3) return idPart + "***";
        return idPart.substring(0, 3) + "***";
    }
}