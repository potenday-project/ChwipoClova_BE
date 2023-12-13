package com.chwipoClova.qa.repository;

import com.chwipoClova.qa.entity.Qa;
import com.chwipoClova.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QaRepository extends JpaRepository<Qa, Long> {

    Optional<Qa> findByInterviewInterviewIdAndQaId(Long interviewId, Long qaId);

    List<Qa> findByInterviewInterviewIdOrderByQaId(Long interviewId);

    Qa findFirstByInterviewInterviewIdOrderByQaIdDesc(Long interviewId);

    @Modifying
    @Query(value = "update Qa q set q.answer = null where q.interviewId = :interviewId and answer is not null", nativeQuery = true)
    int initQa(@Param("interviewId") Long interviewId);
}
