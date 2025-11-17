package com.crm.project.dto.response;

import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportResultResponse<T> {
    List<T> validList;
    List<T> invalidList;
}
