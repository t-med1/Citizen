package com.company.platform.dto;

import com.company.platform.validation.PasswordMatches;
import com.company.platform.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PasswordMatches
public class UserRegisterDTO {

    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(max = 150)
    private String fullName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @UniqueEmail
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @NotBlank(message = "Merci de confirmer le mot de passe")
    private String confirmPassword;

    private String phone;

    private String address;
}
