package com.chwipoClova.qa.controller;


import com.chwipoClova.common.response.CommonResponse;
import com.chwipoClova.qa.request.QaAnswerInsertReq;
import com.chwipoClova.qa.service.QaService;
import com.chwipoClova.user.response.UserLoginRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Qa", description = "답변 API")
@RequestMapping("qa")
public class QaController {

    private final QaService qaService;


}
