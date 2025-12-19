package com.crm.project.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private boolean deleted;

    private String address;

    private LocalDate dateOfBirth;

    private String avatarUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
