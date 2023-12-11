package com.chwipoClova.qa.repository;

import com.chwipoClova.qa.entity.Qa;
import com.chwipoClova.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QaRepository extends JpaRepository<Qa, Long> {

    Optional<Qa> findByInterviewInterviewIdAndQaId(Long interviewId, Long qaId);

    List<Qa> findByInterviewInterviewIdOrderByQaId(Long interviewId);

}
