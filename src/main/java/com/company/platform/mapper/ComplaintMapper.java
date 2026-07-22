package com.company.platform.mapper;

import com.company.platform.dto.ComplaintDTO;
import com.company.platform.entity.Complaint;
import org.springframework.stereotype.Component;

@Component
public class ComplaintMapper {

    public ComplaintDTO toDTO(Complaint c) {
        if (c == null) return null;
        return ComplaintDTO.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .location(c.getLocation())
                .categoryId(c.getCategory() != null ? c.getCategory().getId() : null)
                .categoryName(c.getCategory() != null ? c.getCategory().getName() : null)
                .status(c.getStatus())
                .citizenId(c.getCitizen() != null ? c.getCitizen().getId() : null)
                .citizenName(c.getCitizen() != null ? c.getCitizen().getFullName() : null)
                .assignedAgentId(c.getAssignedAgent() != null ? c.getAssignedAgent().getId() : null)
                .assignedAgentName(c.getAssignedAgent() != null ? c.getAssignedAgent().getFullName() : null)
                .commentCount(c.getComments() == null ? 0 : c.getComments().size())
                .attachmentCount(c.getAttachments() == null ? 0 : c.getAttachments().size())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
