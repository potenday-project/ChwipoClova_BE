package com.chwipoClova.interview.entity;

import com.chwipoClova.qa.entity.QaEditor;
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

    @Column(name = "status")
    @Schema(description = "상태 (0 : 미완료, 1 : 완료)")
    private Integer status;

    @Column(name = "feedback")
    @Schema(description = "면접관의속마음")
    private String feedback;
    
    @Column(name = "regDate")
    @Schema(description = "등록일")
    private Date regDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    // @PrePersist 메서드 정의 (최초 등록시 호출)
    @PrePersist
    public void prePersist() {
        this.regDate = new Date(); // 현재 날짜와 시간으로 등록일 설정
    }


    public InterviewEditor.InterviewEditorBuilder toEditor() {
        return InterviewEditor.builder()
                .status(status)
                .feedback(feedback);
    }

    public void edit(InterviewEditor interviewEditor) {
        status = interviewEditor.getStatus();
        feedback = interviewEditor.getFeedback();
    }

}
