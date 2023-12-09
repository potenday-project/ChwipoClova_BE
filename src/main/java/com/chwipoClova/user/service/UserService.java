package com.chwipoClova.user.service;

import com.chwipoClova.common.dto.Token;
import com.chwipoClova.common.dto.TokenDto;
import com.chwipoClova.common.repository.TokenRepository;
import com.chwipoClova.common.utils.JwtUtil;
import com.chwipoClova.user.dto.KakaoToken;
import com.chwipoClova.user.dto.KakaoUserInfo;
import com.chwipoClova.user.entity.User;
import com.chwipoClova.user.repository.UserRepository;
import com.chwipoClova.user.response.UserLoginRes;
import com.chwipoClova.user.response.UserSnsUrlRes;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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

    public UserLoginRes kakaoLogin(String code, HttpServletResponse response) {
        KakaoToken kakaoToken = requestAccessToken(code);
        KakaoUserInfo kakaoUserInfo = requestOauthInfo(kakaoToken);

        long snsId = kakaoUserInfo.getId();
        String email = kakaoUserInfo.getEmail();
        String nickname = kakaoUserInfo.getNickname();
        Integer snsType = kakaoUserInfo.getOAuthProvider().getCode();

        Optional<User> userInfo = userRepository.findBySnsTypeAndSnsId(snsType, snsId);

        User userInfoRst;
        // 유저 정보가 있다면 업데이트 없으면 등록
        if (userInfo.isPresent()) {
            userInfoRst = userInfo.get();
        } else {
            log.info("신규유저 등록 {}", nickname);
            User user = User.builder()
                    .snsId(snsId)
                    .email(email)
                    .name(nickname)
                    .snsType(kakaoUserInfo.getOAuthProvider().getCode())
                    .regDate(new Date())
                    .build();
            userInfoRst = userRepository.save(user);
        }

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

        return UserLoginRes.builder()
                .snsId(userInfoRst.getSnsId())
                .userId(userId)
                .email(userInfoRst.getEmail())
                .name(userInfoRst.getName())
                .snsType(userInfoRst.getSnsType())
                .regDate(userInfoRst.getRegDate())
                .modifyDate(userInfoRst.getModifyDate())
                .build();
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
}
