package com.chwipoClova.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;

@Slf4j
public class JwtProvider {

    private final long ACCESS_TOKEN_EXPIRE_TIME;            // 30분
    private final long REFRESH_TOKEN_EXPIRE_TIME;  // 7일

    private final Key key;

    public JwtProvider(@Value("${jwt.secret}") String secretKey ,
                       @Value("${jwt.access-token-expire-time}") long accessTime,
                       @Value("${jwt.refresh-token-expire-time}") long refreshTime
    ) {
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    protected String createToken(String id, long tokenValid) {
        // ex) sub : abc@abc.com
        Claims claims = Jwts.claims().setSubject(id);

/*        // ex)  auth : ROLE_USER,ROLE_ADMIN
        claims.put(AUTHORITIES_KEY,
                auth.stream()
                        .map(Authority::getAuthorityName)
                        .collect(Collectors.joining(","))
        );*/

        // 현재시간
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // 토큰 발행 유저 정보
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(new Date(now.getTime() + tokenValid)) // 토큰 만료시간
                .signWith(key, SignatureAlgorithm.HS512) // 키와 알고리즘 설정
                .compact();
    }

    /**
     *
     * @param id
     * @return 엑세스 토큰 생성
     */
    public String createAccessToken(String id) {
        return this.createToken(id, ACCESS_TOKEN_EXPIRE_TIME);
    }

    /**
     *
     * @param id
     * @return 리프레시 토큰 생성
     */
    public String createRefreshToken(String id) {
        return this.createToken(id, REFRESH_TOKEN_EXPIRE_TIME);
    }

    /**
     *
     * @param token
     * @return 토큰 값을 파싱하여 클레임에 담긴 id 값을 가져온다.
     */
    public String getMemberIdByToken(String token) {
        // 토큰의 claim 의 sub 키에 이메일 값이 들어있다.
        return this.parseClaims(token).getSubject();
    }

/*    public TokenDTO createTokenDTO(String accessToken,String refreshToken) {
        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(BEARER_TYPE)
                .build();
    }*/
/*
    public Authentication getAuthentication(String accessToken) throws BizException{

        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null || !StringUtils.hasText(claims.get(AUTHORITIES_KEY).toString())) {
            throw new BizException(AuthorityExceptionType.NOT_FOUND_AUTHORITY); // 유저에게 아무런 권한이 없습니다.
        }

        log.debug("claims.getAuth = {}",claims.get(AUTHORITIES_KEY));
        log.debug("claims.getEmail = {}",claims.getSubject());

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        authorities.stream().forEach(o->{
            log.debug("getAuthentication -> authorities = {}",o.getAuthority());
        });

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new CustomEmailPasswordAuthToken(principal, "", authorities);
    }*/

    public int validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return 1;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            return 2;
        } catch (Exception e) {
            log.info("잘못된 토큰입니다.");
            return -1;
        }
    }


    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) { // 만료된 토큰이 더라도 일단 파싱을 함
            return e.getClaims();
        }
    }
}
