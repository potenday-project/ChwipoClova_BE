package com.chwipoClova.common.repository;

import com.chwipoClova.common.dto.Token;
import com.chwipoClova.common.entity.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {

    @Procedure("API_LOG_SAVE")
    void apiLogSave(Long userId, String apiUrl, String reqData, String resData, String message);
}
