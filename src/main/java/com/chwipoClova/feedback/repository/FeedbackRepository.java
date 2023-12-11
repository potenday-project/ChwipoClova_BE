package com.chwipoClova.feedback.repository;

import com.chwipoClova.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByQaQaIdOrderByFeedbackId(Long qaId);
}
