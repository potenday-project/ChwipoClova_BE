package com.chwipoClova.qa.entity;

import com.chwipoClova.interview.entity.Interview;
import com.chwipoClova.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Entity(name = "Qa")
@Table(name = "Qa")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties()
@DynamicInsert
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Qa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qaId")
    @Schema(description = "질문답변 ID")
    private Long qaId;

    @Column(name = "question")
    @Schema(description = "질문")
    private String question;

    @Column(name = "answer")
    @Schema(description = "답변")
    private String answer;

    @Column(name = "aiAnswer")
    @Schema(description = "AI답변")
    private String aiAnswer;

    @Column(name = "regDate")
    @Schema(description = "등록일")
    private Date regDate;

    @Column(name = "modifyDate")
    @Schema(description = "수정일")
    private Date modifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewId")
    private Interview interview;

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
