package com.company.platform.dto;

import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {

    private long totalUsers;
    private long totalComplaints;
    private long totalCategories;
    private long totalAgents;

    @Builder.Default
    private Map<String, Long> complaintsByStatus = new LinkedHashMap<>();

    @Builder.Default
    private Map<String, Long> complaintsByCategory = new LinkedHashMap<>();

    @Builder.Default
    private List<ComplaintDTO> recentComplaints = List.of();

    @Builder.Default
    private List<UserDTO> recentUsers = List.of();
}
