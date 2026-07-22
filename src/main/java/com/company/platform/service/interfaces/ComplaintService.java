package com.company.platform.service.interfaces;

import com.company.platform.dto.ComplaintDTO;
import com.company.platform.entity.Complaint;
import com.company.platform.entity.ComplaintStatus;
import com.company.platform.entity.User;

import java.util.List;

public interface ComplaintService {

    List<ComplaintDTO> findAll();

    List<ComplaintDTO> findForUser(User currentUser);

    ComplaintDTO findById(Long id);

    Complaint getEntityById(Long id);

    ComplaintDTO create(ComplaintDTO dto, User citizen);

    ComplaintDTO updateStatus(Long id, ComplaintStatus status, Long assignedAgentId);

    void delete(Long id);

    List<ComplaintDTO> search(String keyword, ComplaintStatus status);

    long countAll();

    long countByStatus(ComplaintStatus status);

    boolean canAccess(Long complaintId, User user);
}
