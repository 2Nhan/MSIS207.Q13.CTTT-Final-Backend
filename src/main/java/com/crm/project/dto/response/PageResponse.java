package com.crm.project.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> content;

    private boolean hasPre;

    private boolean hasNext;

    private int pageNumber;

    private int totalPages;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.hasPre = page.hasPrevious();
        this.hasNext = page.hasNext();
        this.pageNumber = page.getNumber() + 1;
        this.totalPages = page.getTotalPages();
    }
}
