package com.company.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {

    private Long id;

    @NotBlank(message = "Le nom du rôle est obligatoire")
    @Pattern(regexp = "^ROLE_[A-Z_]+$", message = "Le nom doit respecter le format ROLE_XXXX")
    private String name;

    private String description;

    private int userCount;
}
