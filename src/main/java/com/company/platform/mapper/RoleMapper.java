package com.company.platform.mapper;

import com.company.platform.dto.RoleDTO;
import com.company.platform.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleDTO toDTO(Role role) {
        if (role == null) return null;
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .userCount(role.getUsers() == null ? 0 : role.getUsers().size())
                .build();
    }

    public Role toEntity(RoleDTO dto) {
        return Role.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}
