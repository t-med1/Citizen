package com.company.platform.service.implementations;

import com.company.platform.dto.ComplaintDTO;
import com.company.platform.entity.Category;
import com.company.platform.entity.Complaint;
import com.company.platform.entity.ComplaintStatus;
import com.company.platform.entity.User;
import com.company.platform.exception.ResourceNotFoundException;
import com.company.platform.mapper.ComplaintMapper;
import com.company.platform.repository.CategoryRepository;
import com.company.platform.repository.ComplaintRepository;
import com.company.platform.repository.UserRepository;
import com.company.platform.service.interfaces.ComplaintService;
import com.company.platform.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ComplaintMapper complaintMapper;
    private final NotificationService notificationService;

    @Override
    public List<ComplaintDTO> findAll() {
        return complaintRepository.findRecent().stream().map(complaintMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ComplaintDTO> findForUser(User currentUser) {
        List<Complaint> complaints;
        if (currentUser.hasRole("ROLE_ADMIN")) {
            complaints = complaintRepository.findRecent();
        } else if (currentUser.hasRole("ROLE_AGENT")) {
            complaints = complaintRepository.findByAssignedAgent(currentUser);
        } else {
            complaints = complaintRepository.findByCitizen(currentUser);
        }
        return complaints.stream().map(complaintMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ComplaintDTO findById(Long id) {
        return complaintMapper.toDTO(getEntityById(id));
    }

    @Override
    public Complaint getEntityById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réclamation", id));
    }

    @Override
    public ComplaintDTO create(ComplaintDTO dto, User citizen) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", dto.getCategoryId()));

        Complaint complaint = Complaint.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .status(ComplaintStatus.NEW)
                .citizen(citizen)
                .category(category)
                .build();

        Complaint saved = complaintRepository.save(complaint);
        return complaintMapper.toDTO(saved);
    }

    @Override
    public ComplaintDTO updateStatus(Long id, ComplaintStatus status, Long assignedAgentId) {
        Complaint complaint = getEntityById(id);
        if (!complaint.getStatus().canTransitionTo(status)) {
            throw new IllegalStateException(
                    "Transition de statut invalide : " + complaint.getStatus() + " → " + status);
        }
        complaint.setStatus(status);

        if (assignedAgentId != null) {
            User agent = userRepository.findById(assignedAgentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Agent", assignedAgentId));
            if (!agent.hasRole("ROLE_AGENT")) {
                throw new IllegalArgumentException("L'utilisateur sélectionné n'est pas un agent");
            }
            complaint.setAssignedAgent(agent);
        }

        Complaint saved = complaintRepository.save(complaint);

        notificationService.notify(
                saved.getCitizen(),
                "Le statut de votre réclamation \"" + saved.getTitle() + "\" est maintenant : " + status,
                "/complaints/" + saved.getId()
        );

        return complaintMapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        Complaint complaint = getEntityById(id);
        complaintRepository.delete(complaint);
    }

    @Override
    public List<ComplaintDTO> search(String keyword, ComplaintStatus status) {
        String cleanKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return complaintRepository.search(cleanKeyword, status).stream()
                .map(complaintMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long countAll() {
        return complaintRepository.count();
    }

    @Override
    public long countByStatus(ComplaintStatus status) {
        return complaintRepository.countByStatus(status);
    }

    @Override
    public boolean canAccess(Long complaintId, User user) {
        Complaint complaint = getEntityById(complaintId);
        if (user.hasRole("ROLE_ADMIN")) {
            return true;
        }
        if (user.hasRole("ROLE_AGENT")) {
            return complaint.getAssignedAgent() != null && complaint.getAssignedAgent().getId().equals(user.getId());
        }
        return complaint.getCitizen() != null && complaint.getCitizen().getId().equals(user.getId());
    }
}
