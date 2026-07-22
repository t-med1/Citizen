package com.company.platform.service.interfaces;

import com.company.platform.dto.AttachmentDTO;
import com.company.platform.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {

    List<AttachmentDTO> findByComplaint(Long complaintId);

    AttachmentDTO upload(Long complaintId, MultipartFile file);

    Attachment getEntityById(Long id);

    void delete(Long id);
}
