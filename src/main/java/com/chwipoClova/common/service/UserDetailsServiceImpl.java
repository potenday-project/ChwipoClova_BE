package com.chwipoClova.common.service;

import com.chwipoClova.common.dto.UserDetailsImpl;
import com.chwipoClova.user.entity.User;
import com.chwipoClova.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl {

    private final UserRepository userRepository;

    public UserDetails loadUserByUserId(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("토큰 id정보가 올바르지 않습니다.");
            return null;
            }
        );
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setUser(user);

        return userDetails;
    }
}
