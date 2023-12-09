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

    public UsersEditor.UsersEditorBuilder toEditor() {
        return UsersEditor.builder()
                .name(name)
                .modifyDate(modifyDate);
    }

    public void edit(UsersEditor usersEditor) {
        name = usersEditor.getName();
        modifyDate = usersEditor.getModifyDate();
    }
}