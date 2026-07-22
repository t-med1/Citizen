package com.company.platform.mapper;

import com.company.platform.dto.CommentDTO;
import com.company.platform.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDTO toDTO(Comment comment) {
        if (comment == null) return null;
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .complaintId(comment.getComplaint() != null ? comment.getComplaint().getId() : null)
                .authorName(comment.getAuthor() != null ? comment.getAuthor().getFullName() : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
