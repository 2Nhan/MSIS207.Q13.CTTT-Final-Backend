package com.crm.project.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageRenameResponse {
    private String id;

    private String name;
}
