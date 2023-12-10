package com.chwipoClova.user.service;

import com.chwipoClova.common.dto.Token;
import com.chwipoClova.common.dto.TokenDto;
import com.chwipoClova.common.exception.CommonException;
import com.chwipoClova.common.exception.ExceptionCode;
import com.chwipoClova.common.repository.TokenRepository;
import com.chwipoClova.common.response.CommonResponse;
import com.chwipoClova.common.response.MessageCode;
import com.chwipoClova.common.utils.JwtUtil;
import com.chwipoClova.user.dto.KakaoToken;
import com.chwipoClova.user.dto.KakaoUserInfo;
import com.chwipoClova.user.entity.User;
import com.chwipoClova.user.repository.UserRepository;
import com.chwipoClova.user.request.UserLoginReq;
import com.chwipoClova.user.response.UserInfoRes;
import com.chwipoClova.user.response.UserLoginRes;
import com.chwipoClova.user.response.UserSnsUrlRes;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final RestTemplate restTemplate;

    private final JwtUtil jwtUtil;

    @Value("${kakao.url.auth}")
    private String kakaoAuthUrl;

    @Value("${kakao.url.token}")
    private String tokenUrl;

    @Value("${kakao.url.api}")
    private String apiUrl;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.client_secret}")
    private String clientSecret;

    @Value("${kakao.grant_type}")
    private String grantType;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;


    public UserSnsUrlRes getKakaoUrl() {
        String kakaoUrl = kakaoAuthUrl + "?response_type=code" + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;
        UserSnsUrlRes userSnsUrlRes = UserSnsUrlRes.builder()
                .url(kakaoUrl)
                .build();
        return userSnsUrlRes;
    }

    @Transactional
    public CommonResponse kakaoLogin(String code, HttpServletResponse response) {
        KakaoToken kakaoToken = requestAccessToken(code);
        KakaoUserInfo kakaoUserInfo = requestOauthInfo(kakaoToken);

        long snsId = kakaoUserInfo.getId();
        String email = kakaoUserInfo.getEmail();
        String nickname = kakaoUserInfo.getNickname();
        Integer snsType = kakaoUserInfo.getOAuthProvider().getCode();
        String thumbnailImageUrl = kakaoUserInfo.getThumbnailImageUrl();
        String profileImageUrl = kakaoUserInfo.getProfileImageUrl();

        Optional<User> userInfo = userRepository.findBySnsTypeAndSnsId(snsType, snsId);

        // 유저 정보가 있다면 업데이트 없으면 등록
        if (userInfo.isPresent()) {
            User userInfoRst = userInfo.get();

            Long userId = userInfoRst.getUserId();

            TokenDto tokenDto = jwtUtil.createAllToken(String.valueOf(userId));

            // Refresh토큰 있는지 확인
            Optional<Token> refreshToken = tokenRepository.findByUserUserId(userInfoRst.getUserId());

            // 있다면 새토큰 발급후 업데이트
            // 없다면 새로 만들고 디비 저장
            if(refreshToken.isPresent()) {
                tokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
            }else {
                Token newToken = new Token(tokenDto.getRefreshToken(),  User.builder().userId(userInfoRst.getUserId()).build());
                tokenRepository.save(newToken);
            }

            // response 헤더에 Access Token / Refresh Token 넣음
            setHeader(response, tokenDto);

            UserLoginRes userLoginRes = UserLoginRes.builder()
                    .snsId(userInfoRst.getSnsId())
                    .userId(userId)
                    .email(userInfoRst.getEmail())
                    .name(userInfoRst.getName())
                    .snsType(userInfoRst.getSnsType())
                    .thumbnailImage(userInfoRst.getThumbnailImage())
                    .profileImage(userInfoRst.getProfileImage())
                    .regDate(userInfoRst.getRegDate())
                    .modifyDate(userInfoRst.getModifyDate())
                    .build();

            return new CommonResponse<>(String.valueOf(HttpStatus.OK.value()), userLoginRes, HttpStatus.OK.getReasonPhrase());
        } else {
            log.info("신규유저 등록 {}", nickname);
            User user = User.builder()
                    .snsId(snsId)
                    .email(email)
                    .name(nickname)
                    .snsType(snsType)
                    .thumbnailImage(thumbnailImageUrl)
                    .profileImage(profileImageUrl)
                    .regDate(new Date())
                    .build();
            userRepository.save(user);
            return new CommonResponse<>(MessageCode.NEW_USER.getCode(), null, MessageCode.NEW_USER.getMessage());
        }

    }

    public KakaoToken requestAccessToken(String code) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();;
        body.add("grant_type", grantType);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, httpHeaders);

        KakaoToken response = restTemplate.postForObject(tokenUrl, request, KakaoToken.class);

        // TODO 토큰 정보를 가져오지 못하면 예외발생 처리 추가
        assert response != null;
        return response;
    }

    public KakaoUserInfo requestOauthInfo(KakaoToken kakaoToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + kakaoToken.getAccessToken());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();;
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, httpHeaders);
        KakaoUserInfo response = restTemplate.postForObject(apiUrl, request, KakaoUserInfo.class);

        // TODO 유저 정보를 가져오지 못하면 예외발생 처리 추가
        assert response != null;
        return response;
    }

    private void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }

    public UserInfoRes selectUserInfo(String email) {
        Optional<User> usersInfo = userRepository.findByEmailAndSnsType(email, 1);
        if (!usersInfo.isPresent()) {
            throw new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode());
        }

        User user = usersInfo.get();

        return UserInfoRes.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .thumbnailImage(user.getThumbnailImage())
                .profileImage(user.getProfileImage())
                .regDate(user.getRegDate())
                .modifyDate(user.getModifyDate())
                .build();
    }
}
