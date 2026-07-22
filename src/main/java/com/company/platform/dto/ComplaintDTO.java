package com.company.platform.dto;

import com.company.platform.entity.ComplaintStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintDTO {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private String location;

    @NotNull(message = "Sélectionnez une catégorie")
    private Long categoryId;
    private String categoryName;

    private ComplaintStatus status;

    private Long citizenId;
    private String citizenName;

    private Long assignedAgentId;
    private String assignedAgentName;

    private int commentCount;
    private int attachmentCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
