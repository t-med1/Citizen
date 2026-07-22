package com.company.platform.service.implementations;

import com.company.platform.dto.CommentDTO;
import com.company.platform.entity.Comment;
import com.company.platform.entity.Complaint;
import com.company.platform.entity.User;
import com.company.platform.exception.ResourceNotFoundException;
import com.company.platform.mapper.CommentMapper;
import com.company.platform.repository.CommentRepository;
import com.company.platform.repository.ComplaintRepository;
import com.company.platform.service.interfaces.CommentService;
import com.company.platform.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ComplaintRepository complaintRepository;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;

    @Override
    public List<CommentDTO> findByComplaint(Long complaintId) {
        Complaint complaint = fetchComplaint(complaintId);
        return commentRepository.findByComplaintOrderByCreatedAtAsc(complaint).stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO addComment(Long complaintId, String content, User author) {
        Complaint complaint = fetchComplaint(complaintId);

        Comment comment = Comment.builder()
                .content(content)
                .complaint(complaint)
                .author(author)
                .build();
        Comment saved = commentRepository.save(comment);

        if (complaint.getCitizen() != null && !complaint.getCitizen().getId().equals(author.getId())) {
            notificationService.notify(complaint.getCitizen(),
                    "Nouveau commentaire sur votre réclamation \"" + complaint.getTitle() + "\"",
                    "/complaints/" + complaint.getId());
        }
        if (complaint.getAssignedAgent() != null && !complaint.getAssignedAgent().getId().equals(author.getId())) {
            notificationService.notify(complaint.getAssignedAgent(),
                    "Nouveau commentaire sur la réclamation \"" + complaint.getTitle() + "\"",
                    "/complaints/" + complaint.getId());
        }

        return commentMapper.toDTO(saved);
    }

    private Complaint fetchComplaint(Long complaintId) {
        return complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Réclamation", complaintId));
    }
}
