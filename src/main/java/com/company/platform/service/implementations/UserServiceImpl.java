package com.company.platform.service.implementations;

import com.company.platform.dto.ProfileUpdateDTO;
import com.company.platform.dto.UserDTO;
import com.company.platform.dto.UserRegisterDTO;
import com.company.platform.entity.Role;
import com.company.platform.entity.User;
import com.company.platform.exception.DuplicateResourceException;
import com.company.platform.exception.ResourceNotFoundException;
import com.company.platform.mapper.UserMapper;
import com.company.platform.repository.RoleRepository;
import com.company.platform.repository.UserRepository;
import com.company.platform.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO findById(Long id) {
        return userMapper.toDTO(getEntityById(id));
    }

    @Override
    public User getEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
    }

    @Override
    public User getEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun utilisateur avec l'email : " + email));
    }

    @Override
    public UserDTO create(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Un utilisateur avec cet email existe déjà : " + dto.getEmail());
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire à la création");
        }

        User user = new User();
        userMapper.updateEntityFromDTO(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(resolveRoles(dto.getRoleIds()));

        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO update(Long id, UserDTO dto) {
        User user = getEntityById(id);

        if (!user.getEmail().equalsIgnoreCase(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Un utilisateur avec cet email existe déjà : " + dto.getEmail());
        }

        userMapper.updateEntityFromDTO(dto, user);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setRoles(resolveRoles(dto.getRoleIds()));

        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        User user = getEntityById(id);
        userRepository.delete(user);
    }

    @Override
    public void registerCitizen(UserRegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Un compte existe déjà avec cet email : " + dto.getEmail());
        }
        Role citizenRole = roleRepository.findByName("ROLE_CITIZEN")
                .orElseThrow(() -> new ResourceNotFoundException("Le rôle ROLE_CITIZEN n'existe pas encore"));

        Set<Role> roles = new HashSet<>();
        roles.add(citizenRole);

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .enabled(true)
                .roles(roles)
                .build();

        userRepository.save(user);
    }

    @Override
    public void updateProfile(Long userId, ProfileUpdateDTO dto) {
        User user = getEntityById(userId);
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());

        boolean wantsPasswordChange = dto.getNewPassword() != null && !dto.getNewPassword().isBlank();
        if (wantsPasswordChange) {
            if (dto.getCurrentPassword() == null || !passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Le mot de passe actuel est incorrect");
            }
            if (dto.getNewPassword().length() < 6) {
                throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 6 caractères");
            }
            if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
                throw new IllegalArgumentException("Les nouveaux mots de passe ne correspondent pas");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        userRepository.save(user);
    }

    @Override
    public long countAll() {
        return userRepository.count();
    }

    @Override
    public long countAgents() {
        return userRepository.findAllByRoleName("ROLE_AGENT").size();
    }

    @Override
    public List<UserDTO> findAgents() {
        return userRepository.findAllByRoleName("ROLE_AGENT").stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    private Set<Role> resolveRoles(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("Sélectionnez au moins un rôle");
        }
        return roleIds.stream()
                .map(id -> roleRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Rôle", id)))
                .collect(Collectors.toSet());
    }
}
