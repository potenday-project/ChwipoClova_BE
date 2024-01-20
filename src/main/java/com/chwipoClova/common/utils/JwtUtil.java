package com.chwipoClova.common.utils;

import com.chwipoClova.common.dto.Token;
import com.chwipoClova.common.dto.TokenDto;
import com.chwipoClova.common.repository.TokenRepository;
import com.chwipoClova.common.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final UserDetailsServiceImpl userDetailsService;
    private final TokenRepository tokenRepository;

    private static final long ACCESS_TIME =  2 * 24 * 60 * 60 * 1000L;

    private static final long REFRESH_TIME =  14 * 24 * 60 * 60 * 1000L;

    private static final int REFRESH_COOKIE_TIME = 14 * 24 * 60 * 60;

    public static final String ACCESS_TOKEN = "accessToken";

    public static final String REFRESH_TOKEN = "refreshToken";

    public static final String AUTHORIZATION = "Authorization";

    public static final String BEARER = "Bearer ";

    @Value("${cors.domain}")
    private String domain;


    @Value("${jwt.secretKey}")
    private String secretKey;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // bean으로 등록 되면서 딱 한번 실행이 됩니다.
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오는 기능
    public String getHeaderToken(HttpServletRequest request, String type) {

        String tokenName = type.equals("Access") ? ACCESS_TOKEN : REFRESH_TOKEN;
        String authorization = request.getHeader(AUTHORIZATION);

        if (authorization != null && authorization.startsWith(BEARER)) {
            return authorization.substring(7);
        } else {
            return null;
        }
    }

    // 토큰 생성
    public TokenDto createAllToken(String userId) {
        return new TokenDto(createToken(userId, "Access"), createToken(userId, "Refresh"));
    }

    public String createToken(String id, String type) {

        Date date = new Date();

        long time = type.equals("Access") ? ACCESS_TIME : REFRESH_TIME;

        return Jwts.builder()
                .setSubject(id)
                .setExpiration(new Date(date.getTime() + time))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();

    }

    // 토큰 검증
    public Boolean tokenValidation(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    // refreshToken 토큰 검증
    // db에 저장되어 있는 token과 비교
    // db에 저장한다는 것이 jwt token을 사용한다는 강점을 상쇄시킨다.
    // db 보다는 redis를 사용하는 것이 더욱 좋다. (in-memory db기 때문에 조회속도가 빠르고 주기적으로 삭제하는 기능이 기본적으로 존재합니다.)
    public Boolean refreshTokenValidation(String token) {

        // 1차 토큰 검증
        if(!tokenValidation(token)) return false;

        String idFromToken = getIdFromToken(token);
        Long userId = Long.parseLong(idFromToken);
        // DB에 저장한 토큰 비교
        Optional<Token> refreshToken = tokenRepository.findByUserUserId(userId);

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    // 인증 객체 생성
    public Authentication createAuthentication(Long id) {
        UserDetails userDetails = userDetailsService.loadUserByUserId(id);
        // spring security 내에서 가지고 있는 객체입니다. (UsernamePasswordAuthenticationToken)
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 id 가져오는 기능
    public String getIdFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_TOKEN, accessToken);
    }

    // 리프레시 토큰 헤더 설정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader(REFRESH_TOKEN, refreshToken);
    }

    public void setCookieRefreshToken(HttpServletResponse response, String refreshToken) {
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .maxAge(REFRESH_COOKIE_TIME)
                .path("/")
                .secure(true)
                //.domain(domain)
                .sameSite("None")
                .httpOnly(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        /*response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        Cookie cookie = new Cookie(REFRESH_TOKEN, refreshToken);
        cookie.setMaxAge(REFRESH_COOKIE_TIME);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);*/
    }

    public void setDelCookieRefreshToken(HttpServletResponse response) {
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN, null)
                .maxAge(0)
                .path("/")
                .secure(true)
                .domain(domain)
                .sameSite("None")
                .httpOnly(true)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

/*        Cookie cookie = new Cookie(REFRESH_TOKEN, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);*/

    }

    public String getCookieToken(HttpServletRequest request, String type) {
        Cookie[] cookies = request.getCookies();

        String cookieName = type.equals("Access") ? ACCESS_TOKEN : REFRESH_TOKEN;

        AtomicReference<String> cookieToken = new AtomicReference<>();
        if (cookies != null) {
            Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(cookieName))
                    .findFirst()
                    .ifPresent(cookie -> {
                        cookieToken.set(cookie.getValue());
                    });
        }
        return cookieToken.get();
    }

    public void setResonseJwtToken(HttpServletResponse response, String accessToken, String refreshToken) {
        setHeaderAccessToken(response, accessToken);
        setCookieRefreshToken(response, refreshToken);
    }

}
