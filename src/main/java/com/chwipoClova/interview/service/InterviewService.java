package com.chwipoClova.interview.service;

import com.chwipoClova.common.exception.CommonException;
import com.chwipoClova.common.exception.ExceptionCode;
import com.chwipoClova.common.response.CommonResponse;
import com.chwipoClova.common.response.MessageCode;
import com.chwipoClova.common.utils.ApiUtils;
import com.chwipoClova.feedback.request.FeedbackGenerateReq;
import com.chwipoClova.feedback.service.FeedbackService;
import com.chwipoClova.interview.entity.Interview;
import com.chwipoClova.interview.repository.InterviewRepository;
import com.chwipoClova.interview.request.InterviewDeleteReq;
import com.chwipoClova.interview.request.InterviewFeedbackGenerateReq;
import com.chwipoClova.interview.request.InterviewInitQaReq;
import com.chwipoClova.interview.request.InterviewInsertReq;
import com.chwipoClova.interview.response.InterviewInsertRes;
import com.chwipoClova.interview.response.InterviewListRes;
import com.chwipoClova.interview.response.InterviewQaListRes;
import com.chwipoClova.interview.response.InterviewRes;
import com.chwipoClova.qa.entity.Qa;
import com.chwipoClova.qa.request.QaAnswerInsertReq;
import com.chwipoClova.qa.request.QaQuestionInsertReq;
import com.chwipoClova.qa.response.QaCountRes;
import com.chwipoClova.qa.response.QaListForFeedbackRes;
import com.chwipoClova.qa.response.QaListRes;
import com.chwipoClova.qa.response.QaQuestionInsertRes;
import com.chwipoClova.qa.service.QaService;
import com.chwipoClova.recruit.request.RecruitInsertReq;
import com.chwipoClova.recruit.response.RecruitInsertRes;
import com.chwipoClova.recruit.service.RecruitService;
import com.chwipoClova.resume.entity.Resume;
import com.chwipoClova.resume.repository.ResumeRepository;
import com.chwipoClova.user.entity.User;
import com.chwipoClova.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class InterviewService {

    private final ResumeRepository resumeRepository;

    private final UserRepository userRepository;

    private final RecruitService recruitService;

    private final InterviewRepository interviewRepository;

    private final QaService qaService;

    private final FeedbackService feedbackService;

    @Value("${limit.size.interview}")
    private Integer interviewLimitSize;

    @Transactional
    public InterviewInsertRes insertInterview(InterviewInsertReq interviewInsertReq, MultipartFile file) throws IOException {
        Long userId = interviewInsertReq.getUserId();
        Long resumeId = interviewInsertReq.getResumeId();
        String recruitContent = interviewInsertReq.getRecruitContent();

        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));
        Resume resume = resumeRepository.findByUserUserIdAndResumeId(userId, resumeId).orElseThrow(() -> new CommonException(ExceptionCode.RESUME_NULL.getMessage(), ExceptionCode.RESUME_NULL.getCode()));

        List<Interview> interviewList = interviewRepository.findByUserUserIdOrderByRegDate(userId);
        if (interviewList != null && interviewList.size() >= interviewLimitSize) {
            throw new CommonException(ExceptionCode.INTERVIEW_LIST_OVER.getMessage(), ExceptionCode.INTERVIEW_LIST_OVER.getCode());
        }

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
                .questionData(questionData)
                .build();
    }

    public InterviewRes selectInterview(Long userId, Long interviewId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));
        Interview interview = interviewRepository.findByUserUserIdAndInterviewId(userId, interviewId).orElseThrow(() -> new CommonException(ExceptionCode.INTERVIEW_NULL.getMessage(), ExceptionCode.INTERVIEW_NULL.getCode()));

        List<QaListForFeedbackRes> listForFeedbackResList = qaService.selectQaListForFeedback(interview.getInterviewId());

        return InterviewRes.builder()
                .interviewId(interview.getInterviewId())
                .userId(user.getUserId())
                .title(interview.getTitle())
                .status(interview.getStatus())
                .feedback(interview.getFeedback())
                .regDate(interview.getRegDate())
                .qaData(listForFeedbackResList)
                .build();
    }

    public List<InterviewListRes> selectInterviewList(Long userId) {
        List<InterviewListRes> interviewListRes = new ArrayList<>();

        List<Interview> interviewList = interviewRepository.findByUserUserIdOrderByRegDate(userId);

        interviewList.stream().forEach(interview -> {
            QaCountRes qaCountRes = qaService.selectQaListUseCount(interview.getInterviewId());

            InterviewListRes interviewListRes1 = InterviewListRes.builder()
                    .interviewId(interview.getInterviewId())
                    .userId(interview.getUser().getUserId())
                    .title(interview.getTitle())
                    .useCnt(qaCountRes.getUseCnt())
                    .totalCnt(qaCountRes.getTotalCnt())
                    .status(interview.getStatus())
                    .regDate(interview.getRegDate())
                    .build();
            interviewListRes.add(interviewListRes1);
        });

        return interviewListRes;
    }

    public InterviewQaListRes selectQaList(Long userId, Long interviewId) {
        userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));
        interviewRepository.findByUserUserIdAndInterviewId(userId, interviewId).orElseThrow(() -> new CommonException(ExceptionCode.INTERVIEW_NULL.getMessage(), ExceptionCode.INTERVIEW_NULL.getCode()));

        QaCountRes qaCountRes = qaService.selectQaListUseCount(interviewId);
        Integer totalCnt = qaCountRes.getTotalCnt();
        Integer useCnt = qaCountRes.getUseCnt();
        Long lastQaId = qaCountRes.getLastQaId();
        List<QaListRes> qaListResList = qaService.selectQaList(interviewId);

        return InterviewQaListRes.builder()
                .interviewId(interviewId)
                .userId(userId)
                .totalCnt(totalCnt)
                .useCnt(useCnt)
                .lastQaId(lastQaId)
                .qaData(qaListResList)
                .build();
    }

    @Transactional
    public CommonResponse initQa(InterviewInitQaReq interviewInitQaReq) {
        Long userId = interviewInitQaReq.getUserId();
        Long interviewId = interviewInitQaReq.getInterviewId();
        userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));
        Interview interview = interviewRepository.findByUserUserIdAndInterviewId(userId, interviewId).orElseThrow(() -> new CommonException(ExceptionCode.INTERVIEW_NULL.getMessage(), ExceptionCode.INTERVIEW_NULL.getCode()));

        Integer status = interview.getStatus();

        // 완료 된 질문은 사용 불가
        if (status == 1) {
            throw new CommonException(ExceptionCode.INTERVIEW_COMPLETE.getMessage(), ExceptionCode.INTERVIEW_COMPLETE.getCode());
        }

        qaService.initQa(interviewId);
        return new CommonResponse<>(MessageCode.OK.getCode(), null, MessageCode.OK.getMessage());
    }

    public void downloadInterview(Long userId, Long interviewId, HttpServletResponse response) throws IOException {
        Interview interview = interviewRepository.findByUserUserIdAndInterviewId(userId, interviewId).orElseThrow(() -> new CommonException(ExceptionCode.INTERVIEW_NULL.getMessage(), ExceptionCode.INTERVIEW_NULL.getCode()));

        Integer status = interview.getStatus();

        // 완료 된 질문은 사용 불가
        if (status == 1) {
            throw new CommonException(ExceptionCode.INTERVIEW_COMPLETE.getMessage(), ExceptionCode.INTERVIEW_COMPLETE.getCode());
        }

        InterviewRes interviewRes = selectInterview(userId, interviewId);

        String title = interviewRes.getTitle();

        String name = URLEncoder.encode(title, "UTF-8");

        String filename = name + ".txt";

        StringBuilder stringBuilder = new StringBuilder();

        String feedback = interviewRes.getFeedback();

        stringBuilder.append(title + "면접 결과");

        stringBuilder.append("\n");
        stringBuilder.append("\n");

        stringBuilder.append("면접 관의 속마음");
        stringBuilder.append("\n");
        stringBuilder.append(feedback);

        stringBuilder.append("\n");
        stringBuilder.append("\n");

        stringBuilder.append("티키타카의 피드백");

        stringBuilder.append("\n");
        stringBuilder.append("\n");

        interviewRes.getQaData().stream().forEach(qaListForFeedbackRes -> {
            stringBuilder.append(qaListForFeedbackRes.getFeedback1());
            stringBuilder.append("\n");

            stringBuilder.append(qaListForFeedbackRes.getQuestion());
            stringBuilder.append("\n");

            stringBuilder.append(qaListForFeedbackRes.getFeedback2());
            stringBuilder.append("\n");
        });

        String content = stringBuilder.toString();

        byte[] fileByte = content.getBytes();

        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(fileByte.length);
        response.setHeader("Content-Disposition", "attachment; FileName=\"" + filename +"\";");
        response.setHeader("Access-Control-Expose-Headers", "X-Filename");
        response.setHeader("X-Filename", filename);
        response.setHeader("Content-Transfer-Encoding",  "binary");
        response.getOutputStream().write(fileByte);

        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    public CommonResponse generateFeedback(FeedbackGenerateReq feedbackGenerateReq) {
        return feedbackService.generateFeedback(feedbackGenerateReq);
    }

    @Transactional
    public CommonResponse insertAnswer(QaAnswerInsertReq qaAnswerInsertReq) throws IOException {
       return qaService.insertAnswer(qaAnswerInsertReq);
    }

    @Transactional
    public CommonResponse deleteInterview(InterviewDeleteReq interviewDeleteReq) {
        Long userId = interviewDeleteReq.getUserId();
        Long interviewId = interviewDeleteReq.getInterviewId();
        userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));
        Interview interview = interviewRepository.findByUserUserIdAndInterviewId(userId, interviewId).orElseThrow(() -> new CommonException(ExceptionCode.INTERVIEW_NULL.getMessage(), ExceptionCode.INTERVIEW_NULL.getCode()));

        // 피드백 삭제
        qaService.selectQaList(interviewId).stream().forEach(qaListRes -> {
            feedbackService.deleteFeedback(qaListRes.getQaId());
        });

        // 질문 삭제
        qaService.deleteQa(interviewId);

        // 면접 삭제
        interviewRepository.delete(interview);
        return new CommonResponse<>(MessageCode.OK.getCode(), null, MessageCode.OK.getMessage());
    }
}
