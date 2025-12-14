package com.economic.survey.domain.user;
import com.economic.survey.domain.BaseTimeEntity; // 생성시간/수정시간 자동화 (선택사항, 아래 설명 참조)
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "USERS") // H2에서 User는 예약어일 수 있어 테이블명 명시
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String picture;

    // Enum을 문자열(GUEST, USER)로 DB에 저장
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 어떤 소셜 로그인인지 구분 (google, naver 등)
    @Column
    private String provider;

    // 소셜 로그인 제공자의 PK (구글의 sub 값 등)
    @Column
    private String providerId;

    @Builder
    public User(String name, String email, String picture, Role role, String provider, String providerId) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    // 소셜 로그인 시, 프로필 사진이나 이름이 변경되면 업데이트하기 위한 메소드
    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}