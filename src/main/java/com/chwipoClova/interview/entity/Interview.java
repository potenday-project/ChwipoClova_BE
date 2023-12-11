package com.chwipoClova.interview.entity;

import com.chwipoClova.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Entity(name = "Interview")
@Table(name = "Interview")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties()
@DynamicInsert
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interviewId")
    @Schema(description = "면접ID")
    private Long interviewId;

    @Column(name = "title")
    @Schema(description = "제목")
    private String title;

    @Column(name = "resumeSummary")
    @Schema(description = "이력서 요약")
    private String resumeSummary;

    @Column(name = "recruitSummary")
    @Schema(description = "채용공고 요약")
    private String recruitSummary;

    @Column(name = "regDate")
    @Schema(description = "등록일")
    private Date regDate;

    @Column(name = "modifyDate")
    @Schema(description = "수정일")
    private Date modifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

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

}
