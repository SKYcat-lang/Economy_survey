package com.economic.survey.web;

import com.economic.survey.config.auth.dto.SessionUser;
import com.economic.survey.service.posts.PostsService;
import com.economic.survey.web.dto.PostsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final HttpSession httpSession;
    private final PostsService postsService;

    @GetMapping("/")
    public String loginPage(Model model) {
        // 1. 이미 로그인 된 사용자인지 확인
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        // 2. 로그인 된 상태라면 굳이 로그인 버튼을 볼 필요 없으므로 /main으로 리다이렉트
        if (user != null) {
            return "redirect:/main";
        }

        // 3. 비로그인 상태라면 로그인 버튼만 있는 페이지 반환
        return "login";
    }

    @GetMapping("/main")
    public String mainPage(Model model) {
        // SecurityConfig에서 막아놨기 때문에, 여기까지 들어왔다면 무조건 로그인 된 상태임.
        // 하지만 세션 정보를 쓰기 위해 가져옴.
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        model.addAttribute("isMain", true);

        // 실제 서비스 화면 반환
        return "main";
    }

    @GetMapping("/board")
    public String boardPage(Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        model.addAttribute("isBoard", true);

        // 게시글 목록은 여기서만 필요함
        model.addAttribute("posts", postsService.findAllDesc());

        return "board"; // board.mustache 반환
    }

    // 1. 게시글 상세 페이지 (view)
    @GetMapping("/posts/view/{id}")
    public String postsView(@PathVariable Long id, Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        String email = (user != null) ? user.getEmail() : null; // 이메일 추출

        if(user != null) model.addAttribute("userName", user.getName());
        model.addAttribute("isBoard", true);

        // 수정된 findById 호출 (id, email 전달)
        PostsResponseDto dto = postsService.findById(id, email);

        model.addAttribute("post", dto);
        return "posts-view";
    }

    // 2. 게시글 수정 페이지 (update)
    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        String email = (user != null) ? user.getEmail() : null;

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        model.addAttribute("isBoard", true);

        // 수정 페이지에서도 동일한 DTO를 쓰므로 email을 넘겨줍니다.
        // (수정 화면에서 좋아요 여부는 안 쓰더라도 생성자 규격은 맞춰야 함)
        PostsResponseDto dto = postsService.findById(id, email);
        model.addAttribute("post", dto);

        return "posts-update";
    }

    // 2. 게시글 수정 페이지 (update)
    @GetMapping("/graph")
    public String graphIndex(Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        model.addAttribute("isGraph", true);

        return "graph";
    }
}