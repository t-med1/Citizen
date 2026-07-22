package com.company.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(max = 150, message = "150 caractères maximum")
    private String fullName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    private String password;

    private String phone;

    private String address;

    private boolean enabled = true;

    @NotEmpty(message = "Sélectionnez au moins un rôle")
    @Builder.Default
    private Set<Long> roleIds = new HashSet<>();

    @Builder.Default
    private Set<String> roleNames = new HashSet<>();

    private LocalDateTime createdAt;
}
