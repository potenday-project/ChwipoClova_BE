package com.chwipoClova.qa.service;

import com.chwipoClova.common.exception.CommonException;
import com.chwipoClova.common.exception.ExceptionCode;
import com.chwipoClova.common.response.CommonResponse;
import com.chwipoClova.common.response.MessageCode;
import com.chwipoClova.feedback.entity.Feedback;
import com.chwipoClova.feedback.request.FeedbackInsertReq;
import com.chwipoClova.feedback.response.FeedbackListRes;
import com.chwipoClova.feedback.service.FeedbackService;
import com.chwipoClova.interview.repository.InterviewRepository;
import com.chwipoClova.qa.entity.Qa;
import com.chwipoClova.qa.entity.QaEditor;
import com.chwipoClova.qa.repository.QaRepository;
import com.chwipoClova.qa.request.QaAnswerDataInsertReq;
import com.chwipoClova.qa.request.QaAnswerInsertReq;
import com.chwipoClova.qa.request.QaQuestionInsertReq;
import com.chwipoClova.qa.response.QaCountRes;
import com.chwipoClova.qa.response.QaListRes;
import com.chwipoClova.qa.response.QaQuestionInsertRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Service
@Slf4j
public class QaService {

    private final QaRepository qaRepository;

    private final InterviewRepository interviewRepository;

    private final FeedbackService feedbackService;

    @Transactional
    public List<QaQuestionInsertRes> insertQaQuestionList(List<QaQuestionInsertReq> qaQuestionInsertReqList) throws IOException {
        List<Qa> qaList = new ArrayList<>();
        qaQuestionInsertReqList.stream().forEach(qaQuestionInsertReq -> {
            Qa qa = Qa.builder()
                    .question(qaQuestionInsertReq.getQuestion())
                    .aiAnswer(qaQuestionInsertReq.getAiAnswer())
                    .interview(qaQuestionInsertReq.getInterview())
                    .build();
            qaList.add(qa);
        });
        List<Qa> qaListRst = qaRepository.saveAll(qaList);

        List<QaQuestionInsertRes> qaQuestionInsertResList = new ArrayList<>();

        qaListRst.stream().forEach(qa -> {
            QaQuestionInsertRes qaQuestionInsertRes = QaQuestionInsertRes.builder()
                    .qaId(qa.getQaId())
                    .question(qa.getQuestion())
                    .aiAnswer(qa.getAiAnswer())
                    .interviewId(qa.getInterview().getInterviewId())
                    .regDate(qa.getRegDate())
                    .modifyDate(qa.getModifyDate())
                    .build();
            qaQuestionInsertResList.add(qaQuestionInsertRes);
        });

        return qaQuestionInsertResList;
    }

    @Transactional
    public CommonResponse insertAnswer(QaAnswerInsertReq qaAnswerInsertReq) throws IOException {
        Long userId = qaAnswerInsertReq.getUserId();
        Long interviewId = qaAnswerInsertReq.getInterviewId();
        interviewRepository.findByUserUserIdAndInterviewId(userId, interviewId).orElseThrow(() -> new CommonException(ExceptionCode.INTERVIEW_NULL.getMessage(), ExceptionCode.INTERVIEW_NULL.getCode()));

        List<QaAnswerDataInsertReq> qaAnswerDataInsertReqList = qaAnswerInsertReq.getAnswerData();

        List<FeedbackInsertReq> feedbackInsertListReq = new ArrayList<>();

        qaAnswerDataInsertReqList.stream().forEach(qaAnswerDataInsertReq -> {
            Qa qa = qaRepository.findByInterviewInterviewIdAndQaId(interviewId, qaAnswerDataInsertReq.getQaId()).orElseThrow(() -> new CommonException(ExceptionCode.QA_NULL.getMessage(), ExceptionCode.QA_NULL.getCode()));
            QaEditor.QaEditorBuilder editorBuilder = qa.toEditor();
            QaEditor qaEditor = editorBuilder.answer(qaAnswerDataInsertReq.getAnswer())
                    .build();
            qa.edit(qaEditor);

            // 피드백 정보
            FeedbackInsertReq feedbackInsertReq = new FeedbackInsertReq();
            feedbackInsertReq.setQaId(qa.getQaId());
            feedbackInsertReq.setAnswer(qa.getAnswer());
            feedbackInsertReq.setQuestion(qa.getQuestion());
            feedbackInsertListReq.add(feedbackInsertReq);
        });

        // 피드백 요청 및 등록
        feedbackService.insertFeedback(feedbackInsertListReq);

        return new CommonResponse<>(MessageCode.OK.getCode(), null, MessageCode.OK.getMessage());
    }

    public List<QaListRes> selectQaList(Long interviewId) {
        List<QaListRes> qaListResList = new ArrayList<>();

        List<Qa> qaList = qaRepository.findByInterviewInterviewIdOrderByQaId(interviewId);

        qaList.stream().forEach(qa -> {
            QaListRes qaListRes = QaListRes.builder()
                    .interviewId(qa.getInterview().getInterviewId())
                    .qaId(qa.getQaId())
                    .question(qa.getQuestion())
                    .answer(qa.getAnswer())
                    //.aiAnswer(qa.getAiAnswer())
                    .regDate(qa.getRegDate())
                    .modifyDate(qa.getModifyDate())
                    .build();
            qaListResList.add(qaListRes);
        });
        return qaListResList;
    }

    public QaCountRes selectQaListUseCount(Long interviewId) {
        List<QaListRes> qaListRes = selectQaList(interviewId);

        AtomicInteger index = new AtomicInteger(0); // 시작 인덱스
        qaListRes.stream().forEach(qaListRes1 -> {
            if (StringUtils.isNotBlank(qaListRes1.getAnswer())) {
                index.getAndIncrement();
            }
        });

        int useCnt = index.get();
        int totalCnt = qaListRes.size();

        return QaCountRes.builder()
                .useCnt(useCnt)
                .totalCnt(totalCnt)
                .build();
    }

}
