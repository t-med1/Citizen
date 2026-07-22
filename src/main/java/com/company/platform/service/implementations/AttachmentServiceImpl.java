package com.company.platform.service.implementations;

import com.company.platform.dto.AttachmentDTO;
import com.company.platform.entity.Attachment;
import com.company.platform.entity.Complaint;
import com.company.platform.exception.ResourceNotFoundException;
import com.company.platform.mapper.AttachmentMapper;
import com.company.platform.repository.AttachmentRepository;
import com.company.platform.repository.ComplaintRepository;
import com.company.platform.service.interfaces.AttachmentService;
import com.company.platform.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ComplaintRepository complaintRepository;
    private final AttachmentMapper attachmentMapper;
    private final FileStorageUtil fileStorageUtil;

    @Override
    public List<AttachmentDTO> findByComplaint(Long complaintId) {
        Complaint complaint = fetchComplaint(complaintId);
        return attachmentRepository.findByComplaint(complaint).stream()
                .map(attachmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AttachmentDTO upload(Long complaintId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier sélectionné");
        }
        Complaint complaint = fetchComplaint(complaintId);
        String storedName = fileStorageUtil.store(file);

        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .filePath(storedName)
                .complaint(complaint)
                .build();

        return attachmentMapper.toDTO(attachmentRepository.save(attachment));
    }

    @Override
    public Attachment getEntityById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pièce jointe", id));
    }

    @Override
    public void delete(Long id) {
        Attachment attachment = getEntityById(id);
        fileStorageUtil.delete(attachment.getFilePath());
        attachmentRepository.delete(attachment);
    }

    private Complaint fetchComplaint(Long complaintId) {
        return complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Réclamation", complaintId));
    }
}
