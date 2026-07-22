package com.company.platform.service.interfaces;

import com.company.platform.dto.CommentDTO;
import com.company.platform.entity.User;

import java.util.List;

public interface CommentService {

    List<CommentDTO> findByComplaint(Long complaintId);

    CommentDTO addComment(Long complaintId, String content, User author);
}
