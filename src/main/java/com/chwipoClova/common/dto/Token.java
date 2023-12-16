package com.chwipoClova.common.dto;

import com.chwipoClova.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Data
@Entity(name = "Token")
@Table(name = "Token")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties()
@DynamicInsert
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Token VO")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;
    @NotBlank
    private String refreshToken;

    private Date regDate;

    private Date modifyDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    public Token(String token, User user) {
        this.refreshToken = token;
        this.user = user;
    }

    public Token updateToken(String token) {
        this.refreshToken = token;
        return this;
    }

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

    public TokenEditor.TokenEditorBuilder toEditor() {
        return TokenEditor.builder()
                .refreshToken(refreshToken);
    }

    public void edit(TokenEditor tokenEditor) {
        refreshToken = tokenEditor.getRefreshToken();
    }
}
