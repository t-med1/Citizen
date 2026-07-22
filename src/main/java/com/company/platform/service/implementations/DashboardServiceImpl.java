package com.company.platform.service.implementations;

import com.company.platform.dto.ComplaintDTO;
import com.company.platform.dto.DashboardStatsDTO;
import com.company.platform.dto.UserDTO;
import com.company.platform.entity.Complaint;
import com.company.platform.entity.ComplaintStatus;
import com.company.platform.entity.User;
import com.company.platform.mapper.ComplaintMapper;
import com.company.platform.mapper.UserMapper;
import com.company.platform.repository.CategoryRepository;
import com.company.platform.repository.ComplaintRepository;
import com.company.platform.repository.UserRepository;
import com.company.platform.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final CategoryRepository categoryRepository;
    private final ComplaintMapper complaintMapper;
    private final UserMapper userMapper;

    @Override
    public DashboardStatsDTO getStats() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (ComplaintStatus status : ComplaintStatus.values()) {
            byStatus.put(status.name(), complaintRepository.countByStatus(status));
        }

        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (Object[] row : complaintRepository.countGroupedByCategory()) {
            byCategory.put((String) row[0], (Long) row[1]);
        }

        List<ComplaintDTO> recentComplaints = complaintRepository.findRecent().stream()
                .limit(5)
                .map(complaintMapper::toDTO)
                .collect(Collectors.toList());

        List<UserDTO> recentUsers = userRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());

        return DashboardStatsDTO.builder()
                .totalUsers(userRepository.count())
                .totalComplaints(complaintRepository.count())
                .totalCategories(categoryRepository.count())
                .totalAgents(userRepository.findAllByRoleName("ROLE_AGENT").size())
                .complaintsByStatus(byStatus)
                .complaintsByCategory(byCategory)
                .recentComplaints(recentComplaints)
                .recentUsers(recentUsers)
                .build();
    }
}
