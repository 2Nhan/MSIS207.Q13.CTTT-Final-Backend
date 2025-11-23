package com.crm.project.dto.response;

import lombok.*;

import java.util.Map;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportResponse {
    List<Map<String, String>> data;
}
