package com.crm.project.internal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatistics {
    private Long totalLeads;
    private Long openLeads;
    private Long convertedLeads;
    private Long totalActivities;
    private Long pendingActivities;
    private Long completedActivities;
}
