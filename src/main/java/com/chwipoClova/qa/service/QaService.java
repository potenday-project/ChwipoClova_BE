package com.chwipoClova.qa.service;

import com.chwipoClova.common.exception.CommonException;
import com.chwipoClova.common.exception.ExceptionCode;
import com.chwipoClova.common.response.CommonResponse;
import com.chwipoClova.common.response.MessageCode;
import com.chwipoClova.feedback.request.FeedbackInsertReq;
import com.chwipoClova.feedback.service.FeedbackService;
import com.chwipoClova.interview.entity.Interview;
import com.chwipoClova.interview.entity.InterviewEditor;
import com.chwipoClova.interview.repository.InterviewRepository;
import com.chwipoClova.qa.entity.Qa;
import com.chwipoClova.qa.entity.QaEditor;
import com.chwipoClova.qa.repository.QaRepository;
import com.chwipoClova.qa.request.QaAnswerDataInsertReq;
import com.chwipoClova.qa.request.QaAnswerInsertReq;
import com.chwipoClova.qa.request.QaGenerateReq;
import com.chwipoClova.qa.request.QaQuestionInsertReq;
import com.chwipoClova.qa.response.QaCountRes;
import com.chwipoClova.qa.response.QaListForFeedbackRes;
import com.chwipoClova.qa.response.QaListRes;
import com.chwipoClova.qa.response.QaQuestionInsertRes;
import com.chwipoClova.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Service
@Slf4j
public class QaService {

    private final QaRepository qaRepository;

    private final InterviewRepository interviewRepository;

    private final FeedbackService feedbackService;

    private final UserRepository userRepository;

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
        Interview interview = interviewRepository.findByUserUserIdAndInterviewId(userId, interviewId).orElseThrow(() -> new CommonException(ExceptionCode.INTERVIEW_NULL.getMessage(), ExceptionCode.INTERVIEW_NULL.getCode()));

        List<QaAnswerDataInsertReq> qaAnswerDataInsertReqList = qaAnswerInsertReq.getAnswerData();

        List<FeedbackInsertReq> feedbackInsertListReq = new ArrayList<>();

        Qa lastQa = qaRepository.findFirstByInterviewInterviewIdOrderByQaIdDesc(interviewId);
        Long lastQaId = lastQa.getQaId();

        AtomicBoolean lastCkAtomic = new AtomicBoolean(false);
        qaAnswerDataInsertReqList.stream().forEach(qaAnswerDataInsertReq -> {
            String answer = qaAnswerDataInsertReq.getAnswer();

            // 답변 내용이 있을 경우 답변 저장 및 피드백 생성
            if (StringUtils.isNotBlank(answer)) {
                Qa qa = qaRepository.findByInterviewInterviewIdAndQaId(interviewId, qaAnswerDataInsertReq.getQaId()).orElseThrow(() -> new CommonException(ExceptionCode.QA_NULL.getMessage(), ExceptionCode.QA_NULL.getCode()));
                QaEditor.QaEditorBuilder editorBuilder = qa.toEditor();
                QaEditor qaEditor = editorBuilder.answer(qaAnswerDataInsertReq.getAnswer())
                        .build();
                qa.edit(qaEditor);

                if (lastQaId == qa.getQaId()) {
                    lastCkAtomic.set(true);
                }

                // 피드백 정보
                FeedbackInsertReq feedbackInsertReq = new FeedbackInsertReq();
                feedbackInsertReq.setQaId(qa.getQaId());
                feedbackInsertReq.setAnswer(qa.getAnswer());
                feedbackInsertReq.setQuestion(qa.getQuestion());
                feedbackInsertListReq.add(feedbackInsertReq);
            }
        });

        // 마지막 답변이 있을 경우 면접 완료 처리 및 피드백 생성
        Boolean lastCk = lastCkAtomic.get();
        if (lastCk) {
            // 피드백 요청 및 등록
            feedbackService.insertFeedback(feedbackInsertListReq);

            // 면접 완료 처리
            InterviewEditor.InterviewEditorBuilder editorBuilder = interview.toEditor();
            InterviewEditor interviewEditor = editorBuilder.status(1).build();
            interview.edit(interviewEditor);
        }

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
                    .regDate(qa.getRegDate())
                    .modifyDate(qa.getModifyDate())
                    .build();
            qaListResList.add(qaListRes);
        });
        return qaListResList;
    }

    public List<QaListForFeedbackRes> selectQaListForFeedback(Long interviewId) {
        List<QaListRes> qaListRes = selectQaList(interviewId);
        List<QaListForFeedbackRes> listForFeedbackResList = new ArrayList<>();

        qaListRes.stream().forEach(qaListRes1 -> {
            AtomicReference<String> feedback1 = new AtomicReference<>("");
            AtomicReference<String> feedback2 = new AtomicReference<>("");
            feedbackService.selectFeedbackList(qaListRes1.getQaId()).stream().forEach(feedbackListRes -> {
                Integer type = feedbackListRes.getType();
                if (type == 1) {
                    feedback1.set(feedbackListRes.getContent());
                } else if (type == 2) {
                    feedback2.set(feedbackListRes.getContent());
                }
            });

            QaListForFeedbackRes qalistForFeedbackRes = QaListForFeedbackRes.builder()
                    .interviewId(qaListRes1.getInterviewId())
                    .qaId(qaListRes1.getQaId())
                    .question(qaListRes1.getQuestion())
                    .answer(qaListRes1.getAnswer())
                    .regDate(qaListRes1.getRegDate())
                    .modifyDate(qaListRes1.getModifyDate())
                    .keyword(feedback1.get())
                    .bestAnswer(feedback2.get())
                    .build();
            listForFeedbackResList.add(qalistForFeedbackRes);
        });
        return listForFeedbackResList;
    }

    public QaCountRes selectQaListUseCount(Long interviewId) {
        List<QaListRes> qaListRes = selectQaList(interviewId);

        AtomicInteger index = new AtomicInteger(0); // 시작 인덱스
        AtomicReference<Long> lastQaIdAtomic = new AtomicReference<>();
        qaListRes.stream().forEach(qaListRes1 -> {
            if (StringUtils.isNotBlank(qaListRes1.getAnswer())) {
                index.getAndIncrement();
                lastQaIdAtomic.set(qaListRes1.getQaId());
            }
        });

        int useCnt = index.get();
        int totalCnt = qaListRes.size();
        Long lastQaId = lastQaIdAtomic.get();

        return QaCountRes.builder()
                .useCnt(useCnt)
                .totalCnt(totalCnt)
                .lastQaId(lastQaId)
                .build();
    }

    @Transactional
    public void initQa(Long interviewId) {
        qaRepository.initQa(interviewId);
    }

    public void deleteQa(Long interviewId) {
        qaRepository.deleteByInterviewInterviewId(interviewId);
    }

    public void generateQa(QaGenerateReq qaGenerateReq) throws IOException {
        Long interviewId = qaGenerateReq.getInterviewId();
        Long userId = qaGenerateReq.getUserId();

        userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));
        Interview interview = interviewRepository.findByUserUserIdAndInterviewId(userId, interviewId).orElseThrow(() -> new CommonException(ExceptionCode.INTERVIEW_NULL.getMessage(), ExceptionCode.INTERVIEW_NULL.getCode()));
        Integer status = interview.getStatus();

        if (status != 0) {
            throw new CommonException(ExceptionCode.INTERVIEW_COMPLETE.getMessage(), ExceptionCode.INTERVIEW_COMPLETE.getCode());
        }

        List<Qa> qaList = qaRepository.findByInterviewInterviewIdOrderByQaId(interviewId);

        // 피드백 데이터가 있는지 확인 있으면 오류 발생
        qaList.stream().forEach(qa -> {
            int feedbackSize = feedbackService.selectFeedbackList(qa.getQaId()).size();
            if (feedbackSize > 0) {
                throw new CommonException(ExceptionCode.FEEDBACK_NOT_NULL.getMessage(), ExceptionCode.FEEDBACK_NOT_NULL.getCode());
            }
        });

        // 기존 질문 전부 삭제
        qaRepository.deleteAll(qaList);

        // 새로운 질문 생성
        insertQa(interview);
    }

    public List<QaQuestionInsertRes> insertQa(Interview interviewRst) throws IOException {
        String time = LocalDateTime.now().format(DateTimeFormatter.ISO_TIME);
        String q1 = "질문1입니다." + time;

        String q2 = "질문2입니다." + time;

        // 질문 답변 저장
        List<QaQuestionInsertReq> qaQuestionInsertReqList = new ArrayList<>();
        QaQuestionInsertReq qaQuestionInsertReq1 = new QaQuestionInsertReq();
        qaQuestionInsertReq1.setInterview(interviewRst);
        qaQuestionInsertReq1.setQuestion(q1);
        qaQuestionInsertReqList.add(qaQuestionInsertReq1);

        QaQuestionInsertReq qaQuestionInsertReq2 = new QaQuestionInsertReq();
        qaQuestionInsertReq2.setInterview(interviewRst);
        qaQuestionInsertReq2.setQuestion(q2);
        qaQuestionInsertReqList.add(qaQuestionInsertReq2);
        return insertQaQuestionList(qaQuestionInsertReqList);
    }
}
