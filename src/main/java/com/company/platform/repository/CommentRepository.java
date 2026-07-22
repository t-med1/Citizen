package com.company.platform.repository;

import com.company.platform.entity.Comment;
import com.company.platform.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByComplaintOrderByCreatedAtAsc(Complaint complaint);
}
