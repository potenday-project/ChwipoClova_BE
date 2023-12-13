package com.chwipoClova.feedback.controller;

import com.chwipoClova.common.response.CommonResponse;
import com.chwipoClova.feedback.request.FeedbackGenerateReq;
import com.chwipoClova.feedback.service.FeedbackService;
import com.chwipoClova.qa.request.QaAnswerInsertReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "피드백 API")
@RequestMapping("feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;




}
