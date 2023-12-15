package com.chwipoClova.common.repository;

import com.chwipoClova.common.dto.Token;
import com.chwipoClova.common.entity.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {
}
