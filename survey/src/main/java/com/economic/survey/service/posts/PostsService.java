package com.economic.survey.service.posts;

import com.economic.survey.domain.comments.Comment;
import com.economic.survey.domain.comments.CommentRepository;
import com.economic.survey.domain.likes.LikesRepository;
import com.economic.survey.domain.likes.Likes;
import com.economic.survey.domain.posts.Posts;
import com.economic.survey.domain.posts.PostsRepository;
import com.economic.survey.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAllDesc() {
        return postsRepository.findAllDesc().stream()
                .map(PostsListResponseDto::new) // 여기서 DTO 변환되면서 마스킹됨
                .collect(Collectors.toList());
    }
    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts posts = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        // JPA의 영속성 컨텍스트 덕분에 update 쿼리를 날릴 필요 없이 값만 변경하면 됩니다.
        posts.update(requestDto.getTitle(), requestDto.getContent());

        return id;
    }

    @Transactional
    public void delete(Long id) {
        Posts posts = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        postsRepository.delete(posts);
    }
    // 1. 게시글 상세 조회 (조회수/댓글/좋아요 포함)
    @Transactional(readOnly = true)
    public PostsResponseDto findById(Long id, String userEmail) { // 파라미터에 userEmail 추가
        Posts entity = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        // 1. 좋아요 개수 가져오기
        int likesCount = likesRepository.countByPosts(entity);

        // 2. 현재 로그인한 사용자가 좋아요를 눌렀는지 확인
        boolean isLiked = false;
        if (userEmail != null) {
            isLiked = likesRepository.findByUserEmailAndPosts(userEmail, entity).isPresent();
        }

        // 3. 3개의 값을 생성자에 전달
        return new PostsResponseDto(entity, likesCount, isLiked);
    }

    // 2. 좋아요 토글 (누르면 ON, 다시 누르면 OFF)
    @Transactional
    public boolean toggleLike(Long postsId, String userEmail) {
        Posts posts = postsRepository.findById(postsId).orElseThrow();
        Optional<Likes> likes = likesRepository.findByUserEmailAndPosts(userEmail, posts);

        if (likes.isPresent()) {
            likesRepository.delete(likes.get()); // 이미 있으면 취소
            return false;
        } else {
            likesRepository.save(Likes.builder().posts(posts).userEmail(userEmail).build()); // 없으면 추가
            return true;
        }
    }

    // 3. 댓글 저장
    @Transactional
    public Long saveComment(Long postsId, CommentSaveRequestDto dto, String userEmail) {
        Posts posts = postsRepository.findById(postsId).orElseThrow();
        Comment parent = null;

        // 부모 댓글이 있다면 조회
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId()).orElseThrow();
        }

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .author(userEmail)
                .posts(posts)
                .parent(parent) // 부모 설정
                .build();

        return commentRepository.save(comment).getId();
    }
}