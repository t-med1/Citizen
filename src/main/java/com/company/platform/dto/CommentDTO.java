package com.company.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long id;

    @NotBlank(message = "Le commentaire ne peut pas être vide")
    private String content;

    private Long complaintId;
    private String authorName;
    private LocalDateTime createdAt;
}
