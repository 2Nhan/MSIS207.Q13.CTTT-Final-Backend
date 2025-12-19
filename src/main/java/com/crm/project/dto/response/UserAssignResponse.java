package com.crm.project.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAssignResponse {
    private String id;
    private String fullName;
    private String email;
    private String avatarUrl;
}
