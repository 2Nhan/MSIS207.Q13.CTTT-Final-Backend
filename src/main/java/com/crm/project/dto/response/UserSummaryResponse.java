package com.crm.project.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryResponse {
    private Long totalUsers;
    private Long newThisMonth;
    private Long activeUsers;
}
