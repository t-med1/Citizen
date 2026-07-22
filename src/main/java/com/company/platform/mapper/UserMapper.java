package com.company.platform.mapper;

import com.company.platform.dto.UserDTO;
import com.company.platform.entity.Role;
import com.company.platform.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .enabled(user.isEnabled())
                .roleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toSet()))
                .roleNames(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }

    public void updateEntityFromDTO(UserDTO dto, User user) {
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setEnabled(dto.isEnabled());
    }
}
