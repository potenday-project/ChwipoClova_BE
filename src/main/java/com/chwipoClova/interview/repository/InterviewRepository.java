package com.chwipoClova.interview.repository;

import com.chwipoClova.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
}
