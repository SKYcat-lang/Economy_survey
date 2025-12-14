package com.economic.survey.config.auth;

import com.economic.survey.config.auth.dto.OAuthAttributes;
import com.economic.survey.config.auth.dto.SessionUser;
import com.economic.survey.domain.user.User;
import com.economic.survey.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession; // Session 사용
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본 OAuth2UserService 객체 생성
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        // 2. 구글 등에서 유저 정보를 가져옵니다.
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 3. 현재 로그인 진행 중인 서비스를 구분하는 코드 (google, naver 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 4. OAuth2 로그인 진행 시 키가 되는 필드값 (PK) (구글은 "sub", 네이버/카카오는 다름)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 5. OAuthAttributes: attribute를 담을 클래스 (개발자가 생성)
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 6. 유저 정보 저장 또는 업데이트
        User user = saveOrUpdate(attributes);

        // 7. 세션에 사용자 정보 저장 (SessionUser DTO 사용)
        httpSession.setAttribute("user", new SessionUser(user));

        // 8. 로그인 성공 리턴
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    // 구글 사용자 정보가 업데이트 되었을 때를 대비해 update 기능 구현
    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}