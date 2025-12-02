package com.crm.project.dto.request;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationItemRequest {
    private String id;

    private Integer quantity;
}
