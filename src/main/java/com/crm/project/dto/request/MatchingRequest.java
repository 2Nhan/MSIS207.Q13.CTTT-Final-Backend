package com.crm.project.dto.request;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchingRequest {
    private Map<String, String> matching;
}
