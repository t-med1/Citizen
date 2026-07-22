package com.company.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateDTO {

    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(max = 150)
    private String fullName;

    private String phone;

    private String address;

    private String currentPassword;

    private String newPassword;

    private String confirmNewPassword;
}
