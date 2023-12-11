package com.chwipoClova.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Entity(name = "User")
@Table(name = "User")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties()
@DynamicInsert
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "유저 정보 VO")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    @Schema(description = "아이디")
    private Long userId;

    @Column(name = "name")
    @Schema(description = "이름")
    private String name;

    @Column(name = "email")
    @Schema(description = "이메일")
    private String email;

    @Column(name = "regDate")
    @Schema(description = "가입일")
    private Date regDate;

    @Column(name = "modifyDate")
    @Schema(description = "수정일")
    private Date modifyDate;

    @Column(name = "snsType")
    @Schema(description = "소셜 로그인 플랫폼 (1 : 카카오)")
    private Integer snsType;

    @Column(name = "snsId")
    @Schema(description = "소셜회원 ID")
    private Long snsId;

    @Column(name = "thumbnailImage")
    @Schema(description = "프로필 미리보기 이미지")
    private String thumbnailImage;

    @Column(name = "profileImage")
    @Schema(description = "프로필 이미지")
    private String profileImage;

    // @PrePersist 메서드 정의 (최초 등록시 호출)
    @PrePersist
    public void prePersist() {
        this.regDate = new Date(); // 현재 날짜와 시간으로 등록일 설정
    }

    // @PreUpdate 메서드 정의 (업데이트 시 호출)
    @PreUpdate
    public void preUpdate() {
        this.modifyDate = new Date(); // 현재 날짜와 시간으로 수정일 업데이트
    }

    public UserEditor.UserEditorBuilder toEditor() {
        return UserEditor.builder()
                .name(name)
                .modifyDate(modifyDate);
    }

    public void edit(UserEditor usersEditor) {
        name = usersEditor.getName();
        modifyDate = usersEditor.getModifyDate();
    }
}
