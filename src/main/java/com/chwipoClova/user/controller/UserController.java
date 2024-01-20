package com.chwipoClova.user.controller;

import com.chwipoClova.common.dto.UserDetailsImpl;
import com.chwipoClova.common.response.CommonMsgResponse;
import com.chwipoClova.common.response.CommonResponse;
import com.chwipoClova.common.response.MessageCode;
import com.chwipoClova.common.utils.JwtUtil;
import com.chwipoClova.user.entity.User;
import com.chwipoClova.user.request.UserLoginReq;
import com.chwipoClova.user.request.UserLogoutReq;
import com.chwipoClova.user.response.UserInfoRes;
import com.chwipoClova.user.response.UserLoginRes;
import com.chwipoClova.user.response.UserSnsUrlRes;
import com.chwipoClova.user.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 API")
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    @Operation(summary = "유저 정보 조회", description = "유저 정보 조회")
    @GetMapping("/getUserInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    }
    )
    public UserInfoRes getUserInfo(Authentication authentication) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl)authentication.getPrincipal();
        Long userId = userDetailsImpl.getUser().getUserId();
        return userService.selectUserInfoForUserId(userId);
    }

    @Operation(summary = "카카오 로그인 URL", description = "카카오 로그인 URL")
    @GetMapping("/getKakaoUrl")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    }
    )
    public UserSnsUrlRes getKakaoUrl() throws Exception {
        return userService.getKakaoUrl();
    }

    @Operation(summary = "카카오 개발 로그인 URL", description = "카카오 개발 로그인 URL")
    @GetMapping("/getKakaoDevUrl")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    }
    )
    public UserSnsUrlRes getKakaoDevUrl() throws Exception {
        return userService.getKakaoLocalUrl();
    }

    @Operation(summary = "카카오 로그인", description = "카카오 로그인 (카카오 로그인 URL 호출해서 로그인 성공하면 나오는 코드를 입력)")
    @GetMapping("/kakaoLogin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserLoginRes.class))),
            @ApiResponse(responseCode = "700", description = "신규 가입되었습니다.", content = @Content(schema = @Schema(implementation = String.class)))
    }
    )
    public CommonResponse kakaoLogin(@Schema(description = "로그인코드", example = "1", name = "code") @RequestParam(name = "code") String code, HttpServletResponse response) throws Exception {
        return userService.kakaoLogin(code, response);
    }

    @Operation(summary = "카카오 개발 로그인", description = "카카오 개발 로그인 (카카오 로그인 URL 호출해서 로그인 성공하면 나오는 코드를 입력)")
    @GetMapping("/kakaoDevLogin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserLoginRes.class))),
            @ApiResponse(responseCode = "700", description = "신규 가입되었습니다.", content = @Content(schema = @Schema(implementation = String.class)))
    }
    )
    public CommonResponse kakaoDevLogin(@Schema(description = "로그인코드", example = "1", name = "code") @RequestParam(name = "code") String code, HttpServletResponse response) throws Exception {
        return userService.kakaoDevLogin(code, response);
    }


    @Hidden
    @Operation(summary = "카카오 로그인 콜백", description = "카카오 로그인 콜백")
    @GetMapping("/kakaoCallback")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    }
    )
    public String kakaoCallback(@Schema(description = "로그인코드", example = "1", name = "code") @RequestParam(name = "code") String code, HttpServletResponse response) throws Exception {
        return code;
    }

    @Operation(summary = "로그아웃", description = "로그아웃")
    @PostMapping("/logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
    }
    )
    public CommonResponse logout(@RequestBody UserLogoutReq userLogoutReq,
                                 @Parameter(hidden = true) HttpServletResponse response
    ) {
        return userService.logout(response, userLogoutReq);
    }
    @Operation(summary = "쿠키테스트", description = "쿠키테스트")
    @GetMapping("/cookie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
    }
    )
    public CommonResponse cookie(@Parameter(hidden = true) HttpServletResponse response
    ) {

        jwtUtil.setResonseJwtToken(response, "11234", "1232142421");
        return new CommonResponse<>(MessageCode.OK.getCode(), null, MessageCode.OK.getMessage());
    }

}
