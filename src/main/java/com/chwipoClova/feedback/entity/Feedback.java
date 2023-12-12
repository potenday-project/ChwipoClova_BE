package com.chwipoClova.feedback.entity;


import com.chwipoClova.qa.entity.Qa;
import com.chwipoClova.qa.entity.QaEditor;
import com.chwipoClova.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Entity(name = "Feedback")
@Table(name = "Feedback")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties()
@DynamicInsert
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedbackId")
    @Schema(description = "피드백 ID")
    private Long feedbackId;

    @Column(name = "type")
    @Schema(description = "피드백타입(1, 2, 3)")
    private Integer type;

    @Column(name = "content")
    @Schema(description = "피드백내용")
    private String content;

    @Column(name = "regDate")
    @Schema(description = "등록일")
    private Date regDate;

    @Column(name = "modifyDate")
    @Schema(description = "수정일")
    private Date modifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qaId")
    private Qa qa;

    // @PrePersist 메서드 정의 (최초 등록시 호출)
    @PrePersist
    public void prePersist() {
        this.regDate = new Date(); // 현재 날짜와 시간으로 등록일 설정
    }

    @PreUpdate
    public void preUpdate() {
        this.modifyDate = new Date(); // 현재 날짜와 시간으로 수정일 업데이트
    }

    public FeedbackEditor.FeedbackEditorBuilder toEditor() {
        return FeedbackEditor.builder()
                .content(content);
    }
    public void edit(FeedbackEditor feedbackEditor) {
        content = feedbackEditor.getContent();
    }
}
