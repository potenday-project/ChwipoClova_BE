package com.chwipoClova.feedback.service;

import com.chwipoClova.common.exception.CommonException;
import com.chwipoClova.common.exception.ExceptionCode;
import com.chwipoClova.common.response.CommonResponse;
import com.chwipoClova.common.response.MessageCode;
import com.chwipoClova.common.utils.ApiUtils;
import com.chwipoClova.feedback.entity.Feedback;
import com.chwipoClova.feedback.entity.FeedbackEditor;
import com.chwipoClova.feedback.repository.FeedbackRepository;
import com.chwipoClova.feedback.request.FeedbackGenerateReq;
import com.chwipoClova.feedback.request.FeedbackInsertReq;
import com.chwipoClova.feedback.response.FeedBackApiRes;
import com.chwipoClova.feedback.response.FeedbackListRes;
import com.chwipoClova.interview.entity.Interview;
import com.chwipoClova.interview.repository.InterviewRepository;
import com.chwipoClova.qa.entity.Qa;
import com.chwipoClova.qa.entity.QaEditor;
import com.chwipoClova.qa.repository.QaRepository;
import com.chwipoClova.user.entity.User;
import com.chwipoClova.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    private final QaRepository qaRepository;

    private final InterviewRepository interviewRepository;

    private final UserRepository userRepository;

    private final ApiUtils apiUtils;

    @Transactional
    public CommonResponse insertFeedback(String allAnswerData, List<FeedbackInsertReq> feedbackInsertListReq) throws IOException {

        // TODO 피드백 연동 필요함 답변이 있는 경우만 전달하자
        // 키워드
        String apiKeywordRst = apiUtils.keyword(allAnswerData);

        // 모범답안
        String apiBestRst = apiUtils.best(allAnswerData);

        // 피드백 매핑
        setApiFeedbackData(apiKeywordRst, apiBestRst, feedbackInsertListReq);

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

    @Transactional
    public CommonResponse generateFeedback(FeedbackGenerateReq feedbackGenerateReq) {
        Long interviewId = feedbackGenerateReq.getInterviewId();
        Long userId = feedbackGenerateReq.getUserId();

        userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));
        Interview interview = interviewRepository.findByUserUserIdAndInterviewId(userId, interviewId).orElseThrow(() -> new CommonException(ExceptionCode.INTERVIEW_NULL.getMessage(), ExceptionCode.INTERVIEW_NULL.getCode()));
        Integer status = interview.getStatus();

        if (status != 1) {
            throw new CommonException(ExceptionCode.INTERVIEW_NOT_COMPLETE.getMessage(), ExceptionCode.INTERVIEW_NOT_COMPLETE.getCode());
        }

        List<Qa> qaList = qaRepository.findByInterviewInterviewIdOrderByQaId(interviewId);

        // TODO 피드백 연동 필요함 답변이 있는 경우만 전달하자
        List<FeedBackApiRes> feedBackApiListRes = new ArrayList<>();
        qaList.stream().forEach(qa -> {
            String answer = qa.getAnswer();
            if (StringUtils.isNotBlank(answer)) {
                Long qaId = qa.getQaId();
                FeedBackApiRes feedBackApiRes = new FeedBackApiRes();
                feedBackApiRes.setQaId(qaId);
                feedBackApiRes.setType(1);
                feedBackApiRes.setContent("재생성 피드백1입니다.");
                feedBackApiListRes.add(feedBackApiRes);
                FeedBackApiRes feedBackApiRes2 = new FeedBackApiRes();
                feedBackApiRes2.setQaId(qaId);
                feedBackApiRes2.setType(2);
                feedBackApiRes2.setContent("재생성 피드백2입니다.");
                feedBackApiListRes.add(feedBackApiRes2);
            }
        });

        insertAllFeedback(feedBackApiListRes);
        return new CommonResponse<>(MessageCode.OK.getCode(), null, MessageCode.OK.getMessage());
    }

    @Transactional
    public void insertAllFeedback(List<FeedBackApiRes> feedBackApiListRes) {
        feedBackApiListRes.stream().forEach(feedBackApiRes -> {
            Long qaId = feedBackApiRes.getQaId();
            Qa qa = qaRepository.findById(qaId).orElseThrow(() -> new CommonException(ExceptionCode.QA_NULL.getMessage(), ExceptionCode.QA_NULL.getCode()));

            Integer type = feedBackApiRes.getType();
            String content = feedBackApiRes.getContent();
            // 피드백 데이터가 있다면 수정 없으면 등록
            Optional<Feedback> feedbackOptional = feedbackRepository.findByQaQaIdAndType(qaId, type);
            if (feedbackOptional.isPresent()) {

                Feedback feedbackInfo = feedbackOptional.get();
                FeedbackEditor.FeedbackEditorBuilder editorBuilder = feedbackInfo.toEditor();
                FeedbackEditor feedbackEditor = editorBuilder.content(content)
                        .build();
                feedbackInfo.edit(feedbackEditor);
            } else {
                Feedback feedback = Feedback.builder()
                        .type(type)
                        .content(content)
                        .qa(qa)
                        .build();
                feedbackRepository.save(feedback);
            }
        });
    }

    @Transactional
    public void deleteFeedback(Long qaId) {
        feedbackRepository.deleteByQaQaId(qaId);
    }

    private void setApiFeedbackData(String apiKeywordRst, String apiBestRst, List<FeedbackInsertReq> feedbackInsertListReq) {
        List<FeedbackListRes> feedbackList = new ArrayList<>();
        String[] splitKeywordList = apiKeywordRst.split("\n");

        // 키워드 가공
        for (String splitSummary : splitKeywordList) {
            if (splitSummary.indexOf(".") != -1) {
                String num = splitSummary.substring(0, splitSummary.indexOf("."));
                if (org.apache.commons.lang3.StringUtils.isNumeric(num)) {
                    String content = splitSummary.substring(splitSummary.indexOf(".") + 1).trim();

                    FeedbackListRes feedbackListRes = FeedbackListRes.builder()
                            .qaId(Long.parseLong(num))
                            .type(1)
                            .content(content)
                            .build();
                    feedbackList.add(feedbackListRes);
                }
            }
        }

        // 모법답안 가공
        String[] splitBestList = apiBestRst.split("\n");
        for (String splitSummary : splitBestList) {
            if (splitSummary.indexOf(".") != -1) {
                String num = splitSummary.substring(0, splitSummary.indexOf("."));
                if (org.apache.commons.lang3.StringUtils.isNumeric(num)) {
                    String content = splitSummary.substring(splitSummary.indexOf(".") + 1).trim();

                    FeedbackListRes feedbackListRes = FeedbackListRes.builder()
                            .qaId(Long.parseLong(num))
                            .type(2)
                            .content(content)
                            .build();
                    feedbackList.add(feedbackListRes);
                }
            }
        }

        List<FeedBackApiRes> feedBackApiListRes = new ArrayList<>();
        feedbackInsertListReq.stream().forEach(feedbackInsertReq -> {

            Long qaId = feedbackInsertReq.getQaId();
            Long apiNum = feedbackInsertReq.getApiNum();

            feedbackList.stream().forEach(feedback -> {
                if (apiNum == feedback.getQaId()) {
                    FeedBackApiRes feedBackApiRes = new FeedBackApiRes();
                    feedBackApiRes.setQaId(qaId);
                    feedBackApiRes.setType(feedback.getType());
                    feedBackApiRes.setContent(feedback.getContent());
                    feedBackApiListRes.add(feedBackApiRes);
                }
            });
            insertAllFeedback(feedBackApiListRes);
        });
    }
}
