package com.economic.survey.web;

import com.economic.survey.config.auth.dto.SessionUser;
import com.economic.survey.service.posts.PostsService;
import com.economic.survey.web.dto.CommentSaveRequestDto;
import com.economic.survey.web.dto.PostsSaveRequestDto;
import com.economic.survey.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;
    private final HttpSession httpSession;

    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto) {
        // 보안 강화: 작성자는 클라이언트(JS)에서 받는 게 아니라, 서버 세션에서 직접 꺼냅니다.
        // 이렇게 하면 남의 이름으로 글을 쓰는 조작을 막을 수 있습니다.
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        PostsSaveRequestDto newDto = PostsSaveRequestDto.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .author(user.getEmail()) // 세션 이메일 강제 주입
                .build();

        return postsService.save(newDto);
    }

    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
        return postsService.update(id, requestDto);
    }

    @DeleteMapping("/api/v1/posts/{id}")
    public Long delete(@PathVariable Long id) {
        postsService.delete(id);
        return id;
    }

    // 댓글 작성
    @PostMapping("/api/v1/posts/{id}/comments")
    public Long saveComment(@PathVariable Long id, @RequestBody CommentSaveRequestDto dto) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        return postsService.saveComment(id, dto, user.getEmail());
    }

    // 좋아요
    @PostMapping("/api/v1/posts/{id}/likes")
    public boolean like(@PathVariable Long id) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        return postsService.toggleLike(id, user.getEmail());
    }
}