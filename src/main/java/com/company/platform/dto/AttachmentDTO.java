package com.company.platform.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentDTO {

    private Long id;
    private String fileName;
    private String fileType;
    private Long complaintId;
    private LocalDateTime uploadedAt;
}
