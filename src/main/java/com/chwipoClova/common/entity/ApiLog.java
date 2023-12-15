package com.chwipoClova.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Entity(name = "ApiLog")
@Table(name = "ApiLog")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties()
@DynamicInsert
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "API 로그 VO")
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apiLogId")
    @Schema(description = "API LOG ID")
    private Long apiLogId;

    @Column(name = "userId")
    @Schema(description = "유저 ID")
    private Long userId;

    @Column(name = "apiUrl")
    @Schema(description = "api 호출 URL")
    private String apiUrl;

    @Column(name = "message")
    @Schema(description = "결과 메세지")
    private String message;

    @Column(name = "regDate")
    @Schema(description = "등록일")
    private Date regDate;

    @PrePersist
    public void prePersist() {
        this.regDate = new Date(); // 현재 날짜와 시간으로 등록일 설정
    }
}
