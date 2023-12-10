package com.chwipoClova.resume.entity;

import com.chwipoClova.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Entity(name = "Resume")
@Table(name = "Resume")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties()
@DynamicInsert
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "이력서 정보 VO")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resumeId")
    @Schema(description = "이력서ID")
    private Long resumeId;

    @Column(name = "fileName")
    @Schema(description = "파일이름")
    private String fileName;

    @Column(name = "filePath")
    @Schema(description = "파일경로")
    private String filePath;

    @Column(name = "fileSize")
    @Schema(description = "파일크기")
    private Long fileSize;

    @Column(name = "orginalFileName")
    @Schema(description = "원본파일이름")
    private String orginalFileName;

    @Column(name = "summary")
    @Schema(description = "요약")
    private String summary;

    @Column(name = "regDate")
    @Schema(description = "등록일")
    private Date regDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    // @PrePersist 메서드 정의 (최초 등록시 호출)
    @PrePersist
    public void prePersist() {
        this.regDate = new Date(); // 현재 날짜와 시간으로 등록일 설정
    }
}
