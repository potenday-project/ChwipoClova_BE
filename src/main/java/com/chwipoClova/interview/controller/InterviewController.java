package com.chwipoClova.interview.controller;

import com.chwipoClova.interview.request.InterviewInsertReq;
import com.chwipoClova.interview.response.InterviewInsertRes;
import com.chwipoClova.interview.response.InterviewListRes;
import com.chwipoClova.interview.response.InterviewNotCompRes;
import com.chwipoClova.interview.response.InterviewRes;
import com.chwipoClova.interview.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = "미완료 면접 질문 조회", description = "미완료 면접 질문 조회")
    @GetMapping(path = "/getNotCompInterview")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    }
    )
    public InterviewNotCompRes getNotCompInterview(
            @Schema(description = "userId", example = "1", name = "userId") @RequestParam(name = "userId") Long userId,
            @Schema(description = "interviewId", example = "1", name = "interviewId") @RequestParam(name = "interviewId") Long interviewId

    ) {
        return interviewService.selectNotCompInterview(userId, interviewId);
    }

}
