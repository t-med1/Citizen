package com.company.platform.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private Long id;
    private String message;
    private String link;
    private boolean read;
    private LocalDateTime createdAt;
}
