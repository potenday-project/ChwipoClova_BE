package com.chwipoClova.interview.service;

import com.chwipoClova.common.exception.CommonException;
import com.chwipoClova.common.exception.ExceptionCode;
import com.chwipoClova.interview.entity.Interview;
import com.chwipoClova.interview.repository.InterviewRepository;
import com.chwipoClova.interview.request.InterviewInsertReq;
import com.chwipoClova.interview.response.InterviewInsertRes;
import com.chwipoClova.qa.request.QaQuestionInsertReq;
import com.chwipoClova.qa.response.QaQuestionInsertRes;
import com.chwipoClova.qa.service.QaService;
import com.chwipoClova.recruit.request.RecruitInsertReq;
import com.chwipoClova.recruit.response.RecruitInsertRes;
import com.chwipoClova.recruit.service.RecruitService;
import com.chwipoClova.resume.entity.Resume;
import com.chwipoClova.resume.repository.ResumeRepository;
import com.chwipoClova.user.entity.User;
import com.chwipoClova.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class InterviewService {

    private final ResumeRepository resumeRepository;

    private final UserRepository userRepository;

    private final RecruitService recruitService;

    private final InterviewRepository interviewRepository;

    private final QaService qaService;

    @Transactional
    public InterviewInsertRes insertInterview(InterviewInsertReq interviewInsertReq, MultipartFile file) throws IOException {
        Long userId = interviewInsertReq.getUserId();
        Long resumeId = interviewInsertReq.getResumeId();
        String recruitContent = interviewInsertReq.getRecruitContent();

        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));
        Resume resume = resumeRepository.findByUserUserIdAndResumeId(userId, resumeId).orElseThrow(() -> new CommonException(ExceptionCode.RESUME_NULL.getMessage(), ExceptionCode.RESUME_NULL.getCode()));

        // 채용 공고 등록 및 조회
        RecruitInsertReq recruitInsertReq = new RecruitInsertReq();
        recruitInsertReq.setUserId(userId);
        recruitInsertReq.setRecruitContent(recruitContent);
        recruitInsertReq.setFile(file);
        RecruitInsertRes recruitInsertRes = recruitService.insertRecruit(recruitInsertReq);

        String title = recruitInsertRes.getTitle();
        String recruitSummary = recruitInsertRes.getSummary();
        String resumeSummary = resume.getSummary();

        // TODO 이력서 요약과 채용공고 요약을 이용해서 질문, AI 답변 생성
        String q1 = "질문1입니다.";
        String qAi1 = "질문1 AI 답변입니다.";

        String q2 = "질문2입니다.";
        String qAi2 = "질문2 AI 답변입니다.";

        // 면접 저장
        Interview interview = Interview.builder()
                .title(title)
                .recruitSummary(recruitSummary)
                .resumeSummary(resumeSummary)
                .user(user)
                .build();
        Interview interviewRst = interviewRepository.save(interview);

        // 질문 답변 저장
        List<QaQuestionInsertReq> qaQuestionInsertReqList = new ArrayList<>();
        QaQuestionInsertReq qaQuestionInsertReq1 = new QaQuestionInsertReq();
        qaQuestionInsertReq1.setInterview(interview);
        qaQuestionInsertReq1.setQuestion(q1);
        qaQuestionInsertReq1.setAiAnswer(qAi1);
        qaQuestionInsertReqList.add(qaQuestionInsertReq1);

        QaQuestionInsertReq qaQuestionInsertReq2 = new QaQuestionInsertReq();
        qaQuestionInsertReq2.setInterview(interview);
        qaQuestionInsertReq2.setQuestion(q2);
        qaQuestionInsertReq2.setAiAnswer(qAi2);
        qaQuestionInsertReqList.add(qaQuestionInsertReq2);
        List<QaQuestionInsertRes> questionData = qaService.insertQaQuestionList(qaQuestionInsertReqList);

        return InterviewInsertRes.builder()
                .interviewId(interviewRst.getInterviewId())
                .userId(userId)
                .title(interviewRst.getTitle())
                .regDate(interviewRst.getRegDate())
                .modifyDate(interviewRst.getModifyDate())
                .questionData(questionData)
                .build();
    }

}
