package com.chwipoClova.qa.service;

import com.chwipoClova.interview.entity.Interview;
import com.chwipoClova.interview.repository.InterviewRepository;
import com.chwipoClova.qa.entity.Qa;
import com.chwipoClova.qa.repository.QaRepository;
import com.chwipoClova.qa.request.QaQuestionInsertReq;
import com.chwipoClova.qa.response.QaQuestionInsertRes;
import com.chwipoClova.recruit.request.RecruitInsertReq;
import com.chwipoClova.recruit.response.RecruitInsertRes;
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
public class QaService {

    private final QaRepository qaRepository;

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
}
