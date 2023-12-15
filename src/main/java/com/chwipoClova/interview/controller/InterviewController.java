package com.chwipoClova.interview.controller;

import com.chwipoClova.common.response.CommonResponse;
import com.chwipoClova.feedback.request.FeedbackGenerateReq;
import com.chwipoClova.interview.request.*;
import com.chwipoClova.interview.response.InterviewInsertRes;
import com.chwipoClova.interview.response.InterviewListRes;
import com.chwipoClova.interview.response.InterviewQaListRes;
import com.chwipoClova.interview.response.InterviewRes;
import com.chwipoClova.interview.service.InterviewService;
import com.chwipoClova.qa.request.QaAnswerInsertReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Interview", description = "면접 API")
@RequestMapping("interview")
public class InterviewController {

    private final InterviewService interviewService;

    @Operation(summary = "면접 등록", description = "면접 등록")
    @PostMapping(path = "/insertInterview", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    }
    )
    public InterviewInsertRes insertInterview(
            @RequestPart(value = "interviewData") InterviewInsertReq interviewInsertReq,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {
        return interviewService.insertInterview(interviewInsertReq, file);
    }

    @Operation(summary = "면접 결과 조회", description = "면접 결과 조회")
    @GetMapping(path = "/getInterview")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    }
    )
    public InterviewRes getInterview(
            @Schema(description = "userId", example = "1", name = "userId") @RequestParam(name = "userId") Long userId,
            @Schema(description = "interviewId", example = "1", name = "interviewId") @RequestParam(name = "interviewId") Long interviewId
    ) {
        return interviewService.selectInterview(userId, interviewId);
    }

    @Operation(summary = "면접 목록 조회", description = "면접 목록 조회")
    @GetMapping(path = "/getInterviewList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    }
    )
    public List<InterviewListRes> getInterviewList(@Schema(description = "userId", example = "1", name = "userId") @RequestParam(name = "userId") Long userId) {
        return interviewService.selectInterviewList(userId);
    }

    @Operation(summary = "면접 질문 조회", description = "면접 질문 조회")
    @GetMapping(path = "/getQaList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    }
    )
    public InterviewQaListRes getQaList(
            @Schema(description = "userId", example = "1", name = "userId") @RequestParam(name = "userId") Long userId,
            @Schema(description = "interviewId", example = "1", name = "interviewId") @RequestParam(name = "interviewId") Long interviewId

    ) {
        return interviewService.selectQaList(userId, interviewId);
    }

    @Operation(summary = "면접 삭제", description = "면접 삭제")
    @DeleteMapping(path = "/deleteInterview")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))}
    )
    public CommonResponse deleteInterview(
            @RequestBody InterviewDeleteReq interviewDeleteReq
            ) {
        return interviewService.deleteInterview(interviewDeleteReq);
    }

    @Operation(summary = "답변 초기화", description = "답변 초기화")
    @PostMapping("/initQa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
    }
    )
    public CommonResponse initQa(@RequestBody InterviewInitQaReq interviewInitQaReq) throws Exception {
        return interviewService.initQa(interviewInitQaReq);
    }

    @Operation(summary = "면접 결과 내보내기", description = "면접 결과 내보내기")
    @GetMapping("/downloadInterview")
    public void downloadInterview(
            @Schema(description = "userId", example = "1", name = "userId") @RequestParam(name = "userId") Long userId,
            @Schema(description = "interviewId", example = "1", name = "interviewId") @RequestParam(name = "interviewId") Long interviewId,
            HttpServletResponse response
    ) throws IOException {
        interviewService.downloadInterview(userId, interviewId, response);
    }


    @Operation(summary = "피드백 재생성", description = "피드백 재생성")
    @PostMapping("/generateFeedback")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
    }
    )
    public CommonResponse generateFeedback (@RequestBody FeedbackGenerateReq feedbackGenerateReq) throws Exception {
        return interviewService.generateFeedback(feedbackGenerateReq);
    }

    @Operation(summary = "답변 저장", description = "답변 저장")
    @PostMapping("/insertAnswer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
    }
    )
    public CommonResponse insertAnswer(@RequestBody QaAnswerInsertReq qaAnswerInsertReq) throws Exception {
        return interviewService.insertAnswer(qaAnswerInsertReq);
    }
}
