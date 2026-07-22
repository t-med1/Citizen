package com.company.platform.mapper;

import com.company.platform.dto.AttachmentDTO;
import com.company.platform.entity.Attachment;
import org.springframework.stereotype.Component;

@Component
public class AttachmentMapper {

    public AttachmentDTO toDTO(Attachment a) {
        if (a == null) return null;
        return AttachmentDTO.builder()
                .id(a.getId())
                .fileName(a.getFileName())
                .fileType(a.getFileType())
                .complaintId(a.getComplaint() != null ? a.getComplaint().getId() : null)
                .uploadedAt(a.getUploadedAt())
                .build();
    }
}
