package com.chwipoClova.interview.controller;

import com.chwipoClova.interview.request.InterviewInsertReq;
import com.chwipoClova.interview.response.InterviewInsertRes;
import com.chwipoClova.interview.service.InterviewService;
import com.chwipoClova.resume.response.ResumeUploadRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

}
