package com.company.platform.service.implementations;

import com.company.platform.dto.RoleDTO;
import com.company.platform.entity.Role;
import com.company.platform.exception.DuplicateResourceException;
import com.company.platform.exception.ResourceNotFoundException;
import com.company.platform.mapper.RoleMapper;
import com.company.platform.repository.RoleRepository;
import com.company.platform.service.interfaces.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleDTO> findAll() {
        return roleRepository.findAll().stream().map(roleMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public RoleDTO findById(Long id) {
        return roleMapper.toDTO(fetch(id));
    }

    @Override
    public RoleDTO create(RoleDTO dto) {
        if (roleRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Un rôle avec ce nom existe déjà : " + dto.getName());
        }
        Role role = roleMapper.toEntity(dto);
        role.setId(null);
        return roleMapper.toDTO(roleRepository.save(role));
    }

    @Override
    public RoleDTO update(Long id, RoleDTO dto) {
        Role role = fetch(id);
        if (!role.getName().equals(dto.getName()) && roleRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Un rôle avec ce nom existe déjà : " + dto.getName());
        }
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        return roleMapper.toDTO(roleRepository.save(role));
    }

    @Override
    public void delete(Long id) {
        Role role = fetch(id);
        if (!role.getUsers().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer un rôle encore attribué à des utilisateurs");
        }
        roleRepository.delete(role);
    }

    private Role fetch(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rôle", id));
    }
}
