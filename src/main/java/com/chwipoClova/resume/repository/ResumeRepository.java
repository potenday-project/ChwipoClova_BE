package com.chwipoClova.resume.repository;

import com.chwipoClova.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUserUserIdOrderByRegDate(Long userId);

    Optional<Resume> findByUserUserIdAndResumeId(Long userId, Long resumeId);

    Optional<Resume> findTop1ByUserUserIdOrderByRegDate(Long userId);
}
