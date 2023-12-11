package com.chwipoClova.feedback.service;

import com.chwipoClova.common.exception.CommonException;
import com.chwipoClova.common.exception.ExceptionCode;
import com.chwipoClova.common.response.CommonResponse;
import com.chwipoClova.common.response.MessageCode;
import com.chwipoClova.feedback.entity.Feedback;
import com.chwipoClova.feedback.repository.FeedbackRepository;
import com.chwipoClova.feedback.request.FeedbackInsertReq;
import com.chwipoClova.feedback.response.FeedBackApiRes;
import com.chwipoClova.feedback.response.FeedbackListRes;
import com.chwipoClova.qa.entity.Qa;
import com.chwipoClova.qa.repository.QaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    private final QaRepository qaRepository;

    @Transactional
    public CommonResponse insertFeedback(List<FeedbackInsertReq> feedbackInsertListReq) throws IOException {

        // TODO 피드백 연동 필요함
        // 테스트 데이터
        List<FeedBackApiRes> feedBackApiListRes = new ArrayList<>();
        feedbackInsertListReq.stream().forEach(feedbackInsertReq -> {
            Long qaId = feedbackInsertReq.getQaId();
            FeedBackApiRes feedBackApiRes = new FeedBackApiRes();
            feedBackApiRes.setQaId(qaId);
            feedBackApiRes.setType(1);
            feedBackApiRes.setContent("피드백1입니다.");
            feedBackApiListRes.add(feedBackApiRes);
            FeedBackApiRes feedBackApiRes2 = new FeedBackApiRes();
            feedBackApiRes2.setQaId(qaId);
            feedBackApiRes2.setType(2);
            feedBackApiRes2.setContent("피드백2입니다.");
            feedBackApiListRes.add(feedBackApiRes2);
        });

        feedBackApiListRes.stream().forEach(feedBackApiRes -> {
            Long qaId = feedBackApiRes.getQaId();
            Qa qa = qaRepository.findById(qaId).orElseThrow(() -> new CommonException(ExceptionCode.QA_NULL.getMessage(), ExceptionCode.QA_NULL.getCode()));

            Feedback feedback = Feedback.builder()
                    .type(feedBackApiRes.getType())
                    .content(feedBackApiRes.getContent())
                    .qa(qa)
                    .build();
            feedbackRepository.save(feedback);
        });

        return new CommonResponse<>(MessageCode.OK.getCode(), null, MessageCode.OK.getMessage());
    }

    public List<FeedbackListRes> selectFeedbackList(Long qaId) {
        List<FeedbackListRes> feedbackListResList = new ArrayList<>();

        List<Feedback> feedbackList = feedbackRepository.findByQaQaIdOrderByFeedbackId(qaId);
        feedbackList.stream().forEach(feedback -> {
            FeedbackListRes feedbackListRes = FeedbackListRes.builder()
                    .qaId(feedback.getQa().getQaId())
                    .feedbackId(feedback.getFeedbackId())
                    .type(feedback.getType())
                    .content(feedback.getContent())
                    .regDate(feedback.getRegDate())
                    .modifyDate(feedback.getModifyDate())
                    .build();
            feedbackListResList.add(feedbackListRes);
        });
        return feedbackListResList;
    }

}
