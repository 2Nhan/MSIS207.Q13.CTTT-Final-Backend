package com.crm.project.dto.response;

import lombok.*;

import java.util.Set;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportPreviewResponse {
    private Set<String> userHeader;
    private Set<String> systemHeader;
    private List<?> data;
}
