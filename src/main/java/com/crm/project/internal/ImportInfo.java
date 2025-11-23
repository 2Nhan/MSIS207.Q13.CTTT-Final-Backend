package com.crm.project.internal;

import lombok.*;

import java.util.Map;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportInfo {
    List<Map<String, String>> data;
}
