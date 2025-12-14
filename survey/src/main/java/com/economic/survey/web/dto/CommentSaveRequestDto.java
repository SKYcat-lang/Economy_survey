package com.economic.survey.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentSaveRequestDto {
    private String content;
    private Long parentId; // 대댓글일 경우 부모 댓글 ID, 일반 댓글이면 null

    // 테스트 코드 등에서 사용할 수 있도록 빌더 패턴 적용 가능
    public CommentSaveRequestDto(String content, Long parentId) {
        this.content = content;
        this.parentId = parentId;
    }
}