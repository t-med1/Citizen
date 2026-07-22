package com.company.platform.service.interfaces;

import com.company.platform.dto.ProfileUpdateDTO;
import com.company.platform.dto.UserDTO;
import com.company.platform.dto.UserRegisterDTO;
import com.company.platform.entity.User;

import java.util.List;

public interface UserService {

    List<UserDTO> findAll();

    UserDTO findById(Long id);

    User getEntityById(Long id);

    User getEntityByEmail(String email);

    UserDTO create(UserDTO dto);

    UserDTO update(Long id, UserDTO dto);

    void delete(Long id);

    void registerCitizen(UserRegisterDTO dto);

    void updateProfile(Long userId, ProfileUpdateDTO dto);

    long countAll();

    long countAgents();

    List<UserDTO> findAgents();
}
