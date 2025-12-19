package com.crm.project.dto.response;

import com.crm.project.internal.UserStatistics;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String roleName;
    private Boolean deleted;
    private UserStatistics statistics;

    private List<ActivitySummaryResponse> activities;
}
