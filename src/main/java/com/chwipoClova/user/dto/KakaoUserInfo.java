package com.chwipoClova.user.dto;

import com.chwipoClova.user.enums.UserLoginType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Date;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfo {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("connected_at")
    private Date connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoAccount {
        private KakaoProfile profile;

        private String email;

    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoProfile {
        private String nickname;

        private String thumbnail_image_url;

        private String profile_image_url;

        private boolean is_default_image;
    }

    public String getEmail() {
        return kakaoAccount.email;
    }

    public String getNickname() {
        return kakaoAccount.profile.nickname;
    }

    public String getThumbnailImageUrl() {
        return kakaoAccount.profile.thumbnail_image_url;
    }

    public String getProfileImageUrl() {
        return kakaoAccount.profile.profile_image_url;
    }

    public boolean isDefaultImage() {
        return kakaoAccount.profile.is_default_image;
    }

    public UserLoginType getOAuthProvider() {
        return UserLoginType.KAKAO;
    }
}
